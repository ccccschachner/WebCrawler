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
        Parser parser = mock(Parser.class);
        when(parser.getBrokenUrls()).thenReturn(new String[]{"http://example.com/broken", "http://test.org/error"});

        markdownContentWriter.writeBrokenLinks(parser, 1);

        verify(markdownFileWriter).writeBrokenLink("http://example.com/broken", 1);
        verify(markdownFileWriter).writeBrokenLink("http://test.org/error", 1);
        verifyNoMoreInteractions(markdownFileWriter);
    }

    @Test
    public void testWriteContentOfPageToMarkdown() {
        Parser parser = mock(Parser.class);
        when(parser.getHeadings()).thenReturn(new String[]{"Heading 1", "Heading 2", "Heading 3"});
        String url = "http://example.com/page";

        markdownContentWriter.writeContentOfPageToMarkdown(parser, url, 2);

        verify(markdownFileWriter).writeLink(url, 2);
        verify(markdownFileWriter).writeHeadings(new String[]{"Heading 1", "Heading 2", "Heading 3"}, 2);
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
