import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for creating the CSVs that record testing results.
 */
public class CSVCreator {
    public CSVWriter csvWriter;
    public String repository;
    public String commitHash;
    public String commitNumber;
    public String workflow;
    public String temperature;

    public CSVCreator(String repo, String commitHash, String commitNumber, String workflow, String temperature) {
        this.repository = getRepoNameFromDirectory(repo);
        this.commitHash = commitHash;
        this.commitNumber = commitNumber;
        this.workflow = workflow;
        this.temperature = temperature;
        String folderPath = "../" + this.repository + "_" + this.workflow + "_" + this.temperature;
        DirectoryCreator.createDirectory(folderPath);
        try {
            csvWriter = new CSVWriter(new FileWriter("../" + this.repository + "_" + this.workflow + "_" +
                    this.temperature + "/commit_" + this.commitNumber + ".csv"));
        } catch (IOException e) {
            System.out.println("An error occurred while initializing the CSVWriter.");
            e.printStackTrace();
        }
    }

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
