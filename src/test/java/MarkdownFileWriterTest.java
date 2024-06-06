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


public class MarkdownFileWriterTest {

    /*private MarkdownFileWriter markdownFileWriter;
    private final String testFilePath = "src/test/RessourceMarkdownTest.md";
    private final String compareFilePath ="src/test/RessourceMarkdownCompare.md";
    private final int depth = 2;

    @BeforeEach
    public void setUp(){
        markdownFileWriter =new MarkdownFileWriter(testFilePath);
    }

    @Test
    public void testInitializeWriterNotNull(){
        markdownFileWriter.initializeWriter(testFilePath);
        assertNotNull(markdownFileWriter.getWriter());
    }

    @Test
    public void testWriteHeader() {
        markdownFileWriter = Mockito.spy(new MarkdownFileWriter(testFilePath));

        String header = "https://example.com";
        markdownFileWriter.writeHeader(header, depth);
        markdownFileWriter.closeWriter();

        String expectedHeader = String.format("input: <a>%s</a>\n<br>depth: %d\n\n", header, depth);
        verify(markdownFileWriter, times(1)).writeLine(expectedHeader);

        writeToRessourceMarkdownCompare(expectedHeader);
        assertFilesMatch();
    }

    @Test
    public void testWriteHeadings(){
        Parser parserMock = mock(Parser.class);
        String[] headings = parserMock.getHeadings();

        markdownFileWriter = Mockito.spy(new MarkdownFileWriter(testFilePath));
        markdownFileWriter.writeHeadings(headings, depth);
        markdownFileWriter.closeWriter();

        String expectedHeading= markdownFileWriter.addHeadingMarking("h1") +" " + markdownFileWriter.addDepthMarking(depth) + "Heading1\n";
        verify(markdownFileWriter, times(1)).writeLine(expectedHeading);

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
        markdownFileWriter = Mockito.spy(new MarkdownFileWriter(testFilePath));

        String link1 = "Link1";
        String link2 = "Link2";
        markdownFileWriter.writeLink(link1, depth);
        markdownFileWriter.writeLink(link2, depth);
        markdownFileWriter.closeWriter();

        String expectedLink1 = String.format("<br>%s link to <a>%s</a>\n", markdownFileWriter.addDepthMarking(depth), link1);
        String expectedLink2 = String.format("<br>%s link to <a>%s</a>\n", markdownFileWriter.addDepthMarking(depth), link2);

        verify(markdownFileWriter, times(1)).writeLine(expectedLink1);
        verify(markdownFileWriter, times(1)).writeLine(expectedLink2);

        writeToRessourceMarkdownCompare(expectedLink1+expectedLink2);
        assertFilesMatch();
    }

    @Test
    public void testWriteLine(){
        writeToRessourceMarkdownCompare("Test");
        markdownFileWriter.writeLine("Test");
        markdownFileWriter.closeWriter();
        assertFilesMatch();
    }
    @Test
    public void testWriteLineNoEmptyDocument(){
        String lineToWrite = "Test";
        markdownFileWriter.writeLine(lineToWrite);
        markdownFileWriter.closeWriter();
        assertFileNotEmpty();
    }
    @Test
    public void testWriteBrokenLink(){
        String lineToWrite="<br> broken link <a>brokenLink</a>\n\n";
        writeToRessourceMarkdownCompare(lineToWrite);
        markdownFileWriter.writeBrokenLink("brokenLink",0);
        markdownFileWriter.closeWriter();
        assertFilesMatch();
    }

    @Test
    public void testAddDepthMarkingDepth1(){assertEquals("-->", markdownFileWriter.addDepthMarking(1));}
    @Test
    public void testAddDepthMarkingDepth2(){
        assertEquals("---->", markdownFileWriter.addDepthMarking(2));
    }
    @Test
    public void testAddDepthMarkingDepth3(){
        assertEquals("------>", markdownFileWriter.addDepthMarking(3));
    }
    @Test
    public void testAddDepthMarkingDepth4(){
        assertEquals("-------->", markdownFileWriter.addDepthMarking(4));
    }
    @Test
    public void testAddDepthMarkingDepth5(){
        assertEquals("---------->", markdownFileWriter.addDepthMarking(5));
    }
    @Test
    public void testAddDepthMarkingDefault(){
        assertTrue(markdownFileWriter.addDepthMarking(0).isEmpty());
    }

    @Test
    public void testAddHeadingMarkingHeading1(){
        assertEquals("#", markdownFileWriter.addHeadingMarking("h1"));
    }
    @Test
    public void testAddHeadingMarkingHeading2(){
        assertEquals("##", markdownFileWriter.addHeadingMarking("h2"));
    }
    @Test
    public void testAddHeadingMarkingHeading3(){assertEquals("###", markdownFileWriter.addHeadingMarking("h3"));}
    @Test
    public void testAddHeadingMarkingHeading4(){assertEquals("####", markdownFileWriter.addHeadingMarking("h4"));}
    @Test
    public void testAddHeadingMarkingHeading5(){
        assertEquals("#####", markdownFileWriter.addHeadingMarking("h5"));
    }
    @Test
    public void testAddHeadingMarkingHeading6(){
        assertEquals("######", markdownFileWriter.addHeadingMarking("h6"));
    }
    @Test
    public void testAddHeadingMarkingDefault(){
        assertTrue(markdownFileWriter.addHeadingMarking("h7").isEmpty());
    }

    @Test
    public void testCloseWriter_Closed() throws IOException {
        FileWriter writer = mock(FileWriter.class);
        MarkdownFileWriter markdownFileWriter = new MarkdownFileWriter(testFilePath);

        markdownFileWriter.setWriter(writer);
        markdownFileWriter.closeWriter();

        verify(writer, times(1)).close();
    }

    @Test
    public void testCloseWriter_TestException(){
        FileWriter writer = mock(FileWriter.class);
        IOException ioException = mockIOException(writer);

        MarkdownFileWriter markdownFileWriter = new MarkdownFileWriter(testFilePath);
        markdownFileWriter.setWriter(writer);

        RuntimeException expectedException = assertThrows(RuntimeException.class, markdownFileWriter::closeWriter);
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
        markdownFileWriter =null;
    }*/

}
