/**
 * User: Vasily
 * Date: 25.02.14
 * Time: 18:14
 */

import java.io.{BufferedWriter, FileWriter}
import scala.xml.XML

object OpenCorpaToSentenceConverter extends App {
  val corpus =  scala.xml.XML.loadFile("/Users/Vasily/Dropbox/homework/NLP/opcorpora.xml")
  val writer = new BufferedWriter(new FileWriter("/Users/Vasily/Dropbox/homework/NLP/sentence-seq.txt"))
  var i = 0

  corpus \ "text" foreach {
    text => text \ "paragraphs" \ "paragraph"  foreach {
      paragraph => {
        paragraph \ "sentence" foreach (sentence => {
          writer.write((sentence \ "source").text)
        })
        writer.write(".\n")
      }
    }
  }
  writer.flush()
  writer.close()
}
