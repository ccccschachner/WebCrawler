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
import java.util.Objects;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class MarkdownWriterTest {

    private MarkdownWriter markdownWriter;
    private String testFilePath = "src/test/RessourceMarkdownTest.md";
    private String compareFilePath ="src/test/RessourceMarkdownCompare.md";

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
    public void testWriteInDocument() {
        MarkdownWriter markdownWriterMock=mock(MarkdownWriter.class);

        Parser parserMock=mock(Parser.class);
        mockElemetLinks(parserMock);
        mockElemetHeadings(parserMock);
        markdownWriterMock.writeInDocument(parserMock, 0);

        verify(markdownWriterMock).writeLinks(parserMock, 0);
        verify(markdownWriterMock).writeHeadings(parserMock, 0);
    }

    @Test
    public void testWriteHeader() {

        markdownWriter= Mockito.spy(new MarkdownWriter(testFilePath));
        markdownWriter.writeHeader("https://example.com",3,"en");

        String testHeader="input: <a>https://example.com</a>\n<br>depth: 3\n<br>source language: \n<br>target language: en\n";

        verify(markdownWriter, times(1)).writeLine(testHeader);

        markdownWriter.closeWriter();
        writeToRessourceMarkdownCompare(testHeader);
        assertTrue(compareMarkdownFiles());
    }

    @Test
    public void testWriteHeadings(){
        Parser parserMock = mock(Parser.class);
        mockElemetHeadings(parserMock);

        markdownWriter= Mockito.spy(new MarkdownWriter(testFilePath));

        int depth = 2;
        markdownWriter.writeHeadings(parserMock, depth);

        String lineHeading1=markdownWriter.addHeadingMarking("h1") +" " +markdownWriter.addDepthMarking(depth) + "Heading1\n";
        String lineHeading2=markdownWriter.addHeadingMarking("h1") +" " +markdownWriter.addDepthMarking(depth) + "Heading2\n";

        verify(markdownWriter, times(1)).writeLine(lineHeading1);
        verify(markdownWriter, times(1)).writeLine(lineHeading2);

        markdownWriter.closeWriter();
        writeToRessourceMarkdownCompare(lineHeading1+lineHeading2);
        assertTrue(compareMarkdownFiles());
    }
    private void mockElemetHeadings(Parser parserMock){
        Elements headingsMock = mock(Elements.class);

        Element heading1Mock = mock(Element.class);
        when(heading1Mock.text()).thenReturn("Heading1");
        when(heading1Mock.tagName()).thenReturn("h1");

        Element heading2Mock = mock(Element.class);
        when(heading2Mock.text()).thenReturn("Heading2");
        when(heading2Mock.tagName()).thenReturn("h1");

        when(headingsMock.iterator()).thenReturn(java.util.Arrays.asList(heading1Mock, heading2Mock).iterator());
        when(parserMock.getHeadings()).thenReturn(headingsMock);
    }

    @Test
    public void testWriteLinksWitchMocks(){
        Parser parserMock = mock(Parser.class);
        mockElemetLinks(parserMock);

        markdownWriter= Mockito.spy(new MarkdownWriter(testFilePath));

        int depth = 2;
        markdownWriter.writeLinks(parserMock, depth);

        String lineLink1="<br>" + markdownWriter.addDepthMarking(depth) + " link to <a>Link1</a>\n\n";
        String lineLink2="<br>" + markdownWriter.addDepthMarking(depth) + " link to <a>Link2</a>\n\n";

        verify(markdownWriter, times(1)).writeLine(lineLink1);
        verify(markdownWriter, times(1)).writeLine(lineLink2);

        markdownWriter.closeWriter();
        writeToRessourceMarkdownCompare(lineLink1+lineLink2);
        assertTrue(compareMarkdownFiles());
    }

    private void mockElemetLinks(Parser parserMock){
        Elements linksMock = mock(Elements.class);

        Element link1Mock = mock(Element.class);
        when(link1Mock.text()).thenReturn("Link1");

        Element link2Mock = mock(Element.class);
        when(link2Mock.text()).thenReturn("Link2");

        when(linksMock.iterator()).thenReturn(java.util.Arrays.asList(link1Mock, link2Mock).iterator());
        when(parserMock.getLinks()).thenReturn(linksMock);
    }

    @Test
    public void testWriteLine(){
        writeToRessourceMarkdownCompare("Test");
        markdownWriter.writeLine("Test");
        markdownWriter.closeWriter();
        assertTrue(compareMarkdownFiles());
    }
    @Test
    public void testWriteLineNoEmptyDocument(){
        markdownWriter.writeLine("Test");
        markdownWriter.closeWriter();
        String testContent=getContent(testFilePath);
        assertFalse(testContent != null && testContent.isEmpty());
    }
    private String getContent(String filePath){
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            fail();
        }
        return null;
    }
    @Test
    public void testWriteBrokenLink(){
        String lineToWrite="<br> broken link <a>brokenLink</a>\n\n";
        writeToRessourceMarkdownCompare(lineToWrite);
        markdownWriter.writeBrokenLink("brokenLink",0);
        markdownWriter.closeWriter();
        assertTrue(compareMarkdownFiles());
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
    public void testCloseWriter_ClosesWriter() throws IOException {
        FileWriter writer = mock(FileWriter.class);
        MarkdownWriter markdownWriter = new MarkdownWriter(testFilePath);
        markdownWriter.setWriter(writer);

        markdownWriter.closeWriter();
        verify(writer, times(1)).close();
    }

    @Test
    public void testCloseWriter_TestException() throws IOException {
        FileWriter writer = mock(FileWriter.class);
        IOException ioException = new IOException("Test IOException");
        doThrow(ioException).when(writer).close();
        MarkdownWriter markdownWriter = new MarkdownWriter(testFilePath);
        markdownWriter.setWriter(writer);

        RuntimeException exception = assertThrows(RuntimeException.class, markdownWriter::closeWriter);
        assertEquals(ioException, exception.getCause());
    }

    @AfterEach
    public void tearDown(){
        markdownWriter=null;
    }

    private void writeToRessourceMarkdownCompare(String lineToWrite){
        try (FileWriter fileWriter = new FileWriter(compareFilePath)) {
            fileWriter.write(lineToWrite);
        }catch (IOException e){
            fail();
        }
    }
    private boolean compareMarkdownFiles(){
        String contentTest = getContent(testFilePath);
        String contentCompare=getContent(compareFilePath);
        return Objects.equals(contentTest, contentCompare);
    }
}
