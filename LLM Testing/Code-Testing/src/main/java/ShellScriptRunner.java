import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShellScriptRunner {
    public static void main(String[] args) {
        // Hard Coded Student Repo
        String repo = "../repos_output/assignment-1-repository-4";
        runTesting(repo);
    }

    public static void runTesting(String repositoryDirectory) {
        String xmlPath = repositoryDirectory + "/target/surefire-reports/TEST-nz.ac.auckland.se281.MainTest.xml";
        TestResultAnalyzer testingResults;

        String compileResponse = runCommand(repositoryDirectory, "COMPILE");
        boolean isBuildSucceeded = compileResponse.contains("BUILD SUCCESS");
        if (isBuildSucceeded) {
            // Running Provided Tests
            runCommand(repositoryDirectory, "CLEAR_TESTS");
            runCommand(repositoryDirectory, "ADD_PROVIDED_TESTS");
            runCommand(repositoryDirectory, "TEST");

            List<Integer> providedTestResults = TestResultAnalyzer.extractTestResults(xmlPath);
            int totalProvided = providedTestResults.get(0);
            int failures = providedTestResults.get(1);
            int errors = providedTestResults.get(2);
            int skipped = providedTestResults.get(3);
            int numPassedProvidedTests = totalProvided - failures - errors - skipped;

            // Getting failed provided tests
            Map<String, String> providedFailureMessages = TestResultAnalyzer.extractFailedTestDetails(xmlPath);
            List<String> providedTestNames = new ArrayList<>(providedFailureMessages.keySet());
            List<String> providedAsserts = new ArrayList<>(providedFailureMessages.values());
            List<String> failedProvidedTests = TestFinder.extractTestMethods(providedTestNames, "PROVIDED");
            FailureFileWriter.writeFailuresToFile(failedProvidedTests, providedAsserts, "PROVIDED");

            // Running Hidden Tests
            runCommand(repositoryDirectory, "CLEAR_TESTS");
            runCommand(repositoryDirectory, "ADD_HIDDEN_TESTS");
            runCommand(repositoryDirectory, "TEST");

            List<Integer> hiddenTestResults = TestResultAnalyzer.extractTestResults(xmlPath);
            int totalHidden = hiddenTestResults.get(0);
            failures = hiddenTestResults.get(1);
            errors = hiddenTestResults.get(2);
            skipped = hiddenTestResults.get(3);
            int numPassedHiddenTests = totalHidden - failures - errors - skipped;

            // Getting failed hidden tests
            Map<String, String> hiddenFailureMessages = TestResultAnalyzer.extractFailedTestDetails(xmlPath);
            List<String> hiddenTestNames = new ArrayList<>(hiddenFailureMessages.keySet());
            List<String> hiddenAsserts = new ArrayList<>(hiddenFailureMessages.values());
            List<String> failedHiddenTests = TestFinder.extractTestMethods(hiddenTestNames, "HIDDEN");
            FailureFileWriter.writeFailuresToFile(failedHiddenTests, hiddenAsserts, "HIDDEN");

            testingResults = new TestResultAnalyzer(true, numPassedProvidedTests, numPassedHiddenTests, totalProvided, totalHidden, new ArrayList<>(), providedTestNames, hiddenTestNames);

        } else {
            System.out.println("Build Failed");
            System.out.println(compileResponse);
            ArrayList<String> errorMessages = TestResultAnalyzer.getCompilationErrors(compileResponse);
            testingResults = new TestResultAnalyzer(false, -1, -1, -1, -1, errorMessages, new ArrayList<>(), new ArrayList<>());
            ErrorFileWriter.writeErrorsToFile(errorMessages);
        }
    }

    private static String runCommand(String repositoryDirectory, String command) {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            String bashExecutablePath;
            if (osName.contains("win")) {
                bashExecutablePath = "C:\\Program Files\\Git\\bin\\bash.exe";
            } else if (osName.contains("mac") || osName.contains("nix") || osName.contains("nux")) {
                bashExecutablePath = "/bin/bash";
            } else {
                throw new UnsupportedOperationException("Unsupported operating system: " + osName);
            }

            String[] scriptCommand = {bashExecutablePath, "-c", "./src/main/java/script.sh " + repositoryDirectory + " " + command};
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
            // Remove ANSI escape sequences from the line
            line = line.replaceAll("\u001B\\[[;\\d]*m", "");
            output.append(line).append(System.lineSeparator());
        }
        return output.toString();
    }
}

