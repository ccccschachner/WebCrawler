import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Parser {
    private final String url;
    private String documentTitle;
    private Document document;
    private String[] headings;
    private Elements allAnchors;
    private final List<Element> intactAnchors = new ArrayList<>();
    private final List<Element> brokenAnchors = new ArrayList<>();
    private String[] intactUrls;
    private String[] brokenUrls;

    public Parser(String url) {
        this.url = url;
        parse();
    }

    private void parse() {
        createDocument();
        if (document != null) {
            storeHeadings();
            storeAllAnchors();
            storeUrls();
        }
    }

    private void createDocument() {
        try {
            document = Jsoup.connect(url).get();
            documentTitle = document.title();
        } catch (IOException e) {
            System.err.println("Error connecting to: " + url + "\n" + e.getMessage());//TODO Error Handling
        }
    }

    private void storeHeadings() {
        Elements headingsElements = document.select("h1, h2, h3, h4, h5, h6");
        headings = headingsElements.stream()
                .map(element -> element.tagName() + ": " + element.text())
                .toArray(String[]::new);
    }

    private void storeAllAnchors() {
        allAnchors = document.select("a");
    }

    private void storeUrls() {
        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
            List<Future<?>> futures = new ArrayList<>();

            for (Element anchor : allAnchors) {
                futures.add(executor.submit(() -> sortAnchors(anchor)));
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            executor.shutdown();
        }


        storeIntactUrls();
        storeBrokenUrls();
    }

    private void storeIntactUrls() {
        intactUrls = intactAnchors.stream()
                .map(anchor -> anchor.attr("abs:href"))
                .toArray(String[]::new);
    }

    private void storeBrokenUrls() {
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
        return !href.isEmpty() && (href.startsWith("http://") || href.startsWith("https://"));
    }

    private void sortAnchors(Element link) {
        if (hasValidHref(link)) {
            try {
                Jsoup.connect(link.attr("abs:href"))
                        .method(Connection.Method.HEAD)
                        .timeout(3000)
                        .execute();
                synchronized (intactAnchors) {
                    intactAnchors.add(link);
                }
            } catch (IOException e) {
                synchronized (brokenAnchors) {
                    brokenAnchors.add(link);
                }
            }
        }
    }
}

