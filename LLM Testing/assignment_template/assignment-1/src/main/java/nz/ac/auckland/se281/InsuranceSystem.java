package nz.ac.auckland.se281;

import nz.ac.auckland.se281.Main.PolicyType;

import java.util.ArrayList;
import java.util.List;

public class InsuranceSystem {
    
    private List<Profile> database;
    
    public InsuranceSystem() {
        database = new ArrayList<>();
    }

    public void printDatabase() {
        if (database.isEmpty()) {
            MessageCli.PRINT_DB_POLICY_COUNT.printMessage("0", "s", ".");
        } else if (database.size() == 1) {
            MessageCli.PRINT_DB_POLICY_COUNT.printMessage("1", "", ":");
            printProfile(database.get(0), 1);
        } else {
            MessageCli.PRINT_DB_POLICY_COUNT.printMessage(Integer.toString(database.size()), "s", ":");
            for (int i = 0; i < database.size(); i++) {
                printProfile(database.get(i), i + 1);
            }
        }
    }

    public void createNewProfile(String userName, String age) {
        userName = toTitleCase(userName);
        int ageInt;
        try {
            ageInt = Integer.parseInt(age);
            if (userName.length() < 3) {
                MessageCli.INVALID_USERNAME_TOO_SHORT.printMessage(userName);
            } else if (!isUnique(userName)) {
                MessageCli.INVALID_USERNAME_NOT_UNIQUE.printMessage(userName);
            } else if (ageInt < 1) {
                MessageCli.INVALID_AGE.printMessage(age, userName);
            } else {
                Profile newProfile = new Profile(userName, ageInt);
                database.add(newProfile);
                MessageCli.PROFILE_CREATED.printMessage(userName, age);
            }
        } catch (NumberFormatException e) {
            MessageCli.INVALID_AGE.printMessage(age, userName);
        }
    }

    public void loadProfile(String userName) {
        userName = toTitleCase(userName);
        for (Profile profile : database) {
            if (profile.getUserName().equals(userName)) {
                MessageCli.PROFILE_LOADED.printMessage(userName);
                return;
            }
        }
        MessageCli.NO_PROFILE_FOUND_TO_LOAD.printMessage(userName);
    }

    public void unloadProfile() {
        MessageCli.PROFILE_UNLOADED.printMessage("");
    }

    public void deleteProfile(String userName) {
        userName = toTitleCase(userName);
        for (Profile profile : database) {
            if (profile.getUserName().equals(userName)) {
                database.remove(profile);
                MessageCli.PROFILE_DELETED.printMessage(userName);
                return;
            }
        }
        MessageCli.NO_PROFILE_FOUND_TO_DELETE.printMessage(userName);
    }

    public void createPolicy(PolicyType type, String[] options) {
        // TODO: Complete this method.
    }
    
    private String toTitleCase(String word) {
        String[] words = word.split(" ");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String firstLetter = words[i].substring(0, 1);
            String restOfWord = words[i].substring(1);
            result.append(firstLetter.toUpperCase()).append(restOfWord.toLowerCase());
            if (i < words.length - 1) {
                result.append(" ");
            }
        }
        return result.toString();
    }
    
    private boolean isUnique(String userName) {
        for (Profile profile : database) {
            if (profile.getUserName().equals(userName)) {
                return false;
            }
        }
        return true;
    }
    
    private void printProfile(Profile profile, int rank) {
        MessageCli.PRINT_DB_PROFILE_HEADER_MINIMAL.printMessage(Integer.toString(rank), profile.getUserName(),
                Integer.toString(profile.getAge()));
    }
}