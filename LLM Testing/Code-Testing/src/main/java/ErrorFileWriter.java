import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ErrorFileWriter {
    public static void writeErrorsToFile(List<String> errorList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("errors.txt"))) {
            for (String error : errorList) {
                writer.write(error);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
