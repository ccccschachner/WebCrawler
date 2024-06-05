import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Parser {
    private final String url;
    private String documentTitle;
    private Document document;
    private Elements headings;
    private Elements links;

    public Parser(String url) {
        this.url = url;
        parse();
    }

    private void parse(){
        createDocument();
        storeHeadings();
        storeLinks();
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
        links = document.select("a");
    }
    //Todo: store broken links

    public String getDocumentTitle(){
        return documentTitle;
    }

    public Elements getHeadings(){
        return headings;
    }

    public Elements getLinks(){
        return links;
    }
}
