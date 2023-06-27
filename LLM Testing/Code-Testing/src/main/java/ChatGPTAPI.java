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
            try(PrintWriter out = new PrintWriter("response.txt")){
                out.println(response);
            }
            try(PrintWriter out = new PrintWriter("content.txt")){
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
        String prompt = app.getFileFromResourceAsStream("Prompt Templates/Task1_CompilationError.txt");
        PromptWriter promptWriter = new PromptWriter(prompt, "package nz.ac.auckland.se281;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class InsuranceSystem {\n" +
                "    private List<Profile> database;\n" +
                "\n" +
                "    public InsuranceSystem() {\n" +
                "        this.database = new ArrayList<>();\n" +
                "    }\n" +
                "\n" +
                "    public void printDatabase() {\n" +
                "        if (database.isEmpty()) {\n" +
                "            MessageCli.PRINT_DB_POLICY_COUNT.printMessage(\"0\", \"s\", \".\");\n" +
                "        } else if (database.size() == 1) {\n" +
                "            printSingleProfileDatabase();\n" +
                "        } else {\n" +
                "            printMultiProfileDatabase();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public void createProfile(String userName, String age) {\n" +
                "        if (isInvalidUsername(userName)) {\n" +
                "            MessageCli.INVALID_USERNAME_TOO_SHORT.printMessage(userName);\n" +
                "        } else if (isDuplicateUsername(userName)) {\n" +
                "            MessageCli.INVALID_USERNAME_NOT_UNIQUE.printMessage(userName);\n" +
                "        } else if (isInvalidAge(age)) {\n" +
                "            MessageCli.INVALID_AGE.printMessage(age, toTitleCase(userName));\n" +
                "        } else {\n" +
                "            int ageValue = Integer.parseInt(age);\n" +
                "            Profile newProfile = new Profile(toTitleCase(userName), ageValue);\n" +
                "            database.add(newProfile);\n" +
                "            MessageCli.PROFILE_CREATED.printMessage(toTitleCase(userName), String.valueOf(ageValue));\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public void printDB() {\n" +
                "        if (database.isEmpty()) {\n" +
                "            MessageCli.PRINT_DB_POLICY_COUNT.printMessage(\"0\", \"s\", \".\");\n" +
                "        } else if (database.size() == 1) {\n" +
                "            printSingleProfileDB();\n" +
                "        } else {\n" +
                "            printMultiProfileDB();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private boolean isInvalidUsername(String userName) {\n" +
                "        return userName.length() < 3;\n" +
                "    }\n" +
                "\n" +
                "    private boolean isDuplicateUsername(String userName) {\n" +
                "        return database.stream().anyMatch(profile -> profile.getUserName().equalsIgnoreCase(userName));\n" +
                "    }\n" +
                "\n" +
                "    private boolean isInvalidAge(String age) {\n" +
                "        try {\n" +
                "            int ageValue = Integer.parseInt(age);\n" +
                "            return ageValue <= 0;\n" +
                "        } catch (NumberFormatException e) {\n" +
                "            return true;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private String toTitleCase(String text) {\n" +
                "        String[] words = text.split(\" \");\n" +
                "        StringBuilder result = new StringBuilder();\n" +
                "        for (String word : words) {\n" +
                "            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(\" \");\n" +
                "        }\n" +
                "        return result.toString().trim();\n" +
                "    }\n" +
                "\n" +
                "    private void printSingleProfileDatabase() {\n" +
                "        Profile profile = database.get(0);\n" +
                "        MessageCli.PRINT_DB_POLICY_COUNT.printMessage(\"1\", \"profile\", \":\");\n" +
                "        System.out.println(\" \" + (database.indexOf(profile) + 1) + \": \" + profile.getUserName() + \", \" + profile.getAge());\n" +
                "    }\n" +
                "\n" +
                "    private void printMultiProfileDatabase() {\n" +
                "        int profileCount = database.size();\n" +
                "        MessageCli.PRINT_DB_POLICY_COUNT.printMessage(String.valueOf(profileCount), \"s\", \":\");\n" +
                "        for (int i = 0; i < profileCount; i++) {\n" +
                "            Profile profile = database.get(i);\n" +
                "            System.out.println(\" \" + (database.indexOf(profile) + 1) + \": \" + profile.getUserName() + \", \" + profile.getAge());\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private class Profile {\n" +
                "        private String userName;\n" +
                "        private int age;\n" +
                "\n" +
                "        public Profile(String userName, int age) {\n" +
                "            this.userName = userName;\n" +
                "            this.age = age;\n" +
                "        }\n" +
                "\n" +
                "        public String getUserName() {\n" +
                "            return userName;\n" +
                "        }\n" +
                "\n" +
                "        public int getAge() {\n" +
                "            return age;\n" +
                "        }\n" +
                "    }\n" +
                "}\n", "java: cannot find symbol\n" +
                "  symbol:   method printSingleProfileDB()\n" +
                "  location: class nz.ac.auckland.se281.InsuranceSystem", "task1C");
        String newPrompt = promptWriter.output();
        try(PrintWriter out = new PrintWriter("test.txt")){
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