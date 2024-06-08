import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    Parser parser;
    String urlValid = "https://example.com";
    String urlInvalid = ".com";
    String urlWikipedia = "https://de.wikipedia.org/wiki/Wikipedia:Hauptseite";


    @Test
    public void testCreateDocument() {
        parser = new Parser(urlValid);

        assertNotNull(parser.getDocumentTitle());
        assertFalse(parser.getDocumentTitle().isEmpty());
    }

    @Test
    public void testStoreHeadings() {
        parser = new Parser(urlValid);

        String[] headings = parser.getHeadings();
        assertNotNull(headings);
        assertFalse(headings.length == 0);
    }

    @Test
    public void testStoreLinks() {
        parser = new Parser(urlValid);

        String[] links = parser.getIntactUrls();
        assertNotNull(links);
        assertFalse(links.length == 0);
    }

    @Test
    public void testInvalidUrl() {
        assertThrows(Exception.class, () -> {
            parser = new Parser(urlInvalid);
        });
    }

    @Test
    public void testWikipediaIntact() {
        parser = new Parser(urlWikipedia);
        System.out.println("Intact Links:\n" + Arrays.toString(parser.getIntactUrls()));
        assertNotNull(parser.getIntactUrls());
    }

    @Test
    public void testWikipediaBroken() {
        parser = new Parser(urlWikipedia);
        System.out.println("Broken Links:\n" + Arrays.toString(parser.getBrokenUrls()));

    }

    @Test
    public void testGetHeadings() {
        parser = new Parser(urlWikipedia);
        System.out.println("Headings:\n" +Arrays.toString(parser.getHeadings()));
    }

}
