package grails.init


import org.grails.cli.compiler.grape.DependencyResolutionContext
import org.grails.cli.compiler.grape.MavenResolverGrapeEngine
import org.grails.cli.compiler.grape.MavenResolverGrapeEngineFactory
import org.grails.cli.compiler.grape.RepositoryConfiguration

/**
 * Created by jameskleeh on 10/31/16.
 */
class RunCommand {

    static final String DEFAULT_GRAILS_SHELL_VERSION = '7.0.0-SNAPSHOT'

    static void main(String[] args) {

        Properties props = new Properties()
        String grailsVersion
        String grailsShellVersion
        String groovyVersion
        try {
            props.load(new FileInputStream("gradle.properties"))
            grailsVersion = props.getProperty("grailsVersion")
            grailsShellVersion = props.getProperty("grailsShellVersion")
            groovyVersion = props.getProperty("groovyVersion")
        } catch (IOException e) {
            throw new RuntimeException("Could not determine grails version due to missing properties file")
        }

        if(!grailsShellVersion) {
            grailsShellVersion = DEFAULT_GRAILS_SHELL_VERSION
        }

        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(RunCommand.classLoader)

        List<RepositoryConfiguration> repositoryConfigurations = [new RepositoryConfiguration("grailsCentral", new URI("https://repo.grails.org/grails/core"), true)]
        if (groovyVersion && groovyVersion.endsWith("SNAPSHOT")) {
            repositoryConfigurations.add(new RepositoryConfiguration("JFrog OSS snapshot repo", new URI("https://oss.jfrog.org/oss-snapshot-local"), true))
        }

        MavenResolverGrapeEngine grapeEngine = MavenResolverGrapeEngineFactory.create(groovyClassLoader, repositoryConfigurations, new DependencyResolutionContext(), false)
        try {
            grapeEngine.grab([:], [group: "org.apache.grails", module: "grails-shell-cli", version: grailsVersion])
        }
        catch(dependencyResolutionException){
            // Try grails shell version from gradle.properties or default
            grapeEngine.grab([:], [group: "org.apache.grails", module: "grails-shell-cli", version: grailsShellVersion])
        }

        ClassLoader previousClassLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().setContextClassLoader(groovyClassLoader)

        try {
            groovyClassLoader.loadClass('org.grails.cli.GrailsCli').main(args)
        } finally {
            Thread.currentThread().setContextClassLoader(previousClassLoader)
        }
    }
}
