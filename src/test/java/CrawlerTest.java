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
    private final String testTargetLanguage="de";


    @BeforeEach
    public void setUp(){
        crawler=new Crawler(testURL,testDepth,testDomains,testFilePath,testTargetLanguage);
    }

    @Test
    public void testCrawl_ValidUrl(){
        crawler= Mockito.spy(new Crawler(testURL,testDepth,testDomains,testFilePath,testTargetLanguage));

        MarkdownWriter mockMarkdownWriter=mockMarkdownWriter();
        Parser mockParser=mock(Parser.class);

        Elements mockElements = new Elements();
        Element mockElement = mock(Element.class);
        mockElements.add(mockElement);
        when(mockParser.getLinks()).thenReturn(mockElements);
        when(mockElement.attr("abs:href")).thenReturn("https://example.com/page2");
        when(crawler.linkIsBroken("https://example.com/page2")).thenReturn(false);

        doReturn(mockParser).when(crawler).createParser(anyString());


        crawler.crawl(testURL, testDepth);

        verify(mockMarkdownWriter, times(1)).writeInDocument(any(Parser.class), eq(testDepth));
        verify(mockMarkdownWriter, never()).writeBrokenLink(anyString(), anyInt());
        assertTrue(crawler.getVisitedURLs().contains(testURL));
    }

    @Test
    public void testCrawl_BrokenLink(){
        crawler= Mockito.spy(new Crawler(testURL,testDepth,testDomains,testFilePath,testTargetLanguage));

        MarkdownWriter mockMarkdownWriter=mockMarkdownWriter();
        Parser mockParser=mock(Parser.class);

        Elements mockElements = new Elements();
        Element mockElement = mock(Element.class);
        mockElements.add(mockElement);
        when(mockParser.getLinks()).thenReturn(mockElements);
        when(mockElement.attr("abs:href")).thenReturn("https://example.com/page2");
        when(crawler.linkIsBroken("https://example.com/page2")).thenReturn(true);

        doReturn(mockParser).when(crawler).createParser(anyString());

        crawler.crawl(testURL, testDepth);

        verify(crawler, never()).continueCrawlingMatchingDomain(anyString(),anyInt());
        verify(mockMarkdownWriter, times(1)).writeBrokenLink(anyString(), eq(testDepth));
        assertTrue(crawler.getVisitedURLs().contains(testURL));
    }

    @Test
    public void testContinueCrawlingMatchingDomain_Match(){
        crawler= Mockito.spy(new Crawler(testURL,testDepth,testDomains,testFilePath,testTargetLanguage));

        String matchingDomain="example.com";
        testDomains.add(matchingDomain);

        doNothing().when(crawler).crawl(anyString(),anyInt());

        crawler.continueCrawlingMatchingDomain(testURL,1);
        verify(crawler, times(1)).crawl(anyString(), anyInt());
    }

    @Test
    public void testContinueCrawlingMatchingDomain_NoMatch(){
        String NotMatchingDomain="example.org";
        testDomains.add(NotMatchingDomain);
        crawler.continueCrawlingMatchingDomain(testURL,1);
        assertFalse(crawler.getVisitedURLs().contains(NotMatchingDomain));
    }
    @Test
    public void testAddVisitedUrl(){
        crawler.addVisitedUrl(testURL);
        List<String> visitedUrls=crawler.getVisitedURLs();
        assertEquals(1, visitedUrls.size());
        assertEquals(testURL,visitedUrls.get(0));
    }
    @Test
    public void testContinueCrawling_CurrentDepthSmallerDepth_UrlNotVisited(){
        int currentDepth=1;
        assertTrue(crawler.continueCrawling(testURL,currentDepth));
    }

    @Test
    public void testContinueCrawling_CurrentDepthSmallerDepth_UrlVisited(){
        int currentDepth=1;
        crawler.addVisitedUrl(testURL);
        assertFalse(crawler.continueCrawling(testURL,currentDepth));
    }

    @Test
    public void testContinueCrawling_CurrentDepthGreaterDepth_UrlNotVisited(){
        int currentDepth=3;
        assertFalse(crawler.continueCrawling(testURL,currentDepth));
    }

    @Test
    public void testContinueCrawling_CurrentDepthGreaterDepth_UrlVisited(){
        int currentDepth=3;
        crawler.addVisitedUrl(testURL);
        assertFalse(crawler.continueCrawling(testURL,currentDepth));
    }

    @Test
    public void testGetDomainFromURL_ValidURL(){
        String domain="de.wikipedia.org";
        String domainFromUrl= crawler.getDomainFromURL("https://de.wikipedia.org/wiki/Wikipedia:Hauptseite");
        assertEquals(domainFromUrl,domain);
    }

    @Test
    public void testGetDomainFromURL_InvalidURL(){
        String domainFromUrl= crawler.getDomainFromURL("de.wikipedia.org/wiki/Wikipedia:Hauptseite");
        assertNull(domainFromUrl);
    }

    @Test
    public void testCompareIfDomainMatches_True(){
        String domain="de.wikipedia.org";
        String domainFromUrl= crawler.getDomainFromURL("https://de.wikipedia.org/wiki/Wikipedia:Hauptseite");
        assertTrue(crawler.compareIfDomainMatches(domainFromUrl,domain));
    }

    @Test
    public void testCompareIfDomainMatches_False(){
        String domain="de.wikipedia.org";
        String domainFromUrl= crawler.getDomainFromURL("https://en.wikipedia.org/wiki/Main_Page");
        assertFalse(crawler.compareIfDomainMatches(domainFromUrl,domain));
    }

    @Test
    public void testCompareIfDomainMatches_DomainFromURLIsNULL(){
        String domain="de.wikipedia.org";
        assertFalse(crawler.compareIfDomainMatches(null,domain));
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
        doNothing().when(mockMarkdownWriter).writeInDocument(any(), anyInt());
        return  mockMarkdownWriter;
    }

    @AfterEach
    public void tearDown(){
        this.crawler=null;
    }

}
