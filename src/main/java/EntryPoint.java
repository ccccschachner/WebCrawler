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

    private static final String urlsPrompt = "Please enter URLs you want to crawl, separated by a space (e.g. https://example.com):"
    private static final String depthPrompt = "Please enter the depth of websites to crawl (1-5):";
    private static final String domainsPrompt = "Please enter domains to be crawled, separated by a space:";
    private static final String filePathPrompt = "Please enter the file path where you want to store your markdown:\n(format C:\\Users\\Benutzername\\Documents\\markdown\\output.md)";


    public static void main(String[] args) {
        System.out.println("\nWelcome to WebCrawler!");
        initializeScanner();
        storeUserInputs();
        startCrawlerThreads();
        joinThreads();
        createFinalMarkdown();
        printUserInput();
        closeScanner();
    }

    public static void storeUserInputs() {
        urls = promptUserInput(urlsPrompt, urlRegex);
        depth = Integer.parseInt(promptSingleInput(depthPrompt, depthRegex));
        domains = promptUserInput(domainsPrompt, domainRegex);
        filePath = promptSingleInput(filePathPrompt, filePathRegex);
    }

    public static List<String> promptUserInput(String prompt, String regex) {
        List<String> inputs;
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine();
            inputs = List.of(input.split(" "));
            if (inputs.stream().allMatch(i -> i.matches(regex))) {
                break;
            }
            printInvalidInput();
        }
        return inputs;
    }

    public static String promptSingleInput(String prompt, String regex) {
        String input;
        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine();
            if (input.matches(regex)) {
                break;
            }
            printInvalidInput();
        }
        return input;
    }

    public static void initializeScanner() {
        scanner = new Scanner(System.in);
    }

    public static void startCrawlerThreads() {
        int threadCounter = 1;
        for (String url : urls) {
            String output = filePath + "_" + threadCounter;
            files.add(output);
            Thread thread = new Thread(new CrawlTask(url, output, depth, domains));
            thread.start();
            threads.add(thread);
            threadCounter++;
        }
    }

    public static void printUserInput() {
        String result = String.join(" ", urls) + " " + depth + " " + String.join(" ", domains);
        System.out.println("\nThe markdown file based on your inputs [" + result + "] is stored in " + filePath + ".\n");
    }

    public static void createFinalMarkdown() {
        markdownCombiner = new MarkdownCombiner(filePath, files);
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
            }
        }
    }

    public static void printInvalidInput() {
        System.out.println("Invalid Input!");
    }
}
