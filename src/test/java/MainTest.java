import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MainTest {

    private Main main;

    private final String urlValid = "https://example.com";
    private final String urlInvalid = ".com";
    private final int depthValid = 3;
    private final int depthInvalid = 500;
    private final List<String> domainsValid = new ArrayList<>();
    private final List<String> domainsInvalid = new ArrayList<>();
    private final String targetLanguageValid = "en";
    private final String targetLanguageInvalid = "english";

    private final String urlSysOut = "Please enter the URL you want to crawl (e.g. https://example.com):";
    private final String depthSysOut = "Please enter the depth of websites to crawl (1-5):";
    private final String domainsSysOut = "Please enter domains to be crawled, seperated by a space:";
    private final String targetLanguageSysOut = "Please enter the target language in ISO-2 format:";

    //TODO: find better testcases for invalid input?
    @Test
    public void testStoreUrlValid() {
        Main.scanner = new Scanner(new ByteArrayInputStream(urlValid.getBytes()));
        Main.storeUrl();
        assertEquals(urlValid, Main.getUrl());
    }

    @Test
    public void testStoreUrlInvalid() {
        Main.scanner = new Scanner(new ByteArrayInputStream(urlInvalid.getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(outputStream);
        System.setOut(newOut);
        Thread thread = new Thread(() -> {
            while (true) {
                Main.storeUrl();
            }
        });
        thread.start();
        try {
            TimeUnit.MILLISECONDS.sleep(500);
            thread.interrupt();
            String capturedOutput = outputStream.toString();
            assertTrue(capturedOutput.startsWith(urlSysOut));

        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testStoreDepthValid() {
        Main.scanner = new Scanner(new ByteArrayInputStream((depthValid + "\n").getBytes()));
        Main.storeDepth();
        assertEquals(depthValid, Main.getDepth());
    }

    @Test
    public void testStoreDepthInvalid() {
        Main.scanner = new Scanner(new ByteArrayInputStream((depthInvalid + "\n").getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(outputStream);
        System.setOut(newOut);
        Thread thread = new Thread(() -> {
            while (true) {
                Main.storeDepth();
            }
        });
        thread.start();
        try {
            TimeUnit.MILLISECONDS.sleep(500);
            thread.interrupt();
            String capturedOutput = outputStream.toString();
            assertTrue(capturedOutput.startsWith(depthSysOut));

        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
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
    public void testStoreDomainsInvalid() {
        domainsInvalid.add("§!432109");
        domainsInvalid.add(",,,");
        Main.scanner = new Scanner(new ByteArrayInputStream(String.join(" ", domainsInvalid).getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(outputStream);
        System.setOut(newOut);
        Thread thread = new Thread(() -> {
            while (true) {
                Main.storeDomains();
            }
        });
        thread.start();
        try {
            TimeUnit.MILLISECONDS.sleep(500);
            thread.interrupt();
            String capturedOutput = outputStream.toString();
            assertTrue(capturedOutput.startsWith(domainsSysOut));
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }


    @Test
    public void testStoreTargetLanguageValid() {
        Main.scanner = new Scanner(new ByteArrayInputStream(targetLanguageValid.getBytes()));
        Main.storeTargetLanguage();
        assertEquals(targetLanguageValid, Main.getTargetLanguage());
    }

    @Test
    public void testStoreTargetLanguageInvalid() {
        Main.scanner = new Scanner(new ByteArrayInputStream(targetLanguageInvalid.getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(outputStream);
        System.setOut(newOut);
        Thread thread = new Thread(() -> {
            while (true) {
                Main.storeTargetLanguage();
            }
        });
        thread.start();
        try {
            TimeUnit.MILLISECONDS.sleep(500);
            thread.interrupt();
            String capturedOutput = outputStream.toString();
            assertTrue(capturedOutput.startsWith(targetLanguageSysOut));
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
}
