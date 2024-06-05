import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class MarkdownWriterTest {

    private MarkdownWriter markdownWriter;
    private final String testFilePath = "src/test/RessourceMarkdownTest.md";
    private final String compareFilePath ="src/test/RessourceMarkdownCompare.md";
    private final int depth = 2;

    @BeforeEach
    public void setUp(){
        markdownWriter=new MarkdownWriter(testFilePath);
    }

    @Test
    public void testInitializeWriterNotNull(){
        markdownWriter.initializeWriter(testFilePath);
        assertNotNull(markdownWriter.getWriter());
    }

    @Test
    public void testWriteHeader() {
        markdownWriter = Mockito.spy(new MarkdownWriter(testFilePath));

        String header = "https://example.com";
        markdownWriter.writeHeader(header, depth);
        markdownWriter.closeWriter();

        String expectedHeader = String.format("input: <a>%s</a>\n<br>depth: %d\n\n", header, depth);
        verify(markdownWriter, times(1)).writeLine(expectedHeader);

        writeToRessourceMarkdownCompare(expectedHeader);
        assertFilesMatch();
    }

    @Test
    public void testWriteHeadings(){
        Parser parserMock = mock(Parser.class);
        String[] headings = parserMock.getHeadings();

        markdownWriter= Mockito.spy(new MarkdownWriter(testFilePath));
        markdownWriter.writeHeadings(headings, depth);
        markdownWriter.closeWriter();

        String expectedHeading=markdownWriter.addHeadingMarking("h1") +" " +markdownWriter.addDepthMarking(depth) + "Heading1\n";
        verify(markdownWriter, times(1)).writeLine(expectedHeading);

        writeToRessourceMarkdownCompare(expectedHeading+"\n");
        assertFilesMatch();
    }



    private Elements mockElementHeadings(Parser parserMock){
        Elements headingsMock = mock(Elements.class);

        Element heading1Mock = mock(Element.class);
        when(heading1Mock.text()).thenReturn("Heading1");
        when(heading1Mock.tagName()).thenReturn("h1");

        when(headingsMock.iterator()).thenReturn(List.of(heading1Mock).iterator());
        //when(parserMock.getHeadings()).thenReturn(headingsMock);
        return headingsMock;
    }

    @Test
    public void testWriteLink(){
        markdownWriter= Mockito.spy(new MarkdownWriter(testFilePath));

        String link1 = "Link1";
        String link2 = "Link2";
        markdownWriter.writeLink(link1, depth);
        markdownWriter.writeLink(link2, depth);
        markdownWriter.closeWriter();

        String expectedLink1 = String.format("<br>%s link to <a>%s</a>\n", markdownWriter.addDepthMarking(depth), link1);
        String expectedLink2 = String.format("<br>%s link to <a>%s</a>\n", markdownWriter.addDepthMarking(depth), link2);

        verify(markdownWriter, times(1)).writeLine(expectedLink1);
        verify(markdownWriter, times(1)).writeLine(expectedLink2);

        writeToRessourceMarkdownCompare(expectedLink1+expectedLink2);
        assertFilesMatch();
    }

    @Test
    public void testWriteLine(){
        writeToRessourceMarkdownCompare("Test");
        markdownWriter.writeLine("Test");
        markdownWriter.closeWriter();
        assertFilesMatch();
    }
    @Test
    public void testWriteLineNoEmptyDocument(){
        String lineToWrite = "Test";
        markdownWriter.writeLine(lineToWrite);
        markdownWriter.closeWriter();
        assertFileNotEmpty();
    }
    @Test
    public void testWriteBrokenLink(){
        String lineToWrite="<br> broken link <a>brokenLink</a>\n\n";
        writeToRessourceMarkdownCompare(lineToWrite);
        markdownWriter.writeBrokenLink("brokenLink",0);
        markdownWriter.closeWriter();
        assertFilesMatch();
    }

    @Test
    public void testAddDepthMarkingDepth1(){assertEquals("-->",markdownWriter.addDepthMarking(1));}
    @Test
    public void testAddDepthMarkingDepth2(){
        assertEquals("---->",markdownWriter.addDepthMarking(2));
    }
    @Test
    public void testAddDepthMarkingDepth3(){
        assertEquals("------>",markdownWriter.addDepthMarking(3));
    }
    @Test
    public void testAddDepthMarkingDepth4(){
        assertEquals("-------->",markdownWriter.addDepthMarking(4));
    }
    @Test
    public void testAddDepthMarkingDepth5(){
        assertEquals("---------->",markdownWriter.addDepthMarking(5));
    }
    @Test
    public void testAddDepthMarkingDefault(){
        assertTrue(markdownWriter.addDepthMarking(0).isEmpty());
    }

    @Test
    public void testAddHeadingMarkingHeading1(){
        assertEquals("#",markdownWriter.addHeadingMarking("h1"));
    }
    @Test
    public void testAddHeadingMarkingHeading2(){
        assertEquals("##",markdownWriter.addHeadingMarking("h2"));
    }
    @Test
    public void testAddHeadingMarkingHeading3(){assertEquals("###",markdownWriter.addHeadingMarking("h3"));}
    @Test
    public void testAddHeadingMarkingHeading4(){assertEquals("####",markdownWriter.addHeadingMarking("h4"));}
    @Test
    public void testAddHeadingMarkingHeading5(){
        assertEquals("#####",markdownWriter.addHeadingMarking("h5"));
    }
    @Test
    public void testAddHeadingMarkingHeading6(){
        assertEquals("######",markdownWriter.addHeadingMarking("h6"));
    }
    @Test
    public void testAddHeadingMarkingDefault(){
        assertTrue(markdownWriter.addHeadingMarking("h7").isEmpty());
    }

    @Test
    public void testCloseWriter_Closed() throws IOException {
        FileWriter writer = mock(FileWriter.class);
        MarkdownWriter markdownWriter = new MarkdownWriter(testFilePath);

        markdownWriter.setWriter(writer);
        markdownWriter.closeWriter();

        verify(writer, times(1)).close();
    }

    @Test
    public void testCloseWriter_TestException(){
        FileWriter writer = mock(FileWriter.class);
        IOException ioException = mockIOException(writer);

        MarkdownWriter markdownWriter = new MarkdownWriter(testFilePath);
        markdownWriter.setWriter(writer);

        RuntimeException expectedException = assertThrows(RuntimeException.class, markdownWriter::closeWriter);
        assertEquals(ioException, expectedException.getCause());
    }
    private IOException mockIOException(FileWriter writer){
        IOException ioException = new IOException("Test IOException");
        try {
            doThrow(ioException).when(writer).close();
        } catch (IOException e) {
            fail();
        }
        return ioException;
    }

    private String getContent(String filePath){
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            fail();
        }
        return null;
    }

    private void writeToRessourceMarkdownCompare(String lineToWrite){
        try (FileWriter fileWriter = new FileWriter(compareFilePath)) {
            fileWriter.write(lineToWrite);
        }catch (IOException e){
            fail();
        }
    }
    private void assertFilesMatch() {
        String contentTest = getContent(testFilePath);
        String contentCompare = getContent(compareFilePath);
        assertEquals(contentCompare, contentTest);
    }
    private void assertFileNotEmpty() {
        String testContent = getContent(testFilePath);
        assertNotNull(testContent);
        assertFalse(testContent.isEmpty());
    }
    @AfterEach
    public void tearDown(){
        markdownWriter=null;
    }

}
