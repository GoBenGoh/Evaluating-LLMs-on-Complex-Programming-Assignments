import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ErrorFileWriter {
    public static void writeErrorsToFile(List<String> errorList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/errors.txt"))) {
            for (String error : errorList) {
                int start = error.indexOf("src/main/java/nz/ac/auckland/se281/")+35;
                String trimmedError = error.substring(start);
                writer.write(trimmedError);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
