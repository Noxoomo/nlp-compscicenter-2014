import java.io.FileWriter
import scala.io.Source
import Helpers._

/**
 * User: Vasily
 * Date: 14.05.14
 * Time: 20:15
 */
object DictClassifier extends App {
  val good = (for (tweet <- Source.fromFile(args(0)).getLines()) yield tweet).toSet
  val bad = (for (tweet <- Source.fromFile(args(1)).getLines()) yield tweet).toSet

  val firstClassDocuments = (for (tweet <- Source.fromFile(args(2)).getLines()) yield tweet.normalizeTweets.split("\\s+").toSet).toList
  val secondClassDocuments = (for (tweet <- Source.fromFile(args(3)).getLines()) yield tweet.normalizeTweets.split("\\s+").toSet).toList

  def getClass(words: Set[String]) = {
    if (words.intersect(good).size >= words.intersect(bad).size) {
      1
    } else 0
  }

  val firstPredict = firstClassDocuments.map(getClass)
  val secondPredict = secondClassDocuments.map(getClass)
  val tp = firstPredict.foldLeft(0)((tp, value) => if (value == 0) tp + 1 else tp)
  val tn = secondPredict.foldLeft(0)((tn, value) => if (value == 1) tn + 1 else tn)
  val fn = firstPredict.foldLeft(0)((fn, value) => if (value == 1) fn + 1 else fn)
  val fp = secondPredict.foldLeft(0)((fp, value) => if (value == 0) fp + 1 else fp)

  //  print(f"tp = $tp\n tn = $tn\m fn = $fn\n fp = $fp\n")

  val precision = 1.0 * tp / (tp + fp)
  val accuracy = 1.0 * (tp + tn) / (tp + fp + tn + fn)
  val recall = 1.0 * tp / (tp + fn)
  val fm = 2 * accuracy * recall / (accuracy + recall)


  print(f"precision: $precision\naccuracy: $accuracy \nrecall: $recall \nF: $fm")


}