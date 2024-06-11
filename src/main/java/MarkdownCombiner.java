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
            Path targetFile = Paths.get(filePath);

            Files.write(targetFile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            for (String file : files) {
                Path threadFile = Paths.get(file);
                List<String> lines = Files.readAllLines(threadFile);

                String lineSeparator = System.lineSeparator();
                byte[] lineSeparatorBytes = lineSeparator.getBytes();

                Files.write(targetFile, lines, StandardOpenOption.APPEND);
                Files.write(targetFile, lineSeparatorBytes, StandardOpenOption.APPEND);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
