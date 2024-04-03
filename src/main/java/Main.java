import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static String url;
    private static int depth;
    private static List<String> domains = new ArrayList<>();
    private static String targetLanguage;
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("\nWelcome to WebCrawler!");
        storeUserInputs();
        printUserInput();
    }

    public static void storeUserInputs() {
        storeUrl();
        storeDepth();
        storeDomains();
        storeTargetLanguage();
    }

    public static void storeUrl() {
        String urlRegex = "^(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(\\/[a-zA-Z0-9-._?&=]*)?$";

        System.out.println("Please enter the URL you want to crawl (e.g. https://example.com):");
        if (scanner.hasNextLine()) {
            url = scanner.nextLine();
            if (!url.matches(urlRegex)) {
                printInvalidInput();
                storeUrl();
            }
        }
    }

    public static void storeDepth() {
        String depthRegex = "[1-5]";
        System.out.println("Please enter the depth of websites to crawl (1-5):");
        if (scanner.hasNextLine()) {
            String depthInput = scanner.nextLine();
            if (depthInput.matches(depthRegex)) {
                depth = Integer.parseInt(depthInput);
            } else {
                printInvalidInput();
                storeDepth();
            }
        } else {
            printInvalidInput();
            storeDepth();
        }
    }

    public static void storeDomains() {
        String domainRegex = "^(?!.*\\s)[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
        System.out.println("Please enter domains to be crawled, seperated by a space:");
        if (scanner.hasNextLine()) {
            domains.addAll(List.of(scanner.nextLine().split(" ")));
            for (String domain : domains) {
                if (!domain.matches(domainRegex)) {
                    printInvalidInput();
                    storeDomains();
                }
            }
        }
    }

    public static void storeTargetLanguage() {
        String languageRegex = "^([a-z]|[A-Z]){2}$";

        System.out.println("Please enter the target language in ISO-2 format:");
        if (scanner.hasNextLine()) {
            targetLanguage = scanner.next();
            if (!targetLanguage.matches(languageRegex)) {
                printInvalidInput();
                storeTargetLanguage();
            }

        } else {
            storeTargetLanguage();
        }

    }


    public static void printInvalidInput() {
        System.out.println("Invalid Input!");
    }

    public static void printUserInput() {
        scanner.close();
        String result = url + " " + depth + " ";
        for (String domain : domains) {
            if (domain != null) {
                result += domain + " ";
            }
        }
        result += targetLanguage;
        System.out.println("\nThe markdown file based on your inputs\n" + result + "\nis stored in /path/to/file.\n");
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

