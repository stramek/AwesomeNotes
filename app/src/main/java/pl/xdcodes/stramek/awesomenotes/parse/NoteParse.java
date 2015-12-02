package pl.xdcodes.stramek.awesomenotes.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import pl.xdcodes.stramek.awesomenotes.notes.Note;

@ParseClassName("NoteParse")
public class NoteParse extends ParseObject {

    public NoteParse() {
        super();
    }

    public NoteParse(long id, String noteText, boolean important) {
        super();
        setId(id);
        setNoteText(noteText);
        setImportant(important);
    }

    public NoteParse(Note n) {
        super();
        setId(n.getId());
        setNoteText(n.getNoteText());
        setImportant(n.getImportant());
    }

    public long getId() {
        return getLong("id");
    }

    public void setId(long id) {
        put("id", id);
    }

    public String getNoteText() {
        return getString("noteText");
    }

    public void setNoteText(String noteText) {
        put("noteText", noteText);
    }

    public boolean getImportant() {
        return getBoolean("important");
    }

    public void setImportant(boolean important) {
        put("important", important);
    }
}
