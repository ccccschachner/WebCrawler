import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class MarkdownCombinerTest {

    private static final String filePath = "./test_combined.md";
    private static final List<String> files = new ArrayList<>();
    private MarkdownCombiner markdownCombiner;
    private File file;

    @BeforeEach
    public void setUp() {
        markdownCombiner = new MarkdownCombiner(filePath,files);
        file = new File(filePath);

        if (!file.exists()) {
            file.delete();
        }

        files.add("./test_out.md_1");
        files.add("./test_out.md_2");
        EntryPoint.setFiles(files);
    }

    @Test
    public void testFileExists() {
        markdownCombiner.combineFiles();
        file = new File(filePath);
        Assertions.assertTrue(file.exists());
    }

    @Test
    public void testFileIsNotNull() throws IOException {
        markdownCombiner.combineFiles();
        file = new File(filePath);

        List<String> content = Files.readAllLines(Paths.get(filePath));
        Assertions.assertTrue(content.size()>1);
    }
}
