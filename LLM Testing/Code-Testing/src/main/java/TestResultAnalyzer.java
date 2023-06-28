import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestResultAnalyzer {
    private final boolean isCompiled;
    private final int numPassedProvidedTests;
    private final int numPassedHiddenTests;
    private final int totalProvidedTests;
    private final int totalHiddenTests;
    private final ArrayList<String> errors;

    public TestResultAnalyzer(boolean isCompiled, int numPassedProvidedTests, int numPassedHiddenTests, int totalProvidedTests, int totalHiddenTests, ArrayList<String> errors){
        this.isCompiled = isCompiled;
        this.numPassedProvidedTests = numPassedProvidedTests;
        this.numPassedHiddenTests = numPassedHiddenTests;
        this.totalProvidedTests = totalProvidedTests;
        this.totalHiddenTests = totalHiddenTests;
        this.errors = errors;
    }

    public static int getValue(String testingResponse, String valuePattern) {
        Pattern pattern = Pattern.compile(valuePattern + "(\\d+)");
        Matcher matcher = pattern.matcher(testingResponse);

        if (matcher.find()) {
            String testsRun = matcher.group(1);
            return Integer.parseInt(testsRun);
        } else {
            return -1;
        }
    }

    public static ArrayList<String> getCompilationErrors(String compResponse) {
        Set<String> uniqueErrors = new LinkedHashSet<>(); // Use LinkedHashSet to maintain the order of insertion

        Pattern pattern = Pattern.compile("\\[ERROR\\] (.+java:\\[\\d+,\\d+\\] .+)");
        Matcher matcher = pattern.matcher(compResponse);

        while (matcher.find()) {
            String errorLine = matcher.group(1);
            uniqueErrors.add(errorLine);
        }

        return new ArrayList<>(uniqueErrors);
    }
}