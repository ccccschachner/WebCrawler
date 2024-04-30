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
        Parser mockParser=mock(Parser.class);

        Elements mockElements = new Elements();
        Element mockElement = mock(Element.class);
        mockElements.add(mockElement);
        when(mockParser.getLinks()).thenReturn(mockElements);
        when(mockElement.attr("abs:href")).thenReturn("https://example.com/page2");
        when(crawler.linkIsBroken("https://example.com/page2")).thenReturn(false);

        doReturn(mockParser).when(crawler).createParser(anyString());

        crawler.crawl(testURL, testDepth);

        verify(mockMarkdownWriter, times(1)).writeLink(anyString(), eq(testDepth));
        verify(mockMarkdownWriter,times(1)).writeHeadings(any(),eq(testDepth));
        verify(mockMarkdownWriter, never()).writeBrokenLink(anyString(), anyInt());
        verify(crawler,times(1)).crawl(anyString(), anyInt());
        assertTrue(crawler.getVisitedURLs().contains(testURL));
    }

    @Test
    public void testCrawl_ValidUrl(){
        crawler= Mockito.spy(new Crawler(testURL,testDepth,testDomains,testFilePath));

        MarkdownWriter mockMarkdownWriter=mockMarkdownWriter();
        Parser mockParser=mock(Parser.class);

        Elements mockElements = new Elements();
        Element mockElement = mock(Element.class);
        mockElements.add(mockElement);
        when(mockParser.getLinks()).thenReturn(mockElements);
        testDomains.add("example.com");
        when(mockElement.attr("abs:href")).thenReturn("https://example.com/page2");
        when(crawler.linkIsBroken("https://example.com/page2")).thenReturn(false);

        doReturn(mockParser).when(crawler).createParser(anyString());

        crawler.crawl(testURL, testDepth);

        verify(mockMarkdownWriter, times(1)).writeLink(anyString(), eq(testDepth));
        verify(mockMarkdownWriter,times(1)).writeHeadings(any(),eq(testDepth));
        verify(mockMarkdownWriter, never()).writeBrokenLink(anyString(), anyInt());
        verify(crawler,times(2)).crawl(anyString(), anyInt());
        assertTrue(crawler.getVisitedURLs().contains(testURL));
    }

    @Test
    public void testCrawl_BrokenLink(){
        crawler= Mockito.spy(new Crawler(testURL,testDepth,testDomains,testFilePath));

        MarkdownWriter mockMarkdownWriter=mockMarkdownWriter();
        Parser mockParser=mock(Parser.class);

        Elements mockElements = new Elements();
        Element mockElement = mock(Element.class);
        mockElements.add(mockElement);
        when(mockParser.getLinks()).thenReturn(mockElements);
        testDomains.add("example.com");
        when(mockElement.attr("abs:href")).thenReturn("https://example.com/page2");
        when(crawler.linkIsBroken("https://example.com/page2")).thenReturn(true);

        doReturn(mockParser).when(crawler).createParser(anyString());

        crawler.crawl(testURL, testDepth);

        verify(crawler,times(1)).crawl(anyString(),anyInt());
        verify(mockMarkdownWriter, times(1)).writeBrokenLink(anyString(), eq(testDepth));
        assertTrue(crawler.getVisitedURLs().contains(testURL));
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

    @AfterEach
    public void tearDown(){
        this.crawler=null;
    }

}
