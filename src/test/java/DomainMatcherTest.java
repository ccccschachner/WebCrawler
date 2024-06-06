import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class DomainMatcherTest {

    private DomainMatcher domainMatcher;
    List<String> testDomains;

    @BeforeEach
    public void setUp() {
        testDomains = Arrays.asList("example.com", "test.org", "mysite.net");
        domainMatcher = new DomainMatcher(testDomains);
    }

    static Stream<String> validUrls() {
        return Stream.of(
                "http://www.example.com",
                "https://subdomain.example.com/path",
                "http://test.org",
                "http://www.test.org",
                "https://mysite.net",
                "http://subdomain.mysite.net",
                "ftp://example.com"
        );
    }

    @ParameterizedTest
    @MethodSource("validUrls")
    public void testMatchesDomainWithValidDomains(String url) {
        assertTrue(domainMatcher.matchesDomain(url));
    }

    static Stream<String> invalidUrls() {
        return Stream.of(
                "http://unknownsite.com",
                "http://example.com.invalid"
        );
    }

    @ParameterizedTest
    @MethodSource("invalidUrls")
    public void testMatchesDomainWithInvalidDomains(String url) {
        assertFalse(domainMatcher.matchesDomain(url));
    }


    @Test
    public void testMatchesDomainWithInvalidURL() {
        assertFalse(domainMatcher.matchesDomain("invalid-url"));
    }

    @Test
    public void testMatchesDomainWithNullURL() {
        assertFalse(domainMatcher.matchesDomain(null));
    }

    @AfterEach
    public void tearDown(){
        testDomains=null;
        domainMatcher=null;
    }

}
