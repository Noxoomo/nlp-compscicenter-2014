import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * User: Vasily
 * Date: 04.03.14
 * Time: 18:37
 */
public class FileParser {
    private BufferedReader reader = null;
    private HashSet<String> abbreviation = new HashSet<>();
    private Parser parser = new Parser();

    public FileParser(String filename) throws FileNotFoundException {
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            throw e;
        }
    }


    public LinkedList<String> parse() throws IOException {
        String line = removeDoubleSpaces(reader.readLine());

        while (line != null) {
            parser.nextLine(line);
            line = removeDoubleSpaces(reader.readLine());
        }
        return parser.getSentences();
    }

    private String removeDoubleSpaces(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length; ++i) {
            builder.append(arr[i]);
            if (arr[i] == ' ') {
                while (i + 1 < arr.length && arr[i + 1] == ' ')
                    ++i;
            }
        }
        return builder.toString();
    }


}
