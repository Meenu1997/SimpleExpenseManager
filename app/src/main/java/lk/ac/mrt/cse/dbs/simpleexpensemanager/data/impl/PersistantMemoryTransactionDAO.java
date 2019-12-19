package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import android.content.ContentValues;
import android.util.Log;
import java.util.List;
import java.util.Locale;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistantMemoryTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {

    public static final String DATABASE_NAME = "170620P.db";
    public static final String EXPENSE_COLUMN_Id = "Id";
    public static final String EXPENSE_COLUMN_NO = "Account_No";
    public static final String EXPENSE_COLUMN_DATE = "Date";
    public static final String EXPENSE_COLUMN_TYPE = "type";
    public static final String EXPENSE_COLUMN_AMOUNT = "Amount";

    private List<Transaction> transactions;

    public PersistantMemoryTransactionDAO(Context context) {
        super(context, DATABASE_NAME , null,1);
        transactions = new LinkedList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // TODO Auto-generated method stub
		String CREATE_tbltrans_TABLE = "CREATE TABLE"+ tbltrans + "(" +EXPENSE_COLUMN_ID +"INTEGER PRIMARY,"+EXPENSE_COLUMN_NO +"VARCHAR,"+EXPENSE_COLUMN_Date+"DATE,"+EXPENSE_COLUMN_Type+"TEXT,"+EXPENSE_COLUMN_Amount+"DECIMAL"+ ")";
db.excecSQL(CREATE_tbltrans_TABLE);	


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS tbltrans");
        onCreate(db);
    }


    @Override
    public void logTransaction(Date Date, String Account_No, ExpenseType expenseType, double Amount) {
        Transaction transaction = new Transaction(Date, Account_No, expenseType, Amount);
        String accountNumber = transaction.getAccount_No();
        Date dates = transaction.getDate();

        byte[] byteDate = dates.toString().getBytes();
        ExpenseType types = transaction.getExpenseType();
        String strType = types.toString();
        byte[] byteType = toString().getBytes();
        Double amounts = transaction.getAmount();

        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        Log.d("Date",formattedDate);
        byte[] timeStamp = formattedDate.getBytes();


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Account_No", Account_No);
        contentValues.put("Amount", amounts);
        contentValues.put("type",strType);
        contentValues.put("Date", byteDate);


        db.insert("tbltrans", null, contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        transactions.clear();
        Log.d("creation","starting");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( " select * from tbltrans", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){

            String Account_No = res.getString(res.getColumnIndex(EXPENSE_COLUMN_NO));
            Double Amount = res.getDouble(res.getColumnIndex(EXPENSE_COLUMN_AMOUNT));
            String transType = res.getString(res.getColumnIndex(EXPENSE_COLUMN_TYPE));

            ExpenseType type = ExpenseType.valueOf(transType);
            byte[] Date = res.getBlob(res.getColumnIndex(EXPENSE_COLUMN_DATE));


            String str = new String(Date, StandardCharsets.UTF_8);
            Log.d("loadedDate",str);

            Date finalDate;
            try {


                SimpleDateFormat inputFormat = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss 'GMT'z", Locale.ENGLISH);
                finalDate = inputFormat.parse(str);
                transactions.add(new Transaction(finalDate,Account_No,type,Amount));
                Log.d("creation","success");
            }catch (java.text.ParseException e){
                Log.d("creation","failed");
                Calendar cal = Calendar.getInstance();

                finalDate = cal.getTime();
                transactions.add(new Transaction(finalDate,Account_No,type,Amount));

            }


            res.moveToNext();
        }
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}


