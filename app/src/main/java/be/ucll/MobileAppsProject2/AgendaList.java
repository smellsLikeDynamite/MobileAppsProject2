package be.ucll.MobileAppsProject2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AgendaList {
    private String title = null;
    private String pubDate = null;
    private ArrayList<AgendaItem> items;

    private SimpleDateFormat dateInFormat =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public AgendaList() {
        items = new ArrayList<AgendaItem>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getPubDate() {
        return pubDate;
    }

    //http://developer.android.com/reference/java/util/Date.html
    public long getPubDateMillis() {
        try {
            Date date = dateInFormat.parse(pubDate.trim());
            return date.getTime();
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public int addItem(AgendaItem item) {
        items.add(item);
        return items.size();
    }

    public AgendaItem getItem(int index) {
        return items.get(index);
    }

    public ArrayList<AgendaItem> getAllItems() {
        return items;
    }
}
