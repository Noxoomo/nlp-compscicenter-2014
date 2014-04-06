import scala.io.Source

/**
 * User: Vasily
 * Date: 03.04.14
 * Time: 19:10
 */
object TestNewsData extends App() {
  val testSentence = "Test."
  var fn = 0.0
  var tn = 0.0
  var fp = 0.0

  var i = 0
  var tp = 0.0
  for (sentence <- Source.fromFile(args(0)).getLines()) {
    val parser = new Parser()
    val text = if (sentence.charAt(sentence.length - 1).isLetter) sentence + "." else sentence
    val sentences = parser.nextLine(text + " " + testSentence)
    if (sentences.size != 0)
      if (sentences.get(0).equals(text)) {
        tp += 1
      } else {
        fn += 1
        fp += 1
        //              if (sentences.get(0).length > text.length) {
        //                fn += 1
        //              } else fp += 1
      }
  }
  //  println(tp)
  //  println(fp)
  //  println(tn)
  //  println(fn)


  val accuracy = tp / (tp + fp)
  val recall = tp / (tp + fn)
  val fm = 2 * accuracy * recall / (accuracy + recall)

  println(f"accuracy: $accuracy\nrecall: $recall\nFm: $fm")
  //  println(recall)
  //  println(fm)

}
