import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertTrue;

/**
 * User: Vasily
 * Date: 16.03.14
 * Time: 0:20
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

        Parser parser = new Parser();
        parser.nextLine("Some trivial sentence 1. Some trivial question 1? Another sentence, with \"quote...\". Another sentence, с русским. Another sentence, с русским и сокр. каким-то. Another sentence, с русским и сокр. and e.g. каким-то.");
        LinkedList<String> result = parser.getSentences();
        assertTrue(testSentences.size()==result.size());
        while (!testSentences.isEmpty()) {
            assertTrue(testSentences.pop().equals(result.pop()));
        }
    }
}
