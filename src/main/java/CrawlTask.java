public class CrawlTask implements Runnable {
    private String url;
    private final String filePath;
    private MarkdownFileWriter markdownFileWriter;
    private MarkdownContentWriter contentWriter;
    private DomainMatcher domainMatcher;
    private Crawler crawler;


    public CrawlTask(String url, String filePath) {
        this.url = url;
        this.filePath = filePath;
    }

    @Override
    public void run() {
    initializeCrawlingProcess();
        writeHeader(url);
        System.out.println("Crawling " + url + " ...");
        crawlURL(url);
        System.out.println("Finished crawling " + url + ".");
    }

    private void writeHeader(String url) {
        markdownFileWriter.writeHeader(url, EntryPoint.getDepth());
    }

    private void crawlURL(String url) {
        crawler.crawl(url, 0);
        crawler.finishWritingAfterCrawling();
    }

    private void initializeCrawlingProcess() {
        markdownFileWriter = new MarkdownFileWriter(filePath);
        contentWriter = new MarkdownContentWriter(markdownFileWriter);
        domainMatcher = new DomainMatcher(EntryPoint.getDomains());
        crawler = new Crawler(EntryPoint.getDepth(), domainMatcher, contentWriter);
    }

}
