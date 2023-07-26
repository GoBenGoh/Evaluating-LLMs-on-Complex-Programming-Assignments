import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVCreator {
    private CSVWriter csvWriter;
    private int currentRow;
    private String repository;
    private String commit;
    private String workflow;
    private String temperature;

    public CSVCreator(String repo, String commit, String workflow, String temperature) {
        this.repository = getRepoNameFromDirectory(repo);
        this.commit = commit;
        this.workflow = workflow;
        this.temperature = temperature;

        try {
            csvWriter = new CSVWriter(new FileWriter(this.repository + "_" + this.commit + ".csv"));
        } catch (IOException e) {
            System.out.println("An error occurred while initializing the CSVWriter.");
            e.printStackTrace();
        }
        currentRow = 0;
    }

    // Testing CSV creation
    public static void main(String[] args) {
        CSVCreator csvCreator = new CSVCreator("../assignment_template/assignment-1", "CommitHash", "WorkflowName", "0.5");
        csvCreator.createRepoHeader();

        try {
            csvCreator.save();
            System.out.println("CSV file created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating the CSV file.");
            e.printStackTrace();
        }
    }

    public void createRepoHeader() {
        String[] initialHeader = {"Repo = " + repository + ", " + "Commit = " + commit + ", " + "Workflow = " + workflow + ", " + "Temperature = " + temperature};
        csvWriter.writeNext(initialHeader);
        String[] headers = {"Attempt","Task 1 (Provided and Hidden)","Failed Tests","Task 2 (Provided and Hidden)","Failed Tests","Task 3 (Provided and Hidden)","Failed Tests","Compilation Errors"};
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
