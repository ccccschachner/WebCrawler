import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private final int depth;
    private final List<String> visitedURLs;
    private final List<String> domains;
    private MarkdownWriter markdownWriter;

    public Crawler(String url,int depth,List<String> domains,MarkdownWriter markdownWriter){
        this.depth=depth;
        this.domains=domains;
        this.visitedURLs=new ArrayList<>();
        this.markdownWriter=markdownWriter;
        this.markdownWriter.writeHeader(url,depth);
    }

    public void crawl(String url, int currentDepth) {
        if(shouldContinueCrawling(url, currentDepth)){
            Parser parser=createParser(url);
            visitedURLs.add(url);

            writeContentOfPageToMarkdown(parser,url,currentDepth);
            crawlChildLinks(parser,currentDepth);
            writeBrokenLinks(parser,currentDepth);
        }
    }

    private void writeBrokenLinks(Parser parser,int currentDepth) {
        String[] brokenLinks=parser.getBrokenUrls();
        for (String brokenLink : brokenLinks) {
            markdownWriter.writeBrokenLink(brokenLink, currentDepth);
        }
    }

    void writeContentOfPageToMarkdown(Parser parser,String url, int currentDepth){
        markdownWriter.writeLink(url, currentDepth);
        String[] headings = parser.getHeadings();
        markdownWriter.writeHeadings(headings, currentDepth);
    }
    private void crawlChildLinks(Parser parser, int currentDepth) {
        String[] intactUrls = parser.getIntactUrls();
        for (String url : intactUrls) {
            if (matchesDomain(url)) {
                crawl(url, currentDepth + 1);
            }
        }
    }

    boolean matchesDomain(String url) {
        try {
            String host = new URL(url).getHost();
            return domains.stream().anyMatch(host::endsWith);
        } catch (IOException e) {
            return false;
        }
    }
    Parser createParser(String url) {
        return new Parser(url);
    }

    boolean shouldContinueCrawling(String url, int currentDepth){
        return currentDepth<=depth && !visitedURLs.contains(url);
    }

    public void finishWritingAfterCrawling(){markdownWriter.closeWriter();}

    void addVisitedUrl(String url){
        visitedURLs.add(url);
    }

    public List<String> getVisitedURLs() {
        return visitedURLs;
    }

    void setMarkdownWriter(MarkdownWriter markdownWriter){
        this.markdownWriter=markdownWriter;
    }
}
