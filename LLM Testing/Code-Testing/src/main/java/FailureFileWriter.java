import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FailureFileWriter {
    public static void writeFailuresToFile(List<String> failedTests, List<String> assertionMessages, String type) {
        if (failedTests.size() != assertionMessages.size()) {
            throw new IllegalArgumentException("Failed tests and assertion messages must have the same size.");
        }

        String fileName = "";
        if (type.equals("PROVIDED")) {
            fileName = "provided_failures.txt";
        } else if (type.equals("HIDDEN")) {
            fileName = "hidden_failures.txt";
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < failedTests.size(); i++) {
                String failedTest = failedTests.get(i);
                String assertionMessage = assertionMessages.get(i);

                writer.write(failedTest);
                writer.newLine();
                writer.write("java.lang.AssertionError: ");
                writer.newLine();
                writer.write(assertionMessage);
                writer.newLine();
                writer.newLine();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
