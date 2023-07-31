import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShellScriptRunner {
//    public static void main(String[] args) {
//        // Hard Coded Student Repo
//        String repo = "../repos_output/assignment-1-repository-12";
//        runTesting(repo);
//    }

    public static TestResultAnalyzer runTesting(String repositoryDirectory, String key) {
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
            List<Map<String, String>> providedFailureMessages = TestResultAnalyzer.extractFailedTestDetails(xmlPath);
            Map<String, String> t1ProvidedFailureMessages = providedFailureMessages.get(0);
            Map<String, String> t2ProvidedFailureMessages = providedFailureMessages.get(1);
            Map<String, String> t3ProvidedFailureMessages = providedFailureMessages.get(2);

            // Collecting provided test names, assertion messages, and test methods for each task
            List<String> t1ProvidedTestNames = new ArrayList<>(t1ProvidedFailureMessages.keySet());
            List<String> t1ProvidedAsserts = new ArrayList<>(t1ProvidedFailureMessages.values());
            List<String> t1FailedProvidedTests = TestFinder.extractTestMethods(t1ProvidedTestNames, "PROVIDED");
            List<String> t2ProvidedTestNames = new ArrayList<>(t2ProvidedFailureMessages.keySet());
            List<String> t2ProvidedAsserts = new ArrayList<>(t2ProvidedFailureMessages.values());
            List<String> t2FailedProvidedTests = TestFinder.extractTestMethods(t2ProvidedTestNames, "PROVIDED");
            List<String> t3ProvidedTestNames = new ArrayList<>(t3ProvidedFailureMessages.keySet());
            List<String> t3ProvidedAsserts = new ArrayList<>(t3ProvidedFailureMessages.values());
            List<String> t3FailedProvidedTests = TestFinder.extractTestMethods(t3ProvidedTestNames, "PROVIDED");

            // Running Hidden Tests
            runCommand(repositoryDirectory, "CLEAR_TESTS");
            runCommand(repositoryDirectory, "ADD_HIDDEN_TESTS");
            runCommand(repositoryDirectory, "TEST");

//            List<Integer> hiddenTestResults = TestResultAnalyzer.extractTestResults(xmlPath);
//            int totalHidden = hiddenTestResults.get(0);
//            failures = hiddenTestResults.get(1);
//            errors = hiddenTestResults.get(2);
//            skipped = hiddenTestResults.get(3);
//            int numPassedHiddenTests = totalHidden - failures - errors - skipped;

            // Getting failed hidden tests
            List<Map<String, String>> hiddenFailureMessages = TestResultAnalyzer.extractFailedTestDetails(xmlPath);
            Map<String, String> t1HiddenFailureMessages = hiddenFailureMessages.get(0);
            Map<String, String> t2HiddenFailureMessages = hiddenFailureMessages.get(1);
            Map<String, String> t3HiddenFailureMessages = hiddenFailureMessages.get(2);

            // Collecting hidden test names, assertion messages, and test methods for each task
            List<String> t1HiddenTestNames = new ArrayList<>(t1HiddenFailureMessages.keySet());
            List<String> t1HiddenAsserts = new ArrayList<>(t1HiddenFailureMessages.values());
            List<String> t1FailedHiddenTests = TestFinder.extractTestMethods(t1HiddenTestNames, "HIDDEN");
            List<String> t2HiddenTestNames = new ArrayList<>(t2HiddenFailureMessages.keySet());
            List<String> t2HiddenAsserts = new ArrayList<>(t2HiddenFailureMessages.values());
            List<String> t2FailedHiddenTests = TestFinder.extractTestMethods(t2HiddenTestNames, "HIDDEN");
            List<String> t3HiddenTestNames = new ArrayList<>(t3HiddenFailureMessages.keySet());
            List<String> t3HiddenAsserts = new ArrayList<>(t3HiddenFailureMessages.values());
            List<String> t3FailedHiddenTests = TestFinder.extractTestMethods(t3HiddenTestNames, "HIDDEN");

            FailureFileWriter.writeFailuresToFile(t1FailedProvidedTests, t1FailedHiddenTests, t1ProvidedAsserts, t1HiddenAsserts, "T1");
            FailureFileWriter.writeFailuresToFile(t2FailedProvidedTests, t2FailedHiddenTests, t2ProvidedAsserts, t2HiddenAsserts, "T2");
            FailureFileWriter.writeFailuresToFile(t3FailedProvidedTests, t3FailedHiddenTests, t3ProvidedAsserts, t3HiddenAsserts, "T3");

            testingResults = new TestResultAnalyzer(true, t1ProvidedTestNames, t1HiddenTestNames, t2ProvidedTestNames, t2HiddenTestNames, t3ProvidedTestNames, t3HiddenTestNames, "");
            return testingResults;

        } else {
            System.out.println("Build Failed");
            System.out.println(compileResponse);
            String errorMessages = TestResultAnalyzer.getCompilationErrors(compileResponse);
            ChatGPTAPI app = new ChatGPTAPI();
            try{
                String promptTemplate = app.getFileFromResource("Prompt Templates/NaturalLanguageError.txt");
                String responseContent = app.getFileFromResource("content.txt");
                String newPrompt = new PromptWriter(promptTemplate, responseContent, errorMessages, "naturalLanguage")
                        .createNaturalLanguageErrorPrompt();
                String request = "src/main/java/NaturalLanguageRequest.json";
                Request newRequest = new Request("gpt-3.5-turbo", newPrompt); // object for gson to convert
                Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                JsonElement jsonElement = gson.toJsonTree(newRequest);
                String jsonString = gson.toJson(jsonElement);
                try(PrintWriter out = new PrintWriter(request)){
                    out.println(jsonString);
                }
                try(PrintWriter out = new PrintWriter("src/main/resources/newNaturalLanguageErrorPrompt.txt")){
                    out.println(newPrompt);
                }
                catch (IOException e){
                    System.out.println(e);
                }
                ChatGPTAPI.sendNaturalLanguageErrorRequest(key);
            }
            catch (IOException e){
                System.out.println(e);
            }
            ErrorFileWriter.writeErrorsToFile(errorMessages);
            testingResults = new TestResultAnalyzer(false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), errorMessages);
            return testingResults;
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

