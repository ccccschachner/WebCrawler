import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EntryPointTest {

    private final String urlValid = "https://example.com";
    private final int depthValid = 3;
    private final List<String> domainsValid = new ArrayList<>();
    private final String filePathValid="C:\\Users\\user\\Documents\\output.md";
    private final String filePathInvalid="user\\Documents\\output.txt";


    @Test
    public void testStoreUrlValid() {
        EntryPoint.scanner = new Scanner(new ByteArrayInputStream(urlValid.getBytes()));
        EntryPoint.storeUrl();
        assertEquals(urlValid, EntryPoint.getUrl());
    }

    @Test
    public void testStoreDepthValid() {
        EntryPoint.scanner = new Scanner(new ByteArrayInputStream((depthValid + "\n").getBytes()));
        EntryPoint.storeDepth();
        assertEquals(depthValid, EntryPoint.getDepth());
    }

    @Test
    public void testStoreDomainsValid() {
        domainsValid.add("test.com");
        domainsValid.add("example.at");
        EntryPoint.scanner = new Scanner(new ByteArrayInputStream(String.join(" ", domainsValid).getBytes()));
        EntryPoint.storeDomains();
        assertEquals(domainsValid, EntryPoint.getDomains());
    }

    @Test
    public void testStoreFilePathValid() {
        EntryPoint.scanner = new Scanner(new ByteArrayInputStream(filePathValid.getBytes()));
        EntryPoint.storeFilePath();
        assertEquals(filePathValid, EntryPoint.getFilePath());
    }

    @Test
    public void testStoreFilePathInvalid() {
        EntryPoint.scanner = new Scanner(new ByteArrayInputStream(filePathInvalid.getBytes()));
        EntryPoint.storeFilePath();
        assertEquals(filePathInvalid, EntryPoint.getFilePath());
    }

    @Test
    public void testCrawlURL(){
        String url = "http://example.com";

        Crawler crawlerMock=mock(Crawler.class);
        EntryPoint.setCrawler(crawlerMock);
        doNothing().when(crawlerMock).crawl(url, 0);
        doNothing().when(crawlerMock).finishCrawling();

        EntryPoint.crawlURL(url);

        verify(crawlerMock).crawl(url, 0);
        verify(crawlerMock).finishCrawling();
    }

}
