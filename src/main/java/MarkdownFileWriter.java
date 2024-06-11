import java.io.FileWriter;
import java.io.IOException;

public class MarkdownFileWriter {
    private FileWriter writer;
    public MarkdownFileWriter(String filePath){
        initializeWriter(filePath);
    }
    void initializeWriter(String filePath){
        try{
            this.writer= new FileWriter(filePath);
        } catch (IOException e) {
            System.out.println("Error writing Markdown file: " + e.getMessage());
        }
    }

    void writeHeader(String url,int depth) {
        String input="input: <a>"+url+"</a>\n";
        String depthToCrawl="<br>depth: "+depth+"\n\n";
        String lineToWrite=input+depthToCrawl;
        writeLine(lineToWrite);
    }

    void writeLink(String url, int depth) {
        String lineToWrite = "<br>" + addDepthMarking(depth) + " link to <a>" + url + "</a>\n";
        writeLine(lineToWrite);
    }

    void writeHeadings(String[] headings, int depth){
        for(String heading:headings) {
            String lineToWrite = addHeadingMarking(heading.toLowerCase().substring(0,2)) + " "+addDepthMarking(depth) +heading.substring(4)+ "\n";
            writeLine(lineToWrite);
        }
        writeLine("\n");
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
        } finally {
            try {
                writer.flush();
            } catch (IOException e) {
                System.out.println("Error flushing Markdown file: " + e.getMessage());
            }
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
            System.out.println("Error closing MarkdownWriter: "+e.getMessage());
        }
    }
    public FileWriter getWriter() {
        return writer;
    }
    void setWriter(FileWriter writer){this.writer=writer;}
}
