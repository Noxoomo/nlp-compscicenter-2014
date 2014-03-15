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
public class Parser {
    private BufferedReader reader = null;
    private LinkedList<String> sentences = new LinkedList<>();
    private HashSet<String> abbreviation = new HashSet<>();

    public Parser(String filename) throws FileNotFoundException {
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            throw e;
        }
    }

    private StringBuilder sentence = new StringBuilder();
    private StringBuilder token = new StringBuilder();

    private void addToken(char c) {
        token.append(c);
    }

    private void endToken() {
        sentence.append(token.toString());
        token = new StringBuilder();
    }

    private void endSentence() {
        sentences.add(sentence.toString());
        sentence = new StringBuilder();
    }


    public LinkedList<String> parse() throws IOException {
        State state = State.reading;
        String line = removeDoubleSpaces(reader.readLine());

        while (line != null) {
            char buffer[] = line.toCharArray();
            for (int i = 0; i < buffer.length; ++i) {
                switch (state) {
                    case reading: {
                        switch (buffer[i]) {
                            case ' ':
                                addToken(' ');
                                endToken();
                                break;
                            case '.':
                                if (i + 2 < buffer.length && buffer[i + 1] == ' ' && Character.isLowerCase(buffer[i + 2])) {
                                    addToken(buffer[i]);
                                    addToken(' ');
                                    endToken();
                                    ++i;
                                    break;
                                } else if (i + 1 < buffer.length && Character.isLowerCase(buffer[i + 1])) {
                                    addToken(buffer[i]);
                                    addToken(buffer[i + 1]);
                                    endToken();
                                    ++i;
                                    break;
                                }
                                addToken(buffer[i]);
                                endToken();
                                endSentence();
                                break;
                            case '?':
                            case '!': {
                                addToken(buffer[i]);
                                endToken();
                                endSentence();
                                break;
                            }
                            case '\"': {
                                addToken(buffer[i]);
                                state = State.inQuote;
                                break;
                            }
                            default: {
                                addToken(buffer[i]);
                            }
                        }
                        break;
                    }
                    case inQuote: {
                        switch (buffer[i]) {
                            case '\"': {
                                addToken(buffer[i]);
                                state = State.reading;
                                break;
                            }
                            default: {
                                addToken(buffer[i]);
                                break;
                            }
                        }
                    }
                }
            }
            line = removeDoubleSpaces(reader.readLine());
        }

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


    enum State {
        reading, inQuote, meetDot
    }

}
