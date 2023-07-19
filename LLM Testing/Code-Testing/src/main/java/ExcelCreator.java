import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelCreator {
    private Workbook workbook;
    private Sheet sheet;
    private int currentRow;

    public ExcelCreator() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Data");
        currentRow = 0;
    }

    // Testing excel creation
    public static void main(String[] args) {
        ExcelCreator excelCreator = new ExcelCreator();
        excelCreator.createRepoHeader("../assignment_template/assignment-1", "CommitHash", "WorkflowName", "0.5");

        try {
            excelCreator.save("../data.xlsx");
            System.out.println("Spreadsheet created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating the spreadsheet.");
            e.printStackTrace();
        }
    }

    public void createRepoHeader(String repo, String commit, String workflow, String temperature) {
        Row initialHeaderRow = sheet.createRow(currentRow++);
        Cell initialHeaderCell = initialHeaderRow.createCell(0);
        initialHeaderCell.setCellValue(getRepoNameFromDirectory(repo) + " " + commit + " " + workflow + " Temperature = " + temperature);

        createAttemptHeader();
    }

    private void createAttemptHeader() {
        Row headerRow = sheet.createRow(currentRow++);
        String[] headers = {"Attempt", "Task 1", "Task 2", "Task 3", "Reason for Failure"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    public void save(String filePath) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }
    }

    private String getRepoNameFromDirectory(String directory) {
        File file = new File(directory);
        String repoName = file.getName();
        return repoName;
    }
}
