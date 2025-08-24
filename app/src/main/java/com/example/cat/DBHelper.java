package com.example.cat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "student.db";
    public static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Student table with UNIQUE regno
        db.execSQL("CREATE TABLE student (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "regno TEXT UNIQUE)");

        // Fees table referencing student by ID
        db.execSQL("CREATE TABLE fees (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER, " +
                "total_fees REAL, " +
                "fees_paid REAL, " +
                "fees_balance REAL, " +
                "completion_date TEXT, " +
                "FOREIGN KEY(student_id) REFERENCES student(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS fees");
        db.execSQL("DROP TABLE IF EXISTS student");
        onCreate(db);
    }

    // Insert student, preventing duplicates using UNIQUE regno
    public boolean insertStudent(String name, String regno) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("regno", regno);
        try {
            long result = db.insertOrThrow("student", null, cv);
            return result != -1;
        } catch (SQLiteConstraintException e) {
            return false; // duplicate regno
        }
    }

    // Insert fees record linked to a student
    public long insertFees(int studentId, double total, double paid, double balance, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("student_id", studentId);
        cv.put("total_fees", total);
        cv.put("fees_paid", paid);
        cv.put("fees_balance", balance);
        cv.put("completion_date", date);
        return db.insert("fees", null, cv);
    }

    //  Fetch joined student and their fee details
    public Cursor getStudentWithFees() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT s.id, s.name, s.regno, " +
                "f.total_fees, f.fees_paid, f.fees_balance, f.completion_date " +
                "FROM student s JOIN fees f ON s.id = f.student_id", null);
    }

    // 🔍 Get student ID using their regno
    public int getStudentIdByRegNo(String regno) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM student WHERE regno = ?", new String[]{regno});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        } else {
            cursor.close();
            return -1; // not found
        }
    }


    public boolean regNoExists(String regno) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM student WHERE regno = ? LIMIT 1", new String[]{regno});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}
