public class MarkdownContentWriter {
    private final MarkdownFileWriter markdownFileWriter;

    public MarkdownContentWriter(MarkdownFileWriter markdownFileWriter) {
        this.markdownFileWriter = markdownFileWriter;
    }

    public void writeBrokenLinks(Parser parser, int currentDepth) {
        String[] brokenLinks = parser.getBrokenUrls();
        for (String brokenLink : brokenLinks) {
            markdownFileWriter.writeBrokenLink(brokenLink, currentDepth);
        }
    }

    public void writeContentOfPageToMarkdown(Parser parser, String url, int currentDepth) {
        markdownFileWriter.writeLink(url, currentDepth);
        String[] headings = parser.getHeadings();
        markdownFileWriter.writeHeadings(headings, currentDepth);
    }

    public void closeMarkDownContentWriter(){
        markdownFileWriter.closeWriter();
    }
}
