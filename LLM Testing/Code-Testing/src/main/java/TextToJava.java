import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TextToJava {
    public static void convertTextToJavaFile() throws IOException {
        String textFilePath = "./src/main/resources/content.txt";
        String javaFilePath = "../assignment_template/assignment-1/src/main/java/nz/ac/auckland/se281/InsuranceSystem.java";
        String content = readFile(textFilePath);
        String javaCode = extractJavaCode(content);
        saveJavaFile(javaCode, javaFilePath);
    }

    public static void convertStringToJavaFile(String string, String repoPath) throws IOException{
        String insuranceSystemPath = "/src/main/java/nz/ac/auckland/se281/InsuranceSystem.java";
        String javaCode = extractJavaCode(string);
        saveJavaFile(javaCode, repoPath + insuranceSystemPath);
        deleteDuplicateFiles(repoPath);
    }

    public static void deleteDuplicateFiles(String repoPath) {
        File srcFolder = new File(repoPath + "/src/main/java/nz/ac/auckland/se281");
        File[] files = srcFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (!fileName.equals("Main.java") && !fileName.equals("MessageCli.java") && !fileName.equals("InsuranceSystem.java")) {
                    // Delete the file
                    try {
                        file.delete();
                    } catch (SecurityException e) {
                        System.err.println("Failed to delete: " + fileName);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String extractJavaCode(String text) {
        // Case where code surrounded by backticks
        int startIndex = text.indexOf("```java");
        int endIndex = text.lastIndexOf("```");

        if (startIndex == -1){
            startIndex = text.indexOf("```");
            if (startIndex == 0){
                String codeBlock;
                if (endIndex != startIndex){ // code between a pair of ```
                    codeBlock = text.substring(startIndex + 3, endIndex).trim();
                }
                else { // code starts with ``` but doesn't end with ```
                    codeBlock = text.substring(startIndex + 3);
                }
                return codeBlock;

            }
            if (startIndex>0){ // code ends with ``` but does not start with ```
                String codeBlock = text.substring(0, startIndex).trim();
                return codeBlock;
            }
        }

        if (startIndex != -1 && endIndex != -1) { // code between ```java and ```
            if (endIndex <= startIndex){ // endIndex is the ``` in ```java
                String codeBlock = text.substring(startIndex + 7).trim();
                return codeBlock;
            }
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

