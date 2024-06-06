import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private final int depth;
    private final List<String> visitedURLs;
    private MarkdownContentWriter contentWriter;
    private DomainMatcher domainMatcher;

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
            crawlChildLinks(parser,currentDepth);
            contentWriter.writeBrokenLinks(parser,currentDepth);
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

    boolean matchesDomain(String url) {
        return domainMatcher.matchesDomain(url);
    }
    Parser createParser(String url) {
        return new Parser(url);
    }

    boolean shouldContinueCrawling(String url, int currentDepth){
        return currentDepth<=depth && !visitedURLs.contains(url);
    }

    public void finishWritingAfterCrawling(){contentWriter.closeMarkDownContentWriter();}

    void addVisitedUrl(String url){
        visitedURLs.add(url);
    }

    public List<String> getVisitedURLs() {
        return visitedURLs;
    }
}
