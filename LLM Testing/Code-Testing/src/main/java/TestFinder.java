import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class is used for extracting the actual test methods within MainTest.java or MainTestHidden.java to send back to
 * GPT as part of the test failure prompt.
 */
public class TestFinder {

    public static List<String> extractTestMethods(List<String> testNames, String type) {
        Map<String, String> foundTestMethods = new LinkedHashMap<>();

        String filePath = "";
        if (type.equals("PROVIDED")) {
            filePath = "../Tests/MainTest.java";
        } else if (type.equals("HIDDEN")) {
            filePath = "../Tests/MainTestHidden.java";
        }

        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
            CompilationUnit cu = StaticJavaParser.parse(fileContent);
            TestMethodVisitor visitor = new TestMethodVisitor(testNames, foundTestMethods);
            visitor.visit(cu, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> orderedTestMethods = new ArrayList<>();
        for (String testName : testNames) {
            if (foundTestMethods.containsKey(testName)) {
                orderedTestMethods.add(foundTestMethods.get(testName));
            }
        }
        return orderedTestMethods;
    }

    private static class TestMethodVisitor extends VoidVisitorAdapter<Void> {
        private final List<String> testNames;
        private final Map<String, String> foundTestMethods;

        public TestMethodVisitor(List<String> testNames, Map<String, String> foundTestMethods) {
            this.testNames = testNames;
            this.foundTestMethods = foundTestMethods;
        }

        @Override
        public void visit(MethodDeclaration md, Void arg) {
            String methodName = md.getNameAsString();
            if (testNames.contains(methodName) && !foundTestMethods.containsKey(methodName)) {
                foundTestMethods.put(methodName, LexicalPreservingPrinter.print(md));
            }
        }
    }
}
