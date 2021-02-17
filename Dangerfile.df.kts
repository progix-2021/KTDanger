// Dangerfile.df.kts
/*
 * Use external dependencies using the following annotations:
 */
@file:Repository("https://repo.maven.apache.org")
@file:DependsOn("org.apache.commons:commons-text:1.6")

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.apache.commons.text.WordUtils
import systems.danger.kotlin.*
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

// register plugin MyDangerPlugin

danger(args) {

    val allSourceFiles = git.modifiedFiles + git.createdFiles
    val changelogChanged = allSourceFiles.contains("CHANGELOG.md")
    val sourceChanges = allSourceFiles.firstOrNull { it.contains("src") }

    onGitHub {
        val xmlFile: File = File("detekt-hint-report.xml")
        allSourceFiles.forEach { println(it.toString()) }
        val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
        xmlDoc.documentElement.normalize()
        val fileList: NodeList = xmlDoc.getElementsByTagName("file")
        for (i in 0 until fileList.length) {
            var fileNode = fileList.item(i) as Element
            
            val fileName = fileNode.getAttribute("name")
            println("Filename: $fileName")

            for (k in 0 until fileNode.getElementsByTagName("error").length) {
                val error = fileNode.getElementsByTagName("error").item(k) as Element
                println("Error")

                val line = error.getAttribute("line")
                println("Line: $line")
                val message = error.getAttribute("message")
                println("Message: $message")

                if (allSourceFiles.any { fileName.trim().contains(it.trim()) }) {
                    // Find the sourcefile without the /github/workflow prefix to its path.
                    val file = allSourceFiles.find { fileName.trim().contains(it.trim()) } ?: fileName

                    // Only notify about the warning if the file has been modified in this PR
                    println("Adds warning for $fileName")
                    warn(message, file, line.toInt())
                }
            }
        }
        val isTrivial = pullRequest.title.contains("#trivial")

        // Changelog
        if (!isTrivial && !changelogChanged && sourceChanges != null) {
            warn(WordUtils.capitalize("any changes to library code should be reflected in the Changelog.\n\nPlease consider adding a note there and adhere to the [Changelog Guidelines](https://github.com/Moya/contributors/blob/master/Changelog%20Guidelines.md)."))
        }

        // Big PR Check
        if ((pullRequest.additions ?: 0) - (pullRequest.deletions ?: 0) > 300) {
            warn("Big PR, try to keep changes smaller if you can")
        }

        // Work in progress check
        if (pullRequest.title.contains("WIP", false)) {
            warn("PR is classed as Work in Progress")
        }
    }

    onGit {
        //No Java files check
        createdFiles.filter {
            it.endsWith(".java")
        }.forEach {
            // Using apache commons-text dependency to be sure the dependency resolution always works
            warn(WordUtils.capitalize("please consider to create new files in Kotlin"), it, 1)
        }
    }
}
