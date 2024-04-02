import java.util.Scanner;

public class Main {
    private static String url;
    private static int depth;
    private static String[] domains = new String[10];
    private static String targetLanguage;
    private final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("\nWelcome to WebCrawler!");
        storeUserInputs();
        printUserInput();
    }

    private static void storeUserInputs() {
        storeUrl();
        storeDepth();
        storeDomains();
        storeTargetLanguage();
    }

    private static void storeUrl() {
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

    private static void storeDepth() {
        System.out.println("Please enter the depth of websites to crawl (1-5):");
        if (scanner.hasNextInt()) {
            depth = Integer.parseInt(scanner.nextLine());
            if (depth > 5 || depth < 1) {
                printInvalidInput();
                storeDepth();
            }
        } else {
            printInvalidInput();
            scanner.next();
            storeDepth();
        }
    }

    private static void storeDomains() {
        String domainRegex = "^(?!.*\\s)[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
        System.out.println("Please enter domains to be crawled, seperated by a space:");
        if (scanner.hasNextLine()) {
            domains = scanner.nextLine().split(" ");
            for(int i = 0; i<domains.length; i++){
                if(!domains[i].matches(domainRegex)){
                    printInvalidInput();
                    domains = new String[10];
                    storeDomains();
                }
            }
        }
    }

    private static void storeTargetLanguage() {
        String languageRegex = "^([a-z]|[A-Z]){2}$";

        System.out.println("Please enter the target language in ISO-2 format:");
        if (scanner.hasNextLine()) {
            targetLanguage = scanner.next();
            if (!targetLanguage.matches(languageRegex)) {
                printInvalidInput();
                storeTargetLanguage();
            }
            scanner.close();
        } else {
            storeTargetLanguage();
        }

    }


    private static void printInvalidInput() {
        System.out.println("Invalid Input!");
    }

    private static void printUserInput(){
        String result = url + " " + depth + " ";
        for (String domain : domains) {
            if (domain != null) {
                result += domain + " ";
            }
        }
        result += targetLanguage;
        System.out.println("\nThe markdown file based on your inputs:\n" + result + "\nis stored in /path/to/file.\n");
    }

}

