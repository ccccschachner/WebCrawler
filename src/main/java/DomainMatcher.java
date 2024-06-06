import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DomainMatcher {
    private final List<String> domains;

    public DomainMatcher(List<String> domains) {
        this.domains = domains;
    }

    public boolean matchesDomain(String url) {
        try {
            String host = new URL(url).getHost();
            return domains.stream().anyMatch(host::endsWith);
        } catch (IOException e) {
            return false;
        }
    }
}
