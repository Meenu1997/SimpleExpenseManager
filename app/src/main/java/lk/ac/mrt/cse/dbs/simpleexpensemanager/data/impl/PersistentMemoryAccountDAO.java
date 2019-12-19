
package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * This is an In-Memory implementation of the AccountDAO interface. This is not a persistent storage. A HashMap is
 * used to store the account details temporarily in the memory.
 */
public class PersistentMemoryAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    //private final Map<String, Account> accounts;

    public static final String DATABASE_NAME = "170620P.db";
    public static final String CONTACTS_COLUMN_NO = "Account_No";
    public static final String CONTACTS_COLUMN_BANK_NAME = "Bank_Name";
    public static final String CONTACTS_COLUMN_HOLDER_NAME = "Holder_Name";
    public static final String CONTACTS_COLUMN_Balance = "Balance";

    public PersistentMemoryAccountDAO(Context context) {
        super(context, DATABASE_NAME , null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table account " +
                        "(Account_No text primary key, Bank_Name text,Holder_Name text,Balance double)"
        );
        db.execSQL(
                "create table tbltrans " +
                        "(Account_No text, type text, date BLOB , amount double)"
        );

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS account");
        onCreate(db);
    }



    @Override
    public List<String> getAccountNumbersList() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NO)));
            res.moveToNext();
        }
        return array_list;


    }

    @Override
    public List<Account> getAccountsList()
    {
        ArrayList<Account> array_list = new ArrayList<Account>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String Account_No = res.getString(res.getColumnIndex(CONTACTS_COLUMN_NO));
            String Bank_Name = res.getString(res.getColumnIndex(CONTACTS_COLUMN_BANK_NAME));
            String accountHolder_Name = res.getString(res.getColumnIndex(CONTACTS_COLUMN_HOLDER_NAME));
            Double Balance = res.getDouble(res.getColumnIndex(CONTACTS_COLUMN_Balance));

            array_list.add(new Account(Account_No,Bank_Name,accountHolder_Name,Balance));
            res.moveToNext();
        }
        return array_list;
    }

    @Override
    public Account getAccount(String Account_No) throws InvalidAccountException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account where id="+Account_No+"", null );

        String Account_No = res.getString(res.getColumnIndex(CONTACTS_COLUMN_NO));
        String Bank_Name = res.getString(res.getColumnIndex(CONTACTS_COLUMN_BANK_NAME));
        String accountHolder_Name = res.getString(res.getColumnIndex(CONTACTS_COLUMN_HOLDER_NAME));
        Double Balance = res.getDouble(res.getColumnIndex(CONTACTS_COLUMN_Balance));

        return  new Account(Account_No,Bank_Name,accountHolder_Name,Balance);


    }

    @Override
    public void addAccount(Account account) {
        String Account_No = account.getAccount_No();
        String Bank_Name = account.getBank_Name();
        String Holder_Name = account.getAccountHolder_Name();
        Double Balance = account.getBalance();


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Account_No", Account_No);
        contentValues.put("Bank_Name", Bank_Name);
        contentValues.put("Holder_Name", Holder_Name);
        contentValues.put("Balance", Balance);

        db.insert("account", null, contentValues);

    }

    @Override
    public void removeAccount(String Account_No) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("account",
                "Account_No = ? ",
                new String[] { Account_No});
    }

    @Override
    public void updateBalance(String Account_No, ExpenseType expenseType, double amount) throws InvalidAccountException {
//        if (!accounts.containsKey(Account_No)) {
//            String msg = "Account " + Account_No + " is invalid.";
//            throw new InvalidAccountException(msg);
//        }
//        Account account = accounts.get(Account_No);
//        // specific implementation based on the transaction type
//        switch (expenseType) {
//            case EXPENSE:
//                account.setBalance(account.getBalance() - amount);
//                break;
//            case INCOME:
//                account.setBalance(account.getBalance() + amount);
//                break;
//        }
//        accounts.put(Account_No, account);
    }
}
