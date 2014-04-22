/**
 * User: Vasily
 * Date: 27.03.14
 * Time: 19:40
 */

import java.io.{BufferedWriter, FileWriter}
import scala.io.Source
import Helpers._


object NaiveBayesianLearner extends App {
  val classEndIndicator = "----end_of_class----"

  val firstClassDocuments = for (file <- new java.io.File(args(0)).listFiles
                                 if file.isFile) yield Source.fromFile(file.getAbsolutePath).getLines().mkString(" ").normalize().split("\\s+")
  val secondClassDocuments = for (file <- new java.io.File(args(1)).listFiles
                                  if file.isFile) yield Source.fromFile(file.getAbsolutePath).getLines().mkString(" ").normalize().split("\\s+")


  val firstProb = 1.0 * firstClassDocuments.size / (firstClassDocuments.size + secondClassDocuments.size)
  val secondProb = 1.0 * secondClassDocuments.size / (firstClassDocuments.size + secondClassDocuments.size)

  //we have virtual «others» word to solve problem with new words
  val firstDictSize = firstClassDocuments.foldLeft(0)(_ + _.size)
  val secondDictSize = secondClassDocuments.foldLeft(0)(_ + _.size)
  val dictSize = firstDictSize + secondDictSize
  val unknownProb = 1.0 / dictSize
  val knownWordProb = (dictSize - 1.0) / dictSize


  val firstFreq = firstClassDocuments.flatMap(_.map(str => str)).groupBy(str => str).mapValues(_.size * knownWordProb / firstDictSize)
  val secondFreq = secondClassDocuments.flatMap(_.map(str => str)).groupBy(str => str).mapValues(_.size * knownWordProb / secondDictSize)


  val writer = new BufferedWriter(new FileWriter(args(2)))
  writer.write(f"$unknownProb\n")
  writer.write(firstDictSize + "\n")
  writer.write(firstProb + "\n")
  writer.write(firstFreq.map {
    case (word, freq) => f"$word $freq"
  }.mkString("\n"))
  writer.write("\n" + classEndIndicator + "\n")
  writer.write(secondDictSize + "\n")
  writer.write(secondProb + "\n")
  writer.write(secondFreq.map {
    case (word, freq) => f"$word $freq"
  }.mkString("\n"))
  writer.flush()
  writer.close()
}