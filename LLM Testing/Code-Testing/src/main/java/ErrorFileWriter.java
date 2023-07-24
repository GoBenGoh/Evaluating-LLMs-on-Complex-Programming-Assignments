import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ErrorFileWriter {
    public static void writeErrorsToFile(String errors) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/errors.txt"))) {
            writer.write(errors);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
