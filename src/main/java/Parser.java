import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Parser {
    private String url;
    private String title;
    private Document document;
    private Elements headings;
    private Elements links;

    public Parser(String url) throws IOException {
        this.url = url;
        parse();
    }

    public void parse() throws IOException {
        createDocument();
        storeHeadings();
        storeLinks();
    }

    public void createDocument() throws IOException {
        try {
            document = Jsoup.connect(url).get();
            title = document.title();
        } catch (IOException e) {
            System.err.println("Error connecting to: " + url + "\n" + e.getMessage());
        }
    }

    public void storeHeadings() {
        headings = document.select("h1, h2, h3, h4, h5, h6");
    }

    public void storeLinks() {
        links = document.select("a[href]");
    }

    public String getTitle(){
        return title;
    }

    public Elements getHeadings(){
        return headings;
    }

    public Elements getLinks(){
        return links;
    }
}
