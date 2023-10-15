import com.google.gson.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This class is responsible for sending requests to the GPT API and testing its responses.
 */
public class ChatGPTAPI {
    public String content;
    public ChatGPTAPI(String content){
        this.content = content;
    }
    public boolean badResponse = false;

    /**
     * This method sends POST requests to the GPT API.
     * It also logs the requests to GPT and responses from GPT.
     * @param request
     * @param key
     * @param isNaturalLanguageRequest
     * @param temperature
     * @param attempt
     * @param repo
     * @param workflow
     * @param commitNumber
     * @return
     */
    public String sendGPTRequest(String request, String key, boolean isNaturalLanguageRequest, double temperature,
                                 int attempt, String repo, String workflow, int commitNumber) {
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
            Logger.logToGPT(jsonInputString, temperature, attempt, repo, workflow, commitNumber);
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
            String content = getResponseContent(response);
            if (isNaturalLanguageRequest){
                createResponseTxtFiles("src/main/resources/NaturalLanguageResponse.txt", response,
                        "src/main/resources/NaturalLanguageContent.txt", content);
            }
            else{
                createResponseTxtFiles("src/main/resources/response.txt", response,
                        "src/main/resources/content.txt", content);
                Logger.logFromGPT(response, content, temperature, attempt, repo, workflow, commitNumber);
                this.content=content;
            }
            return content;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        throw new RuntimeException("Unexpected Error in sendGPTRequest");
    }

