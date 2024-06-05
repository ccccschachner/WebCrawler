import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Parser {
    private final String url;
    private String documentTitle;
    private Document document;
    private Elements headings;
    private Elements allLinks;
    private Elements intactLinks = new Elements();
    private Elements brokenLinks = new Elements();

    public Parser(String url) {
        this.url = url;
        parse();
    }

    private void parse() {
        createDocument();
        storeHeadings();
        storeLinks();
        checkLinks();
    }

    public void createDocument() {
        try {
            document = Jsoup.connect(url).get();
            documentTitle = document.title();
        } catch (IOException e) {
            System.err.println("Error connecting to: " + url + "\n" + e.getMessage());
        }
    }

    public void storeHeadings() {
        headings = document.select("h1, h2, h3, h4, h5, h6");
    }

    public void storeLinks() {
        allLinks = document.select("a");
    }

    public void checkLinks() {
        for (Element link : allLinks) {
            String url = link.attr("abs:href");

            try{
                Jsoup.connect(url);
                intactLinks.add(link);
            }
            catch (Exception e){
                brokenLinks.add(link);
            }
        }
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public Elements getHeadings() {
        return headings;
    }

    public Elements getIntactLinks() {
        return intactLinks;
    }

    public Elements getBrokenLinks() {
        return brokenLinks;
    }
}
