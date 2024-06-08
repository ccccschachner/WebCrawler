public class CrawlTask implements Runnable {
    private String url;


    public CrawlTask(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        EntryPoint.writeHeader(url);
        System.out.println("Crawling " + url + " ...");
        EntryPoint.crawlURL(url);
        System.out.println("Finished crawling " + url + ".");
    }

}