    /**
     * This method records the responses from GPT into txt files.
     * @param fileName
     * @param response
     * @param fileName1
     * @param content
     * @throws FileNotFoundException
     */
    private static void createResponseTxtFiles(String fileName, StringBuffer response, String fileName1, String content)
            throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(response);
        }
        try (PrintWriter out = new PrintWriter(fileName1)) {
            out.println(content);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method extracts the message content from GPT's response.
     * @param response
     * @return
     */
    private static String getResponseContent(StringBuffer response) {
        JsonObject jsonObject = JsonParser.parseString(String.valueOf(response)).getAsJsonObject();
        JsonElement choices =  jsonObject.get("choices");
        JsonElement message = choices.getAsJsonArray().get(0).getAsJsonObject().get("message");
        JsonElement content = message.getAsJsonObject().get("content");
        return content.getAsString();
    }

    /**
     * This method tests GPT's code completion on a given commit.
     * It has 10 iterations to pass all the test cases.
     * Testing for the commit will stop before 10 iterations if all test cases pass.
     * @param args
     * @param repo
     * @param commit
     * @param commitNumber
     * @param workflow
     * @throws IOException
     */
    public void runTestIterations(String[] args, String repo, String commit, int commitNumber, String workflow)
            throws IOException{
        boolean task1 = true;
        boolean isStart = true;

        String temperature = String.valueOf(args[1]);
        String commitOrder = String.valueOf(commitNumber);
        CSVCreator CSVCreator = new CSVCreator(repo, commit, commitOrder, workflow, temperature);
        CSVCreator.createRepoHeader();
        TestResultAnalyzer resultAnalyzer = new TestResultAnalyzer(false, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "");

        for(int i = 0; i < 10; i++){
            int attempt = i + 1;
            System.out.println(attempt);

            if (isStart){
                resultAnalyzer = startTesting("1", args, repo, attempt, CSVCreator, resultAnalyzer);
                if (!this.badResponse){
                    isStart = false;
                }
            }
            else {
                String errors = resultAnalyzer.getErrors();
                String task1Failures = resultAnalyzer.getT1Failures();
                String task2Failures = resultAnalyzer.getT2Failures();

                if(errors != ""){  // If there are compilation errors in GPT's code
                    System.out.println("Last iteration had compilation errors");
                    if (task1)
                        resultAnalyzer = startTesting("1c", args, repo, attempt, CSVCreator, resultAnalyzer);
                    else
                        resultAnalyzer = startTesting("2c", args, repo, attempt, CSVCreator, resultAnalyzer);
                }
                else{
                    // If there are task 1 failures in GPT's code
                    if (!task1Failures.equals("")) {
                        System.out.println("Last iteration had Task 1 test failures: \n");
                        System.out.println(task1Failures);
                        resultAnalyzer = startTesting("1f", args, repo, attempt, CSVCreator, resultAnalyzer);
                    }
                    // If there are task 2 failures in GPT's code
                    else if (task1Failures.equals("") && !task2Failures.equals("")) {
                        System.out.println("Last iteration had Task 2 test failures: \n");
                        System.out.println(task2Failures);
                        task1 = false;
                        resultAnalyzer = startTesting("2f", args, repo, attempt, CSVCreator, resultAnalyzer);
                    }
                    // All tests pass
                    else{
                        System.out.println("All tests passed");
                        CSVCreator.save();
                        return;
                    }
                }
            }
        }
        System.out.println("10 iterations reached");
        CSVCreator.save();
    }

    /**
     * This method is responsible for creating requests to GPT to complete code.
     * The responses are parsed and tested here.
     * Results are recorded here.
     * @param mode
     * @param args
     * @param repo
     * @param attempt
     * @param CSVCreator
     * @param priorResults
     * @return
     * @throws IOException
     */
    private TestResultAnalyzer startTesting(String mode, String[] args, String repo, int attempt, CSVCreator CSVCreator,
                                            TestResultAnalyzer priorResults)
            throws IOException{
        String responseContent = this.content;
        String jsonRequest = setJsonRequest(mode);
        boolean isNaturalLanguage = false;
        if (args[2] == "natural"){
            isNaturalLanguage = true;
        }
        PromptWriter promptWriter = setPromptWriter(mode, responseContent, isNaturalLanguage, priorResults);

        String newPrompt = promptWriter.output(); // new prompt in string form
        Request newRequest;
        if (args[1] == null){ //default 0.7 temperature
            // object for gson to convert
            newRequest = new Request("gpt-3.5-turbo-16k", newPrompt, 0.7);
        }
        else{
            // object for gson to convert
            newRequest = new Request("gpt-3.5-turbo-16k", newPrompt, Double.valueOf(args[1]));
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create(); // format JSON
        JsonElement jsonElement = gson.toJsonTree(newRequest);
        String jsonString = gson.toJson(jsonElement);
        try(PrintWriter out = new PrintWriter(jsonRequest)){
            out.println(jsonString);
        }
        try(PrintWriter out = new PrintWriter("src/main/resources/newPrompt.txt")){
            out.println(newPrompt);
        }
        // Ask GPT to complete the code
        System.out.println("Sending request to ChatGPT");
        String content = sendGPTRequest(jsonRequest, args[0], false, Double.valueOf(args[1]),
                attempt, CSVCreator.repository, CSVCreator.workflow, Integer.parseInt(CSVCreator.commitNumber));
        if (content.indexOf("package nz.ac.auckland.se281") == -1){ // Bad response by ChatGPT
            System.out.println("Bad Response from ChatGPT!");
            // Set the content to the same content before the GPT request
            this.content = responseContent;
            // Set badResponse status in case it is the first iteration
            this.badResponse = true;
            // Return last results so the next iteration has the same prompt in hopes for a better response
            return priorResults;
        }
        this.badResponse = false;
        try {
            TextToJava.convertStringToJavaFile(content, repo);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Run Testing
        TestResultAnalyzer testingResults = ShellScriptRunner.runTesting(repo);

        // If natural language explanation for errors is specified
        if(args.length == 3){
            if(!testingResults.isCompiled() && args[2].equals("natural")){
                String nLRequest = writeNaturalLanguageJson(testingResults.getErrors());
                System.out.println("Sending compilation error natural language request");

                // Ask GPT to explain compilation errors
                sendGPTRequest(nLRequest, args[0], true, Double.valueOf(args[1]),
                        attempt, CSVCreator.repository, CSVCreator.workflow, Integer.parseInt(CSVCreator.commitNumber));
            }
            else{
                System.out.println("Not sending natural language request");
            }
        }

        // Update CSV file with testing results
        CSVCreator.addAttemptInfo(attempt, testingResults);
        return testingResults;
    }

    /**
     * This method is not fully functional.
     * It is meant to create the JSON request for a natural language prompt to ask GPT to explain compilation failures.
     * @param errorMessages
     * @return
     * @throws IOException
     */
    @Deprecated
    public String writeNaturalLanguageJson(String errorMessages) throws IOException {
        String promptTemplate = getFileFromResource("Prompt Templates/NaturalLanguageError.txt");
        String responseContent = this.content;
        String newPrompt = new PromptWriter(promptTemplate, responseContent, errorMessages, "naturalLanguage")
                .createNaturalLanguageErrorPrompt();

        String jsonRequest = "src/main/java/NaturalLanguageRequest.json";
        Request newRequest = new Request("gpt-3.5-turbo", newPrompt); // object for gson to convert
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonElement jsonElement = gson.toJsonTree(newRequest);
        String jsonString = gson.toJson(jsonElement);
        try(PrintWriter out = new PrintWriter(jsonRequest)){
            out.println(jsonString);
        }
        try(PrintWriter out = new PrintWriter("src/main/resources/newNaturalLanguageErrorPrompt.txt")){
            out.println(newPrompt);
        }
        return jsonRequest;
    }

    /**
     * This method writes prompts based on prompt templates in the resources folder.
     * @param mode
     * @param responseContent
     * @param isNaturalLanguage
     * @param results
     * @return
     * @throws IOException
     */
    private static PromptWriter setPromptWriter(String mode, String responseContent,
                                                boolean isNaturalLanguage, TestResultAnalyzer results)
            throws IOException {
        String promptTemplate;
        String error;
        String failure;
        if (mode.equals("1")){
            promptTemplate = getFileFromResource("Prompt Templates/Task1.txt");
            return new PromptWriter(promptTemplate, responseContent, "t1");
        }
        else if (mode.equals("1c")){
            promptTemplate = getFileFromResource("Prompt Templates/Task1_CompilationError.txt");
            if (isNaturalLanguage){
                error = getFileFromResource("NaturalLanguageContent.txt");
            }
            else{
                error = results.getErrors();
            }
            return new PromptWriter(promptTemplate, responseContent, error, "c");
        }
        else if (mode.equals("1f")){
            promptTemplate = getFileFromResource("Prompt Templates/Task1_FailedTests.txt");
            failure = results.getT1Failures();
            System.out.println("T1 Failures: \n" + failure);
            return new PromptWriter(promptTemplate, responseContent, failure, "f");
        }
        else if (mode.equals("2")){
            promptTemplate = getFileFromResource("Prompt Templates/Task2.txt");
            return new PromptWriter(promptTemplate, responseContent, "t2");
        }
        else if (mode.equals("2c")){
            promptTemplate = getFileFromResource("Prompt Templates/Task2_CompilationError.txt");
            if (isNaturalLanguage){
                error = getFileFromResource("NaturalLanguageContent.txt");
            }
            else{
                error = results.getErrors();
            }
            return new PromptWriter(promptTemplate, responseContent, error, "c");
        }
        else if (mode.equals("2f")){
            promptTemplate = getFileFromResource("Prompt Templates/Task2_FailedTests.txt");
            failure = results.getT2Failures();
            System.out.println("T2 Failures: \n" + failure);
            return new PromptWriter(promptTemplate, responseContent, failure, "f");
        }
        else {
            throw new RuntimeException("The prompt argument is invalid.");
        }
    }

    /**
     * Sets the JSON request to be used in the next POST request to the GPT API.
     * @param mode
     * @return
     */
    private static String setJsonRequest(String mode){
        if (mode.equals("1")){
            return "src/main/java/InitialTask1Request.json";
        }
        else if (mode.equals("1c")){
            return "src/main/java/Task1CompilationFailureRequest.json";
        }
        else if (mode.equals("1f")){
            return "src/main/java/Task1FailedTestsRequest.json";
        }
        else if (mode.equals("2")){
            return  "src/main/java/InitialTask2Request.json";
        }
        else if (mode.equals("2c")){
            return "src/main/java/Task2CompilationFailureRequest.json";
        }
        else if (mode.equals("2f")){
            return  "src/main/java/Task2FailedTestsRequest.json";
        }
        else {
            throw new RuntimeException("The prompt argument is invalid.");
        }
    }

    /**
     * This method helps get files from the resource folder.
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String getFileFromResource(String fileName) throws IOException {
        // The class loader that loaded the class
        ClassLoader classLoader = ChatGPTAPI.class.getClassLoader();
        String inputStream = new String(classLoader.getResourceAsStream(fileName).readAllBytes(),
                StandardCharsets.UTF_8);

        // The stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }

    }

    public static String readLinesAsString(File file) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())),
                    StandardCharsets.UTF_8);

        return text;
    }
}