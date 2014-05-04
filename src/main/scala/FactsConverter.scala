import java.io.{FileWriter, BufferedWriter}
import java.nio.charset.Charset
import scala.collection.mutable

import Helpers._
import scala.util.matching.Regex

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


  class Fact(val firstText: String, val secondText: String,
             val userOffset: Int, val userLength: Int,
             val systemOffset: Int, val systemLength: Int,
             val entityOffset: Int, val cathegory: String)

  object Fact {
    def apply(firstText: String, secondText: String,
              userOffset: Int, userLength: Int,
              systemOffset: Int, systemLength: Int,
              entityOffset: Int, cathegory: String) = new Fact(firstText, secondText, userOffset, userLength, systemOffset, systemLength, entityOffset, cathegory)
  }

  class Task(val id: String, facts: List[Fact]) {
    var content: String = ""

    def getEntities: String = {
      val spaceEdit = new Regex("[\\s|\n|\t]+")
      val words = spaceEdit.replaceAllIn(content.removePunctuation(), " ").markEntities(facts)
      "#Document " + id + "\n\n" + words.mkString("\n") + "\n\n"

    }
  }


  var tasks = mutable.Set[Task]()
  var documents = mutable.Map[String, String]()

  def load(args: Array[String]) = {
    val taskFile = scala.xml.XML.loadFile(args(0))
    println("Task file: " + args(0))

    // Reading task file

    var count = 0

    taskFile \ "task" foreach {
      task => {
        val idPart = (task \ "@id").text.replace("qaf2", "vybory").replace("qaf1", "080404").replace("qaf3", "shevard")
        val facts = ((task \ "fact").filter(fact => (fact \ "@relevance").text == "vital") map {
          fact => {
            val firstText = (fact \ "@firstText").text
            val secondText = (fact \ "@secondText").text
            val userOffset = (fact \ "@userOffset").text.toInt
            val userLength = (fact \ "@userLength").text.toInt

            val systemOffset = (fact \ "@systemOffset").text.toInt
            val systemLength = (fact \ "@systemLength").text.toInt

            val entityOffset = (fact \ "@entityOffset").text.toInt
            val cathegory = (fact \ "@cathegory").text
            Fact(firstText, secondText, userOffset, userLength, systemOffset, systemLength, entityOffset, cathegory)
          }
        }).toList
        tasks += new Task(idPart, facts)

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
          val idPart = (document \ "docID").text
          val content = base64((document \ "content").text)
          documents += idPart -> content
        }
          count += 1
      }

      println(count + " documents been read.")
    }

    count = 0
    for (task <- tasks) {
      try {
        task.content = documents(task.id)
      } catch {
        case e: Exception =>
          task.content = "Content not found."
          count += 1
      }
    }
    tasks = tasks.filter(_.content != "Content not found.")

    println("Total not found: " + count)
  }

  // Main part

  load(args.slice(1, args.length))
  val writer = new BufferedWriter(new FileWriter(args(0)))
  println("Writing to file: " + args(0))
  for (task <- tasks) {
    writer.write(task.getEntities)
  }
  writer.flush()
  writer.close()
  println("Done.")


  //B-ORG — begin of organization name
  //I-ORG — in organization name
  //B-PER — begin person
  //I-PER — in person
}