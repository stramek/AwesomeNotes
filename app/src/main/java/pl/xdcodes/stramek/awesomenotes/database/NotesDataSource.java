package pl.xdcodes.stramek.awesomenotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pl.xdcodes.stramek.awesomenotes.notes.Note;

public class NotesDataSource {

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = { NoteTable.COLUMN_ID, NoteTable.COLUMN_TITLE, NoteTable.COLUMN_NOTE };

    public NotesDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Note createNote(String title, String description) {
        ContentValues values = new ContentValues();
        values.put(NoteTable.COLUMN_TITLE, title);
        values.put(NoteTable.COLUMN_NOTE, description);
        long insertId = database.insert(NoteTable.TABLE_NOTES, null, values);
        Cursor cursor = database.query(NoteTable.TABLE_NOTES, allColumns, NoteTable.COLUMN_ID + " = " + insertId,
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
        Note note = new Note();
        note.setId(cursor.getLong(0));
        note.setTitle(cursor.getString(1));
        note.setNoteText(cursor.getString(2));
        return note;
    }
}
