import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EntryPointTest {

    private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;
    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;


    @BeforeEach
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }

    @AfterEach
    public void restoreSystemInputOutput() {
        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);
    }

    @Test
    public void testInitializeScanner() {
        EntryPoint.initializeScanner();
        assertNotNull(EntryPoint.scanner);
    }

    @Test
    public void testPromptUserInputValid() {
        provideInput("https://example.com https://test.com\n");
        EntryPoint.initializeScanner();
        List<String> urls = EntryPoint.promptUserInput("Enter URLs:", EntryPoint.urlRegex);
        assertEquals(2, urls.size());
        assertEquals("https://example.com", urls.get(0));
        assertEquals("https://test.com", urls.get(1));
    }

    @Test
    public void testPromptUserInputInvalid() {
        provideInput("invalid-url\nhttps://example.com\n");
        EntryPoint.initializeScanner();
        List<String> urls = EntryPoint.promptUserInput("Enter URLs:", EntryPoint.urlRegex);
        assertEquals(1, urls.size());
        assertEquals("https://example.com", urls.get(0));
        assertTrue(getOutput().contains("Invalid Input!"));
    }

    @Test
    public void testPromptSingleInputValid() {
        provideInput("3\n");
        EntryPoint.initializeScanner();
        String depth = EntryPoint.promptSingleInput("Enter depth:", EntryPoint.depthRegex);
        assertEquals("3", depth);
    }

    @Test
    public void testPromptSingleInputInvalid() {
        provideInput("10\n3\n");
        EntryPoint.initializeScanner();
        String depth = EntryPoint.promptSingleInput("Enter depth:", EntryPoint.depthRegex);
        assertEquals("3", depth);
        assertTrue(getOutput().contains("Invalid Input!"));
    }

    @Test
    public void testStoreUserInputs() {
        provideInput("https://example.com\n3\nexample.com\nC:\\Users\\User\\Documents\\output.md\n");
        EntryPoint.initializeScanner();
        EntryPoint.storeUserInputs();
        assertEquals(1, EntryPoint.urls.size());
        assertEquals("https://example.com", EntryPoint.urls.get(0));
        assertEquals(3, EntryPoint.depth);
        assertEquals(1, EntryPoint.domains.size());
        assertEquals("example.com", EntryPoint.domains.get(0));
        assertEquals("C:\\Users\\User\\Documents\\output.md", EntryPoint.filePath);
    }


    @Test
    public void testPrintUserInput() {
        EntryPoint.urls = List.of("https://example.com");
        EntryPoint.depth = 2;
        EntryPoint.domains = List.of("example.com");
        EntryPoint.filePath = "output";
        EntryPoint.printUserInput();
        String output = getOutput();
        assertTrue(output.contains("https://example.com 2 example.com"));
        assertTrue(output.contains("output"));
    }

    @Test
    public void testCreateFinalMarkdown() throws Exception {
        EntryPoint.filePath = "test_output";
        EntryPoint.files = List.of("test_file1", "file2");

        File outputFile = new File(EntryPoint.filePath);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        assertFalse(outputFile.exists());

        EntryPoint.createFinalMarkdown();

        assertTrue(outputFile.exists());

        String content = new String(Files.readAllBytes(Paths.get(EntryPoint.filePath)));
        assertTrue(content.contains("Expected Content"));
    }

    @Test
    public void testCloseScanner() {
        EntryPoint.initializeScanner();
        EntryPoint.closeScanner();

        assertThrows(IllegalStateException.class, () -> {
            EntryPoint.scanner.next();
        });
    }

    @Test
    public void testJoinThreads() {
        Thread thread = new Thread();
        EntryPoint.threads.add(thread);
        EntryPoint.joinThreads();
        assertFalse(thread.isAlive());
    }

    @Test
    public void testPrintInvalidInput() {
        EntryPoint.printInvalidInput();
        assertTrue(getOutput().contains("Invalid Input!"));
    }

    @Test
    public void testStartCrawlerThreads(){
        EntryPoint.startCrawlerThreads();
        assertNotNull(EntryPoint.threads);
    }


}
