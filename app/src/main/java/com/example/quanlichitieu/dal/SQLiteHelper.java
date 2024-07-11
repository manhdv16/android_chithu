package com.example.quanlichitieu.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.quanlichitieu.model.Statistic;
import com.example.quanlichitieu.model.Type;
import com.example.quanlichitieu.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QLTC.db";
    private static int DATABASE_VERSION = 1;

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table types("+
                "id integer primary key autoincrement,"+
                "name text not null,"+
                "status text not null)";
        db.execSQL(sql);
        String sql1 = "create table transactions("+
                "id integer primary key autoincrement,"+
                "amount real not null,"+
                "type text not null,"+
                "date_update text not null,"+
                "note text,"+
                "status text not null)";
        db.execSQL(sql1);
        String sql2 = "CREATE TABLE statistics (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "time_period text NOT NULL," +
                "total_income REAL NOT NULL," +
                "total_expense REAL NOT NULL," +
                "profit REAL NOT NULL)";
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void insertType(Type c){
        String sql = "insert into types(name,status)" + "values(?,?)";
        String[] args = {c.getName(),c.getStatus()};
        SQLiteDatabase st = getWritableDatabase();
        st.execSQL(sql,args);
    }
    public boolean updateType(Type c) {
        ContentValues values = new ContentValues();
        values.put("name",c.getName());
        values.put("status",c.getStatus());
        SQLiteDatabase st = getWritableDatabase();
        int rowsAffected = st.update("types", values, "id = ?", new String[] { String.valueOf(c.getId()) });
        st.close();
        return rowsAffected != 0;
    }
//    public void deleteType(int id){
//        String whereClause = "id = ?";
//        String[] args ={Integer.toString(id)};
//        SQLiteDatabase st = getWritableDatabase();
//        st.delete("types",whereClause,args);
//    }
    public boolean insertTransaction(Transaction t){
        ContentValues values = new ContentValues();
        values.put("amount",t.getAmount());
        values.put("type",t.getType());
        values.put("date_update",t.getDateUpdate());
        values.put("note",t.getNote());
        values.put("status",t.getStatus());
        SQLiteDatabase st = getWritableDatabase();
        long result = st.insert("transactions", null, values);
        st.close();
        return result!=-1;
    }
    public boolean updateTransaction(Transaction t){
        ContentValues values = new ContentValues();
        values.put("amount",t.getAmount());
        values.put("type",t.getType());
        values.put("date_update",t.getDateUpdate());
        values.put("note",t.getNote());
        values.put("status",t.getStatus());
        SQLiteDatabase st = getWritableDatabase();
        int rowsAffected = st.update("transactions", values, "id = ?", new String[] { String.valueOf(t.getId()) });
        st.close();
        return rowsAffected != 0;
    }
    public void deleteTransaction(int id){
        String whereClause = "id = ?";
        String[] args ={Integer.toString(id)};
        SQLiteDatabase st = getWritableDatabase();
        st.delete("transactions",whereClause,args);
    }
    public void insertOrUpdateStatistics(Transaction t, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        String date = t.getDateUpdate();
        date = date.substring(3,10);
        Cursor cursor = db.rawQuery("SELECT * FROM statistics WHERE time_period = ?", new String[] { date });

        if (cursor.moveToFirst()) {
            double currentTotalIncome = cursor.getDouble(2);
            double currentTotalExpense = cursor.getDouble(3);
            double currentProfit = cursor.getDouble(4);
            ContentValues values = new ContentValues();
            double newProfit=0;
            if(status.equals("delete")){
                if (t.getStatus().equals("Chi tiêu")) {
                    double newTotalExpense = currentTotalExpense - t.getAmount();
                    newProfit = currentTotalIncome - newTotalExpense;
                    values.put("total_expense", newTotalExpense);
                }else {
                    double newTotalIncome = currentTotalIncome - t.getAmount();
                    newProfit = newTotalIncome-currentTotalExpense;
                    values.put("total_income", newTotalIncome);
                }
                values.put("profit", newProfit);
                db.update("statistics", values, "time_period = ?", new String[] { date });
            }
            else if(status.equals("add")){
                if (t.getStatus().equals("Chi tiêu")) {
                    double newTotalExpense = currentTotalExpense + t.getAmount();
                    newProfit = currentTotalIncome - newTotalExpense;
                    values.put("total_expense", newTotalExpense);
                }
                else {
                    double newTotalIncome = currentTotalIncome + t.getAmount();
                    newProfit = newTotalIncome-currentTotalExpense;
                    values.put("total_income", newTotalIncome);
                }
                values.put("profit", newProfit);
                db.update("statistics", values, "time_period = ?", new String[] { date });
            }
        }
        else {
            ContentValues values = new ContentValues();
            values.put("time_period", date);
            if(t.getStatus().equals("Chi tiêu")){
                values.put("total_expense", t.getAmount());
                values.put("total_income", 0);
                values.put("profit", 0-t.getAmount());
            }else{
                values.put("total_income", t.getAmount());
                values.put("total_expense", 0);
                values.put("profit", t.getAmount());
            }
            db.insert("statistics", null, values);
        }
        cursor.close();
        db.close();
    }
    public void updateStatistics(Transaction o, Transaction n) {
        String odate = o.getDateUpdate().substring(3,10);
        String ndate = n.getDateUpdate().substring(3,10);
        SQLiteDatabase db = this.getWritableDatabase();
        if(odate.equals(ndate)) {
            Cursor cursor = db.rawQuery("SELECT * FROM statistics WHERE time_period = ?", new String[] { odate });

            if (cursor.moveToFirst()) {
                double currentTotalIncome = cursor.getDouble(2);
                double currentTotalExpense = cursor.getDouble(3);
                double currentProfit = cursor.getDouble(4);
                ContentValues values = new ContentValues();
                double newProfit = 0;

                if(o.getStatus().equals("Chi tiêu")){
                    if(o.getStatus().equals(n.getStatus())) {
                        double newTotalExpense = currentTotalExpense+n.getAmount()-o.getAmount();
                        newProfit = currentTotalIncome-newTotalExpense;
                        values.put("total_expense", newTotalExpense);
                        values.put("profit", newProfit);
                    } else {
                        // Thu nhập
                        double newTotalExpense = currentTotalExpense-o.getAmount();
                        double newTotalIncome = currentTotalIncome+n.getAmount();
                        newProfit = newTotalIncome-newTotalExpense;
                        values.put("total_expense", newTotalExpense);
                        values.put("total_income", newTotalIncome);
                        values.put("profit", newProfit);
                    }
                }
                else{
                    // Thu nhập
                    if(o.getStatus().equals(n.getStatus())) {
                        double newTotalIncome = currentTotalIncome+n.getAmount()-o.getAmount();
                        newProfit = newTotalIncome-currentTotalExpense;
                        values.put("total_income", newTotalIncome);
                        values.put("profit", newProfit);
                    }
                    else {
                        // Chi tiêu
                        double newTotalExpense = currentTotalExpense+n.getAmount();
                        double newTotalIncome = currentTotalIncome-o.getAmount();
                        newProfit = newTotalIncome-newTotalExpense;
                        values.put("total_expense", newTotalExpense);
                        values.put("total_income", newTotalIncome);
                        values.put("profit", newProfit);
                    }
                }
                values.put("profit", newProfit);
                db.update("statistics", values, "time_period = ?", new String[] { odate });
            }
        }
        else {
            // khác date: sửa income, expense ở cả 2 date cũ mới
            Cursor cursorOld = db.rawQuery("SELECT * FROM statistics WHERE time_period = ?", new String[] { odate });
            Cursor cursorNew = db.rawQuery("SELECT * FROM statistics WHERE time_period = ?", new String[] { ndate });
            if (cursorOld.moveToFirst()) {
                double currentTotalIncome = cursorOld.getDouble(2);
                double currentTotalExpense = cursorOld.getDouble(3);
                double currentProfit = cursorOld.getDouble(4);
                ContentValues values = new ContentValues();
                double newProfit = 0;
                if(o.getStatus().equals("Chi tiêu")) {
                    double newTotalExpense = currentTotalExpense-o.getAmount();
                    newProfit = currentTotalIncome-newTotalExpense;
                    values.put("total_expense", newTotalExpense);
                    values.put("profit", newProfit);

                }else{
                    double newTotalIncome = currentTotalIncome-o.getAmount();
                    newProfit = newTotalIncome-currentTotalExpense;
                    values.put("total_income", newTotalIncome);
                    values.put("profit", newProfit);
                }
                db.update("statistics", values, "time_period = ?", new String[] { odate });
            }
            if (cursorNew.moveToFirst()) {
                double currentTotalIncome = cursorNew.getDouble(2);
                double currentTotalExpense = cursorNew.getDouble(3);
                double currentProfit = cursorNew.getDouble(4);
                ContentValues values = new ContentValues();
                double newProfit = 0;
                if(o.getStatus().equals("Chi tiêu")) {
                    double newTotalExpense = currentTotalExpense+o.getAmount();
                    newProfit = currentTotalIncome-newTotalExpense;
                    values.put("total_expense", newTotalExpense);
                    values.put("profit", newProfit);

                }else{
                    double newTotalIncome = currentTotalIncome+o.getAmount();
                    newProfit = newTotalIncome-currentTotalExpense;
                    values.put("total_income", newTotalIncome);
                    values.put("profit", newProfit);
                }
                db.update("statistics", values, "time_period = ?", new String[] { ndate });
            }
        }
    }
    public Statistic getStatisticByDate(String date) {
        date = date.substring(0,7);
        SQLiteDatabase db = this.getReadableDatabase();
        Statistic statistic = null;
        Cursor cursor = db.rawQuery("SELECT * FROM statistics WHERE time_period = ?", new String[]{date});
        if (cursor != null && cursor.moveToFirst()) {
            double totalIncome = cursor.getDouble(2);
            double totalExpense = cursor.getDouble(3);
            double profit = cursor.getDouble(4);
            statistic = new Statistic(date, totalIncome, totalExpense);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return statistic;
    }
    public List<Transaction> getAllTransactionByMonth(String month) {
        List<Transaction> list = new ArrayList<>();

        return list;
    }

    public List<Type> getAllType(){
        List<Type> list = new ArrayList<>();
        SQLiteDatabase st = getReadableDatabase();
        Cursor rs = st.query("types",null,null,null,null,null,null);
        while(rs!=null && rs.moveToNext()){
            list.add(new Type(rs.getString(0),rs.getString(1),rs.getString(2)));
        }
        return list;
    }
    public List<Transaction> getAllTransaction(){
        List<Transaction> list = new ArrayList<>();
        String sql = "select t.id,t.amount,t.type,t.date_update, t.note,t.status from transactions t";
        SQLiteDatabase st = getReadableDatabase();
        Cursor rs = st.rawQuery(sql,null);
        while(rs!=null &&rs.moveToNext()){
            list.add(new Transaction(
                    rs.getString(0),
                    rs.getDouble(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5)));
        }
        return list;
    }
}
