/**
 * This class is responsible for writing the prompts sent to GPT.
 */
public class PromptWriter {
    private String prompt;
    private String response;
    private String type;
    private String extra;

    public PromptWriter(String prompt, String response, String type){
        this.prompt = prompt;
        this.response=response;
        this.type=type;
    }
    public PromptWriter(String prompt, String response, String extra, String type){
        this.prompt = prompt;
        this.response=response;
        this.type=type;
        this.extra = extra;
    }

    /**
     * Creates a prompt depending on the type of request that needs to be sent.
     * It places code, compilation errors, and test failures into the correct placeholder areas of each prompt template.
     * @return
     */
    public String output(){
        int start = response.indexOf("package nz.ac.auckland.se281");
        String trimmedResponse = response.substring(start);
        if (trimmedResponse.charAt(trimmedResponse.length()-1)=='`'){
            trimmedResponse=trimmedResponse.substring(0,trimmedResponse.length()-4); // remove backticks
        }

        if(type == "c"){
            int errorStart = prompt.indexOf("The compilation error is displayed below.") + 46; // Start after the lines above
            String newPrompt = modifyPrompt(prompt, errorStart, extra);
            int codeStart = newPrompt.indexOf("Write your response in one java file.") + 42; // Start after the lines above
            newPrompt = modifyPrompt(newPrompt, codeStart, trimmedResponse);
            return newPrompt;
        }
        else if(type == "f") {
            int codeStart = prompt.indexOf("Write your response in one java file.") + 42; // Start after the lines above
            String newPrompt = modifyPrompt(prompt, codeStart, trimmedResponse);
            int testStart = newPrompt.indexOf("Here are the failing test cases:") + 37; // Start after the lines above
            newPrompt = modifyPrompt(newPrompt, testStart, extra);
            return newPrompt;
        }
        else if (type == "t1" || type == "t2"){
            int codeStart = prompt.indexOf("Write your response in one java file.") + 42; // Start after the lines above
            String newPrompt = modifyPrompt(prompt, codeStart, trimmedResponse);
            return newPrompt;
        }
        else{
            throw new RuntimeException("Type must be c, f, t1, or t2");
        }
    }

    /**
     * This method writes the compilation errors into the placeholder area of the natural language explanation prompt.
     * This method is currently unused as the natural language functionality is not fully implemented.
     * @return
     */
    public String createNaturalLanguageErrorPrompt(){
        int responseStart = response.indexOf("package nz.ac.auckland.se281");
        String trimmedResponse = response.substring(responseStart);
        if (trimmedResponse.charAt(trimmedResponse.length()-1)=='`'){
            trimmedResponse=trimmedResponse.substring(0,trimmedResponse.length()-4); // remove backticks
        }
        int start = 61;
        String newPrompt = modifyPrompt(prompt, start, extra);
        int codeStart =
                newPrompt.indexOf("The next section contains the code that is throwing the compilation errors:") + 80;
        newPrompt = modifyPrompt(newPrompt, codeStart, trimmedResponse);
        return newPrompt;
    }

    /**
     * This method inserts messages into the placeholder sections of prompts.
     * @param prompt
     * @param index
     * @param message
     * @return
     */
    private String modifyPrompt(String prompt, int index, String message){
        String newString = new String();
        for (int i = 0; i < prompt.length(); i++) {

            // Insert the original string character
            // into the new string
            newString += prompt.charAt(i);

            if (i == index) {

                // Insert the string to be inserted
                // into the new string
                newString += message;
            }
        }
        return newString;
    }

}
