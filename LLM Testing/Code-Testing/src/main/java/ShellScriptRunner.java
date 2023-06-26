import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShellScriptRunner {
    public static void main(String[] args) {
        try {
            String[] command = {"C:\\Program Files\\Git\\bin\\bash.exe", "-c", "./Code-Testing/src/main/java/script.sh ./repos_output/assignment-1-repository-12 TEST"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read the script's output
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
            String outputString = output.toString();

            System.out.println("Output: " + outputString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

