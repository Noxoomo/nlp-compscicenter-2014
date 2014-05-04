import java.io.{BufferedWriter, FileWriter}
import scala.io.Source
import Helpers._

/**
 * User: Vasily
 * Date: 22.04.14
 * Time: 9:51
 */
object featuresExtractionMerged extends App {


  def extract(source: String): List[List[String]] = {
    val sentences = (for (line <- Source.fromFile(source).getLines() if line != "") yield {
      if (line.charAt(0) == '#') "###"
      else line.extractFeaturesTwoClass()
    }).toList
    sentences.split("###")
  }

  //  var result = extract(args(0))
  //  print("test")

  def save(dest: String, sentences: List[List[String]]) {
    val writer = new BufferedWriter(new FileWriter(dest))

    for (sentence <- sentences) {
      writer.write(sentence.mkString("\n") + "\n\n")
    }
    writer.flush()
    writer.close()
  }

  def save_cv(dest: String, sentences: Array[List[String]], folds: Int = 5) {
    for (i <- 0 until folds) {
      print(i)
      val learnWriter = new BufferedWriter(new FileWriter(f"$dest-$i-learn"))
      val testWriter = new BufferedWriter(new FileWriter(f"$dest-$i-test"))
      for (j <- 0 until sentences.length) {
        if (j % folds != i) {
          learnWriter.write(sentences(j).mkString("\n") + "\n\n")
        } else {
          testWriter.write(sentences(j).mkString("\n") + "\n\n")
        }
      }
      learnWriter.flush()
      learnWriter.close()
      testWriter.flush()
      testWriter.close()
    }
  }

  save_cv(args(1), extract(args(0)).toArray)

  save(args(1), extract(args(0)))
  save(args(3), extract(args(2)))

  //  extract(args(0), args(1))
  //  extract(args(2), args(3))


}
