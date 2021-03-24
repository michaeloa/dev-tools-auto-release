package no.elhub.tools.autorelease

import picocli.CommandLine
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

@CommandLine.Command(
    name = "auto-release",
    mixinStandardHelpOptions = true,
    description = ["auto-release ."],
    optionListHeading = "@|bold %nOptions|@:%n",
    sortOptions = false
)
class AutoRelease : Callable<Int> {

    @CommandLine.Parameters(
        index = "0",
        description = ["The file path to process (defaults to current location)"]
    )
    var path = "."

    @CommandLine.Option(
        names = ["-p", "--project"],
        description = ["The type of project. Valid options are: gradle, maven (defaults to maven)"])
    var project = "maven"

    @CommandLine.Option(
        names = ["-i", "--include"],
        description = ["Property file to include in the build"])
    var include = ""

    override fun call(): Int {
        val type: ProjectType
        try {
            type = ProjectType.valueOf(project.toUpperCase())
        } catch (e: IllegalArgumentException ) {
            println("Invalid argument: the project of type $project is not supported by this application")
            return 1
        }
        println("Processing a project of type $type...")
        val repository = VersionedRepository(Paths.get(path).toFile())
        println("Current version: ${repository.currentVersion}")
        println("Unprocessed messages: ${repository.untaggedMessages.size}")
        /*
        repository.untaggedMessages.forEach {
            println("\t$it")
        }
         */
        val currentVersion = repository.currentVersion
        val increaseVersion = VersionBump.analyze(repository.untaggedMessages)
        println("Setting version...")
        val nextVersion = currentVersion.increase(increaseVersion)
        val nextVersionString = if (nextVersion != currentVersion) {
            nextVersion
        } else { // Minor bump and add snapshot
            "${currentVersion.increase(VersionBump.MINOR)}-SNAPSHOT"
        }
        println("Next version: $nextVersionString")
        val publishCommand: String
        when (type) {
            ProjectType.GRADLE -> {
                VersionFile.setVersion(
                    Paths.get("gradle.properties"),
                    StandardPattern.gradleVersion,
                    String.format(StandardPattern.gradleFormat, nextVersionString)
                )
                publishCommand = StandardPattern.gradlePublish
                if (include.isNotEmpty())
                    publishCommand += "-DbuildInfoConfig.propertiesFile=$include"
            }
            ProjectType.MAVEN -> {
                VersionFile.setVersion(
                    Paths.get("pom.xml"),
                    StandardPattern.mavenVersion,
                    String.format(StandardPattern.mavenFormat, nextVersionString)
                )
                publishCommand = StandardPattern.mavenPublish
            }
            else -> {
                println("Currently auto-release does not handle projects of this type.")
                exitProcess(1)
            }
        }
        if (nextVersion != currentVersion) {
            repository.setTag("v$nextVersionString")
            println("Publish release...")
            publishCommand.runCommand(File("."))

        }
        return 0
    }
}

fun main(args: Array<String>): Unit = exitProcess(CommandLine(AutoRelease()).execute(*args))

fun String.runCommand(workingDir: File) {
    ProcessBuilder(*split(" ").toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor(60, TimeUnit.MINUTES)
}
