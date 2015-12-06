package pl.xdcodes.stramek.awesomenotes.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NoteTable {

    private static final String TAG = "NoteTable";

    public static final String COLUMN_ID = "_id";
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_IMPORTANT = "important";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NOTES
            + "("
            + COLUMN_ID + " LONG, "
            + COLUMN_NOTE + " text not null, "
            + COLUMN_IMPORTANT + " INTEGER"
            + ");";

    private static final String DROP_LESSONS = "DROP TABLE IF EXISTS " + TABLE_NOTES;

    public static void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Database creating...");
        db.execSQL(DATABASE_CREATE);
        Log.d(TAG, "Table " + TABLE_NOTES + " created.");
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Database updating...");
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        if(newVersion > oldVersion) {
            switch(oldVersion) {
                case 1:
                    break;
                default:
                    Log.d(TAG, "Unknown version " + oldVersion + ". Creating new database.");
                    db.execSQL(DROP_LESSONS);
                    onCreate(db);
                    Log.d(TAG, "All data lost.");
                    break;
            }
        }
    }

}
