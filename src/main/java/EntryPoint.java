import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EntryPoint {
    private static String url;
    private static int depth;
    private static List<String> domains = new ArrayList<>();
    private static String filePath;

    public static Scanner scanner;
    private static Crawler crawler;
    private static MarkdownWriter markdownWriter;

    private static final String urlRegex = "^(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(\\/[a-zA-Z0-9-._?&=]*)?$";
    private static final String depthRegex = "[1-5]";
    private static final String domainRegex = "^(?!.*\\s)[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    private static final String filePathRegex = "^(.*/)?(?:$|(.+?)(?:(\\.[^.]*$)|$))";


    public static void main(String[] args) {
        System.out.println("\nWelcome to WebCrawler!");
        initializeScanner();
        storeUserInputs();
        initializeMarkdownWriter();
        initializeCrawler();
        crawlURL(url);
        printUserInput();
        closeScanner();
    }

    public static void initializeScanner() {
        scanner = new Scanner(System.in);
    }

    public static void closeScanner() {
        scanner.close();
    }

    public static void storeUserInputs() {
        storeUrl();
        storeDepth();
        storeDomains();
        storeFilePath();
    }

    public static void storeUrl() {
        System.out.println("Please enter the URL you want to crawl (e.g. https://example.com):");
        if (scanner.hasNextLine()) {
            url = scanner.nextLine();

            if (!url.matches(urlRegex)) {
                url = "";
                printInvalidInput();
                storeUrl();
            }

        } else {
            storeUserInputs();
        }
    }

    public static void storeDepth() {
        System.out.println("Please enter the depth of websites to crawl (1-5):");
        if (scanner.hasNextLine()) {
            String depthInput = scanner.nextLine();

            if (depthInput.matches(depthRegex)) {
                depth = Integer.parseInt(depthInput);

            } else {
                depth = 0;
                printInvalidInput();
                storeDepth();
            }

        } else {
            storeUserInputs();
        }
    }

    public static void storeDomains() {
        System.out.println("Please enter domains to be crawled, separated by a space:");
        if (scanner.hasNextLine()) {
            domains.addAll(List.of(scanner.nextLine().split(" ")));

            for (String domain : domains) {
                if (!domain.matches(domainRegex)) {
                    domains = new ArrayList<>();
                    printInvalidInput();
                    storeDomains();
                }
            }

        } else {
            storeUserInputs();
        }
    }

    public static void storeFilePath() {
        System.out.println("Please enter the file path where you want to store your markdown:\n" +
                "(format C:\\Users\\Benutzername\\Documents\\markdown\\output.md");
        if (scanner.hasNextLine()) {
            filePath = scanner.next();

            if (!filePath.matches(filePathRegex)) {
                filePath = "";
                printInvalidInput();
                storeFilePath();
            }

        } else {
            storeUserInputs();
        }

    }

    public static void printInvalidInput() {
        System.out.println("Invalid Input!");
    }

    public static void printUserInput() {
        String result = url + " " + depth + " ";
        for (String domain : domains) {
            if (domain != null) {
                result += domain + " ";
            }
        }
        System.out.println("\nThe markdown file based on your inputs\n" + result + "\nis stored in " + filePath + "\n");
    }

    private static void initializeMarkdownWriter() {
        markdownWriter=new MarkdownWriter(filePath);
    }

    private static void initializeCrawler() {
        crawler = new Crawler(url, depth, domains, markdownWriter);
    }


    static void crawlURL(String url) {
        System.out.println("Crawling...");
        crawler.crawl(url, 0);
        crawler.finishWritingAfterCrawling();
    }

    public static String getUrl() {
        return url;
    }

    public static int getDepth() {
        return depth;
    }

    public static List<String> getDomains() {
        return domains;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setCrawler(Crawler newCrawler) {
        crawler = newCrawler;
    }

}

