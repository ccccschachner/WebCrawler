public class MarkdownContentWriter {
    private final MarkdownWriter markdownWriter;

    public MarkdownContentWriter(MarkdownWriter markdownWriter) {
        this.markdownWriter = markdownWriter;
    }

    public void writeBrokenLinks(Parser parser, int currentDepth) {
        String[] brokenLinks = parser.getBrokenUrls();
        for (String brokenLink : brokenLinks) {
            markdownWriter.writeBrokenLink(brokenLink, currentDepth);
        }
    }

    public void writeContentOfPageToMarkdown(Parser parser, String url, int currentDepth) {
        markdownWriter.writeLink(url, currentDepth);
        String[] headings = parser.getHeadings();
        markdownWriter.writeHeadings(headings, currentDepth);
    }

    public void closeMarkDownContentWriter(){
        markdownWriter.closeWriter();
    }
}
