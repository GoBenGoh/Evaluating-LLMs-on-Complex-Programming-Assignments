import com.google.gson.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class ChatGPTAPI {
    public static void sendRequest(String request, String key) {
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            //Make sure you put the right Organization key saved earlier.
            con.setDoOutput(true);
            //Make sure you put the right API Key saved earlier.
            con.setRequestProperty("Authorization", "Bearer "+ key);
            //Make sure to REPLACE the path of the json file!
            String jsonInputString = readLinesAsString(new File(request));
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JsonObject jsonObject = JsonParser.parseString(String.valueOf(response)).getAsJsonObject();
            JsonElement choices =  jsonObject.get("choices");
            JsonElement message = choices.getAsJsonArray().get(0).getAsJsonObject().get("message");
            JsonElement content = message.getAsJsonObject().get("content");
            System.out.println(content);
            try(PrintWriter out = new PrintWriter("src/main/resources/response.txt")){
                out.println(response);
            }
            try(PrintWriter out = new PrintWriter("src/main/resources/content.txt")){
                out.println(content.getAsString());
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args) throws IOException {
        ChatGPTAPI app = new ChatGPTAPI();
        String responseContent = app.getFileFromResource("content.txt");
        String request;
        String promptTemplate;
        String error;
        String failure;
        PromptWriter promptWriter;
        if (args[0].equals("1")){
            request = "src/main/java/InitialTask1Request.json";
            promptTemplate = app.getFileFromResource("Prompt Templates/Task1.txt");
            promptWriter = new PromptWriter(promptTemplate, responseContent, "t1");
        }
        else if (args[0].equals("1c")){
            request = "src/main/java/Task1CompilationFailureRequest.json";
            promptTemplate = app.getFileFromResource("Prompt Templates/Task1_CompilationError.txt");
            error = app.getFileFromResource("errors.txt");
            promptWriter = new PromptWriter(promptTemplate, responseContent, error, "c");
        }
        else if (args[0].equals("1f")){
            request = "src/main/java/Task1FailedTestsRequest.json";
            promptTemplate = app.getFileFromResource("Prompt Templates/Task1_FailedTests.txt");
            failure = app.getFileFromResource("provided_failures.txt");
            promptWriter = new PromptWriter(promptTemplate, responseContent, failure, "f");
        }
        else if (args[0].equals("2")){
            request = "src/main/java/InitialTask2Request.json";
            promptTemplate = app.getFileFromResource("Prompt Templates/Task2.txt");
            promptWriter = new PromptWriter(promptTemplate, responseContent, "t2");
        }
        else if (args[0].equals("2c")){
            request = "src/main/java/Task2CompilationFailureRequest.json";
            promptTemplate = app.getFileFromResource("Prompt Templates/Task2_CompilationError.txt");
            error = app.getFileFromResource("errors.txt");
            promptWriter = new PromptWriter(promptTemplate, responseContent, error, "c");
        }
        else if (args[0].equals("2f")){
            request = "src/main/java/Task2FailedTestsRequest.json";
            promptTemplate = app.getFileFromResource("Prompt Templates/Task2_FailedTests.txt");
            failure = app.getFileFromResource("provided_failures.txt");
            promptWriter = new PromptWriter(promptTemplate, responseContent, failure, "f");
        }
        else {
            throw new RuntimeException("The prompt argument is invalid.");
        }
        String newPrompt = promptWriter.output(); // new prompt in string form
        Request newRequest;
        if (args[2] == null){ //default 0.7 temperature
            newRequest = new Request("gpt-3.5-turbo-16k", newPrompt, 0.7); // object for gson to convert
        }
        else{
            newRequest = new Request("gpt-3.5-turbo-16k", newPrompt, Double.valueOf(args[2])); // object for gson to convert
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonElement jsonElement = gson.toJsonTree(newRequest);
        String jsonString = gson.toJson(jsonElement);
        try(PrintWriter out = new PrintWriter(request)){
            out.println(jsonString);
        }
        try(PrintWriter out = new PrintWriter("src/main/resources/newPrompt.txt")){
            out.println(newPrompt);
        }
        sendRequest(request, args[1]);
        try {
            TextToJava.convertTextToJavaFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String repo = "../assignment-1";
        ShellScriptRunner.runTesting(repo);
    }
    private String getFileFromResource(String fileName) throws IOException {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        String inputStream = new String(classLoader.getResourceAsStream(fileName).readAllBytes(), StandardCharsets.UTF_8);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }

    }
    public static String readLinesAsString(File file) {
        List<String> returnLines = new LinkedList<String>();
        String text = "";
        try {
            text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
}