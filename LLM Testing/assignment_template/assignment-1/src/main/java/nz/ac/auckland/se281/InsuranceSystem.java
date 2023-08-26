package nz.ac.auckland.se281;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsuranceSystem {
    private final Map<String, Profile> database;
    private Profile loadedProfile;

    public InsuranceSystem() {
        database = new HashMap<>();
        loadedProfile = null;
    }

    public void printDatabase() {
        int profileCount = database.size();
        if (profileCount == 0) {
            MessageCli.PRINT_DB_POLICY_COUNT.printMessage("0", "s", ".");
        } else if (profileCount == 1) {
            MessageCli.PRINT_DB_POLICY_COUNT.printMessage("1", "", ":");
            printProfile(database.get(1));
        } else {
            MessageCli.PRINT_DB_POLICY_COUNT.printMessage(String.valueOf(profileCount), "s", ":");
            for (Profile profile : database.values()) {
                printProfile(profile);
            }
        }
    }

    public void createNewProfile(String userName, String age) {
        if (userName.length() < 3) {
            MessageCli.INVALID_USERNAME_TOO_SHORT.printMessage(userName);
            return;
        }

        userName = toTitleCase(userName);

        if (database.containsKey(userName)) {
            MessageCli.INVALID_USERNAME_NOT_UNIQUE.printMessage(userName);
            return;
        }

        int ageValue;
        try {
            ageValue = Integer.parseInt(age);
        } catch (NumberFormatException e) {
            MessageCli.INVALID_AGE.printMessage(age, userName);
            return;
        }

        if (ageValue <= 0) {
            MessageCli.INVALID_AGE.printMessage(age, userName);
            return;
        }

        Profile newProfile = new Profile(userName, ageValue);
        database.put(userName, newProfile);
        MessageCli.PROFILE_CREATED.printMessage(userName, age);
    }

    public void loadProfile(String userName) {
        if (loadedProfile != null) {
            if (loadedProfile.getUserName().equalsIgnoreCase(userName)) {
                MessageCli.PROFILE_LOADED.printMessage(userName);
                return;
            } else {
                MessageCli.CANNOT_CREATE_WHILE_LOADED.printMessage(loadedProfile.getUserName());
                return;
            }
        }

        if (!database.containsKey(userName)) {
            MessageCli.NO_PROFILE_FOUND_TO_LOAD.printMessage(userName);
            return;
        }

        loadedProfile = database.get(userName);
        MessageCli.PROFILE_LOADED.printMessage(userName);
    }

    public void unloadProfile() {
        if (loadedProfile == null) {
            MessageCli.NO_PROFILE_LOADED.printMessage();
            return;
        }

        loadedProfile = null;
        MessageCli.PROFILE_UNLOADED.printMessage();
    }

    public void deleteProfile(String userName) {
        if (loadedProfile != null && loadedProfile.getUserName().equalsIgnoreCase(userName)) {
            MessageCli.CANNOT_DELETE_PROFILE_WHILE_LOADED.printMessage(userName);
            return;
        }

        if (!database.containsKey(userName)) {
            MessageCli.NO_PROFILE_FOUND_TO_DELETE.printMessage(userName);
            return;
        }

        database.remove(userName);
        MessageCli.PROFILE_DELETED.printMessage(userName);
    }

    public void createPolicy(Main.PolicyType type, String[] options) {
        if (loadedProfile == null) {
            MessageCli.NO_PROFILE_FOUND_TO_CREATE_POLICY.printMessage();
            return;
        }

        if (type == Main.PolicyType.LIFE && loadedProfile.getLifePolicy() != null) {
            MessageCli.ALREADY_HAS_LIFE_POLICY.printMessage(loadedProfile.getUserName());
            return;
        }

        if (type == Main.PolicyType.LIFE && loadedProfile.getAge() > Profile.MAX_LIFE_POLICY_AGE) {
            MessageCli.OVER_AGE_LIMIT_LIFE_POLICY.printMessage(loadedProfile.getUserName());
            return;
        }

        Policy newPolicy;
        switch (type) {
            case HOME:
                newPolicy = new HomePolicy(options[0], options[1], Boolean.parseBoolean(options[2]));
                break;
            case CAR:
                newPolicy = new CarPolicy(options[0], options[1], options[2], Boolean.parseBoolean(options[3]));
                break;
            case LIFE:
                newPolicy = new LifePolicy(options[0]);
                break;
            default:
                return;
        }

        loadedProfile.addPolicy(newPolicy);
        MessageCli.NEW_POLICY_CREATED.printMessage(type.toString(), loadedProfile.getUserName());
        calculateDiscount();
    }

    private void printProfile(Profile profile) {
        int profileNumber = getProfileNumber(profile);
        String username = profile.getUserName();
        int age = profile.getAge();

        MessageCli.PRINT_DB_PROFILE_HEADER_MINIMAL.printMessage(profileNumber, username, age);
        for (Policy policy : profile.getPolicies()) {
            printPolicy(policy);
        }
    }

    private void printPolicy(Policy policy) {
        if (policy instanceof CarPolicy) {
            CarPolicy carPolicy = (CarPolicy) policy;
            String makeAndModel = carPolicy.getMakeAndModel();
            String licensePlate = carPolicy.getLicensePlate();
            boolean hasMechanicalWarranty = carPolicy.hasMechanicalWarranty();
            MessageCli.PRINT_DB_CAR_POLICY.printMessage(makeAndModel, carPolicy.getSumInsured(),
                    carPolicy.getPremium(), carPolicy.getPremiumAfterDiscount());
        } else if (policy instanceof HomePolicy) {
            HomePolicy homePolicy = (HomePolicy) policy;
            String propertyAddress = homePolicy.getPropertyAddress();
            boolean isRental = homePolicy.isRental();
            MessageCli.PRINT_DB_HOME_POLICY.printMessage(propertyAddress, homePolicy.getSumInsured(),
                    homePolicy.getPremium(), homePolicy.getPremiumAfterDiscount());
        } else if (policy instanceof LifePolicy) {
            LifePolicy lifePolicy = (LifePolicy) policy;
            MessageCli.PRINT_DB_LIFE_POLICY.printMessage(lifePolicy.getSumInsured(), lifePolicy.getPremium(),
                    lifePolicy.getPremiumAfterDiscount());
        }
    }

    private int getProfileNumber(Profile profile) {
        List<Profile> profiles = new ArrayList<>(database.values());
        profiles.sort((p1, p2) -> p1.getUserName().compareToIgnoreCase(p2.getUserName()));

        int index = profiles.indexOf(profile);
        return index + 1;
    }

    private void calculateDiscount() {
        int policyCount = loadedProfile.getPolicies().size();
        if (policyCount == 2) {
            MessageCli.DISCOUNT_TWO.printMessage();
        } else if (policyCount > 2) {
            MessageCli.DISCOUNT_MULTIPLE.printMessage();
        }
    }

    private String toTitleCase(String input) {
        String[] words = input.toLowerCase().split(" ");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            titleCase.append(word.substring(0, 1).toUpperCase()).append(word.substring(1)).append(" ");
        }
        return titleCase.toString().trim();
    }
}