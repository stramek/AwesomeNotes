package pl.xdcodes.stramek.awesomenotes;

public class Note {

    private long id;
    private String title;
    private String subtitle;
    private boolean active;

    Note() {
        active = false;
    }

    Note(String title, String subtitle, boolean active, long id) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.active = active;
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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isActive() {
        return active;
    }
}
