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
        if(currentDepth<=depth && !visitedURLs.contains(url)){
            Parser parser=new Parser(url);
            visitedURLs.add(url);
            markdownWriter.writeDocument(parser,currentDepth);
            Elements links=parser.getLinks();

            for(Element link:links){
                String nextUrl = link.attr("abs:href");
                boolean linkIsBroken=isBroken(nextUrl);
                if(linkIsBroken) {
                    markdownWriter.writeBrokenLink(nextUrl,currentDepth);
                }else{
                    for (String domain : domains) {
                        try {
                            URI uri = new URI(nextUrl);
                            String domainFromUrl = uri.getHost();
                            if (domainFromUrl != null && domainFromUrl.equals(domain)) {
                                crawl(nextUrl, currentDepth + 1);
                                break;
                            }
                        } catch (URISyntaxException e) {
                            System.out.println("Invalid URL: " + nextUrl);
                        }
                    }
                }
            }
        }
    }

    private boolean isBroken(String url) {
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
