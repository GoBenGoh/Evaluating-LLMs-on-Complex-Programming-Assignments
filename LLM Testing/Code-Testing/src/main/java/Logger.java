import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Logger {
    public static void logFromGPT(StringBuffer response, String content, double temperature, int attempt, String repository,
                                  String workflow, int commitNumber)
            throws FileNotFoundException {
        String directory = "../" + repository + "_" + workflow + "_" + temperature + "/commit_" + commitNumber + "/";
        DirectoryCreator.createDirectory(directory);

        try (PrintWriter responseWriter = new PrintWriter(directory+"response_"+attempt+".json")){
            responseWriter.println(response);
        }
        try (PrintWriter contentWriter = new PrintWriter(directory+"content_"+attempt+".txt")){
            contentWriter.println(content);
        }
    }

    public static void logToGPT(String request, double temperature, int attempt, String repository,
                                String workflow, int commitNumber) throws FileNotFoundException {
        String directory = "../" + repository + "_" + workflow + "_" + temperature + "/commit_" + commitNumber +"/";
        DirectoryCreator.createDirectory(directory);

        try (PrintWriter responseWriter = new PrintWriter(directory+"request_"+attempt+".json")){
            responseWriter.println(request);
        }
    }
}
