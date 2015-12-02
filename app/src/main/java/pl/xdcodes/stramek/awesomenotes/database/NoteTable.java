package pl.xdcodes.stramek.awesomenotes.database;

import android.database.sqlite.SQLiteDatabase;

public class NoteTable {

    public static final String COLUMN_ID = "_id";
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_IMPORTANT = "important";

    private static final String DATABASE_CREATE = "create table "+ TABLE_NOTES + "(" + COLUMN_ID
            + " DOUBLE, " + COLUMN_NOTE + " text not null, " + COLUMN_IMPORTANT + " INTEGER);";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

}
