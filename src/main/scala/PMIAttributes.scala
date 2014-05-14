
import Helpers._
import java.io.{BufferedWriter, FileWriter}
import scala.io.Source

/**
 * User: Vasily
 * Date: 14.05.14
 * Time: 18:23
 */
object PMIAttributes extends App {
  val good = (for (tweet <- Source.fromFile(args(0)).getLines()) yield tweet.normalizeTweets().split("\\s+")).toList
  val bad = (for (tweet <- Source.fromFile(args(1)).getLines()) yield tweet.normalizeTweets().split("\\s+")).toList

  val goodFreq = good.flatMap(_.map(str => str)).groupBy(str => str).mapValues(_.size)
  val badFreq = bad.flatMap(_.map(str => str)).groupBy(str => str).mapValues(_.size)
  val allFreq = (goodFreq.keySet.intersect(badFreq.keySet)).map(i => (i, goodFreq.getOrElse(i, 0) + badFreq.getOrElse(i, 0))).toMap
  val goodPMI = allFreq.map({
    case (word, freq) => (word -> goodFreq.getOrElse(word, 0) * 1.0 / freq)
  })
  val badPMI = allFreq.map({
    case (word, freq) => (word -> badFreq.getOrElse(word, 0) * 1.0 / freq)
  })

  val goodWriter = new BufferedWriter(new FileWriter("goodPMI"))
  for ((word, pmi) <- goodPMI) {
    goodWriter.write(f"$word\t$pmi\n")
  }
  goodWriter.flush()
  goodWriter.close()


  val badWriter = new BufferedWriter(new FileWriter("badPMI"))
  for ((word, pmi) <- badPMI) {
    badWriter.write(f"$word\t$pmi\n")
  }
  badWriter.flush()
  badWriter.close()


}
