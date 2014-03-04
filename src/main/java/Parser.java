import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * User: Vasily
 * Date: 04.03.14
 * Time: 18:37
 */
public class Parser {
    private BufferedReader reader = null;
    private LinkedList<String> sentences = new LinkedList<>();
    private int bufferSize = 1000;

    public Parser(String filename) throws FileNotFoundException {
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            throw e;
        }
    }

    public LinkedList<String> parse() throws IOException {
        State state = State.reading;
        StringBuilder sentence = new StringBuilder();
        char buffer[] = new char[bufferSize];
        while (reader.ready()) {
            int read = reader.read(buffer);
            for (int i = 0; i < read; ++i) {
                switch (state) {
                    case reading: {
                        switch (buffer[i]) {
                            case '.':
                            case '?':
                            case '!': {
                                sentence.append(buffer[i]);
                                sentences.add(sentence.toString());
                                sentence = new StringBuilder();
                                break;
                            }
                            case '\"': {
                                sentence.append(buffer[i]);
                                state = State.inQuote;
                                break;
                            }
                            default: {
                                sentence.append(buffer[i]);
                            }
                        }
                        break;
                    }
                    case inQuote: {
                        switch (buffer[i]) {
                            case '\"': {
                                sentence.append(buffer[i]);
                                state = State.reading;
                                break;
                            }
                            default: {
                                sentence.append(buffer[i]);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return sentences;
    }

    enum State {
        reading, inQuote, meetDot
    }

}
