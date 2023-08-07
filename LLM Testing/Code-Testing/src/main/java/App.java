import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        String repo = "../assignment_template/assignment-1";
        String commit = "Initial-Commit";
        String workflow = "Own-Progress";

        ChatGPTAPI.runTestIterations(args, repo, commit, workflow);
    }
}
