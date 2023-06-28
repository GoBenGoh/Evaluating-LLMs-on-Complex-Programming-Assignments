import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ShellScriptRunner {
    public static void main(String[] args) {
        // Hard Coded Student Repo
        String repositoryDirectory = "./repos_output/assignment-1-repository-12";
        TestResultAnalyzer testingResults = new TestResultAnalyzer(false, -1, -1, -1, -1, new String[] {});
        ArrayList<String> errorMessages = new ArrayList<>();

        String compileResponse = runCommand(repositoryDirectory, "COMPILE");
        boolean isBuildSucceeded = compileResponse.contains("BUILD SUCCESS");
        if (isBuildSucceeded) {
            System.out.println("Build Succeeded");
            System.out.println("Setup Provided Tests");
            runCommand(repositoryDirectory, "CLEAR_TESTS");
            System.out.println("Tests Cleared");
            runCommand(repositoryDirectory, "ADD_PROVIDED_TESTS");
            System.out.println("Added Provided Tests");
            String testResponse = runCommand(repositoryDirectory, "TEST");

            int totalProvided = TestResultAnalyzer.getValue(testResponse, "Tests run: ");
            int failures = TestResultAnalyzer.getValue(testResponse, "Failures: ");
            int errors = TestResultAnalyzer.getValue(testResponse, "Errors: ");
            int skipped = TestResultAnalyzer.getValue(testResponse, "Skipped: ");
            int numPassedProvidedTests = totalProvided - failures - errors - skipped;

            if (totalProvided == -1 || failures == -1 || errors == -1 || skipped == -1){
                System.out.println("Failed to get testing information from provided tests");
                testingResults = new TestResultAnalyzer(true, 0, 0, 0, 0, new String[] {});
                return;
            }

            System.out.println("Setup Hidden Tests");
            runCommand(repositoryDirectory, "CLEAR_TESTS");
            System.out.println("Tests Cleared");
            runCommand(repositoryDirectory, "ADD_HIDDEN_TESTS");
            System.out.println("Added Hidden Tests");
            testResponse = runCommand(repositoryDirectory, "TEST");

            int totalHidden = TestResultAnalyzer.getValue(testResponse, "Tests run: ");
            failures = TestResultAnalyzer.getValue(testResponse, "Failures: ");
            errors = TestResultAnalyzer.getValue(testResponse, "Errors: ");
            skipped = TestResultAnalyzer.getValue(testResponse, "Skipped: ");
            int numPassedHiddenTests = totalHidden - failures - errors - skipped;

            if (totalHidden == -1 || failures == -1 || errors == -1 || skipped == -1){
                System.out.println("Failed to get testing information from hidden tests");
                testingResults = new TestResultAnalyzer(true, numPassedProvidedTests, 0, totalProvided, 0, new String[] {});
                return;
            }

            System.out.println("Provided Tests: " + numPassedProvidedTests + "/" + totalProvided);
            System.out.println("Hidden Tests: " + numPassedHiddenTests + "/" + totalHidden);
            testingResults = new TestResultAnalyzer(true, numPassedProvidedTests, numPassedHiddenTests, totalProvided, totalHidden, new String[] {});

        } else {
            System.out.println("Build Failed");
        }
    }

    private static String runCommand(String repositoryDirectory, String command) {
        try {
            String[] scriptCommand = {"C:\\Program Files\\Git\\bin\\bash.exe", "-c", "./Code-Testing/src/main/java/script.sh " + repositoryDirectory + " " + command};
            ProcessBuilder processBuilder = new ProcessBuilder(scriptCommand);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            if (command.equals("CLEAR_TESTS")) {
                return "";
            } else {
                return readOutput(process);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String readOutput(Process process) throws IOException {
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }
        return output.toString();
    }
}

