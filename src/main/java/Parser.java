import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class Parser {
    private final String url;
    private String documentTitle;
    private Document document;
    private String[] headings;
    private Elements allAnchors;
    private Elements intactAnchors = new Elements();
    private Elements brokenAnchors = new Elements();
    private String[] intactUrls;
    private String[] brokenUrls;

    public Parser(String url) {
        this.url = url;
        parse();
    }

    private void parse() {
        createDocument();
        storeHeadings();
        storeAllAnchors();
        storeIntactUrls();
        storeBrokenUrls();
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
        headings = new String[]{document.select("h1, h2, h3, h4, h5, h6").toString()};
    }

    public void storeAllAnchors() {
        allAnchors = document.select("a");
    }

    public void storeIntactUrls() {
        for(Element anchor: allAnchors){
            if(checkElement(anchor)){
                intactAnchors.add(anchor);
            }
        }

        intactUrls = new String[]{intactAnchors.toString()};
    }

    public void storeBrokenUrls() {
        for(Element anchor: allAnchors){
            if(!checkElement(anchor)){
                brokenAnchors.add(anchor);
            }
        }

        brokenUrls = new String[]{brokenAnchors.toString()};
    }


    public String getDocumentTitle() {
        return documentTitle;
    }

    public String[] getHeadings() {
        return headings;
    }

    public String[] getIntactUrls() {
        return intactUrls;
    }

    public String[] getBrokenUrls() {
        return brokenUrls;
    }

    public boolean checkElement(Element link) {
        try {
            Jsoup.connect(link.attr("abs:href")).get();
            return true;
        } catch (Exception e) {
            System.out.println("Error checking link: " + url + ": " + e.getMessage());
            return false;
        }
    }
}
