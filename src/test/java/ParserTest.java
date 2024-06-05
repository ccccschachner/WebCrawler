import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    Parser parser;
    String urlValid = "https://example.com";
    String urlInvalid = ".com";


    @Test
    public void testCreateDocument() {
        parser = new Parser(urlValid);

        assertNotNull(parser.getDocumentTitle());
        assertFalse(parser.getDocumentTitle().isEmpty());
    }

    @Test
    public void testStoreHeadings() {
        parser = new Parser(urlValid);

        Elements headings = parser.getHeadings();
        assertNotNull(headings);
        assertFalse(headings.isEmpty());
    }

    @Test
    public void testStoreLinks() {
        parser = new Parser(urlValid);

        Elements links = parser.getLinks();
        assertNotNull(links);
        assertFalse(links.isEmpty());
    }

    @Test
    public void testInvalidUrl() {
        assertThrows(Exception.class, () -> {
            parser = new Parser(urlInvalid);
        });
    }


}
