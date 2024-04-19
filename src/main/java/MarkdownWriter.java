import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileWriter;
import java.io.IOException;

public class MarkdownWriter {
    String filePath; //TODO add Path
    FileWriter writer;



    public MarkdownWriter(){
        initializeWriter();
    }
    private void initializeWriter(){
        try{
            this.writer= new FileWriter(filePath);
        } catch (IOException e) {
            System.out.println("Error writing Markdown file: " + e.getMessage());
        }
    }
    public void writeDocument(Parser parser,int depth){
        if(depth==0){
            writeHeader(parser,depth);
        }else{
            writeLinks(parser,depth);
            writeHeadings(parser,depth);
        }
    }

    private void writeHeader(Parser parser, int depth) {
        String input="input: <a>"+Main.getUrl()+"</a>\n";
        String depthToCrawl="<br>depth: "+Main.getDepth()+"\n";
        String sourceLanguage="<br>source language: "+"\n"; //TODO add sourceLanguage
        String targetLanguage="<br>target language: "+Main.getTargetLanguage()+"\n";
        String lineToWrite=input+depthToCrawl+sourceLanguage+targetLanguage;
        writeLine(lineToWrite);
        writeHeadings(parser,depth);

    }

    private void writeLinks(Parser parser, int depth) {
        Elements links=parser.getLinks();
        for(Element link:links) {
            String lineToWrite = "<br>" + addDepthMarking(depth) + " link to <a>" + link.text() + "</a>\n\n";
            writeLine(lineToWrite);
        }
    }

    private void writeHeadings(Parser parser, int depth){
        Elements headings=parser.getHeadings();
        for(Element heading:headings) {
            String lineToWrite = addHeadingMarking(heading.tagName().toLowerCase()) + " "+addDepthMarking(depth) + heading.text() + "\n";
            writeLine(lineToWrite);
        }

    }
    private String addHeadingMarking(String tag){
        return switch (tag) {
            case "h1" -> "# ";
            case "h2" -> "## ";
            case "h3" -> "### ";
            case "h4" -> "#### ";
            case "h5" -> "##### ";
            case "h6" -> "######";
            default -> "";
        };
    }
    private String addDepthMarking(int depth){
        return switch (depth) {
            case 1 -> "-->";
            case 2 -> "---->";
            default -> "";
        };
    }
    private void writeLine(String lineToWrite){
        try {
            writer.write(lineToWrite);
        } catch (IOException e) {
            System.out.println("Error writing Markdown file: " + e.getMessage());
        }
    }

    public void writeBrokenLink(String brokenLink, int depth){
        String lineToWrite="<br>"+addDepthMarking(depth)+" broken link <a>"+brokenLink+"</a>\n\n";
        writeLine(lineToWrite);
    }
}
