package grails.init;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Start {

    private static String baseUrl = "http://repo.grails.org/grails/core/org/grails/grails-datastore-web";

    public static void main(String[] args) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            FindReleaseHandler findReleaseHandler = new FindReleaseHandler();
            saxParser.parse(new URL(baseUrl + "/maven-metadata.xml").openStream(), findReleaseHandler);

            final String jarFileName = "grails-datastore-web-" + findReleaseHandler.version + ".jar";
            File wrapperDir = new File(System.getProperty("user.home") + "/.grails/wrapper");
            wrapperDir.mkdirs();

            File existingWrapper = new File(wrapperDir, jarFileName);
            File noVersionJar = new File(wrapperDir, "grails-datastore-web.jar");

            if (!existingWrapper.exists()) {
                URL website = new URL(baseUrl + "/" + findReleaseHandler.version + "/" + jarFileName);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream(existingWrapper);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                if (noVersionJar.exists()) {
                    if (!noVersionJar.delete()) {
                        throw new RuntimeException("Could not delete old grails wrapper jar: " + noVersionJar.getAbsolutePath());
                    }
                }

                Files.copy(existingWrapper.getAbsoluteFile().toPath(), noVersionJar.getAbsoluteFile().toPath(), REPLACE_EXISTING);
            }

            URLClassLoader child = new URLClassLoader(new URL[] {noVersionJar.toURI().toURL()});
            Class classToLoad = Class.forName("grails.init.RunCommand", true, child);
            Method main = classToLoad.getMethod("main", String[].class);
            main.invoke(null, (Object)args);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
