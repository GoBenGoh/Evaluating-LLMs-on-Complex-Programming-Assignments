import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException, GitAPIException, InterruptedException {
        // Test GPT's Own Progress
        String repo = "../assignment_template/assignment-1";
        String commit = "Initial-Commit";
        String workflow = "Own-Progress";
        ChatGPTAPI chatGPTAPI = new ChatGPTAPI();
        chatGPTAPI.runTestIterations(args, repo, commit, 1, workflow);

        // Test GPT's Progress starting from students code
        workflow = "Piggyback";
        // Will replace repos with selected list for testing
        List<String> repoPaths = generateRepoPaths(15);

        for (String studentRepo: repoPaths) {
            JGitRepoHandler repoHandler = new JGitRepoHandler(studentRepo);

            // Will replace all commit hashes with selected list for testing
            List<String> repoCommits = repoHandler.getAllCommitHashes();
            int commitNumber = 1;
            for (String commitHash: repoCommits) {
                chatGPTAPI = new ChatGPTAPI();
                repoHandler.switchToCommit(commitHash);
                chatGPTAPI.runTestIterations(args, studentRepo, commitHash, commitNumber, workflow);
                commitNumber++;
            }

            // Switch back to the initial commit before closing the repository
            repoHandler.switchToCommit(repoCommits.get(0));
            repoHandler.close();
        }
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
