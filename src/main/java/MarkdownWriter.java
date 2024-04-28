import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;

public class MarkdownWriter {
    private FileWriter writer;


    public MarkdownWriter(String filePath){
        initializeWriter(filePath);
    }
    void initializeWriter(String filePath){
        try{
            this.writer= new FileWriter(filePath);
        } catch (IOException e) {
            System.out.println("Error writing Markdown file: " + e.getMessage());
        }
    }

    public void writeInDocument(Parser parser,int depth){
        writeLinks(parser,depth);
        writeHeadings(parser,depth);
    }

    void writeHeader(String url,int depth, String targetLanguageForDisplaying) {
        String input="input: <a>"+url+"</a>\n";
        String depthToCrawl="<br>depth: "+depth+"\n";
        String sourceLanguage="<br>source language: "+"\n"; //TODO add sourceLanguage
        String targetLanguage="<br>target language: "+targetLanguageForDisplaying+"\n";
        String lineToWrite=input+depthToCrawl+sourceLanguage+targetLanguage;
        writeLine(lineToWrite);
    }

    void writeLinks(Parser parser, int depth) {
        Elements links=parser.getLinks();
        for(Element link:links) {
            String lineToWrite = "<br>" + addDepthMarking(depth) + " link to <a>" + link.text() + "</a>\n\n";
            writeLine(lineToWrite);
        }
    }

    //TODO add Translator
    void writeHeadings(Parser parser, int depth){
        Elements headings=parser.getHeadings();
        for(Element heading:headings) {
           // String translatedHeading=Translator.translateHeading(heading.text());
            String lineToWrite = addHeadingMarking(heading.tagName().toLowerCase()) + " "+addDepthMarking(depth) + heading.text() + "\n";
            writeLine(lineToWrite);
        }

    }
    String addHeadingMarking(String tag){
        return switch (tag) {
            case "h1" -> "#";
            case "h2" -> "##";
            case "h3" -> "###";
            case "h4" -> "####";
            case "h5" -> "#####";
            case "h6" -> "######";
            default -> "";
        };
    }
    String addDepthMarking(int depth){
        return switch (depth) {
            case 1 -> "-->";
            case 2 -> "---->";
            case 3 -> "------>";
            case 4 -> "-------->";
            case 5 -> "---------->";
            default -> "";
        };
    }
    void writeLine(String lineToWrite){
        try {
            writer.write(lineToWrite);
        } catch (IOException e) {
            System.out.println("Error writing Markdown file: " + e.getMessage());
        }
    }

    void writeBrokenLink(String brokenLink, int depth){
        String lineToWrite="<br>"+addDepthMarking(depth)+" broken link <a>"+brokenLink+"</a>\n\n";
        writeLine(lineToWrite);
    }

    public void closeWriter(){
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public FileWriter getWriter() {
        return writer;
    }
    void setWriter(FileWriter writer){this.writer=writer;}
}
