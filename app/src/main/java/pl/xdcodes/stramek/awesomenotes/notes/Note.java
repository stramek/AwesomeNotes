package pl.xdcodes.stramek.awesomenotes.notes;

import pl.xdcodes.stramek.awesomenotes.parse.NoteParse;

public class Note {

    private long id;
    private String noteText;
    private boolean important;

    public Note() { }

    public Note(long id, String noteText, boolean important) {
        this.id = id;
        this.noteText = noteText;
        this.important = important;
    }

    public Note(NoteParse n) {
        id = n.getId();
        noteText = n.getNoteText();
        important = n.getImportant();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public boolean getImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }
}
