import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Crawler {
    private int depth;
    private List<String> visitedURLs;
    private List<String> domains;
    private List<Parser> parsers;
    private Map<Integer,String> brokenLinks;

    public Crawler(int depth, List<String> domains){
        this.depth=depth;
        visitedURLs=new ArrayList<>();
        this.domains=domains;
        parsers=new ArrayList<>();
        brokenLinks = new HashMap<>();
    }

    public void crawl(String url, int currentDepth) {
        if(!(currentDepth>depth || visitedURLs.contains(url))){
            Parser parser=new Parser(url);
            parsers.add(parser);
            Elements links=parser.getLinks();
            visitedURLs.add(url);

            for(Element link:links){
                String nextUrl = link.attr("abs:href");
                boolean linkIsBroken=isBroken(nextUrl);
                if(linkIsBroken) {
                    brokenLinks.put(currentDepth, nextUrl);
                }else{
                    for (String domain : domains) {
                        if (nextUrl.startsWith(domain)) {
                            crawl(nextUrl, currentDepth + 1);
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean isBroken(String url) {
        try {
            int statusCode = Jsoup.connect(url).ignoreContentType(true).execute().statusCode();
            return statusCode >= 200 && statusCode < 400;
        } catch (IOException e) {
            System.out.println("Error checking link: " + url + ": " + e.getMessage());
            return false;
        }
    }

    public List<String> getVisitedURLs(){
        return this.visitedURLs;
    }

    public int getDepth() {
        return depth;
    }

    public List<String> getDomains() {
        return domains;
    }

    public List<Parser> getParsers() {
        return parsers;
    }
    public Map<Integer,String> getBrokenLinks() {
        return brokenLinks;
    }

}
