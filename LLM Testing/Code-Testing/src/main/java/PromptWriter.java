import java.io.File;

public class PromptWriter {
    private File file;
    private String prompt;
    private String response;
    private String type;
    private String error;

    public PromptWriter(String prompt, String response, String type){
        this.prompt = prompt;
        this.response=response;
        this.type=type;
    }
    public PromptWriter(String prompt, String response, String error, String type){
        this.prompt = prompt;
        this.response=response;
        this.type=type;
        this.error=error;
    }

    public String output(){
        int start = response.indexOf("package nz.ac.auckland.se281");
        String trimmedResponse = response.substring(start);
        if (trimmedResponse.charAt(trimmedResponse.length()-1)=='`'){
            trimmedResponse=trimmedResponse.substring(0,trimmedResponse.length()-4); // remove backticks
        }

        if(type == "task1C"){
            int errorStart = prompt.indexOf("The compilation error is displayed below.") + 46; // Start after the lines above
            System.out.println(errorStart);
            String newPrompt = modifyPrompt(prompt, errorStart, error);
            int codeStart = newPrompt.indexOf("Only respond with java code!") + 33; // Start after the lines above
            newPrompt = modifyPrompt(newPrompt, codeStart, response);
            return newPrompt;
        }
        else if(type == "task1F") {
            int codeStart = prompt.indexOf("Please fix the following code according to the instructions and the failing" +
                    " test cases. Do not assume another class exists! Do not explain the changes made!" +
                    " Only respond with java code!\n" +
                    "```") + 189; // Start at after the lines above
            String newPrompt = modifyPrompt(prompt, codeStart, response);
            return newPrompt;
        }
        else if (type == "task2"){
            int codeStart = prompt.indexOf("Please fix the following code according to these instructions. Do not" +
                    " assume another class exists! Do not explain the changes made! Only respond with java code!\n" +
                    "```") + 164; // Start at after the lines above
            String newPrompt = modifyPrompt(prompt, codeStart, response);
            return newPrompt;
        }
        else if(type == "task2C"){
            int codeStart = prompt.indexOf("Please rewrite the code below so that it compiles. " +
                    "Do not assume another class exists! Do not explain the changes made! " +
                    "Only respond with java code!\n" +
                    "```") + 152;
            String newPrompt = modifyPrompt(prompt, codeStart, response);
            return newPrompt;
        }
        else if(type == "task2F"){
            int codeStart = prompt.indexOf("Please fix the following code according to the instructions and the failing " +
                    "test cases. Do not assume another class exists! Do not explain the changes made! " +
                    "Only respond with java code!\n" +
                    "```") + 189;
            String newPrompt = modifyPrompt(prompt, codeStart, response);
            return newPrompt;
        }
        else{
            return "Error";
        }
    }

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
