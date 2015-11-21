package pl.xdcodes.stramek.awesomenotes;

/**
 * Created by Stramek on 16.11.2015.
 */
public class Note {
    private String title;
    private String subtitle;
    private boolean active;

    Note(String title, String subtitle, boolean active) {
        this.title = title;
        this.subtitle = subtitle;
        this.active = active;
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
