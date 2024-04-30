import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
        if(continueCrawling(url, currentDepth)){
            Parser parser=createParser(url);
            visitedURLs.add(url);

            markdownWriter.writeLink(url,currentDepth);
            Elements headings=parser.getHeadings();
            markdownWriter.writeHeadings(headings,currentDepth);

            Elements links=parser.getLinks();
            for(Element link:links){
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
    }

    boolean matchesDomain(String url) {
        try {
            URL u = new URL(url);
            String host = u.getHost();
            return domains.stream().anyMatch(host::endsWith);
        } catch (IOException e) {
            return false;
        }
    }
    Parser createParser(String url) {
        return new Parser(url);
    }

    boolean continueCrawling(String url, int currentDepth){
        return currentDepth<=depth && !visitedURLs.contains(url);
    }

    String getDomainFromURL(String url){
        try{
            URI uri=new URI(url);
            return uri.getHost();
        }catch (URISyntaxException e){
            System.out.println("Invalid URL: " + url+" URL cannot be converted to URI");
        }
        return null;
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
