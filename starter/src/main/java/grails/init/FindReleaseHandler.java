package grails.init;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by jameskleeh on 11/2/16.
 */
public class FindReleaseHandler extends DefaultHandler {

    private String releaseVersion;
    private String latestVersion;

    private boolean foundRelease;
    private boolean foundLatest;

    @Override
    public void startElement(String uri, String localName,String qName,
                            Attributes attributes) {
        if (qName.equalsIgnoreCase("RELEASE")) {
            foundRelease = true;
        }
        if (qName.equalsIgnoreCase("LATEST")) {
            foundLatest = true;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (foundRelease) {
            releaseVersion = new String(ch, start, length);
        }
        if (foundLatest) {
            latestVersion = new String(ch, start, length);
        }
        foundLatest = false;
        foundRelease = false;
    }

    public String getVersion() {
        if (releaseVersion != null) {
            return releaseVersion;
        } else if (latestVersion != null) {
            return latestVersion;
        }
        return null;
    }
}
