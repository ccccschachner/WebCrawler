import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private final int depth;
    private final List<String> visitedURLs;
    private final List<String> domains;
    private MarkdownWriter markdownWriter;

    public Crawler(String url,int depth,List<String> domains,String filePath){
        this.depth=depth;
        this.domains=domains;
        this.visitedURLs=new ArrayList<>();
        this.markdownWriter=new MarkdownWriter(filePath);
        this.markdownWriter.writeHeader(url,depth);
    }

    public void crawl(String url, int currentDepth) {
        if(shouldCrawl(url, currentDepth)){
            Parser parser=createParser(url);
            visitedURLs.add(url);

            writeContentOfPageToMarkdown(parser,url,currentDepth);
            crawlChildLinks(parser,currentDepth);
        }
    }

    void writeContentOfPageToMarkdown(Parser parser,String url, int currentDepth){
        markdownWriter.writeLink(url,currentDepth);
        Elements headings=parser.getHeadings();
        markdownWriter.writeHeadings(headings,currentDepth);
    }
    private void crawlChildLinks(Parser parser, int currentDepth) {
        Elements links = parser.getIntactLinks();
        for (Element link : links) {
            String nextUrl = link.attr("abs:href");
            if (matchesDomain(nextUrl)) {
                if (!linkIsBroken(nextUrl)) {
                    crawl(nextUrl, currentDepth + 1);
                } else {
                    markdownWriter.writeBrokenLink(nextUrl, currentDepth);
                }
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

    boolean shouldCrawl(String url, int currentDepth){
        return currentDepth<=depth && !visitedURLs.contains(url);
    }

    boolean linkIsBroken(String url) {
        try {
            Jsoup.connect(url).get();
            return false;
        } catch (IOException e) {
            System.out.println("Error checking link: " + url + ": " + e.getMessage());
            return true;
        }
    }

    public void finishCrawling(){
        markdownWriter.closeWriter();
    }

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
