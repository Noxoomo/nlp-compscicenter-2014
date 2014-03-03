/**
 * User: Vasily
 * Date: 25.02.14
 * Time: 21:41
 */
class SentenceSplitter extends (String => List[String]) {
  private def proceed(text: Array[Char]): List[String] = {
    var i = 0
    var result = List[String]()
    var currentSentence = List[Char]()
    while (i < text.length) {
      text(i) match {
        case '.' => {
          currentSentence = text(i) :: currentSentence
          result = currentSentence.reverse.mkString :: result
          i += 1
          currentSentence = List[Char]()
        }
        case '?' => {
          currentSentence = text(i) :: currentSentence
          result = currentSentence.reverse.mkString :: result
          i += 1
          currentSentence = List[Char]()
        }
        case '!' => {
          currentSentence = text(i) :: currentSentence
          result = currentSentence.reverse.mkString :: result
          i += 1
          currentSentence = List[Char]()
        }

        case c => {
          currentSentence = c :: currentSentence
          i += 1
        }
      }
    }
    result.reverse
  }

  override def apply(text: String): List[String] = {
    proceed(text.replace(". ", ".").replace("! ", "!").replace("? ", "?").replace("... ", "...").toCharArray)
    //text.split(".").toList
  }

}
