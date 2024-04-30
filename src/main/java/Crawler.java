import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private final int depth;
    private final List<String> visitedURLs;
    private final List<String> domains;
    private MarkdownWriter markdownWriter;

    public Crawler(String url,int depth,List<String> domains,String filePath){
        this.depth=depth;
        this.visitedURLs=new ArrayList<>();
        this.domains=domains;
        this.markdownWriter=new MarkdownWriter(filePath);
        this.markdownWriter.writeHeader(url,depth);
    }

    public void crawl(String url, int currentDepth) {
        if(continueCrawling(url,currentDepth)){
            Parser parser=createParser(url);
            addVisitedUrl(url);
            markdownWriter.writeInDocument(parser,currentDepth);

            Elements links=parser.getLinks();
            for(Element link:links){
                String nextUrl = link.attr("abs:href");
                if(linkIsBroken(nextUrl)) {
                    markdownWriter.writeBrokenLink(nextUrl,currentDepth);
                }else{
                    continueCrawlingMatchingDomain(url, currentDepth);
                }
            }
        }
    }
    Parser createParser(String url) {
        return new Parser(url);
    }

    boolean continueCrawling(String url, int currentDepth){
        return currentDepth<=depth && !visitedURLs.contains(url);
    }

    void continueCrawlingMatchingDomain(String url, int currentDepth) {
        for (String domain : domains) {
            String domainFromUrl=getDomainFromURL(url);
            if (compareIfDomainMatches(domainFromUrl,domain)) {
                crawl(url, currentDepth + 1);
                return;
            }
        }
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

    boolean compareIfDomainMatches(String domainFromURL,String domain){
        return domainFromURL != null && domainFromURL.equals(domain);
    }

    boolean linkIsBroken(String url) {
        try {
            Jsoup.connect(url).ignoreContentType(true).execute().statusCode();
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
