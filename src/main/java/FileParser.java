import java.io.*;
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

    LinkedList<String> sentences = null;

    public LinkedList<String> parse() throws IOException {
        String line = removeDoubleSpaces(reader.readLine());

        while (line != null) {
            parser.nextLine(line);
            line = removeDoubleSpaces(reader.readLine());
        }
        sentences = parser.getSentences();
        return sentences;
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

    public void printJson(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write("[" + sentences.get(0).replace("\"", "\\\""));
        String backup = sentences.removeFirst();
        for (String sentence : sentences) {
            writer.write(",\"" + sentence.replace("\"", "\\\"") + "\"");

        }
        sentences.addFirst(backup);
        writer.write("]");
        writer.flush();
        writer.close();

    }

    public void printXml(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (String sentence : sentences) {
            writer.write("<sentence>" + sentence + "<\\sentence>");

        }
        writer.flush();
        writer.close();
    }


}
