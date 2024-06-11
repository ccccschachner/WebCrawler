import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EntryPoint {
    private static List<String> urls = new ArrayList<>();
    private static List<String> domains = new ArrayList<>();
    private static List<String> files = new ArrayList<>();
    protected static List<Thread> threads = new ArrayList<>();

    private static int depth;
    private static String filePath;
    protected static MarkdownCombiner markdownCombiner;
    protected static Scanner scanner;

    private static final String urlRegex = "^(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(\\/[a-zA-Z0-9-._?&=]*)?$";
    private static final String depthRegex = "[1-5]";
    private static final String domainRegex = "^(?!.*\\s)[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    private static final String filePathRegex = "^(.*/)?(?:$|(.+?)(?:(\\.[^.]*$)|$))";


    public static void main(String[] args) {
        System.out.println("\nWelcome to WebCrawler!");
        initializeScanner();
        storeUserInputs();
        startCrawlerThreads();
        joinThreads();
        createFinalMarkdown();
        closeScanner();
        printUserInput();
    }

    public static void storeUserInputs() {
        storeUrls();
        storeDepth();
        storeDomains();
        storeFilePath();
    }

    public static void storeUrls() {
        System.out.println("Please enter URLs you want to crawl, separated by a space (e.g. https://example.com).");
        if (scanner.hasNextLine()) {
            urls.addAll(List.of(scanner.nextLine().split(" ")));

            for (String url : urls) {
                if (!url.matches(urlRegex)) {
                    urls = new ArrayList<>();
                    printInvalidInput();
                    storeUrls();
                }
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
        System.out.println("Please enter the file path where you want to store your markdown:\n" + "(format C:\\Users\\Benutzername\\Documents\\markdown\\output.md)");
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

    public static void initializeScanner() {
        scanner = new Scanner(System.in);
    }

    static void startCrawlerThreads() {
        int threadCounter = 1;

        for (String url : urls) {
            String output = filePath + "_" + threadCounter;
            files.add(output);

            Thread thread = new Thread(new CrawlTask(url, output,depth,domains));
            thread.start();
            threads.add(thread);
            threadCounter++;
        }
    }

    public static void printInvalidInput() {
        System.out.println("Invalid Input!");
    }

    public static void printUserInput() {
        String result = "";

        for (String url : urls) {
            if (url != null) {
                result += url + " ";
            }
        }

        result += depth + " ";

        for (String domain : domains) {
            if (domain != null) {
                result += domain + " ";
            }
        }
        System.out.println("\nThe markdown file based on your inputs [" + result + "] is stored in " + filePath + ".\n");
    }


    public static void createFinalMarkdown() {
        markdownCombiner = new MarkdownCombiner(filePath,files);
        markdownCombiner.combineFiles();
    }

    public static void closeScanner() {
        scanner.close();
    }

    public static void joinThreads() {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Main thread was interrupted: " + e.getMessage());
            } //TODO finally-Klausel
        }
    }


    public static List<String> getUrls() {
        return urls;
    }

    public static List<String> getFiles() {
        return files;
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

    public static void setUrls(List<String> urls) {
        EntryPoint.urls = urls;
    }


    public static void setFilePath(String filePath) {
        EntryPoint.filePath = filePath;
    }

    public static void setDomains(List<String> domains){
        EntryPoint.domains = domains;
    }

    public static void setFiles(List<String> files) {
        EntryPoint.files = files;
    }


    public static List<Thread> getThreads() {
        return threads;
    }
}

