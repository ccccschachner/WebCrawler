import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    private final String urlValid = "https://example.com";
    private final int depthValid = 3;
    private final List<String> domainsValid = new ArrayList<>();
    private final String targetLanguageValid = "en";

    @Test
    public void testStoreUrl() {
        System.setIn(new ByteArrayInputStream(urlValid.getBytes()));
        Main.storeUrl();
        assertEquals(urlValid, Main.getUrl());
    }

    @Test
    public void testStoreDepth() {
        System.setIn(new ByteArrayInputStream((depthValid+"").getBytes()));
        Main.storeDepth();
        assertEquals(depthValid, Main.getDepth());
    }

    @Test
    public void testStoreDomains() {
        domainsValid.add("example.com");
        domainsValid.add("test.com");
        System.setIn(new ByteArrayInputStream(String.join(" ", domainsValid).getBytes()));
        Main.storeDomains();
        assertEquals(domainsValid, Main.getDomains());
    }

    @Test
    public void testStoreTargetLanguage() {
        System.setIn(new ByteArrayInputStream(targetLanguageValid.getBytes()));
        Main.storeTargetLanguage();
        assertEquals(targetLanguageValid, Main.getTargetLanguage());
    }
}
