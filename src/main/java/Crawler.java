import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private final int depth;
    private final List<String> visitedURLs;
    private final MarkdownContentWriter contentWriter;
    private final DomainMatcher domainMatcher;

    public Crawler(int depth,DomainMatcher domainMatcher, MarkdownContentWriter contentWriter){
        this.depth=depth;
        this.visitedURLs=new ArrayList<>();
        this.contentWriter=contentWriter;
        this.domainMatcher=domainMatcher;
    }

    public void crawl(String url, int currentDepth) {
        if(shouldContinueCrawling(url, currentDepth)){
            Parser parser=createParser(url);
            visitedURLs.add(url);

            contentWriter.writeContentOfPageToMarkdown(parser,url,currentDepth);
            contentWriter.writeBrokenLinks(parser,currentDepth);
            crawlChildLinks(parser,currentDepth);
        }
    }

    void crawlChildLinks(Parser parser, int currentDepth) {
        String[] intactUrls = parser.getIntactUrls();
        for (String url : intactUrls) {
            if (matchesDomain(url)) {
                crawl(url, currentDepth + 1);
            }
        }
    }
    //tested
    boolean matchesDomain(String url) {
        return domainMatcher.matchesDomain(url);
    }
    Parser createParser(String url) {
        return new Parser(url);
    }
    //tested
    boolean shouldContinueCrawling(String url, int currentDepth){
        return currentDepth<=depth && !visitedURLs.contains(url);
    }
    public List<String> getVisitedURLs() {
        return visitedURLs;
    }
    //tested
    public void addVisitedUrl(String url) {
        visitedURLs.add(url);
    }
}
