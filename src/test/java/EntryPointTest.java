import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import static org.junit.jupiter.api.Assertions.*;
public class EntryPointTest {

    private final String urlValid = "https://example.com";
    private final int depthValid = 3;
    private final List<String> domainsValid = new ArrayList<>();
    private final String filePathValid = "C:\\Users\\user\\Documents\\output.md";
    private final String filePathInvalid = "user\\Documents\\output.txt";
    private final String domainValid = "example.com";
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;


    @BeforeEach
    public void setUp() {
        EntryPoint.threads.clear();
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDownOut() {
        System.setOut(originalOut);
        EntryPoint.threads.clear();
        EntryPoint.setFiles(new ArrayList<>());
    }


    @Test
    public void testStoreUrlsValid() {
        EntryPoint.scanner = new Scanner(new ByteArrayInputStream(urlValid.getBytes()));
        EntryPoint.storeUrls();
        assertEquals(urlValid, EntryPoint.getUrls().get(0));
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
        EntryPoint.setDomains(domainsValid);
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
    public void testStartCrawlerThreads() throws InterruptedException {
        String url1 = "http://example.com";
        String url2 = "http://example1.com";
        List<String> urls = new ArrayList<>();
        urls.add(url1);
        urls.add(url2);
        String filePath = "output";

        EntryPoint.setUrls(urls);
        EntryPoint.setFilePath(filePath);

        EntryPoint.startCrawlerThreads();

        assertEquals(2, EntryPoint.threads.size());

        for (Thread thread : EntryPoint.threads) {
            thread.join();
        }

        assertEquals(2, EntryPoint.getFiles().size());
        assertTrue(EntryPoint.getFiles().contains("output_1"));
        assertTrue(EntryPoint.getFiles().contains("output_2"));
    }


    @Test
    public void testCloseScanner() {
        EntryPoint.scanner = new Scanner(System.in);
        EntryPoint.closeScanner();

        assertThrows(IllegalStateException.class, () -> {
            EntryPoint.scanner.next();
        });
    }

    @Test
    public void testJoinThreads() {
        Thread thread1 = new Thread(() -> {});
        Thread thread2 = new Thread(() -> {});
        EntryPoint.threads.add(thread1);
        EntryPoint.threads.add(thread2);

        thread1.start();
        thread2.start();

        EntryPoint.joinThreads();

        assertEquals(Thread.State.TERMINATED, thread1.getState());
        assertEquals(Thread.State.TERMINATED, thread2.getState());
    }

    @Test
    public void testPrintUserInput() {
        EntryPoint.setFilePath(filePathValid);
        EntryPoint.setUrls(List.of(urlValid));
        EntryPoint.setDomains(List.of(domainValid));

        EntryPoint.printUserInput();
        assertTrue(outContent.toString().startsWith("\nThe markdown file based on your inputs"));
        assertTrue(outContent.toString().contains(filePathValid));
        assertTrue(outContent.toString().contains(urlValid));
        assertTrue(outContent.toString().contains(domainValid));
    }

    @Test
    public void testGetListFromScannerInput(){
        String testScannerInput="Test1 Test2 Test3";
        List<String> expectedList=List.of("Test1","Test2","Test3");

        List<String> receivedList=EntryPoint.getListFromScannerInput(testScannerInput);

        assertEquals(expectedList,receivedList);
    }

}




