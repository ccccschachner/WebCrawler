import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({CrawlerTest.class, EntryPointTest.class,MarkdownWriterTest.class,ParserTest.class})
public class WebCrawlerAllTests {
}
