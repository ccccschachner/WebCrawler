import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class MarkdownCombiner {
    private final String filePath;
    private final List<String> files;

    MarkdownCombiner(String filePath,  List<String> files){
        this.filePath = filePath;
        this.files=files;
    }

    public void combineFiles() {
        try {
            Files.write(Paths.get(filePath), new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            for (String file : files) {
                List<String> lines = Files.readAllLines(Paths.get(file));
                Files.write(Paths.get(filePath), lines, StandardOpenOption.APPEND);
                Files.write(Paths.get(filePath), System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            }

        } catch (IOException e) {
            e.printStackTrace();//TODO ErrorHandling + finally-Klausel
        }
    }
}
