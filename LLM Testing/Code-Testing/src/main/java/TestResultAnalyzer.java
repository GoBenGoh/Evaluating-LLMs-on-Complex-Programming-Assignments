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
    private final String errors;

    private final List<String> providedTestNames;

    private final List<String> hiddenTestNames;

    public TestResultAnalyzer(boolean isCompiled, int numPassedProvidedTests, int numPassedHiddenTests, int totalProvidedTests, int totalHiddenTests, String errors, List<String> providedTestNames, List<String> hiddenTestNames){
        this.isCompiled = isCompiled;
        this.numPassedProvidedTests = numPassedProvidedTests;
        this.numPassedHiddenTests = numPassedHiddenTests;
        this.totalProvidedTests = totalProvidedTests;
        this.totalHiddenTests = totalHiddenTests;
        this.errors = errors;
        this.providedTestNames = providedTestNames;
        this.hiddenTestNames = hiddenTestNames;
    }

    public static List<Integer> extractTestResults(String xmlFilePath) {
        List<Integer> values = new ArrayList<>();

        try {
            File file = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            Element testsuiteElement = (Element) document.getElementsByTagName("testsuite").item(0);
            int tests = Integer.parseInt(testsuiteElement.getAttribute("tests"));
            int errors = Integer.parseInt(testsuiteElement.getAttribute("errors"));
            int skipped = Integer.parseInt(testsuiteElement.getAttribute("skipped"));
            int failures = Integer.parseInt(testsuiteElement.getAttribute("failures"));

            values.add(tests);
            values.add(failures);
            values.add(errors);
            values.add(skipped);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    public static String getCompilationErrors(String compResponse) {
        int errorsStart = compResponse.indexOf("[ERROR] COMPILATION ERROR : "); // Errors start here in compResponse
        if (errorsStart != -1){ // If there are errors
            String errors = compResponse.substring(errorsStart);
            int replaceFrom = errors.indexOf("[INFO] -------------------------------------------------------------");
            if(replaceFrom!=-1) {
                errors = errors.substring(replaceFrom + 70); // Removes new line and [INFO]------
                int replaceTo = errors.indexOf("[INFO]"); // Stop at next [INFO]
                errors = errors.substring(0, replaceTo);
            }
            return errors;
        }

        // If no match is found, return an empty string
        return "";
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
