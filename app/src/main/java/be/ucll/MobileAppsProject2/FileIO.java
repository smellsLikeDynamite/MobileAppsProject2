package be.ucll.MobileAppsProject2;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FileIO {

    private final String FILENAME = "project2.xml";
    private Context context = null;

    public FileIO (Context context) {
        this.context = context;
    }

    public AgendaList readFile() {
        try {
            // get the XML reader
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlreader = parser.getXMLReader();

            // set content handler
            AgendaListHandler theRssHandler = new AgendaListHandler();
            xmlreader.setContentHandler(theRssHandler);

            // read the file from internal storage
            FileInputStream in = context.openFileInput(FILENAME);

            // parse the data
            InputSource is = new InputSource(in);
            xmlreader.parse(is);

            // set the feed in the activity
            AgendaList feed = theRssHandler.getFeed();
            return feed;
        }
        catch (Exception e) {
            Log.e("project2", e.toString());
            return null;
        }
    }
}
