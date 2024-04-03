import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private final String urlValid = "https://example.com";
    private final String urlInvalid = "example.com";
    private final int depthValid = 3;
    private final List<String> domainsValid = new ArrayList<>();
    private final String targetLanguageValid = "en";

    @Test
    public void testStoreUrlValid() {
        Main.scanner = new Scanner(new ByteArrayInputStream(urlValid.getBytes()));
        Main.storeUrl();
        assertEquals(urlValid, Main.getUrl());
    }

    @Test
    public void testStoreDepthValid() {
        Main.scanner = new Scanner(new ByteArrayInputStream((depthValid+"\n").getBytes()));
        Main.storeDepth();
        assertEquals(depthValid, Main.getDepth());
    }

    @Test
    public void testStoreDomainsValid() {
        domainsValid.add("example.com");
        domainsValid.add("test.com");
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
}
