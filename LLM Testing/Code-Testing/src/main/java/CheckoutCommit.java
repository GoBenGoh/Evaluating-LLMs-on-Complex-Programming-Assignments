package org.example;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import java.io.File;
import java.io.IOException;

public class CheckoutCommit
{
    public static void main( String[] args ) {
        String repositoryPath = "../repos_output/assignment-1-repository-1/.git";
        String destinationPath = "./output";

        try {
            // Clone the repository
            cloneRepository(repositoryPath, destinationPath);

            // Open the cloned repository
            try (Repository clonedRepository = openRepository(destinationPath)) {
                Git git = new Git(clonedRepository);

                //printCommitHistory(git);

                String commitId = "3fc44552d024222305c022970a5c6b46b4dae57b";
                checkoutCommit(git, commitId);
                printCommitDetails(clonedRepository, commitId);
            }
        } catch (IOException | GitAPIException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static void cloneRepository(String repositoryPath, String destinationPath)
            throws GitAPIException {
        Git.cloneRepository()
                .setURI(repositoryPath)
                .setDirectory(new File(destinationPath))
                .call();
    }

    private static Repository openRepository(String destinationPath) throws IOException {
        return FileRepositoryBuilder.create(new File(destinationPath + "/.git"));
    }

    private static void printCommitHistory(Git git) throws GitAPIException, IOException {
        Iterable<RevCommit> commits = git.log().all().call();
        for (RevCommit commit : commits) {
            System.out.println("Commit ID: " + commit.getId().getName());
            System.out.println("Commit message: " + commit.getFullMessage());
            System.out.println();
        }
    }

    private static void checkoutCommit(Git git, String commitId) throws GitAPIException {
        CheckoutCommand checkoutCommand = git.checkout().setName(commitId);
        checkoutCommand.call();
        System.out.println("Checkout completed successfully.");
    }

    private static void printCommitDetails(Repository repository, String commitId) throws IOException {
        RevCommit commit = repository.parseCommit(repository.resolve(commitId));
        System.out.println("Commit ID: " + commit.getId().getName());
        System.out.println("Commit message: " + commit.getFullMessage());
    }
}
