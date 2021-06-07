package grails.init;

import grails.proxy.SystemPropertiesAuthenticator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Start {

    private static final String PROJECT_NAME = "grails4_1-wrapper";
    private static final String DEFAULT_BASE_URL = "https://repo.grails.org/artifactory/core/org/grails/" + PROJECT_NAME;
    private static final File WRAPPER_DIR = new File(System.getProperty("user.home") + "/.grails/wrapper");
    private static final File NO_VERSION_JAR = new File(WRAPPER_DIR, PROJECT_NAME + ".jar");

    private static String getVersion() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            FindReleaseHandler findReleaseHandler = new FindReleaseHandler();
            String baseUrl = System.getProperty("grails.wrapper.baseUrl", DEFAULT_BASE_URL);
            saxParser.parse(new URL(baseUrl + "/maven-metadata.xml").openStream(), findReleaseHandler);

            return findReleaseHandler.getVersion();
        } catch (Exception e) {
            if (!NO_VERSION_JAR.exists()) {
                System.out.println("You must be connected to the internet the first time you use the Grails wrapper");
                e.printStackTrace();
                System.exit(1);
            }
            return null;
        }
    }


    private static boolean updateJar(String version) {

        boolean success = false;

        final String jarFileName = PROJECT_NAME + "-" + version;
        final String jarFileExtension = ".jar";

        WRAPPER_DIR.mkdirs();

        try {
            File dowloadedJar = File.createTempFile(jarFileName, jarFileExtension);

            URL website = new URL(DEFAULT_BASE_URL + "/" + version + "/" + jarFileName + jarFileExtension);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(dowloadedJar);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();

            Files.move(dowloadedJar.getAbsoluteFile().toPath(), NO_VERSION_JAR.getAbsoluteFile().toPath(), REPLACE_EXISTING);

            success = true;
        } catch(Exception e) {
            System.out.println("There was an error downloading the wrapper jar");
            e.printStackTrace();
        }

        return success;
    }

    public static void main(String[] args) {
        Authenticator.setDefault(new SystemPropertiesAuthenticator());

        try {

            if (!NO_VERSION_JAR.exists()) {
                updateJar(getVersion());
            }
            URLClassLoader child = new URLClassLoader(new URL[] {NO_VERSION_JAR.toURI().toURL()});
            Class classToLoad = Class.forName("grails.init.RunCommand", true, child);
            Method main = classToLoad.getMethod("main", String[].class);
            main.invoke(null, (Object)args);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
