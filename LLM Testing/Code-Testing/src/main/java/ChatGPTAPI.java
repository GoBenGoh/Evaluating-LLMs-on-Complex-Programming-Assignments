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
        String prompt = app.getFileFromResourceAsStream("Prompt Templates/Task2.txt");
        PromptWriter promptWriter = new PromptWriter(prompt, "package nz.ac.auckland.se281;\n" +
                "\n" +
                "import nz.ac.auckland.se281.Main.PolicyType;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class InsuranceSystem {\n" +
                "    private List<ClientProfile> database;\n" +
                "    private ClientProfile loadedProfile;\n" +
                "\n" +
                "    public InsuranceSystem() {\n" +
                "        database = new ArrayList<>();\n" +
                "        loadedProfile = null;\n" +
                "    }\n" +
                "\n" +
                "    public void printDatabase() {\n" +
                "        if (database.isEmpty()) {\n" +
                "            MessageCli.PRINT_DB_POLICY_COUNT.printMessage(\"0\", \"s\", \".\");\n" +
                "        } else {\n" +
                "            int count = database.size();\n" +
                "            MessageCli.PRINT_DB_POLICY_COUNT.printMessage(String.valueOf(count), count > 1 ? \"s\" : \"\", \":\");\n" +
                "            for (int i = 0; i < count; i++) {\n" +
                "                printProfile(database.get(i), i + 1);\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public void createNewProfile(String userName, String age) {\n" +
                "        if (!isUsernameValid(userName)) {\n" +
                "            MessageCli.INVALID_USERNAME_TOO_SHORT.printMessage(capitalizeFirstLetter(userName));\n" +
                "        } else if (!isUsernameUnique(userName)) {\n" +
                "            MessageCli.INVALID_USERNAME_NOT_UNIQUE.printMessage(capitalizeFirstLetter(userName));\n" +
                "        } else if (!isAgeValid(age)) {\n" +
                "            MessageCli.INVALID_AGE.printMessage(age, capitalizeFirstLetter(userName));\n" +
                "        } else {\n" +
                "            int parsedAge = Integer.parseInt(age);\n" +
                "            ClientProfile newProfile = new ClientProfile(capitalizeFirstLetter(userName), parsedAge);\n" +
                "            database.add(newProfile);\n" +
                "            MessageCli.PROFILE_CREATED.printMessage(capitalizeFirstLetter(userName), age);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public void loadProfile(String userName) {\n" +
                "        ClientProfile profile = findProfileByUsername(userName);\n" +
                "        if (profile != null) {\n" +
                "            setLoadedProfile(profile);\n" +
                "            MessageCli.PROFILE_LOADED.printMessage(capitalizeFirstLetter(userName));\n" +
                "        } else {\n" +
                "            MessageCli.NO_PROFILE_FOUND_TO_LOAD.printMessage(capitalizeFirstLetter(userName));\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public void unloadProfile() {\n" +
                "        if (isProfileLoaded()) {\n" +
                "            MessageCli.PROFILE_UNLOADED.printMessage(getLoadedProfile().getUsername());\n" +
                "            setLoadedProfile(null);\n" +
                "        } else {\n" +
                "            MessageCli.NO_PROFILE_LOADED.printMessage();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public void deleteProfile(String userName) {\n" +
                "        ClientProfile profile = findProfileByUsername(userName);\n" +
                "        if (profile == null) {\n" +
                "            MessageCli.NO_PROFILE_FOUND_TO_DELETE.printMessage(capitalizeFirstLetter(userName));\n" +
                "        } else if (isProfileLoaded() && getLoadedProfile().equals(profile)) {\n" +
                "            MessageCli.CANNOT_DELETE_PROFILE_WHILE_LOADED.printMessage(capitalizeFirstLetter(userName));\n" +
                "        } else {\n" +
                "            database.remove(profile);\n" +
                "            MessageCli.PROFILE_DELETED.printMessage(capitalizeFirstLetter(userName));\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public void createPolicy(PolicyType type, String[] options) {\n" +
                "        if (!isProfileLoaded()) {\n" +
                "            MessageCli.NO_PROFILE_FOUND_TO_CREATE_POLICY.printMessage();\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        switch (type) {\n" +
                "            case HOME:\n" +
                "                createHomePolicy(options);\n" +
                "                break;\n" +
                "            case CAR:\n" +
                "                createCarPolicy(options);\n" +
                "                break;\n" +
                "            case LIFE:\n" +
                "                createLifePolicy(options);\n" +
                "                break;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private boolean isUsernameValid(String username) {\n" +
                "        return username.length() >= 3;\n" +
                "    }\n" +
                "\n" +
                "    private boolean isUsernameUnique(String username) {\n" +
                "        for (ClientProfile profile : database) {\n" +
                "            if (profile.getUsername().equalsIgnoreCase(capitalizeFirstLetter(username))) {\n" +
                "                return false;\n" +
                "            }\n" +
                "        }\n" +
                "        return true;\n" +
                "    }\n" +
                "\n" +
                "    private boolean isAgeValid(String age) {\n" +
                "        try {\n" +
                "            int parsedAge = Integer.parseInt(age);\n" +
                "            return parsedAge > 0;\n" +
                "        } catch (NumberFormatException e) {\n" +
                "            return false;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private String capitalizeFirstLetter(String name) {\n" +
                "        String[] words = name.split(\" \");\n" +
                "        StringBuilder capitalized = new StringBuilder();\n" +
                "        for (String word : words) {\n" +
                "            capitalized.append(Character.toUpperCase(word.charAt(0)));\n" +
                "            capitalized.append(word.substring(1).toLowerCase());\n" +
                "            capitalized.append(\" \");\n" +
                "        }\n" +
                "        return capitalized.toString().trim();\n" +
                "    }\n" +
                "\n" +
                "    private ClientProfile findProfileByUsername(String username) {\n" +
                "        for (ClientProfile profile : database) {\n" +
                "            if (profile.getUsername().equalsIgnoreCase(capitalizeFirstLetter(username))) {\n" +
                "                return profile;\n" +
                "            }\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    private boolean isProfileLoaded() {\n" +
                "        return loadedProfile != null;\n" +
                "    }\n" +
                "\n" +
                "    private ClientProfile getLoadedProfile() {\n" +
                "        return loadedProfile;\n" +
                "    }\n" +
                "\n" +
                "    private void setLoadedProfile(ClientProfile profile) {\n" +
                "        loadedProfile = profile;\n" +
                "    }\n" +
                "\n" +
                "    private void printProfile(ClientProfile profile, int rank) {\n" +
                "        String prefix = \"\";\n" +
                "        if (isProfileLoaded() && profile.equals(getLoadedProfile())) {\n" +
                "            prefix = \"*** \";\n" +
                "        }\n" +
                "        System.out.printf(\" %s%d: %s, %d\\n\", prefix, rank, profile.getUsername(), profile.getAge());\n" +
                "    }\n" +
                "\n" +
                "    private void createHomePolicy(String[] options) {\n" +
                "        // TODO: Implement this method.\n" +
                "    }\n" +
                "\n" +
                "    private void createCarPolicy(String[] options) {\n" +
                "        // TODO: Implement this method.\n" +
                "    }\n" +
                "\n" +
                "    private void createLifePolicy(String[] options) {\n" +
                "        // TODO: Implement this method.\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class ClientProfile {\n" +
                "    private String username;\n" +
                "    private int age;\n" +
                "\n" +
                "    public ClientProfile(String username, int age) {\n" +
                "        this.username = username;\n" +
                "        this.age = age;\n" +
                "    }\n" +
                "\n" +
                "    public String getUsername() {\n" +
                "        return username;\n" +
                "    }\n" +
                "\n" +
                "    public int getAge() {\n" +
                "        return age;\n" +
                "    }\n" +
                "}", "task2");
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