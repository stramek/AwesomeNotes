package pl.xdcodes.stramek.awesomenotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import pl.xdcodes.stramek.awesomenotes.notes.Note;
import pl.xdcodes.stramek.awesomenotes.parse.NoteParse;

public class NotesDataSource {

    private static final String TAG = "NotesDataSource";
    
    private SQLiteDatabase database;
    private NoteDatabaseHelper dbHelper;
    private String[] allColumns = { NoteTable.COLUMN_ID, NoteTable.COLUMN_NOTE, NoteTable.COLUMN_IMPORTANT };

    private static final AtomicLong TIME_STAMP = new AtomicLong();

    public NotesDataSource(Context context) {
        dbHelper = new NoteDatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Note createNote(String description, boolean important) {
        ContentValues values = new ContentValues();
        values.put(NoteTable.COLUMN_NOTE, description);
        values.put(NoteTable.COLUMN_IMPORTANT, important);
        long uniqueId = getUniqueMillis();
        values.put(NoteTable.COLUMN_ID, uniqueId);
        database.insert(NoteTable.TABLE_NOTES, null, values);

        Cursor cursor = database.query(NoteTable.TABLE_NOTES, allColumns, NoteTable.COLUMN_ID + " = " + uniqueId,
                null, null, null, null);
        cursor.moveToFirst();
        Note note = cursorToNote(cursor);
        cursor.close();
        return note;
    }

    public Note createNote(NoteParse n) {
        ContentValues values = new ContentValues();
        double id = n.getId();
        values.put(NoteTable.COLUMN_NOTE, n.getNoteText());
        values.put(NoteTable.COLUMN_ID, id);
        values.put(NoteTable.COLUMN_IMPORTANT, n.getImportant());
        database.insert(NoteTable.TABLE_NOTES, null, values);

        Cursor cursor = database.query(NoteTable.TABLE_NOTES, allColumns, NoteTable.COLUMN_ID + " = " + id,
                null, null, null, null);
        cursor.moveToFirst();
        Note note = cursorToNote(cursor);
        cursor.close();
        return note;
    }

    public void deleteNote(Note note) {
        long id = note.getId();
        database.delete(NoteTable.TABLE_NOTES, NoteTable.COLUMN_ID + " = " + id, null);
    }

    public void deleteAllNotes() {
        database.delete(NoteTable.TABLE_NOTES, null, null);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        Cursor cursor = database.query(NoteTable.TABLE_NOTES, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = cursorToNote(cursor);
            notes.add(note);
            cursor.moveToNext();
        }

        cursor.close();
        return notes;
    }

    private Note cursorToNote(Cursor cursor) {
        return new Note(cursor.getLong(0), cursor.getString(1), cursor.getInt(2));
    }

    private static long getUniqueMillis() {
        long now = System.currentTimeMillis();
        while (true) {
            long last = TIME_STAMP.get();
            if (now <= last)
                now = last + 1;
            if (TIME_STAMP.compareAndSet(last, now))
                return now;
        }
    }
}
