import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;


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
        Elements headingsElements = document.select("h1, h2, h3, h4, h5, h6");
        List<String> headingsList = headingsElements.stream()
                .map(element -> element.tagName() + ": " + element.text())
                .toList();

        headings = headingsList.toArray(new String[0]);
    }


    public void storeAllAnchors() {
        allAnchors = document.select("a");
        for (Element anchor : allAnchors) {
            sortAnchor(anchor);
        }
    }

    public void storeIntactUrls() {
        intactUrls = intactAnchors.stream()
                .map(anchor -> anchor.attr("abs:href"))
                .toArray(String[]::new);

    }

    public void storeBrokenUrls() {
        brokenUrls = brokenAnchors.stream()
                .map(anchor -> anchor.attr("abs:href"))
                .toArray(String[]::new);
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

    private boolean hasValidHref(Element link) {
        String href = link.attr("abs:href");
        return !href.isEmpty();
    }


    public void sortAnchor(Element link) {
        if(hasValidHref(link)) {
            try {
                Jsoup.connect(link.attr("abs:href"))
                        .method(Connection.Method.HEAD)
                        .timeout(2000)
                        .execute();
                intactAnchors.add(link);

            } catch (IOException e) {
                System.out.println("Error checking link: "  + e.getMessage());
                brokenAnchors.add(link);
            }
        }
    }
}
