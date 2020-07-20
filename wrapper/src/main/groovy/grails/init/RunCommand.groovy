package grails.init

import org.springframework.boot.cli.compiler.grape.AetherGrapeEngine
import org.springframework.boot.cli.compiler.grape.AetherGrapeEngineFactory
import org.springframework.boot.cli.compiler.grape.DependencyResolutionContext
import org.springframework.boot.cli.compiler.grape.RepositoryConfiguration

/**
 * Created by jameskleeh on 10/31/16.
 */
class RunCommand {

    static void main(String[] args) {

        Properties props = new Properties()
        String grailsVersion
        String groovyVersion
        try {
            props.load(new FileInputStream("gradle.properties"))
            grailsVersion = props.getProperty("grailsVersion")
            groovyVersion = props.getProperty("groovyVersion")
        } catch (IOException e) {
            throw new RuntimeException("Could not determine grails version due to missing properties file")
        }

        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(RunCommand.classLoader)

        List<RepositoryConfiguration> repositoryConfigurations = [new RepositoryConfiguration("grailsCentral", new URI("https://repo.grails.org/grails/core"), true)]
        if (groovyVersion.endsWith("SNAPSHOT")) {
            repositoryConfigurations.add(new RepositoryConfiguration("JFrog OSS snapshot repo", new URI("https://oss.jfrog.org/oss-snapshot-local"), true))
        }

        AetherGrapeEngine grapeEngine = AetherGrapeEngineFactory.create(groovyClassLoader, repositoryConfigurations, new DependencyResolutionContext(), false)
        grapeEngine.grab([:], [group: "org.grails", module: "grails-shell", version: grailsVersion])

        ClassLoader previousClassLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().setContextClassLoader(groovyClassLoader)

        try {
            groovyClassLoader.loadClass('org.grails.cli.GrailsCli').main(args)
        } finally {
            Thread.currentThread().setContextClassLoader(previousClassLoader)
        }
    }
}
