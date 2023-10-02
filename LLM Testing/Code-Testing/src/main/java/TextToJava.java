import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TextToJava {
    public static void convertStringToJavaFile(String string, String repoPath) throws IOException{
        String insuranceSystemPath = "/src/main/java/nz/ac/auckland/se281/InsuranceSystem.java";
        String javaCode = removeBackticks(string);
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

    public static String removeBackticks(String text){
        int startIndex = text.indexOf("package");
        String code = text.substring(startIndex);
        return code.replaceAll("`", "");
    }


    public static void saveJavaFile(String javaCode, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(javaCode);
        writer.close();
    }
}

