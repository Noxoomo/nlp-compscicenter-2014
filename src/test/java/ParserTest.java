import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;

import static org.junit.Assert.assertTrue;

/**
 * User: Vasily
 * Date: 04.03.14
 * Time: 19:02
 */
public class ParserTest {
    @Test
    public void testParse() throws Exception {
        LinkedList<String> testSentences = new LinkedList<>();
        testSentences.add("Some trivial sentence 1.");
        testSentences.add(" Some trivial question 1?");
        testSentences.add(" Another sentence, with \"quote...\".");
        testSentences.add(" Another sentence, с русским.");
        testSentences.add(" Another sentence, с русским и сокр. каким-то.");
        testSentences.add(" Another sentence, с русским и сокр. and e.g. каким-то.");
        BufferedWriter writer = new BufferedWriter(new FileWriter("/tmp/test"));
        for (String sentence: testSentences) {
            writer.write(sentence);
        }
        writer.flush();
        writer.close();

        Parser parser = new Parser("/tmp/test");
        LinkedList<String> result = parser.parse();
        assertTrue(testSentences.size()==result.size());
        while (!testSentences.isEmpty()) {
            assertTrue(testSentences.pop().equals(result.pop()));
        }
    }
}
