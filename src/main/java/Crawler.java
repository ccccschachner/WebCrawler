import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private int depth;
    private List<String> visitedURLs;
    private List<String> domains;
    private MarkdownWriter markdownWriter;

    public Crawler(){
        this.depth=Main.getDepth();
        this.visitedURLs=new ArrayList<>();
        this.domains=Main.getDomains();
        this.markdownWriter=new MarkdownWriter();
    }

    public void crawl(String url, int currentDepth) {
        if(continueCrawling(url,currentDepth)){
            Parser parser=new Parser(url);
            visitedURLs.add(url);
            markdownWriter.writeInDocument(parser,currentDepth);

            Elements links=parser.getLinks();
            for(Element link:links){
                String nextUrl = link.attr("abs:href");
                if(linkIsBroken(nextUrl)) {
                    markdownWriter.writeBrokenLink(nextUrl,currentDepth);
                }else{
                    continueCrawlingIfMatchingDomain(url, currentDepth);
                }
            }
        }
    }

    boolean continueCrawling(String url, int currentDepth){
        return currentDepth<=depth && !visitedURLs.contains(url);
    }

    void continueCrawlingIfMatchingDomain(String url, int currentDepth) {
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
            int statusCode = Jsoup.connect(url).ignoreContentType(true).execute().statusCode();
            return statusCode==404;
        } catch (IOException e) {
            System.out.println("Error checking link: " + url + ": " + e.getMessage());
            return false;
        }
    }

    public List<String> getVisitedURLs(){
        return this.visitedURLs;
    }

}
