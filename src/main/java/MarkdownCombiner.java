import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class MarkdownCombiner {
    private String filePath;

    MarkdownCombiner(String filePath){
        this.filePath = filePath;
    }

    public void combineFiles() {
        try {
            List<String> files = EntryPoint.getFiles();
            Files.write(Paths.get(filePath), new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            for (String file : files) {
                List<String> lines = Files.readAllLines(Paths.get(file));
                Files.write(Paths.get(filePath), lines, StandardOpenOption.APPEND);
                Files.write(Paths.get(filePath), System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
