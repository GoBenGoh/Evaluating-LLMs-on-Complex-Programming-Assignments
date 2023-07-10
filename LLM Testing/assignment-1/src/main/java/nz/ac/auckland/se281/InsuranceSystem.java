package nz.ac.auckland.se281;

import nz.ac.auckland.se281.Main.PolicyType;

import java.util.ArrayList;
import java.util.List;

public class InsuranceSystem {
    private List<ClientProfile> database;

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
            MessageCli.PRINT_DB_POLICY_COUNT.printMessage(String.valueOf(database.size()), "s", ":");
            for (int i = 0; i < database.size(); i++) {
                printProfile(database.get(i), i + 1);
            }
        }
    }

    private void printProfile(ClientProfile profile, int rank) {
        MessageCli.PRINT_DB_PROFILE_HEADER_MINIMAL.printMessage(String.valueOf(rank), profile.getUsername(), String.valueOf(profile.getAge()));
    }

    public void createNewProfile(String userName, String age) {
        if (userName.length() < 3) {
            MessageCli.INVALID_USERNAME_TOO_SHORT.printMessage(userName);
            return;
        }

        int parsedAge;
        try {
            parsedAge = Integer.parseInt(age);
        } catch (NumberFormatException e) {
            MessageCli.INVALID_AGE.printMessage(age, userName);
            return;
        }

        if (parsedAge <= 0) {
            MessageCli.INVALID_AGE.printMessage(age, userName);
            return;
        }

        for (ClientProfile profile : database) {
            if (profile.getUsername().equalsIgnoreCase(userName)) {
                MessageCli.INVALID_USERNAME_NOT_UNIQUE.printMessage(userName);
                return;
            }
        }

        ClientProfile newProfile = new ClientProfile(capitalizeName(userName), parsedAge);
        database.add(newProfile);
        MessageCli.PROFILE_CREATED.printMessage(newProfile.getUsername(), String.valueOf(newProfile.getAge()));
    }

    private String capitalizeName(String name) {
        String[] words = name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1).toLowerCase());
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    public void loadProfile(String userName) {
        ClientProfile profile = findProfile(userName);
        if (profile == null) {
            MessageCli.NO_PROFILE_FOUND_TO_LOAD.printMessage(userName);
        } else if (profile.isLoaded()) {
            MessageCli.PROFILE_LOADED.printMessage(profile.getUsername());
        } else {
            profile.setLoaded(true);
            MessageCli.PROFILE_LOADED.printMessage(profile.getUsername());
        }
    }

    public void unloadProfile() {
        ClientProfile loadedProfile = findLoadedProfile();
        if (loadedProfile == null) {
            MessageCli.NO_PROFILE_LOADED.printMessage();
        } else {
            loadedProfile.setLoaded(false);
            MessageCli.PROFILE_UNLOADED.printMessage(loadedProfile.getUsername());
        }
    }

    public void deleteProfile(String userName) {
        ClientProfile profile = findProfile(userName);
        if (profile == null) {
            MessageCli.NO_PROFILE_FOUND_TO_DELETE.printMessage(userName);
        } else if (profile.isLoaded()) {
            MessageCli.CANNOT_DELETE_PROFILE_WHILE_LOADED.printMessage(profile.getUsername());
        } else {
            database.remove(profile);
            MessageCli.PROFILE_DELETED.printMessage(profile.getUsername());
        }
    }

    public void createPolicy(PolicyType type, String[] options) {
        ClientProfile loadedProfile = findLoadedProfile();
        if (loadedProfile == null) {
            MessageCli.NO_PROFILE_FOUND_TO_CREATE_POLICY.printMessage();
            return;
        }

        if (type == PolicyType.LIFE && loadedProfile.hasLifePolicy()) {
            MessageCli.ALREADY_HAS_LIFE_POLICY.printMessage(loadedProfile.getUsername());
            return;
        }

        if (type == PolicyType.LIFE && loadedProfile.getAge() >= 65) {
            MessageCli.OVER_AGE_LIMIT_LIFE_POLICY.printMessage(loadedProfile.getUsername());
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

    private void createHomePolicy(ClientProfile profile, String[] options) {
        double sumInsured = Double.parseDouble(options[0]);
        boolean isRental = options[1].equalsIgnoreCase("y");
        HomePolicy homePolicy = new HomePolicy(profile, sumInsured, isRental);
        profile.addPolicy(homePolicy);
        MessageCli.NEW_POLICY_CREATED.printMessage("home", profile.getUsername());
    }

    private void createCarPolicy(ClientProfile profile, String[] options) {
        double sumInsured = Double.parseDouble(options[0]);
        String makeAndModel = options[1];
        String licensePlate = options[2];
        boolean hasMechanicalWarranty = options[3].equalsIgnoreCase("y");
        CarPolicy carPolicy = new CarPolicy(profile, sumInsured, makeAndModel, licensePlate, hasMechanicalWarranty);
        profile.addPolicy(carPolicy);
        MessageCli.NEW_POLICY_CREATED.printMessage("car", profile.getUsername());
    }

    private void createLifePolicy(ClientProfile profile, String[] options) {
        double sumInsured = Double.parseDouble(options[0]);
        LifePolicy lifePolicy = new LifePolicy(profile, sumInsured);
        profile.addPolicy(lifePolicy);
        MessageCli.NEW_POLICY_CREATED.printMessage("life", profile.getUsername());
    }

    private ClientProfile findProfile(String userName) {
        for (ClientProfile profile : database) {
            if (profile.getUsername().equalsIgnoreCase(userName)) {
                return profile;
            }
        }
        return null;
    }

    private ClientProfile findLoadedProfile() {
        for (ClientProfile profile : database) {
            if (profile.isLoaded()) {
                return profile;
            }
        }
        return null;
    }
}