package grails.proxy;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class SystemPropertiesAuthenticator extends Authenticator {

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        if(getRequestorType() == RequestorType.PROXY) {
            return new PasswordAuthentication(
                    System.getProperty("http.proxyUser", ""),
                    System.getProperty("http.proxyPassword", "").toCharArray());
        }
        return null;
    }
}