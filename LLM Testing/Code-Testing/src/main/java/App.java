import org.eclipse.jgit.api.errors.GitAPIException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) throws IOException, GitAPIException, InterruptedException {
        // Test GPT's Own Progress
        String repo = "../assignment_template/assignment-1";
        String commit = "Initial-Commit";
        String workflow = "Own-Progress";
        String initialCommit = "package nz.ac.auckland.se281;\n" +
                "\n" +
                "import nz.ac.auckland.se281.Main.PolicyType;\n" +
                "\n" +
                "public class InsuranceSystem {\n" +
                "    public InsuranceSystem() {\n" +
                "        // Only this constructor can be used (if you need to initialise fields).\n" +
                "    }\n" +
                "\n" +
                "    public void printDatabase() {\n" +
                "        // TODO: Complete this method.\n" +
                "    }\n" +
                "\n" +
                "    public void createNewProfile(String userName, String age) {\n" +
                "        // TODO: Complete this method.\n" +
                "    }\n" +
                "\n" +
                "    public void loadProfile(String userName) {\n" +
                "        // TODO: Complete this method.\n" +
                "    }\n" +
                "\n" +
                "    public void unloadProfile() {\n" +
                "        // TODO: Complete this method.\n" +
                "    }\n" +
                "\n" +
                "    public void deleteProfile(String userName) {\n" +
                "        // TODO: Complete this method.\n" +
                "    }\n" +
                "\n" +
                "    public void createPolicy(PolicyType type, String[] options) {\n" +
                "        // TODO: Complete this method.\n" +
                "    }\n" +
                "}\n";
        ChatGPTAPI chatGPTAPI = new ChatGPTAPI(initialCommit);
        chatGPTAPI.runTestIterations(args, repo, commit, 1, workflow);

//        // Test GPT's Progress starting from students code
//        workflow = "Piggyback";
//        // Get student repo information from JSON
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<Map<String, Object>> repositoriesToTest = objectMapper.readValue(
//                new File("src/main/java/repos_config.json"),
//                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
//        );
//
//        for (Map<String, Object> repoConfig: repositoriesToTest) {
//            String studentRepo = (String) repoConfig.get("repo_path");
//            JGitRepoHandler repoHandler = new JGitRepoHandler(studentRepo);
//
//            List<Map<String, Object>> repoCommits = (List<Map<String, Object>>) repoConfig.get("repo_commits");
//            for (Map<String, Object> commitInfo : repoCommits) {
//                String commitHash = (String) commitInfo.get("commit_hash");
//                int commitNumber = (int) commitInfo.get("commit_number");
//
//                // Retrieve content of InsuranceSystem.java for current commit
//                String content = FileContentReader.getFileContent(studentRepo);
//
//                // Run testing on commit
//                chatGPTAPI = new ChatGPTAPI(content);
//                repoHandler.switchToCommit(commitHash);
//                chatGPTAPI.runTestIterations(args, studentRepo, commitHash, commitNumber, workflow);
//            }
//
//            // Switch back to the initial commit before closing the repository
//            String initialCommitHash = (String) repoConfig.get("initial_commit");
//            repoHandler.switchToCommit(initialCommitHash);
//            repoHandler.close();
//        }
    }

    public static List<String> generateRepoPaths(int numberOfRepos) {
        List<String> repoPaths = new ArrayList<>();

        for (int i = 1; i <= numberOfRepos; i++) {
            String repoPath = "../repos_output/assignment-1-repository-" + i;
            repoPaths.add(repoPath);
        }

        return repoPaths;
    }
}
