import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import systems.danger.kotlin.Danger
import systems.danger.kotlin.warn
import systems.danger.kotlin.message
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory


val xmlFile: File = File("detekt-hint-report.xml")
val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
xmlDoc.documentElement.normalize()

val danger = Danger(args)
val allSourceFiles = danger.git.modifiedFiles + danger.git.createdFiles
allSourceFiles.forEach { println(it.toString()) }
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

