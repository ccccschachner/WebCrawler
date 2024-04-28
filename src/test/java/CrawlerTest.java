import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CrawlerTest {

    private Crawler crawler;
    private String testFilePath = "src/test/CrawlerTest.md";
    private String testURL;
    private int testDepth;
    private List<String> testDomains;
    private String testTargetLanguage;


    @BeforeEach
    public void setUp(){
        crawler=new Crawler(testURL,testDepth,testDomains,testFilePath,testTargetLanguage);
    }
    @Test
    public void testContinueCrawling_CurrentDepthSmallerDepth(){

    }

    @Test
    public void testGetDomainFromURLWithValidURL(){
        String domain="de.wikipedia.org";
        String domainFromUrl= crawler.getDomainFromURL("https://de.wikipedia.org/wiki/Wikipedia:Hauptseite");
        assertEquals(domainFromUrl,domain);
    }

    @Test
    public void testGetDomainFromURLWithInvalidURL(){
        String domainFromUrl= crawler.getDomainFromURL("de.wikipedia.org/wiki/Wikipedia:Hauptseite");
        assertNull(domainFromUrl);
    }

    @Test
    public void testCompareIfDomainMatchesTrue(){
        String domain="de.wikipedia.org";
        String domainFromUrl= crawler.getDomainFromURL("https://de.wikipedia.org/wiki/Wikipedia:Hauptseite");
        assertTrue(crawler.compareIfDomainMatches(domainFromUrl,domain));
    }

    @Test
    public void testCompareIfDomainMatchesFalse(){
        String domain="de.wikipedia.org";
        String domainFromUrl= crawler.getDomainFromURL("https://en.wikipedia.org/wiki/Main_Page");
        assertFalse(crawler.compareIfDomainMatches(domainFromUrl,domain));
    }

    @Test
    public void testCompareIfDomainMatchesDomainFromURLIsNULL(){
        String domain="de.wikipedia.org";
        assertFalse(crawler.compareIfDomainMatches(null,domain));
    }

    @Test
    public void testLinkIsBrokenTrue(){
        String brokenLink="https://example.com/not_found";
        assertTrue(crawler.linkIsBroken(brokenLink));
    }

    @Test
    public void testLinkIsBrokenFalse(){
        String notBrokenLink="https://www.wikipedia.org";
        assertFalse(crawler.linkIsBroken(notBrokenLink));
    }

    @Test
    void testFinishCrawling() {
        MarkdownWriter mockMarkdownWriter = mock(MarkdownWriter.class);
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(testFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mockMarkdownWriter.setWriter(fileWriter);

        crawler.finishCrawling();

        verify(mockMarkdownWriter).closeWriter();
    }

    @AfterEach
    public void tearDown(){
        this.crawler=null;
    }

}
