import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static String url;
    private static int depth;
    private static List<String> domains = new ArrayList<>();
    private static String targetLanguage;
    private static String filePath;

    public static Scanner scanner;

    private static final String urlRegex = "^(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(\\/[a-zA-Z0-9-._?&=]*)?$";
    private static final String depthRegex = "[1-5]";
    private static final String domainRegex = "^(?!.*\\s)[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    private static final String languageRegex = "^([a-z]|[A-Z]){2}$";
    private static final String filePathRegex="^(([a-zA-Z]:\\\\)|(\\\\\\\\)?)(((\\w+)?)(\\\\\\\\)?)+([\\w\\s-.]+\\\\)*([\\w\\s-.])+\\.md$";

    public static void main(String[] args) {
        System.out.println("\nWelcome to WebCrawler!");
        initializeScanner();
        storeUserInputs();
        storeFilePath();
        crawlURL();
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
        storeTargetLanguage();
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

    public static void storeTargetLanguage() {
        System.out.println("Please enter the target language in ISO-2 format:");
        if (scanner.hasNextLine()) {
            targetLanguage = scanner.next();

            if (!targetLanguage.matches(languageRegex)) {
                targetLanguage = "";
                printInvalidInput();
                storeTargetLanguage();
            }

        } else {
            storeUserInputs();
        }

    }
    //TODO test
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

            System.out.println("Crawling...");

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
        result += targetLanguage;
        System.out.println("\nThe markdown file based on your inputs\n" + result + "\nis stored in "+ filePath+"\n");
    }

    //TODO test
    private static void crawlURL() {
        Crawler crawler=new Crawler(url,depth,domains,filePath,targetLanguage);
        crawler.crawl(url,0);
        crawler.finishCrawling();
    }

    public static String getUrl() {
        return url;
    }

    public static String getTargetLanguage() {
        return targetLanguage;
    }

    public static int getDepth() {
        return depth;
    }

    public static List<String> getDomains() {
        return domains;
    }

}

