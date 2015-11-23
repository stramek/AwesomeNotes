package pl.xdcodes.stramek.awesomenotes.notes;

public class Note {

    private long id;
    private String title;
    private String noteText;
    private boolean important;

    public Note() {
        important = false;
    }

    public Note(long id, String title, String noteText, boolean important) {
        this.id = id;
        this.title = title;
        this.noteText = noteText;
        this.important = important;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public boolean isImportant() {
        return important;
    }
}
