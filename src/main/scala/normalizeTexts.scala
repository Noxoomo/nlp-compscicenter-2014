import java.io.{BufferedWriter, FileWriter}
import scala.io.Source
import Helpers._

/**
 * User: Vasily
 * Date: 21.04.14
 * Time: 21:26
 */
object normalizeTexts extends App {
  for (file <- new java.io.File(args(0)).listFiles
       if file.isFile) {
    val writer = new BufferedWriter(new FileWriter(file.getAbsolutePath + ".fixed"))
    for (line <- Source.fromFile(file.getAbsolutePath).getLines()) {
      writer.write(line.normalize() + "\n")
    }
    writer.flush()
    writer.close()
  }

}
