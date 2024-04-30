import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CrawlerTest {

    private Crawler crawler;
    private final String testFilePath = "src/test/CrawlerTest.md";
    private final String testURL="https://example.com/page1";
    private final int testDepth=2;
    private final List<String> testDomains=new ArrayList<>();

    @BeforeEach
    public void setUp(){
        crawler=new Crawler(testURL,testDepth,testDomains,testFilePath);
    }

    @Test
    public void testCrawl_NoMatchingDomain(){
        crawler= Mockito.spy(new Crawler(testURL,testDepth,testDomains,testFilePath));

        MarkdownWriter mockMarkdownWriter=mockMarkdownWriter();
        mockParser();
        mockUnbrokenLink();

        crawler.crawl(testURL, testDepth);

        verifyLinkAndHeadingsAreWrittenOnce(mockMarkdownWriter);
        verifyBrokenLinkNeverWritten(mockMarkdownWriter);
        verifyCrawlingOnce();
        assertUrlIsMarkedAsVisited();
    }

    @Test
    public void testCrawl_ValidUrl(){
        crawler= Mockito.spy(new Crawler(testURL,testDepth,testDomains,testFilePath));

        MarkdownWriter mockMarkdownWriter=mockMarkdownWriter();
        mockParser();
        mockUnbrokenLink();

        addTestDomain();


        crawler.crawl(testURL, testDepth);

        verifyLinkAndHeadingsAreWrittenOnce(mockMarkdownWriter);
        verifyBrokenLinkNeverWritten(mockMarkdownWriter);
        verifyCrawlingTwice();
        assertUrlIsMarkedAsVisited();
    }

    @Test
    public void testCrawl_BrokenLink(){
        crawler= Mockito.spy(new Crawler(testURL,testDepth,testDomains,testFilePath));

        MarkdownWriter mockMarkdownWriter=mockMarkdownWriter();
        mockParser();
        mockBrokenLink();
        addTestDomain();

        crawler.crawl(testURL, testDepth);

        verifyCrawlingOnce();
        verifyBrokenLinkWrittenTwice(mockMarkdownWriter);
        assertUrlIsMarkedAsVisited();
    }

    @Test
    public void testWriteContentOfPageToMarkdown(){
        crawler= Mockito.spy(new Crawler(testURL,testDepth,testDomains,testFilePath));
        Parser mockParser=mockParser();

        MarkdownWriter mockMarkdownWriter=mockMarkdownWriter();

        crawler.writeContentOfPageToMarkdown(mockParser,testURL,testDepth);

        verifyLinkAndHeadingsAreWrittenOnce(mockMarkdownWriter);
    }


    @Test
    public void testAddVisitedUrl(){
        crawler.addVisitedUrl(testURL);
        List<String> visitedUrls=crawler.getVisitedURLs();
        assertEquals(1, visitedUrls.size());
        assertEquals(testURL,visitedUrls.get(0));
    }
    @Test
    public void testShouldCrawl_CurrentDepthSmallerDepth_UrlNotVisited(){
        int currentDepth=1;
        assertTrue(crawler.shouldCrawl(testURL,currentDepth));
    }

    @Test
    public void testShouldCrawl_CurrentDepthSmallerDepth_UrlVisited(){
        int currentDepth=1;
        crawler.addVisitedUrl(testURL);
        assertFalse(crawler.shouldCrawl(testURL,currentDepth));
    }

    @Test
    public void testShouldCrawl_CurrentDepthGreaterDepth_UrlNotVisited(){
        int currentDepth=3;
        assertFalse(crawler.shouldCrawl(testURL,currentDepth));
    }

    @Test
    public void testShouldCrawl_CurrentDepthGreaterDepth_UrlVisited(){
        int currentDepth=3;
        crawler.addVisitedUrl(testURL);
        assertFalse(crawler.shouldCrawl(testURL,currentDepth));
    }
    @Test
    public void testMatchesDomain_True(){
        String domain="de.wikipedia.org";
        testDomains.add(domain);
        String url_MatchingDomain= "https://de.wikipedia.org/wiki/Wikipedia:Hauptseite";
        assertTrue(crawler.matchesDomain(url_MatchingDomain));
    }

    @Test
    public void testMatchesDomain_False(){
        String domain="de.wikipedia.org";
        testDomains.add(domain);
        String url_NotMatchingDomain= "en.wikipedia.org/wiki/Main_Page";
        assertFalse(crawler.matchesDomain(url_NotMatchingDomain));
    }

    @Test
    public void testMatchesDomain_DomainIsNULL(){
        String domain="de.wikipedia.org";
        testDomains.add(domain);
        assertFalse(crawler.matchesDomain(null));
    }

    @Test
    public void testLinkIsBroken_True(){
        String brokenLink="https://example.com/not_found";
        assertTrue(crawler.linkIsBroken(brokenLink));
    }

    @Test
    public void testLinkIsBroken_False(){
        String notBrokenLink="https://www.wikipedia.org";
        assertFalse(crawler.linkIsBroken(notBrokenLink));
    }

    @Test
    void testFinishCrawling() {
        MarkdownWriter mockMarkdownWriter = mockMarkdownWriter();

        crawler.finishCrawling();

        verify(mockMarkdownWriter).closeWriter();
    }

    private MarkdownWriter mockMarkdownWriter(){
        MarkdownWriter mockMarkdownWriter = mock(MarkdownWriter.class);
        crawler.setMarkdownWriter(mockMarkdownWriter);
        doNothing().when(mockMarkdownWriter).writeLink(anyString(), anyInt());
        doNothing().when(mockMarkdownWriter).writeHeadings(any(),anyInt());
        doNothing().when(mockMarkdownWriter).writeBrokenLink(anyString(),anyInt());
        return  mockMarkdownWriter;
    }
    private void verifyLinkAndHeadingsAreWrittenOnce(MarkdownWriter mockMarkdownWriter){
        verify(mockMarkdownWriter, times(1)).writeLink(anyString(), eq(testDepth));
        verify(mockMarkdownWriter,times(1)).writeHeadings(any(),eq(testDepth));
    }
    private void verifyBrokenLinkNeverWritten(MarkdownWriter mockMarkdownWriter){
        verify(mockMarkdownWriter, never()).writeBrokenLink(anyString(), anyInt());
    }
    private void assertUrlIsMarkedAsVisited(){
        assertTrue(crawler.getVisitedURLs().contains(testURL));
    }
    private void verifyCrawlingOnce(){
        verify(crawler,times(1)).crawl(anyString(), anyInt());
    }
    private void verifyCrawlingTwice(){
        verify(crawler,times(2)).crawl(anyString(), anyInt());
    }
    private void verifyBrokenLinkWrittenTwice(MarkdownWriter mockMarkdownWriter){
        verify(mockMarkdownWriter, times(1)).writeBrokenLink(anyString(), eq(testDepth));
    }
    private void mockUnbrokenLink(){
        when(crawler.linkIsBroken("https://example.com/page2")).thenReturn(false);
    }
    private void mockBrokenLink(){
        when(crawler.linkIsBroken("https://example.com/page2")).thenReturn(true);
    }

    private Parser mockParser(){
        Parser mockParser=mock(Parser.class);
        Element mockLink = mock(Element.class);
        Elements links = new Elements();
        links.add(mockLink);

        when(mockParser.getLinks()).thenReturn(links);
        when(mockLink.attr("abs:href")).thenReturn("https://example.com/page2");
        doReturn(mockParser).when(crawler).createParser(anyString());
        return mockParser;
    }
    private void addTestDomain(){
        testDomains.add("example.com");
    }

    @AfterEach
    public void tearDown(){
        this.crawler=null;
    }

}
