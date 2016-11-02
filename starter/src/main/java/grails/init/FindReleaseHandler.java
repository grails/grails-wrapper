package grails.init;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by jameskleeh on 11/2/16.
 */
public class FindReleaseHandler extends DefaultHandler {

    public String version;

    private boolean foundVersion;

    @Override
    public void startElement(String uri, String localName,String qName,
                            Attributes attributes) {
        if (qName.equalsIgnoreCase("RELEASE")) {
            foundVersion = true;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (foundVersion) {
            version = new String(ch, start, length);
        }
        foundVersion = false;
    }
}
