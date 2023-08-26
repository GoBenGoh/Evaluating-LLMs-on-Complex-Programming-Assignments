import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FailureFileWriter {
    public static void writeFailuresToFile(List<String> failedProvidedTests, List<String> failedHiddenTests, List<String> providedAsserts, List<String> hiddenAsserts, String task) {
        if ((failedProvidedTests.size() != providedAsserts.size()) || (failedHiddenTests.size() != hiddenAsserts.size())) {
            throw new IllegalArgumentException("Failed tests and assertion messages must have the same size.");
        }

        String fileName = "";
        if (task.equals("T1")) {
            fileName = "t1_failures.txt";
        } else if (task.equals("T2")) {
            fileName = "t2_failures.txt";
        } else if (task.equals("T3")) {
            fileName = "t3_failures.txt";
        }

        // Combining provided and hidden lists
        List<String> failedTests = new ArrayList<>(failedProvidedTests);
        failedTests.addAll(failedHiddenTests);
        List<String> assertionMessages = new ArrayList<>(providedAsserts);
        assertionMessages.addAll(hiddenAsserts);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/" + fileName))) {
            for (int i = 0; i < failedTests.size(); i++) {
                String failedTest = failedTests.get(i);
                String assertionMessage = assertionMessages.get(i);

                writer.write(failedTest);
                writer.newLine();
                writer.write(assertionMessage);
                writer.newLine();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getFailuresAsString(List<String> failedProvidedTests, List<String> failedHiddenTests, List<String> providedAsserts, List<String> hiddenAsserts){
        List<String> failedTests = new ArrayList<>(failedProvidedTests);
        failedTests.addAll(failedHiddenTests);
        List<String> assertionMessages = new ArrayList<>(providedAsserts);
        assertionMessages.addAll(hiddenAsserts);
        StringBuffer failures = new StringBuffer();
        for (int i = 0; i < failedTests.size(); i++) {
            String failedTest = failedTests.get(i);
            failures.append(failedTest);
            failures.append("\n");
            String assertionMessage = assertionMessages.get(i);
            failures.append(assertionMessage);
        }
        return failures.toString();
    }
}
