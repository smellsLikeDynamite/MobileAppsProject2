package be.ucll.MobileAppsProject2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AgendaItem {
    private String title = null;
    private String startDate = null;
    private String endDate = null;

    //http://developer.android.com/reference/java/text/SimpleDateFormat.html
    private SimpleDateFormat dateOutFormat =
            new SimpleDateFormat("EEEE h:mm a (MMM d)");

    private SimpleDateFormat dateInFormat =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public void setTitle(String title)     {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartDateFormatted() {
        try {
            Date date = dateInFormat.parse(startDate.trim());
            String startDateFormatted = dateOutFormat.format(date);
            return startDateFormatted;
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public String getEndDateFormatted() {
        try {
            Date date = dateInFormat.parse(endDate.trim());
            String endDateFormatted = dateOutFormat.format(date);
            return endDateFormatted;
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
