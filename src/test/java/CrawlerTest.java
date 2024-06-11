import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CrawlerTest {

    private Crawler crawler;
    private final String testURL="http://example.com";
    private final String testChildLink="http://example.com/child";
    private final int testDepth=2;
    private MarkdownContentWriter mockContentWriter;
    private DomainMatcher mockDomainMatcher;

    private Parser mockParser_FirstIteration;
    private Parser mockParser_SecondIteration;

    @BeforeEach
    public void setUp() {
        mockContentWriter = mock(MarkdownContentWriter.class);
        mockDomainMatcher = mock(DomainMatcher.class);
        crawler = spy(new Crawler(testDepth, mockDomainMatcher, mockContentWriter));
    }

    @Test
    public void testCrawl_ShouldVisitUrlAndWriteContent() {
        Parser mockParser = mockSingleParser();

        crawler.crawl(testURL, 1);

        verifyContentWriterMethods_CalledOnce(mockParser);
        verify(mockContentWriter, never()).closeMarkDownContentWriter();
    }

    @Test
    public void testCrawl_ShouldNotRevisitVisitedUrl() {
        Parser mockParser=mockSingleParser();

        crawler.crawl(testURL, 1);
        crawler.crawl(testURL, 1);

        verifyContentWriterMethods_CalledOnce(mockParser);
    }

    private void verifyContentWriterMethods_CalledOnce(Parser mockParser){
        verify(mockContentWriter, times(1)).writeContentOfPageToMarkdown(mockParser, testURL, 1);
        verify(mockContentWriter, times(1)).writeBrokenLinks(mockParser, 1);
    }

    private Parser mockSingleParser(){
        Parser mockParser = mock(Parser.class);
        when(mockParser.getIntactUrls()).thenReturn(new String[]{testURL});
        doReturn(mockParser).when(crawler).createParser(testURL);

        return mockParser;
    }

    @Test
    public void testCrawl_ShouldFollowChildLinks() {
        mockParser();

        when(mockDomainMatcher.matchesDomain(testChildLink)).thenReturn(true);

        crawler.crawl(testURL, 1);

        verifyOrderOfContentWriterCalls();
    }

    private void verifyOrderOfContentWriterCalls(){
        InOrder inOrder = inOrder(mockContentWriter);
        inOrder.verify(mockContentWriter).writeContentOfPageToMarkdown(mockParser_FirstIteration, testURL, 1);
        inOrder.verify(mockContentWriter).writeBrokenLinks(mockParser_FirstIteration, 1);
        inOrder.verify(mockContentWriter).writeContentOfPageToMarkdown(mockParser_SecondIteration, testChildLink, 2);
        inOrder.verify(mockContentWriter).writeBrokenLinks(mockParser_SecondIteration, 2);
    }

    private void mockParser(){
        mockParser_FirstIteration();
        mockParser_SecondIteration();
    }

    private void mockParser_FirstIteration(){
        mockParser_FirstIteration = mock(Parser.class);
        when(mockParser_FirstIteration.getIntactUrls()).thenReturn(new String[]{testChildLink});
        doReturn(mockParser_FirstIteration).when(crawler).createParser(testURL);
    }

    private void mockParser_SecondIteration(){
        mockParser_SecondIteration = mock(Parser.class);
        when(mockParser_SecondIteration.getIntactUrls()).thenReturn(new String[]{});
        doReturn(mockParser_SecondIteration).when(crawler).createParser(testChildLink);
    }

    @Test
    public void testShouldContinueCrawling_DepthLimit() {
        assertFalse(crawler.shouldContinueCrawling(testURL, testDepth+1));
    }

    @Test
    public void testShouldContinueCrawling_AlreadyVisited() {
        crawler.crawl(testURL, testDepth-1);

        List<String> visitedUrls=crawler.getVisitedURLs();

        assertTrue(visitedUrls.contains(testURL));
        assertFalse(crawler.shouldContinueCrawling(testURL, testDepth));
    }

    @Test
    public void testShouldContinueCrawling_ValidConditions() {
        assertTrue(crawler.shouldContinueCrawling(testURL, testDepth-1));
    }

    @Test
    public void testMatchesDomain_True() {
        when(mockDomainMatcher.matchesDomain(testURL)).thenReturn(true);

        boolean matchesDomain = crawler.matchesDomain(testURL);

        assertTrue(matchesDomain);
        verify(mockDomainMatcher).matchesDomain(testURL);
    }

    @Test
    public void testMatchesDomain_False() {
        when(mockDomainMatcher.matchesDomain(testURL)).thenReturn(false);

        boolean matchesDomain = crawler.matchesDomain(testURL);

        assertFalse(matchesDomain);
        verify(mockDomainMatcher).matchesDomain(testURL);
    }

    @Test
    public void testAddVisitedUrl(){
        crawler.addVisitedUrl(testURL);

        List<String> visitedUrls=crawler.getVisitedURLs();

        assertEquals(1, visitedUrls.size());
        assertEquals(testURL,visitedUrls.get(0));
    }

    @AfterEach
    public void tearDown(){
        this.mockContentWriter=null;
        this.mockDomainMatcher=null;
        this.crawler=null;
    }
}
