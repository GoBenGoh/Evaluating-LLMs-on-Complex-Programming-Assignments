import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestFinder {

    public static List<String> findTests(List<String> testNames, String type) {
        List<String> foundTests = new ArrayList<>();

        String filePath = "";
        if (type.equals("PROVIDED")) {
            filePath = "./Tests/MainTest.java";
        } else if (type.equals("HIDDEN")) {
            filePath = "./Tests/MainTestHidden.java";
        }

        String fileContent = readFileContent(filePath);

        for (String testName : testNames) {
            String pattern = "@Test\\s+public\\s+void\\s+" + testName + "\\(";
            Matcher matcher = Pattern.compile(pattern).matcher(fileContent);
            if (matcher.find()) {
                String testMethod = extractTestMethod(fileContent, matcher.start());
                foundTests.add(testMethod);
            }
        }

        return foundTests;
    }

    private static String readFileContent(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private static String extractTestMethod(String fileContent, int startIndex) {
        int endIndex = fileContent.indexOf("}", startIndex);
        return fileContent.substring(startIndex, endIndex + 1);
    }
}
