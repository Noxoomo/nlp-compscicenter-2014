import java.io.{BufferedWriter, FileWriter}
import scala.io.Source
import scala.util.parsing.json.JSON

/**
 * User: Vasily
 * Date: 16.03.14
 * Time: 18:51
 */


val lines = Source.fromFile("corpusAllNews.json").getLines.mkString
val json = JSON.parseFull(lines)
val writer = new BufferedWriter(new FileWriter("../newsCorpus/corpus"))
val sentences = (json match {
  case Some(list) => list
  case None => List()
}).asInstanceOf[List[String]]
writer.write(sentences.mkString("\n"))
writer.flush()
writer.close()
