import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShellScriptRunner {
    public static void main(String[] args) {
        try {
            String[] command = {"C:\\Program Files\\Git\\bin\\bash.exe", "-c", "./Code-Testing/src/main/java/script.sh ./repos_output/assignment-1-repository-12 COMPILE"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read the script's output
            String outputString = readOutput(process);

            boolean isBuildSucceeded = outputString.contains("BUILD SUCCESS");
            if (isBuildSucceeded) {
                System.out.println("Build Succeeded");
            } else {
                System.out.println("Build Failed");
            }

        } catch (IOException e) {
            e.printStackTrace();
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

