import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVCreator {
    private CSVWriter csvWriter;
    private int currentRow;

    public CSVCreator() {
        try {
            csvWriter = new CSVWriter(new FileWriter("data.csv"));
        } catch (IOException e) {
            System.out.println("An error occurred while initializing the CSVWriter.");
            e.printStackTrace();
        }
        currentRow = 0;
    }

    // Testing CSV creation
    public static void main(String[] args) {
        CSVCreator csvCreator = new CSVCreator();
        csvCreator.createRepoHeader("../assignment_template/assignment-1", "CommitHash", "WorkflowName", "0.5");

        try {
            csvCreator.save();
            System.out.println("CSV file created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating the CSV file.");
            e.printStackTrace();
        }
    }

    public void createRepoHeader(String repo, String commit, String workflow, String temperature) {
        String[] initialHeader = {getRepoNameFromDirectory(repo) + " " + commit + " " + workflow + " Temperature = " + temperature};
        csvWriter.writeNext(initialHeader);

        createAttemptHeader();
    }

    private void createAttemptHeader() {
        String[] headers = {"Attempt", "Task 1", "Task 2", "Task 3", "Reason for Failure"};
        csvWriter.writeNext(headers);
    }

    public void save() throws IOException {
        csvWriter.close();
    }

    private String getRepoNameFromDirectory(String directory) {
        File file = new File(directory);
        return file.getName();
    }
}
