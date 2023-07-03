import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestFinder {

    public static List<String> extractTestMethods(List<String> testNames, String type) {
        List<String> foundTestMethods = new ArrayList<>();

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

        return foundTestMethods;
    }

    private static class TestMethodVisitor extends VoidVisitorAdapter<Void> {
        private final List<String> testNames;
        private final List<String> foundTestMethods;

        public TestMethodVisitor(List<String> testNames, List<String> foundTestMethods) {
            this.testNames = testNames;
            this.foundTestMethods = foundTestMethods;
        }

        @Override
        public void visit(MethodDeclaration md, Void arg) {
            // Check if the method is a test method with a name in the provided list
            if (md.getAnnotationByName("Test").isPresent() && testNames.contains(md.getNameAsString())) {
                foundTestMethods.add(LexicalPreservingPrinter.print(md));
            }
        }
    }
}
