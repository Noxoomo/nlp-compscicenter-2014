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

}