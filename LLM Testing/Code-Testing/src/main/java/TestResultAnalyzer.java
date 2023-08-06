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
    private final List<String> t1ProvidedTestNames;
    private final List<String> t1HiddenTestNames;

    private final List<String> t2ProvidedTestNames;
    private final List<String> t2HiddenTestNames;

    private final List<String> t3ProvidedTestNames;
    private final List<String> t3HiddenTestNames;
    private final String errors;

    public TestResultAnalyzer(boolean isCompiled, List<String> t1ProvidedTestNames, List<String> t1HiddenTestNames, List<String> t2ProvidedTestNames, List<String> t2HiddenTestNames, List<String> t3ProvidedTestNames, List<String> t3HiddenTestNames, String errors){
        this.isCompiled = isCompiled;
        this.t1ProvidedTestNames = t1ProvidedTestNames;
        this.t1HiddenTestNames = t1HiddenTestNames;
        this.t2ProvidedTestNames = t2ProvidedTestNames;
        this.t2HiddenTestNames = t2HiddenTestNames;
        this.t3ProvidedTestNames = t3ProvidedTestNames;
        this.t3HiddenTestNames = t3HiddenTestNames;
        this.errors = errors;
    }

    public boolean isCompiled() {
        return isCompiled;
    }

    public Map<String, List<String>> getAllTestNames() {
        Map<String, List<String>> allTestNames = new HashMap<>();
        allTestNames.put("Test1Provided", t1ProvidedTestNames);
        allTestNames.put("Test1Hidden", t1HiddenTestNames);
        allTestNames.put("Test2Provided", t2ProvidedTestNames);
        allTestNames.put("Test2Hidden", t2HiddenTestNames);
        allTestNames.put("Test3Provided", t3ProvidedTestNames);
        allTestNames.put("Test3Hidden", t3HiddenTestNames);
        return allTestNames;
    }

    public String getErrors() {
        return errors;
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


    public static List<Map<String, String>> extractFailedTestDetails(String xmlFilePath) {
        List<Map<String, String>> taskTestDetails = new ArrayList<>();
        taskTestDetails.add(new LinkedHashMap<>()); // Task 1 test cases
        taskTestDetails.add(new LinkedHashMap<>()); // Task 2 test cases
        taskTestDetails.add(new LinkedHashMap<>()); // Task 3 test cases

        try {
            // Create the XmlReportPoller with the desired maxAttempts and pollingInterval
            XmlReportPoller reportPoller = new XmlReportPoller(10, 1000); // 10 attempts with 1 second interval
            // Use the XmlReportPoller to get the report file
            File xmlReportFile = reportPoller.waitForReport(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlReportFile);
            document.getDocumentElement().normalize();

            NodeList failedTests = document.getElementsByTagName("testcase");

            for (int i = 0; i < failedTests.getLength(); i++) {
                Element testElement = (Element) failedTests.item(i);

                if (testElement.getElementsByTagName("failure").getLength() > 0) {
                    String testName = testElement.getAttribute("name");
                    String assertionMessage = testElement.getElementsByTagName("failure")
                            .item(0)
                            .getTextContent();

                    if (testName.startsWith("T1")) {
                        taskTestDetails.get(0).put(testName, assertionMessage);
                    } else if (testName.startsWith("T2")) {
                        taskTestDetails.get(1).put(testName, assertionMessage);
                    } else if (testName.startsWith("T3")) {
                        taskTestDetails.get(2).put(testName, assertionMessage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return taskTestDetails;
    }

}
