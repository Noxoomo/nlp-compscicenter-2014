import FactsConverter.Fact
import scala.io.Source
import scala.util.matching.Regex
import scala.util.Random

object Helpers {

  implicit class ListWithSplit[T](list: List[T]) {
    def split(value: T) = {
      def splitRec(accRes: List[List[T]], cur: List[T], list: List[T]): List[List[T]] = {
        list match {
          case head :: tail => if (head.equals(value)) splitRec(cur.reverse :: accRes, List(), tail) else splitRec(accRes, head :: cur, tail)
          case List() => (cur.reverse :: accRes).reverse
        }
      }
      splitRec(List(), List(), list)
    }
  }

  implicit class Normalizer(text: String) {
    val punctuation = Set('.', ',', '?', '!', '\"', '…', '(', ')', ';', '-', '«', '»', '–', ":")
    val endings = new Regex("(ий|ой|ый|ая|ие|их|ое|ую|ем|им|ии|ей|ом|ых|ым|ия|ёй)\\b")

    //о|а|я|ы|и|е|у|ю
    def normalize() = {
      endings.replaceAllIn(text.toLowerCase.replace("\n", " ").filterNot(punctuation contains), " ")
      //text.toLowerCase().replace("\n"," ").filterNot(punctuation contains)
    }

    val hashtag = new Regex("#(([\\p{L}\\d])+)\\b")
    val user = new Regex("@(([\\w\\d])+)\\b")
    val link = new Regex("(http://|https://)(([\\w\\d/.])+)(\\s|$)")
    val whitespaces = new Regex("( )+");

    def normalizeTweets() = {
      whitespaces.replaceAllIn(hashtag.replaceAllIn(user.replaceAllIn(link.replaceAllIn(text.replace("\n", " "), ""), " "), " ").toLowerCase, " ").filterNot(punctuation contains)
      //      endings.replaceAllIn(text.toLowerCase.replace("\n", " ").filterNot(punctuation contains), " ")
      //text.toLowerCase().replace("\n"," ").filterNot(punctuation contains)
    }

    val punctuationRegexp = new Regex("(\\.|\\?|,|\\!|\\(|\\)|…|;|«|»|\")")

    def removePunctuation() = {
      punctuationRegexp.replaceAllIn(text, "")
    }
  }

  implicit class EntityExtractor(text: String) {
    def markEntities(facts: List[Fact]) = {
      //fact1: A owns B
      //fact2: A works in B
      //we can't convert facts to B-ORG\B-PER, because we don't know, where is a person and where is   an organisation
      // => So let's classify to entitie
      //Offsets in XML — wrong, can't use them

      val firstEntities = facts.map(fact => fact.firstText.removePunctuation().toLowerCase() ->(fact.systemOffset, fact.systemLength)).toMap
      val secondEntities = facts.map(fact => fact.secondText.removePunctuation().toLowerCase() ->(fact.systemOffset, fact.systemLength)).toMap

      val words = text.split(" ")
      var currOffset = 0
      var inFirst = false
      var inSecond = false

      def isEntity(word: String, dict: Map[String, (Int, Int)]) = {
        firstEntities.get(word.toLowerCase()) match {
          case Some((offset, length)) => true //currOffset >= offset && currOffset < offset + length
          case None => false
        }
      }

      words.filterNot(word => word == "-" || word == "").map(word => {
        if (isEntity(word, firstEntities)) {
          //          currOffset += word.length + 1
          val mapped = word + "\t" + (if (inFirst) "I_ENT" else "B_ENT")
          inFirst = true
          inSecond = false
          mapped
          //          word + "\t" + "ENT"
        } else if (isEntity(word, secondEntities)) {
          //          currOffset += word.length + 1
          val mapped = word + "\t" + (if (inSecond) "I_ENT" else "B_ENT")
          inSecond = true
          inFirst = false
          //          word + "\t" + "ENT"
          mapped
        } else {
          //          currOffset += word.length +1
          inFirst = false
          inSecond = false
          word + "\t" + "O"
        }
      })
    }

  }

  val dict = Source.fromFile("/Users/Vasily/Dropbox/homework/NLP/ushakov.txt").getLines().mkString(" ").split(" ").map(_.toLowerCase).toSet
  val abridgments = Set("ООО", "ОАО", "ИП", "Компания", "Общество", "СМИ", "ФСК", "ИД", "ГУ", "БИ", "НИУ", "НФ")


  implicit class FeaturesExtractor(val entity: String) {
    val rand = new Random()
    val whitespace = new Regex("\t")
    val split_entity = whitespace.split(entity)

    val word = split_entity(0)
    val label = split_entity(1)
    if (word.length == 0) {
      print("a")
    }


    def extractFeaturesToConverter() = {
      val upperFeature = word(0).isUpper
      val twoOrMoreUp = word.foldLeft(0)((acc, c) => {
        acc + {
          if (c.isUpper) 1 else 0
        }
      }) > 1
      val containsLatin = word.exists(c => {
        val code = Char.char2int(c.toLower)
        if (Char.char2int('a') <= code && code <= Char.char2int('z'))
          true
        else false
      })
      val isLong = word.length > 4
      val isAbridgment = abridgments contains word.toLowerCase
      val isEntity = if (label != "O") 1 else 0
      val isQuote = if (label == "B-ORG" && rand.nextBoolean()) 1 else 0
      val wordFixed = word.replace("\"", "")
      f"$wordFixed\t$upperFeature\t$twoOrMoreUp\t$containsLatin\t$isLong\t$isAbridgment\t$isEntity\t$isQuote\t$label"
    }



    def extractFeatures() = {
      val upperFeature = word(0).isUpper
      val twoOrMoreUp = word.foldLeft(0)((acc, c) => {
        acc + {
          if (c.isUpper) 1 else 0
        }
      }) > 1
      val inDict = dict contains word.toLowerCase
      val containsLatin = word.exists(c => {
        val code = Char.char2int(c.toLower)
        if (Char.char2int('a') <= code && code <= Char.char2int('z'))
          true
        else false
      })
      val isLong = word.length > 4
      val isAbridgment = abridgments contains word.toLowerCase
      f"$word\t$upperFeature\t$twoOrMoreUp\t$inDict\t$containsLatin\t$isLong\t$isAbridgment\t$label"
    }

    def extractFeaturesTwoClass() = {
      val upperFeature = word(0).isUpper
      val twoOrMoreUp = word.foldLeft(0)((acc, c) => {
        acc + {
          if (c.isUpper) 1 else 0
        }
      }) > 1
      val inDict = dict contains word.toLowerCase
      val containsLatin = word.exists(c => {
        val code = Char.char2int(c.toLower)
        if (Char.char2int('a') <= code && code <= Char.char2int('z'))
          true
        else false
      })
      val isLong = word.length > 4
      val isAbridgment = abridgments contains word.toLowerCase
      val label = if (this.label == "O") "O" else "ENT"
      f"$word\t$upperFeature\t$twoOrMoreUp\t$inDict\t$containsLatin\t$isLong\t$isAbridgment\t$label"
    }

  }

}



