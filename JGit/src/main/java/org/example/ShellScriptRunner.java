package org.example;

import java.io.IOException;

public class ShellScriptRunner {
    public static void main(String[] args) {
        try {
            String[] command = {"C:\\Program Files\\Git\\bin\\bash.exe", "-c", "./script.sh ./output COMPILE"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.inheritIO();
            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Script executed successfully.");
            } else {
                System.out.println("Script execution failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

