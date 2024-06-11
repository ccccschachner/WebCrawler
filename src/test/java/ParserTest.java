import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ParserTest {

    private Parser parser;
    private final String urlValid = "https://example.com";
    private final String urlInvalid = ".com";
    private final String urlWikipedia = "https://de.wikipedia.org/wiki/Wikipedia:Hauptseite";

    private final MarkdownContentWriter contentWriterMock=mock(MarkdownContentWriter.class);

    @Test
    public void testCreateDocument() {
        parser = new Parser(urlValid,contentWriterMock);

        assertNotNull(parser.getDocumentTitle());
        assertFalse(parser.getDocumentTitle().isEmpty());
    }
    @Test
    public void testStoreHeadings() {
        parser = new Parser(urlValid,contentWriterMock);

        String[] headings = parser.getHeadings();
        assertNotNull(headings);
        assertNotEquals(0, headings.length);
    }

    @Test
    public void testStoreLinks() {
        parser = new Parser(urlValid,contentWriterMock);

        String[] links = parser.getIntactUrls();
        assertNotNull(links);
        assertNotEquals(0, links.length);
    }

    @Test
    public void testInvalidUrl() {
        assertThrows(Exception.class, () -> {
            parser = new Parser(urlInvalid,contentWriterMock);
        });
    }

    @Test
    public void testWikipediaIntact() {
        parser = new Parser(urlWikipedia,contentWriterMock);
        System.out.println("Intact Links:\n" + Arrays.toString(parser.getIntactUrls()));
        assertNotNull(parser.getIntactUrls());
    }

    @Test
    public void testWikipediaBroken() {
        parser = new Parser(urlWikipedia,contentWriterMock);
        System.out.println("Broken Links:\n" + Arrays.toString(parser.getBrokenUrls()));

    }

    @Test
    public void testGetHeadings() {
        parser = new Parser(urlWikipedia,contentWriterMock);
        System.out.println("Headings:\n" +Arrays.toString(parser.getHeadings()));
    }

}
