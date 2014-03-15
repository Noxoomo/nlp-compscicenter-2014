import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * User: Vasily
 * Date: 09.03.14
 * Time: 15:38
 */
public class ParseNews {
    public static void main(String[] args) {
        try {
            FileParser parser = new FileParser("/tmp/news");
            LinkedList<String> result = parser.parse();
            BufferedWriter writer = new BufferedWriter(new FileWriter("/tmp/result"));
            writer.write("{\n" +
                    "\t\"document\": {\n" +
                    "\t\t\"sentences\": \n" +
                    "\t\t[");
            for (String sentence: result) {
                writer.write("\"" + sentence + "\",");
            }
            writer.write("]\n" +
                    "\t\t}\n" +
                    "\n" +
                    "}");
            writer.flush();
            writer.close();
        } catch (IOException e) {

        }

    }
}
