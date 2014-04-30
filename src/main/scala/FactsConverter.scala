import java.io.{FileWriter, BufferedWriter}
import java.nio.charset.Charset
import scala.collection.mutable
import scala.xml.MetaData

/**
 * User: Kribesk
 * Date: 29.04.14.
 */


// Run "./tools.sh news" first!

object FactsConverter extends App {

  // Base64 decoder

  def base64(input: String): String = {
    // Fix: cause it always starts with space & makes much trouble
    val t = input.stripPrefix(" ")
    val bytes = new sun.misc.BASE64Decoder().decodeBuffer(t)
    new String(bytes, Charset.forName("cp1251"))
  }

  // Task structure

  class Task(id: Int, markup: Set[MetaData]) {
    def getId: Int = id

    def getMarkup: Set[MetaData] = markup

    var content: String = ""

    def getEntities: String = {
      val words = content.split("[ \n\t]+")
      var output = "#Document " + id + "\n\n"
      for (word <- words) {
        output += word + " " + 0 + "\n"
      }
      output + "\n"
    }
  }

  var tasks = mutable.Set[Task]()
  var documents = mutable.Map[Int, String]()

  def load(args: Array[String]) = {
    val taskFile = scala.xml.XML.loadFile(args(0))
    println("Task file: " + args(0))

    // Reading task file

    var count = 0

    taskFile \ "task" foreach {
      task => {
        val idPart = (task \ "@id").text.split("-")
        var facts = Set[MetaData]()
        task \ "fact" foreach {
          fact => facts += fact.attributes
        }
        tasks += new Task(idPart.last.toInt, facts)
      }
        count += 1
    }

    println(count + " tasks been read.")

    // Now reading content

    for (i <- 1 to (args.length - 1)) {
      val contentFile = scala.xml.XML.loadFile(args(i))
      println("Content file: " + args(i))
      count = 0

      contentFile \ "document" foreach {
        document => {
          val idPart = (document \ "docID").text.split("-")
          val content = base64((document \ "content").text)
          documents += idPart.last.toInt -> content
        }
          count += 1
      }

      println(count + " documents been read.")
    }

    count = 0
    for (task <- tasks) {
      try {
        task.content = documents(task.getId)
      } catch {
        case e: Exception =>
          task.content = "Content not found."
          count += 1
      }
    }

    println("Total not found: " + count)
  }

  // Main part

  load(args.slice(1, args.length))
  val writer = new BufferedWriter(new FileWriter(args(0)))
  println("Writing to file: " + args(0))
  for (task <- tasks) {
    writer.write(task.getEntities)
  }
  writer.close()
  println("Done.")

  // TODO: Understand markup and offset format
}