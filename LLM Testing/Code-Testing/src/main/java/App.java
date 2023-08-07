import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException, GitAPIException {
//        String repo = "../assignment_template/assignment-1";
//        String commit = "Initial-Commit";
//        String workflow = "Own-Progress";
//
//        ChatGPTAPI.runTestIterations(args, repo, commit, workflow);

        String repo = "../repos_output/assignment-1-repository-1";
        JGitRepoHandler repoHandler = new JGitRepoHandler(repo);
        List<String> repoCommits = repoHandler.getAllCommitHashes();
        for (String commit: repoCommits) {
            System.out.println(commit);
        }

    }
}
