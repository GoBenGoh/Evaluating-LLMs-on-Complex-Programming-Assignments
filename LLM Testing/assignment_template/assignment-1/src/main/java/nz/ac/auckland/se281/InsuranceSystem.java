package nz.ac.auckland.se281;

import nz.ac.auckland.se281.Main.PolicyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsuranceSystem {
    private List<Profile> database;

    public InsuranceSystem() {
        // Only this constructor can be used (if you need to initialise fields).
        this.database = new ArrayList<>();
    }

    public void printDatabase() {
        // Print the entire insurance database.
        int size = database.size();

        if (size == 0) {
            MessageCli.PRINT_DB_POLICY_COUNT.printMessage("0", "s", ".");
        } else if (size == 1) {
            MessageCli.PRINT_DB_POLICY_COUNT.printMessage("1", "", ":");
            printProfile(database.get(0), 1);
        } else {
            MessageCli.PRINT_DB_POLICY_COUNT.printMessage(String.valueOf(size), "s", ":");
            for (int i = 0; i < size; i++) {
                printProfile(database.get(i), i + 1);
            }
        }
    }

    private void printProfile(Profile profile, int rank) {
        String userName = profile.getUserName();
        int age = profile.getAge();

        String profileHeader = MessageCli.PRINT_DB_PROFILE_HEADER_MINIMAL.getMessage(String.valueOf(rank), userName, String.valueOf(age));
        System.out.println(profileHeader);
    }

    public void createNewProfile(String userName, String age) {
        // Create a new client profile with the specified username and age (passed as arguments to the CREATE_PROFILE command).
        // The username should be unique across all other usernames currently in the system,
        // and the age should be a positive integer.
        if (!isUniqueUsername(userName)) {
            MessageCli.INVALID_USERNAME_NOT_UNIQUE.printMessage(userName);
            return;
        }

        int parsedAge = parseAge(age);
        if (parsedAge <= 0) {
            MessageCli.INVALID_AGE.printMessage(age, userName);
            return;
        }

        Profile newProfile = new Profile(userName, parsedAge);
        database.add(newProfile);
        MessageCli.PROFILE_CREATED.printMessage(userName, age);
    }

    private boolean isUniqueUsername(String username) {
        for (Profile profile : database) {
            if (profile.getUserName().equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }

    private int parseAge(String age) {
        try {
            return Integer.parseInt(age);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void loadProfile(String userName) {
        // Load the specified profile <USERNAME>
        Profile profile = getProfileByUsername(userName);
        if (profile == null) {
            MessageCli.NO_PROFILE_FOUND_TO_LOAD.printMessage(userName);
            return;
        }

        profile.setLoaded(true);
        MessageCli.PROFILE_LOADED.printMessage(userName);
    }

    private Profile getProfileByUsername(String username) {
        for (Profile profile : database) {
            if (profile.getUserName().equalsIgnoreCase(username)) {
                return profile;
            }
        }
        return null;
    }

    public void unloadProfile() {
        // Unload the currently-loaded profile
        Profile loadedProfile = getLoadedProfile();
        if (loadedProfile == null) {
            MessageCli.NO_PROFILE_LOADED.printMessage();
            return;
        }

        loadedProfile.setLoaded(false);
        MessageCli.PROFILE_UNLOADED.printMessage(loadedProfile.getUserName());
    }

    private Profile getLoadedProfile() {
        for (Profile profile : database) {
            if (profile.isLoaded()) {
                return profile;
            }
        }
        return null;
    }

    public void deleteProfile(String userName) {
        // Delete the specified profile <USERNAME> from the database
        Profile profile = getProfileByUsername(userName);
        if (profile == null) {
            MessageCli.NO_PROFILE_FOUND_TO_DELETE.printMessage(userName);
            return;
        }

        boolean isLoaded = profile.isLoaded();
        if (isLoaded) {
            MessageCli.CANNOT_DELETE_PROFILE_WHILE_LOADED.printMessage(userName);
            return;
        }

        database.remove(profile);
        MessageCli.PROFILE_DELETED.printMessage(userName);
    }

    public void createPolicy(PolicyType type, String[] options) {
        // Create a new policy for the currently-loaded profile
        Profile loadedProfile = getLoadedProfile();
        if (loadedProfile == null) {
            MessageCli.NO_PROFILE_FOUND_TO_CREATE_POLICY.printMessage();
            return;
        }

        if (loadedProfile.hasLifePolicy() && type == PolicyType.LIFE) {
            MessageCli.ALREADY_HAS_LIFE_POLICY.printMessage(loadedProfile.getUserName());
            return;
        }

        switch (type) {
            case HOME:
                createHomePolicy(loadedProfile, options);
                break;
            case CAR:
                createCarPolicy(loadedProfile, options);
                break;
            case LIFE:
                createLifePolicy(loadedProfile, options);
                break;
        }
    }

    private void createHomePolicy(Profile profile, String[] options) {
        // Create a new home policy
    }

    private void createCarPolicy(Profile profile, String[] options) {
        // Create a new car policy
    }

    private void createLifePolicy(Profile profile, String[] options) {
        // Create a new life policy
    }
}

