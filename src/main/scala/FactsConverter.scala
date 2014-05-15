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
    var contentSentence = content.split("\n").toList

    def getSentencesWithEntities: String = {
      val sentences = facts.map(convertFactToSentence).map(sentence => {
        sentence.mkString("\n")
      }).filter(entityRegex.findFirstIn(_) != None)
      if (sentences.length > 0)
        "#Document " + id + "\n\n" + sentences.mkString("\n") + "\n\n"
      else ""
    }

    val punctuation = Set('.', ',', '?', '!', '…', '(', ')', ';', '-', '–', ":")
    val sentenceEnd = Set('.', '?', '!', '…')
    val punctuationRegexp = new Regex("(\\.|\\?|,|\\!|\\(|\\)|…|;|-|—)")
    val entityRegex = new Regex("(B-ORG|I-ORG|B-PER|I-PER)")
    val quoteRegexp = new Regex("(\"|«|»)")
    val spaceEdit = new Regex("[\\s|\n|\t]+")

    def getSentence(fact: Fact) = {
      def getSentenceByOffset(sentences: List[String], offset: Int): String = {
        sentences match {
          case sentence :: tail => {
            if (sentence.length > offset) sentence
            else {
              getSentenceByOffset(tail, offset - (sentence.length + 1))
            }
          }
          case List() => ""
        }
      }
      getSentenceByOffset(contentSentence, fact.systemOffset + fact.entityOffset)
    }

    abstract class State()

    case class InOrg() extends State()

    case class InPer() extends State()

    case class NonEntity() extends State()

    def convertFactToSentence(fact: Fact): List[String] = {
      val sentence = quoteRegexp.replaceAllIn(getSentence(fact), "\"")

      val words = spaceEdit.replaceAllIn(punctuationRegexp.replaceAllIn(sentence, " "), " ").split(" ").toList

      //      for (word <- words)
      //        if (word == "")
      //          print("aaa")
      val firstEntity = spaceEdit.replaceAllIn(quoteRegexp.replaceAllIn(punctuationRegexp.replaceAllIn(fact.firstText, " "), " "), " ").toLowerCase().split(" ").toSet
      val secondEntity = spaceEdit.replaceAllIn(quoteRegexp.replaceAllIn(punctuationRegexp.replaceAllIn(fact.firstText, " "), " "), " ").toLowerCase().split(" ").toSet
      val minSize = if (firstEntity.size < secondEntity.size) firstEntity.size else secondEntity.size

      def findEntity(word_original: String, entityWords: Set[String]) = {
        val word = spaceEdit.replaceAllIn(quoteRegexp.replaceAllIn(punctuationRegexp.replaceAllIn(word_original, ""), ""), "")
        val half = (word.length * 0.5).toInt
        val toCompare = word.substring(0, half).toLowerCase()
        var isEntity = false
        var entityWord = ""
        for (entity <- entityWords) {
          if (!isEntity) {
            if (entity.length > 3)
              if ((entity.length > half && toCompare == entity.substring(0, half))) {
                isEntity = true
                entityWord = entity
              }
              else {
                if (word.toLowerCase() == entity) {
                  isEntity = true
                  entityWord = entity
                }
              }

          }

        }
        (entityWord, isEntity)
      }
      val titleCase = "ЙФЯЦЫЧУВСКАМИПЕНРТЬОГШЛБЮДЩЗЖЭХЪЁQAZWSXEDCRFVTGBYHNUJMIKOLP".toCharArray.toSet
      def isPerson(word: String) = {
        if ('A' < word.charAt(0) && word.charAt(0) < 'Z')
          false
        else if (titleCase contains word.charAt(0)) {
          !word.substring(1).foldLeft(false)({
            case (status, c) => if (status) status else (titleCase contains c)
          })
        } else false
      }

      def extract(words: List[String], result: List[String], state: State, entityWords: Set[String], beginCount: Int): List[String] = {
        words match {
          case word :: tail => {
            if (word.replace("\"", "").length == 0)
              extract(tail, result, state, entityWords, beginCount)
            else
              state match {
                case InOrg() => {
                  val (entityWord, isEntity) = findEntity(word, entityWords)
                  if (word.charAt(word.length - 1) == '\"') {
                    val curWord = word.replace("\"", "") + "\tI-ORG"
                    extract(tail, curWord :: result, NonEntity(), entityWords - entityWord, 1)
                  } else {
                    if (isEntity) {
                      if (entityWords.size <= minSize && beginCount == 0) {
                        extract(words, result, NonEntity(), entityWords, beginCount)
                      } else {
                        val curWord = word.replace("\"", "") + "\tI-ORG"
                        extract(tail, curWord :: result, InOrg(), entityWords - entityWord, 1)
                      }
                    } else {
                      val curWord = word.replace("\"", "") + "\tO"
                      extract(tail, curWord :: result, NonEntity(), entityWords, beginCount)
                    }

                  }
                }
                case InPer() => {
                  val (entityWord, isEntity) = findEntity(word, entityWords)

                  if (isEntity) {
                    if (entityWords.size <= minSize) {
                      extract(words, result, NonEntity(), entityWords, beginCount)
                    }

                    if (word.charAt(0) == '\"') {
                      val curWord = word.replace("\"", "") + "\tB-ORG"
                      extract(tail, curWord :: result, InOrg(), entityWords - entityWord, 1)
                    } else {
                      if (isPerson(word)) {
                        val curWord = word.replace("\"", "") + "\tI-PER"
                        extract(tail, curWord :: result, InPer(), entityWords - entityWord, 1)
                      } else {
                        val curWord = word.replace("\"", "") + "\tO"
                        extract(tail, curWord :: result, NonEntity(), entityWords - entityWord, 1)
                      }

                    }
                  } else {
                    val curWord = word.replace("\"", "") + "\tO"
                    extract(tail, curWord :: result, NonEntity(), entityWords, 1)
                  }
                }
                case NonEntity() => {
                  val (entityWord, isEntity) = findEntity(word, entityWords)

                  if (word.charAt(0) == '\"' && isEntity) {
                    val curWord = word.replace("\"", "") + "\tB-ORG"
                    extract(tail, curWord :: result, InOrg(), entityWords - entityWord, 1)
                  } else if ((titleCase contains word.charAt(0)) && isEntity && word.length > 3) {

                    if (isPerson(word)) {
                      val curWord = word.replace("\"", "") + "\tB-PER"
                      extract(tail, curWord :: result, InPer(), entityWords - entityWord, 1)
                    } else {
                      val curWord = word.replace("\"", "") + "\tB-ORG"
                      extract(tail, curWord :: result, InOrg(), entityWords - entityWord, 1)
                    }
                  } else {
                    val curWord = word.replace("\"", "") + "\tO"
                    extract(tail, curWord :: result, NonEntity(), entityWords, beginCount)
                  }
                }
              }
          }
          case List() => result.reverse
        }
      }
      extract(words, List(), NonEntity(), firstEntity ++ secondEntity, 0)
    }

    def getEntities: String = {
      val words = spaceEdit.replaceAllIn(content.removePunctuation(), "\t").markEntities(facts)
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
        task.contentSentence = task.content.split("\n").toList
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
    writer.write(task.getSentencesWithEntities)
  }
  writer.flush()
  writer.close()
  println("Done.")


  //B-ORG — begin of organization name
  //I-ORG — in organization name
  //B-PER — begin person
  //I-PER — in person
}