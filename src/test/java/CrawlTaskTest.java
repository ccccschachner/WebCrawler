import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CrawlTaskTest {

    private static final String TEST_URL = "http://example.com";
    private static final String TEST_FILE_PATH = "testFile.md";

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
        crawlTask = spy(new CrawlTask(TEST_URL, TEST_FILE_PATH));
        doNothing().when(crawlTask).initializeCrawlingProcess();

        crawlTask.setMarkdownFileWriter(mockMarkdownFileWriter);
        crawlTask.setContentWriter(mockContentWriter);
        crawlTask.setDomainMatcher(mockDomainMatcher);
        crawlTask.setCrawler(mockCrawler);
    }

    @Test
    public void testRun() {
        doNothing().when(mockMarkdownFileWriter).writeHeader(anyString(), anyInt());
        doNothing().when(mockCrawler).crawl(anyString(), anyInt());
        doNothing().when(crawlTask).finishWritingAfterCrawling();

        crawlTask.run();

        verify(mockMarkdownFileWriter).writeHeader(eq(TEST_URL), anyInt());
        verify(mockCrawler).crawl(eq(TEST_URL), eq(0));
        verify(crawlTask).finishWritingAfterCrawling();
    }

    @Test
    public void testGetURL() {
        assertEquals(TEST_URL, crawlTask.getURL());
    }

    @Test
    public void testGetFilePath() {
        assertEquals(TEST_FILE_PATH, crawlTask.getFilePath());
    }

    @Test
    public void testInitializeCrawlingProcess() {
        crawlTask = new CrawlTask(TEST_URL, TEST_FILE_PATH);
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
