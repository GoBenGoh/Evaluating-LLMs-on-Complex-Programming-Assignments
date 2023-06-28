import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestResultAnalyzer {
    private final boolean isCompiled;
    private final int numPassedProvidedTests;
    private final int numPassedHiddenTests;
    private final int totalProvidedTests;
    private final int totalHiddenTests;
    private final String[] errors;

    public TestResultAnalyzer(boolean isCompiled, int numPassedProvidedTests, int numPassedHiddenTests, int totalProvidedTests, int totalHiddenTests, String[] errors){
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
}
