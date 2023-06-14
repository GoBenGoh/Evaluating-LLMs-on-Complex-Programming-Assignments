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
            con.setRequestProperty("Authorization", "Bearer PLACEHOLDER KEY");
            //Make sure to REPLACE the path of the json file!
            String jsonInputString = FileHelper.readLinesAsString(new File("C:\\Users\\benja\\Documents\\GitHub\\WhoWroteThisCode-HumanOrAI\\LLM Testing\\ChatGPT-API\\Request.json"));
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
            try(PrintWriter out = new PrintWriter("response.txt")){
                out.println(response);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        prompts();
    }
}