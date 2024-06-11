import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CrawlTaskTest {

    private static final String url = "http://example.com";
    private static final String filePath = "testFile.md";

    private static final int depth =2;
    private static final List<String> domains =List.of("example.com");

    private CrawlTask crawlTask;

    @Mock
    private MarkdownFileWriter mockMarkdownFileWriter;

    @Mock
    private MarkdownContentWriter mockContentWriter;

    @Mock
    private DomainMatcher mockDomainMatcher;

    @Mock
    private Crawler mockCrawler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        crawlTask = spy(new CrawlTask(url, filePath, depth, domains));
        doNothing().when(crawlTask).initializeCrawlingProcess();

        crawlTask.setMarkdownFileWriter(mockMarkdownFileWriter);
        crawlTask.setContentWriter(mockContentWriter);
        crawlTask.setDomainMatcher(mockDomainMatcher);
        crawlTask.setCrawler(mockCrawler);
    }


    @Test
    public void testRun() {
        MarkdownFileWriter mockMarkdownFileWriter = mock(MarkdownFileWriter.class);
        Crawler mockCrawler = mock(Crawler.class);

        doNothing().when(mockMarkdownFileWriter).writeHeader(anyString(), anyInt());
        doNothing().when(mockCrawler).crawl(anyString(), anyInt());
        doNothing().when(crawlTask).finishWritingAfterCrawling();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        crawlTask.run();
        System.setOut(System.out);

        assertTrue(outputStream.toString().contains("Finished"));

    }

    @Test
    public void testGetURL() {
        assertEquals(url, crawlTask.getURL());
    }

    @Test
    public void testGetFilePath() {
        assertEquals(filePath, crawlTask.getFilePath());
    }

    @Test
    public void testInitializeCrawlingProcess() {
        crawlTask = new CrawlTask(url, filePath, depth, domains);
        crawlTask.run();

        assertNotNull(crawlTask.getMarkdownFileWriter());
        assertNotNull(crawlTask.getContentWriter());
        assertNotNull(crawlTask.getDomainMatcher());
        assertNotNull(crawlTask.getCrawler());
    }

    @Test
    public void testFinishWritingAfterCrawling() {
        crawlTask.finishWritingAfterCrawling();
        verify(mockContentWriter).closeMarkDownContentWriter();
    }
}
