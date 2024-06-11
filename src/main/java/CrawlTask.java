import java.util.List;

public class CrawlTask implements Runnable {
    private final String url;
    private final String filePath;
    private final int depth;
    private final List<String> domains;
    private MarkdownFileWriter markdownFileWriter;
    private MarkdownContentWriter contentWriter;
    private DomainMatcher domainMatcher;
    private Crawler crawler;


    public CrawlTask(String url, String filePath, int depth, List<String> domains) {
        this.url = url;
        this.filePath = filePath;
        this.depth=depth;
        this.domains=domains;
    }

    @Override
    public void run() {
    initializeCrawlingProcess();
        writeHeader(url);
        System.out.println("Crawling " + url + " ...");
        crawlURL(url);
        System.out.println("Finished crawling " + url + ".");
    }

    public void writeHeader(String url) {
        markdownFileWriter.writeHeader(url, depth);
    }

    public void crawlURL(String url) {
        crawler.crawl(url, 0);
        finishWritingAfterCrawling();
    }

    public void initializeCrawlingProcess() {
        markdownFileWriter = new MarkdownFileWriter(filePath);
        contentWriter = new MarkdownContentWriter(markdownFileWriter);
        domainMatcher = new DomainMatcher(domains);
        crawler = new Crawler(depth, domainMatcher, contentWriter);
    }

    public void finishWritingAfterCrawling(){contentWriter.closeMarkDownContentWriter();}

    public String getURL() {
        return url;
    }

    public String getFilePath() {
        return filePath;
    }

    public MarkdownFileWriter getMarkdownFileWriter() {
        return markdownFileWriter;
    }

    public MarkdownContentWriter getContentWriter() {
        return contentWriter;
    }

    public Crawler getCrawler() {
        return crawler;
    }

    public DomainMatcher getDomainMatcher() {
        return domainMatcher;
    }

    public void setCrawler(Crawler crawler) {
        this.crawler = crawler;
    }

    public void setDomainMatcher(DomainMatcher domainMatcher) {
        this.domainMatcher = domainMatcher;
    }

    public void setContentWriter(MarkdownContentWriter contentWriter) {
        this.contentWriter = contentWriter;
    }

    public void setMarkdownFileWriter(MarkdownFileWriter markdownFileWriter) {
        this.markdownFileWriter = markdownFileWriter;
    }
}
