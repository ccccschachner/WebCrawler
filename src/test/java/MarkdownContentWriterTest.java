import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class MarkdownContentWriterTest {
    private MarkdownFileWriter markdownFileWriter;

    private MarkdownContentWriter markdownContentWriter;

    @BeforeEach
    public void setUp() {
        markdownFileWriter=mock(MarkdownFileWriter.class);
        markdownContentWriter = new MarkdownContentWriter(markdownFileWriter);
    }

    @Test
    public void testWriteBrokenLinks() {
        String[] brokenLinks={"http://example.com/broken", "http://test.org/error"};
        int depth=1;

        Parser parser=mockParser_getBrokenLinks(brokenLinks);

        markdownContentWriter.writeBrokenLinks(parser, depth);

        verify(markdownFileWriter).writeBrokenLink(brokenLinks[0], depth);
        verify(markdownFileWriter).writeBrokenLink(brokenLinks[1], depth);
        verifyNoMoreInteractions(markdownFileWriter);
    }

    private Parser mockParser_getBrokenLinks(String[] brokenLinks) {
        Parser parser = mock(Parser.class);
        when(parser.getBrokenUrls()).thenReturn(brokenLinks);
        return parser;
    }

    @Test
    public void testWriteContentOfPageToMarkdown() {
        String[] headings={"Heading 1", "Heading 2", "Heading 3"};
        int depth=2;
        String url = "http://example.com/page";

        Parser parser=mockParser_getHeadings(headings);

        markdownContentWriter.writeContentOfPageToMarkdown(parser, url, depth);

        verify(markdownFileWriter).writeLink(url, depth);
        verify(markdownFileWriter).writeHeadings(headings, depth);
        verifyNoMoreInteractions(markdownFileWriter);
    }

    private Parser mockParser_getHeadings(String[] headings) {
        Parser parser = mock(Parser.class);
        when(parser.getHeadings()).thenReturn(headings);
        return parser;
    }

    @Test
    public void testWriteErrorMessageIntoReport(){
        String errorMessage="Test Error";

        markdownContentWriter.writeErrorMessageIntoReport(errorMessage);

        verify(markdownFileWriter).writeLine(errorMessage);
        verifyNoMoreInteractions(markdownFileWriter);
    }

    @Test
    public void testCloseMarkDownContentWriter() {
        markdownContentWriter.closeMarkDownContentWriter();
        verify(markdownFileWriter).closeWriter();
        verifyNoMoreInteractions(markdownFileWriter);
    }

    @AfterEach
    public void tearDown(){
        markdownFileWriter=null;
        markdownContentWriter=null;
    }
}
