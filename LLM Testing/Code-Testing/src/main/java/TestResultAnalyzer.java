import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestResultAnalyzer {
    private final boolean isCompiled;
    private final int numPassedProvidedTests;
    private final int numPassedHiddenTests;
    private final int totalProvidedTests;
    private final int totalHiddenTests;
    private final List<String> errors;

    private final List<String> providedTestNames;

    private final List<String> hiddenTestNames;

    public TestResultAnalyzer(boolean isCompiled, int numPassedProvidedTests, int numPassedHiddenTests, int totalProvidedTests, int totalHiddenTests, List<String> errors, List<String> providedTestNames, List<String> hiddenTestNames){
        this.isCompiled = isCompiled;
        this.numPassedProvidedTests = numPassedProvidedTests;
        this.numPassedHiddenTests = numPassedHiddenTests;
        this.totalProvidedTests = totalProvidedTests;
        this.totalHiddenTests = totalHiddenTests;
        this.errors = errors;
        this.providedTestNames = providedTestNames;
        this.hiddenTestNames = hiddenTestNames;
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

    public static Map<String, String> extractFailedTestDetails(String xmlFilePath) {
        Map<String, String> failedTestDetails = new LinkedHashMap<>();

        try {
            File file = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            NodeList failedTests = document.getElementsByTagName("testcase");

            for (int i = 0; i < failedTests.getLength(); i++) {
                Element testElement = (Element) failedTests.item(i);

                if (testElement.getElementsByTagName("failure").getLength() > 0) {
                    String testName = testElement.getAttribute("name");
                    String assertionMessage = testElement.getElementsByTagName("failure")
                            .item(0)
                            .getTextContent();

                    failedTestDetails.put(testName, assertionMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return failedTestDetails;
    }

}
