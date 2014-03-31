/**
 * User: Vasily
 * Date: 27.03.14
 * Time: 19:40
 */

import java.io.{BufferedWriter, FileWriter}
import scala.io.Source

object NaiveBayesianLearner extends App {
  val punctuation = Set('.', ',', '?', '!', '\"', '…', '(', ')', ';')
  val classEndIndicator = "----end_of_class----"

  val firstClassDocuments = for (file <- new java.io.File(args(0)).listFiles
                                 if file.isFile) yield Source.fromFile(file.getAbsolutePath).getLines().mkString(" ").filterNot(punctuation contains).split(" ")
  val secondClassDocuments = for (file <- new java.io.File(args(1)).listFiles
                                  if file.isFile) yield Source.fromFile(file.getAbsolutePath).getLines().mkString(" ").filterNot(punctuation contains).split(" ")


  val firstProb = 1.0 * firstClassDocuments.size / (firstClassDocuments.size + secondClassDocuments.size)
  val secondProb = 1.0 * secondClassDocuments.size / (firstClassDocuments.size + secondClassDocuments.size)

  //we have virtual «others» word to solve problem with new words
  val firstDictSize = firstClassDocuments.foldLeft(1)(_ + _.size)
  val secondDictSize = secondClassDocuments.foldLeft(1)(_ + _.size)


  val firstFreq = firstClassDocuments.flatMap(_.map(str => str)).groupBy(str => str).mapValues(_.size * 1.0 / firstDictSize)
  val secondFreq = secondClassDocuments.flatMap(_.map(str => str)).groupBy(str => str).mapValues(_.size * 1.0 / secondDictSize)

  val writer = new BufferedWriter(new FileWriter(args(2)))
  writer.write(firstDictSize + "\n")
  writer.write(firstProb + "\n")
  writer.write(firstFreq.map {
    case (word, freq) => word + " " + freq
  }.mkString("\n"))
  writer.write(classEndIndicator + "\n")
  writer.write(secondDictSize + "\n")
  writer.write(secondProb + "\n")
  writer.write(secondFreq.map {
    case (word, freq) => word + " " + freq
  }.mkString("\n"))

}