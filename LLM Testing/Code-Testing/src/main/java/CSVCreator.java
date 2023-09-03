import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CSVCreator {
    private CSVWriter csvWriter;
    private String repository;
    private String commitHash;
    private String commitNumber;
    private String workflow;
    private String temperature;

    public CSVCreator(String repo, String commitHash, String commitNumber, String workflow, String temperature) {
        this.repository = getRepoNameFromDirectory(repo);
        this.commitHash = commitHash;
        this.commitNumber = commitNumber;
        this.workflow = workflow;
        this.temperature = temperature;

        createRepoTestingDirectory();
        try {
            csvWriter = new CSVWriter(new FileWriter("../" + this.repository + "_" + this.workflow + "_" + this.temperature + "/commit_" + this.commitNumber + ".csv"));
        } catch (IOException e) {
            System.out.println("An error occurred while initializing the CSVWriter.");
            e.printStackTrace();
        }
    }

    private void createRepoTestingDirectory() {
        String folderPath = "../" + this.repository + "_" + this.workflow + "_" + this.temperature;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("Folder created: " + folderPath);
            } else {
                System.out.println("Failed to create folder: " + folderPath);
            }
        }
    }

    // Testing CSV creation
//    public static void main(String[] args) {
//        // Creating a sample CSVCreator instance
//        CSVCreator csvCreator = new CSVCreator("repo", "commit", "workflow", "0.5");
//        csvCreator.createRepoHeader();
//
//        // Sample TestResultAnalyzer data for compiled code
//        boolean isCompiled = true;
//        List<String> t1ProvidedTestNames = Arrays.asList("test1a", "test1b");
//        List<String> t1HiddenTestNames = Arrays.asList("test1c", "test1d", "test1e");
//        List<String> t2ProvidedTestNames = Arrays.asList("test2a", "test2b", "test2c");
//        List<String> t2HiddenTestNames = Arrays.asList("test2d", "test2e");
//        List<String> t3ProvidedTestNames = Arrays.asList("test3a", "test3b");
//        List<String> t3HiddenTestNames = Arrays.asList("test3c", "test3d", "test3e");
//        String errors = "";
//
//        TestResultAnalyzer testResults = new TestResultAnalyzer(isCompiled, t1ProvidedTestNames, t1HiddenTestNames,
//                t2ProvidedTestNames, t2HiddenTestNames, t3ProvidedTestNames, t3HiddenTestNames, errors);
//
//        // Sample TestResultAnalyzer data for non-compiled code
//        isCompiled = false;
//        errors = "/Users/cameronnathan/Documents/GitHub/WhoWroteThisCode-HumanOrAI/LLM Testing/repos_output/assignment-1-repository-4/src/main/java/nz/ac/auckland/se281/Main.java:[175,55] incompatible types: java.lang.String cannot be converted to int\n" +
//                "/Users/cameronnathan/Documents/GitHub/WhoWroteThisCode-HumanOrAI/LLM Testing/repos_output/assignment-1-repository-4/src/main/java/nz/ac/auckland/se281/InsuranceSystem.java:[19,13] package system does not exist";
//
//        TestResultAnalyzer testResults2 = new TestResultAnalyzer(isCompiled, t1ProvidedTestNames, t1HiddenTestNames,
//                t2ProvidedTestNames, t2HiddenTestNames, t3ProvidedTestNames, t3HiddenTestNames, errors);
//
//        csvCreator.addAttemptInfo(1, testResults);
//        csvCreator.addAttemptInfo(2, testResults2);
//
//        try {
//            csvCreator.save();
//            System.out.println("CSV file created successfully.");
//        } catch (IOException e) {
//            System.out.println("An error occurred while creating the CSV file.");
//            e.printStackTrace();
//        }
//    }

    public void createRepoHeader() {
        String[] initialHeader = {"Repo = " + repository + ", " + "Commit = " + commitHash + ", " + "Workflow = " + workflow + ", " + "Temperature = " + temperature};
        csvWriter.writeNext(initialHeader);
        String[] headers = {"Attempt","Task 1 (Provided)","Failed Tests","Task 1 (Hidden)","Failed Tests","Task 2 (Provided)","Failed Tests","Task 2 (Hidden)","Failed Tests","Task 3 (Provided)","Failed Tests","Task 3 (Hidden)","Failed Tests", "Compilation Errors"};
        csvWriter.writeNext(headers);
    }

    public void save() throws IOException {
        csvWriter.close();
    }

    public void addAttemptInfo(int attempt, TestResultAnalyzer testResults) {
        boolean isCompiled = testResults.isCompiled();
        Map<String, List<String>> testNames = testResults.getAllTestNames();
        String errors = testResults.getErrors();

        if (isCompiled) {
            int totalTask1Provided = 6;
            int totalTask1Hidden = 12;
            int totalTask2Provided = 8;
            int totalTask2Hidden = 16;
            int totalTask3Provided = 7;
            int totalTask3Hidden = 14;

            List<String> t1ProvidedTestNames = testNames.get("Test1Provided");
            List<String> t1HiddenTestNames = testNames.get("Test1Hidden");
            List<String> t2ProvidedTestNames = testNames.get("Test2Provided");
            List<String> t2HiddenTestNames = testNames.get("Test2Hidden");
            List<String> t3ProvidedTestNames = testNames.get("Test3Provided");
            List<String> t3HiddenTestNames = testNames.get("Test3Hidden");

            String task1ProvidedResults = (totalTask1Provided - t1ProvidedTestNames.size()) + "/" + totalTask1Provided;
            String task1HiddenResults = (totalTask1Hidden - t1HiddenTestNames.size()) + "/" + totalTask1Hidden;
            String task2ProvidedResults = (totalTask2Provided - t2ProvidedTestNames.size()) + "/" + totalTask2Provided;
            String task2HiddenResults = (totalTask2Hidden - t2HiddenTestNames.size()) + "/" + totalTask2Hidden;
            String task3ProvidedResults = (totalTask3Provided - t3ProvidedTestNames.size()) + "/" + totalTask3Provided;
            String task3HiddenResults = (totalTask3Hidden - t3HiddenTestNames.size()) + "/" + totalTask3Hidden;

            String[] attemptData = {String.valueOf(attempt), task1ProvidedResults, t1ProvidedTestNames.toString(), task1HiddenResults, t1HiddenTestNames.toString(), task2ProvidedResults, t2ProvidedTestNames.toString(), task2HiddenResults, t2HiddenTestNames.toString(), task3ProvidedResults, t3ProvidedTestNames.toString(), task3HiddenResults, t3HiddenTestNames.toString(), "-"};
            csvWriter.writeNext(attemptData);
        } else {
            String[] attemptData = {String.valueOf(attempt), "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", errors};
            csvWriter.writeNext(attemptData);
        }
    }

    private String getRepoNameFromDirectory(String directory) {
        File file = new File(directory);
        return file.getName();
    }
}
