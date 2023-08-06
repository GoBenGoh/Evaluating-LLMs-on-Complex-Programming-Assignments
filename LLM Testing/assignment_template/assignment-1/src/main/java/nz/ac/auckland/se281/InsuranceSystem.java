package nz.ac.auckland.se281;

import nz.ac.auckland.se281.Main.PolicyType;

import java.util.ArrayList;
import java.util.List;

public class InsuranceSystem {
    private List<ClientProfile> database;
    private ClientProfile loadedProfile;

    public InsuranceSystem() {
        database = new ArrayList<>();
        loadedProfile = null;
    }

    public void printDatabase() {
        if (database.isEmpty()) {
            MessageCli.PRINT_DB_POLICY_COUNT.printMessage("0", "s", ".");
        } else {
            int count = database.size();
            MessageCli.PRINT_DB_POLICY_COUNT.printMessage(String.valueOf(count), count > 1 ? "s" : "", ":");
            for (int i = 0; i < count; i++) {
                printProfile(database.get(i), i + 1);
            }
        }
    }

    public void createNewProfile(String userName, String age) {
        if (!isUsernameValid(userName)) {
            MessageCli.INVALID_USERNAME_TOO_SHORT.printMessage(capitalizeFirstLetter(userName));
        } else if (!isUsernameUnique(userName)) {
            MessageCli.INVALID_USERNAME_NOT_UNIQUE.printMessage(capitalizeFirstLetter(userName));
        } else if (!isAgeValid(age)) {
            MessageCli.INVALID_AGE.printMessage(age, capitalizeFirstLetter(userName));
        } else {
            int parsedAge = Integer.parseInt(age);
            ClientProfile newProfile = new ClientProfile(capitalizeFirstLetter(userName), parsedAge);
            database.add(newProfile);
            MessageCli.PROFILE_CREATED.printMessage(capitalizeFirstLetter(userName), age);
        }
    }

    public void loadProfile(String userName) {
        ClientProfile profile = findProfileByUsername(userName);
        if (profile != null) {
            setLoadedProfile(profile);
            MessageCli.PROFILE_LOADED.printMessage(capitalizeFirstLetter(userName));
        } else {
            MessageCli.NO_PROFILE_FOUND_TO_LOAD.printMessage(capitalizeFirstLetter(userName));
        }
    }

    public void unloadProfile() {
        if (isProfileLoaded()) {
            MessageCli.PROFILE_UNLOADED.printMessage(getLoadedProfile().getUsername());
            setLoadedProfile(null);
        } else {
            MessageCli.NO_PROFILE_LOADED.printMessage();
        }
    }

    public void deleteProfile(String userName) {
        ClientProfile profile = findProfileByUsername(userName);
        if (profile == null) {
            MessageCli.NO_PROFILE_FOUND_TO_DELETE.printMessage(capitalizeFirstLetter(userName));
        } else if (isProfileLoaded() && getLoadedProfile().equals(profile)) {
            MessageCli.CANNOT_DELETE_PROFILE_WHILE_LOADED.printMessage(capitalizeFirstLetter(userName));
        } else {
            database.remove(profile);
            MessageCli.PROFILE_DELETED.printMessage(capitalizeFirstLetter(userName));
        }
    }

    public void createPolicy(PolicyType type, String[] options) {
        if (!isProfileLoaded()) {
            MessageCli.NO_PROFILE_FOUND_TO_CREATE_POLICY.printMessage();
            return;
        }

        switch (type) {
            case HOME:
                createHomePolicy(options);
                break;
            case CAR:
                createCarPolicy(options);
                break;
            case LIFE:
                createLifePolicy(options);
                break;
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 3;
    }

    private boolean isUsernameUnique(String username) {
        for (ClientProfile profile : database) {
            if (profile.getUsername().equalsIgnoreCase(capitalizeFirstLetter(username))) {
                return false;
            }
        }
        return true;
    }

    private boolean isAgeValid(String age) {
        try {
            int parsedAge = Integer.parseInt(age);
            return parsedAge > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String capitalizeFirstLetter(String name) {
        String[] words = name.split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            capitalized.append(Character.toUpperCase(word.charAt(0)));
            capitalized.append(word.substring(1).toLowerCase());
            capitalized.append(" ");
        }
        return capitalized.toString().trim();
    }

    private ClientProfile findProfileByUsername(String username) {
        for (ClientProfile profile : database) {
            if (profile.getUsername().equalsIgnoreCase(capitalizeFirstLetter(username))) {
                return profile;
            }
        }
        return null;
    }

    private boolean isProfileLoaded() {
        return loadedProfile != null;
    }

    private ClientProfile getLoadedProfile() {
        return loadedProfile;
    }

    private void setLoadedProfile(ClientProfile profile) {
        loadedProfile = profile;
    }

    private void printProfile(ClientProfile profile, int rank) {
        String prefix = "";
        if (isProfileLoaded() && profile.equals(getLoadedProfile())) {
            prefix = "*** ";
        }
        System.out.printf(" %s%d: %s, %d\n", prefix, rank, profile.getUsername(), profile.getAge());
    }

    private void createHomePolicy(String[] options) {
        // TODO: Implement this method.
    }

    private void createCarPolicy(String[] options) {
        // TODO: Implement this method.
    }

    private void createLifePolicy(String[] options) {
        // TODO: Implement this method.
    }
}
