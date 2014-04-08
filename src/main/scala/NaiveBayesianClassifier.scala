
import scala.io.Source
import Helpers._

object NaiveBayesianClassifier extends App {
  val classEndIndicator = "----end_of_class----"


  val firstClassDocuments = for (file <- new java.io.File(args(0)).listFiles
                                 if file.isFile) yield Source.fromFile(file.getAbsolutePath).getLines().mkString(" ").normalize().split("\\s+")
  val secondClassDocuments = for (file <- new java.io.File(args(1)).listFiles
                                  if file.isFile) yield Source.fromFile(file.getAbsolutePath).getLines().mkString(" ").normalize().split("\\s+")
  val source = Source.fromFile(args(2)).getLines()
  val unknownProb = source.next().toDouble
  val config = source.toList.split(classEndIndicator)
  val firstStats = config.head
  val secondStats = config.tail.head
  val firstDictSize = firstStats.head.toInt
  val secondDictSize = secondStats.head.toInt
  val firstProb = firstStats.tail.head.toDouble
  val secondProb = secondStats.tail.head.toDouble
  val firstFreq = Map() ++ firstStats.tail.tail.map(entry => {
    val data = entry.split(" ")
    data(0) -> data(1).toDouble
  })
  val secondFreq = Map() ++ secondStats.tail.tail.map(entry => {
    val data = entry.split(" ")
    data(0) -> data(1).toDouble
  })
  val firstPredict = firstClassDocuments.map(getClass)
  val secondPredict = secondClassDocuments.map(getClass)
  val tp = firstPredict.foldLeft(0)((tp, value) => if (value == 0) tp + 1 else tp)
  val tn = secondPredict.foldLeft(0)((tn, value) => if (value == 1) tn + 1 else tn)
  val fn = firstPredict.foldLeft(0)((fn, value) => if (value == 1) fn + 1 else fn)
  val fp = secondPredict.foldLeft(0)((fp, value) => if (value == 0) fp + 1 else fp)

  val precision = 1.0 * tp / (tp + fp)
  val accuracy = 1.0 * (tp + tn) / (tp + fp + tn + fn)
  val recall = 1.0 * tp / (tp + fn)
  val fm = 2 * accuracy * recall / (accuracy + recall)

  def getClass(document: Array[String]) = {
    val firstLL = document.map(getFirstProb).foldLeft(Math.log(firstProb))(_ + _)
    val secondLL = document.map(getSecondProb).foldLeft(Math.log(secondProb))(_ + _)
    if (firstLL > secondLL) 0 else 1
  }

  def getFirstProb(word: String) = {
    firstFreq get word match {
      case Some(freq) => Math.log(freq)
      case None => Math.log(unknownProb)
    }
  }

  def getSecondProb(word: String) = {
    secondFreq get word match {
      case Some(freq) => Math.log(freq)
      case None => Math.log(unknownProb)
    }
  }



  print(f"precision: $precision\naccuracy: $accuracy \nrecall: $recall \nF: $fm")


}
