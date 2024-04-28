import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainTest {

    private final String urlValid = "https://example.com";
    private final int depthValid = 3;
    private final List<String> domainsValid = new ArrayList<>();
    private final String targetLanguageValid = "en";
    private final String filePathValid="C:\\Users\\user\\Documents\\output.md";
    private final String filePathInvalid="user\\Documents\\output.txt";


    @Test
    public void testStoreUrlValid() {
        Main.scanner = new Scanner(new ByteArrayInputStream(urlValid.getBytes()));
        Main.storeUrl();
        assertEquals(urlValid, Main.getUrl());
    }

    @Test
    public void testStoreDepthValid() {
        Main.scanner = new Scanner(new ByteArrayInputStream((depthValid + "\n").getBytes()));
        Main.storeDepth();
        assertEquals(depthValid, Main.getDepth());
    }

    @Test
    public void testStoreDomainsValid() {
        domainsValid.add("test.com");
        domainsValid.add("example.at");
        Main.scanner = new Scanner(new ByteArrayInputStream(String.join(" ", domainsValid).getBytes()));
        Main.storeDomains();
        assertEquals(domainsValid, Main.getDomains());
    }

    @Test
    public void testStoreTargetLanguageValid() {
        Main.scanner = new Scanner(new ByteArrayInputStream(targetLanguageValid.getBytes()));
        Main.storeTargetLanguage();
        assertEquals(targetLanguageValid, Main.getTargetLanguage());
    }

    @Test
    public void testStoreFilePathValid() {
        Main.scanner = new Scanner(new ByteArrayInputStream(filePathValid.getBytes()));
        Main.storeFilePath();
        assertEquals(filePathValid, Main.getFilePath());
    }

    @Test
    public void testStoreFilePathInvalid() {
        Main.scanner = new Scanner(new ByteArrayInputStream(filePathInvalid.getBytes()));
        Main.storeFilePath();
        assertEquals(filePathInvalid, Main.getFilePath());
    }

    @Test
    public void testCrawlURL(){
        String url = "http://example.com";

        Crawler crawlerMock=mock(Crawler.class);
        Main.setCrawler(crawlerMock);
        doNothing().when(crawlerMock).crawl(url, 0);
        doNothing().when(crawlerMock).finishCrawling();

        Main.crawlURL(url);

        verify(crawlerMock).crawl(url, 0);
        verify(crawlerMock).finishCrawling();
    }

}
