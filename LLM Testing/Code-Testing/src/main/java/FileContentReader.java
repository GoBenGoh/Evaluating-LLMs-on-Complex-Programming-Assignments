import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileContentReader {
    public static String getFileContent(String repoPath) throws IOException {
        List<String> excludedFiles = List.of("Main.java", "MessageCli.java", "InsuranceSystem.java");
        StringBuilder content = new StringBuilder();

        // Get content of InsuranceSystem.java
        String insuranceSystemPath = "/src/main/java/nz/ac/auckland/se281/InsuranceSystem.java";
        String insuranceSystemFilePath = repoPath + insuranceSystemPath;

        try (BufferedReader insuranceSystemReader = new BufferedReader(new FileReader(insuranceSystemFilePath))) {
            String line;
            while ((line = insuranceSystemReader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        // Iterate through other files and append their content
        String srcPath = repoPath + "/src/main/java/nz/ac/auckland/se281";
        File directory = new File(srcPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !excludedFiles.contains(file.getName()) && !file.getAbsolutePath().equals(insuranceSystemFilePath)) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                    }
                }
            }
        }

        return content.toString();
    }
}
