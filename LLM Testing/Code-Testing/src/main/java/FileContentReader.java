import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileContentReader {
    public static String getFileContent(String repoPath) throws IOException {
        String insuranceSystemPath = "/src/main/java/nz/ac/auckland/se281/InsuranceSystem.java";
        String filePath = repoPath + insuranceSystemPath;

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return content.toString();
    }
}
