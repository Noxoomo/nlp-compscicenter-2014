/**
 * User: Vasily
 * Date: 25.02.14
 * Time: 18:14
 */

import java.io.{BufferedWriter, FileWriter}


object OpenCorpaToSentenceConverter extends App {
  //Set working directory in IDEA to place, where this files are
  val corpus = scala.xml.XML.loadFile(args(0))
  val writer = new BufferedWriter(new FileWriter(args(1)))
  val bad = new BufferedWriter(new FileWriter(args(2)))
  val testSentence = "Test."
  val accuracy = tp / (tp + fp)
  val recall = tp / (tp + fn)
  val fm = 2 * accuracy * recall / (accuracy + recall)
  var i = 0
  var tp = 0.0
  var fn = 0.0
  var tn = 0.0
  var fp = 0.0
  corpus \ "text" foreach {
    text => text \ "paragraphs" \ "paragraph" foreach {
      paragraph => {
        paragraph \ "sentence" foreach (sentence => {
          val parser = new Parser()
          val tmp = (sentence \ "source").text
          val text = if (tmp.charAt(tmp.length - 1).isLetter) tmp + "." else tmp
          val sentences = parser.nextLine(text + " " + testSentence)
          if (sentences.size != 0)
            if (sentences.get(0).equals(text)) {
              tp += 1
            } else {
              bad.write(text + "\n")
              fn += 1
              fp += 1
              //              if (sentences.get(0).length > text.length) {
              //                fn += 1
              //              } else fp += 1
            }
          writer.write(text)
        })
        writer.write(".\n")
      }
    }
  }
  writer.flush()
  writer.close()

  //  println(tp)
  //  println(fp)
  //  println(tn)
  //  println(fn)

  println(f"accuracy: $accuracy\nrecall: $recall\nFm: $fm")

}
