package nz.ac.auckland.se281;

import java.util.ArrayList;
import java.util.List;

public class InsuranceSystem {
    // Create a static list to represent the database
    private static List<ClientProfile> database = new ArrayList<>();

    // Implement the printDatabase() method
    public void printDatabase() {
        if (database.size() == 0) {
            System.out.println("Database has 0 profiles.");
        } else if (database.size() == 1) {
            System.out.println("Database has 1 profile:");
            System.out.println(" 1: " + database.get(0).getTitleCaseUsername() + ", " + database.get(0).getAge());
        } else {
            System.out.println("Database has " + database.size() + " profiles:");
            for (int i = 0; i < database.size(); i++) {
                System.out.println(" " + (i + 1) + ": " + database.get(i).getTitleCaseUsername() + ", "
                        + database.get(i).getAge());
            }
        }
    }

    // Implement the createNewProfile() method
    public void createNewProfile(String userName, String age) {
        // Check if username already exists in the database
        for (ClientProfile profile : database) {
            if (profile.getUsername().equalsIgnoreCase(userName)) {
                System.out.println("Usernames must be unique. No profile was created for '" + userName + "'.");
                return;
            }
        }

        // Check if username is at least 3 characters long
        if (userName.length() < 3) {
            System.out.println(
                    "'" + userName + "' is an invalid username, it should be at least 3 characters long. No profile was created.");
            return;
        }

        // Check if age is a positive integer
        try {
            int ageInt = Integer.parseInt(age);
            if (ageInt <= 0) {
                System.out.println(
                        "'" + age + "' is an invalid age, please provide a positive whole number only. No profile was created for " + userName + ".");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println(
                    "'" + age + "' is an invalid age, please provide a positive whole number only. No profile was created for " + userName + ".");
            return;
        }

        // Add the new profile to the database
        ClientProfile newProfile = new ClientProfile(userName, age);
        database.add(newProfile);

        // Print success message
        System.out.println("New profile created for " + newProfile.getTitleCaseUsername() + " with age "
                + newProfile.getAge() + ".");
    }
}

class ClientProfile {
    private final String username;
    private final String age;

    public ClientProfile(String username, String age) {
        this.username = username;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public String getAge() {
        return age;
    }

    public String getTitleCaseUsername() {
        return username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
    }
}