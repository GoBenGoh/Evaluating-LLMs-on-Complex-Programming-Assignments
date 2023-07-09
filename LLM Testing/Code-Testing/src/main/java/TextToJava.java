import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TextToJava {
    public static void main(String[] args) {
        try {
            convertTextToJavaFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convertTextToJavaFile() throws IOException {
        String textFilePath = "./src/main/resources/content.txt";
        String javaFilePath = "../assignment-1/src/main/java/nz/ac/auckland/se281/InsuranceSystem.java";

        String content = readFile(textFilePath);
        String javaCode = extractJavaCode(content);
        saveJavaFile(javaCode, javaFilePath);
    }

    public static String extractJavaCode(String text) {
        // Case where code surrounded by backticks
        int startIndex = text.indexOf("```java");
        int endIndex = text.lastIndexOf("```");

        if (startIndex != -1 && endIndex != -1) {
            String codeBlock = text.substring(startIndex + 7, endIndex).trim();
            return codeBlock;
        }

        // Case where code isn't surrounded by backticks
        startIndex = text.indexOf("package");
        if (startIndex != -1) {
            return text.substring(startIndex);
        }

        return null;
    }

    public static String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }

    public static void saveJavaFile(String javaCode, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(javaCode);
        writer.close();
    }
}

