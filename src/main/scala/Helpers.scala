import scala.io.Source
import scala.util.matching.Regex

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
    val punctuation = Set('.', ',', '?', '!', '\"', '…', '(', ')', ';', '-', '«', '»', '–')
    val endings = new Regex("(ий|ой|ый|ая|ие|их|ое|ую|ем|им|ии|ей|ом|ых|ым|ия|ёй)\\b")

    //о|а|я|ы|и|е|у|ю
    def normalize() = {
      endings.replaceAllIn(text.toLowerCase.replace("\n", " ").filterNot(punctuation contains), " ")
      //text.toLowerCase().replace("\n"," ").filterNot(punctuation contains)
    }
  }

  val dict = Source.fromFile("/Users/Vasily/Dropbox/homework/NLP/ushakov.txt").getLines().mkString(" ").split(" ").map(_.toLowerCase).toSet
  val preposition = Set('в', "на", "для", "к", "c")

  implicit class FeaturesExtractor(entity: String) {
    val whitespace = new Regex("\t")
    val split_entity = whitespace.split(entity)
    val word = split_entity(0)
    val label = split_entity(1)


    def extractFeatures() = {
      val upperFeature = word(0).isUpper
      val allUp = word.forall(_.isUpper)
      val inDict = dict contains word.toLowerCase
      val containsLatin = word.exists(c => {
        val code = Char.char2int(c.toLower)
        if (Char.char2int('a') <= code && code <= Char.char2int('z'))
          true
        else false
      })
      val isPreposition = preposition contains word.toLowerCase
      f"$word\t$upperFeature\t$allUp\t$inDict\t$containsLatin\t$isPreposition\t$label"
    }
  }

}