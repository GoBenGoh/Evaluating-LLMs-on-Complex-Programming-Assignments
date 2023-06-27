import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShellScriptRunner {
    public static void main(String[] args) {
        String repositoryDirectory = "./repos_output/assignment-1-repository-12";
        String compileResponse = runCommand(repositoryDirectory, "COMPILE");

        boolean isBuildSucceeded = compileResponse.contains("BUILD SUCCESS");
        if (isBuildSucceeded) {
            System.out.println("Build Succeeded");

            runCommand(repositoryDirectory, "CLEAR_TESTS");
            System.out.println("Tests Cleared");

            runCommand(repositoryDirectory, "ADD_PROVIDED_TESTS");
            System.out.println("Added Provided Tests");

//            runCommand(repositoryDirectory, "ADD_HIDDEN_TESTS");
//            System.out.println("Added Hidden Tests");

            String testResponse = runCommand(repositoryDirectory, "TEST");
            System.out.println(testResponse);
        } else {
            System.out.println("Build Failed");
        }
    }

    private static String runCommand(String repositoryDirectory, String command) {
        try {
            String[] scriptCommand = {"C:\\Program Files\\Git\\bin\\bash.exe", "-c", "./Code-Testing/src/main/java/script.sh " + repositoryDirectory + " " + command};
            ProcessBuilder processBuilder = new ProcessBuilder(scriptCommand);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            if (command.equals("CLEAR_TESTS")) {
                return "";
            } else {
                return readOutput(process);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String readOutput(Process process) throws IOException {
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }
        return output.toString();
    }
}

