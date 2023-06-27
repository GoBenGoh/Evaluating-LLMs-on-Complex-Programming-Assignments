import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
public class ChatGPTAPI {
    public static void prompts() {
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            //Make sure you put the right Organization key saved earlier.
            con.setDoOutput(true);
            //Make sure you put the right API Key saved earlier.
            con.setRequestProperty("Authorization", "Bearer PLACEHOLDER_KEY");
            //Make sure to REPLACE the path of the json file!
            String jsonInputString = FileHelper.readLinesAsString(new File("src/main/java/Task1CompilationFailureRequest.json"));
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
        String prompt = app.getFileFromResourceAsStream("Prompt Templates/Task2_CompilationError.txt");
        String responseContent = app.getFileFromResourceAsStream("content.txt");
        PromptWriter promptWriter = new PromptWriter(prompt, responseContent,"java: cannot find symbol\n" +
                "  symbol:   method printSingleProfileDB()\n" +
                "  location: class nz.ac.auckland.se281.InsuranceSystem", "c");
        String newPrompt = promptWriter.output();
        try(PrintWriter out = new PrintWriter("src/main/resources/prompt.txt")){
            out.println(newPrompt);
        }
        //prompts();
    }
    private String getFileFromResourceAsStream(String fileName) throws IOException {

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
}