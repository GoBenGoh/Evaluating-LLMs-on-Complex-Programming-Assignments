package nz.ac.auckland.se281;

import nz.ac.auckland.se281.Main.PolicyType;

import java.util.ArrayList;
import java.util.List;

public class InsuranceSystem {
    private List<ClientProfile> database = new ArrayList<>();

    public InsuranceSystem() {
        // Only this constructor can be used (if you need to initialise fields).
    }

    public void printDatabase() {
        if (database.isEmpty()) {
            System.out.println(MessageCli.PRINT_DB_POLICY_COUNT.getMessage("0", "s", "."));
        } else if (database.size() == 1) {
            System.out.println(MessageCli.PRINT_DB_POLICY_COUNT.getMessage("1", "", ":"));
            System.out.println(MessageCli.PRINT_DB_PROFILE_HEADER_MINIMAL.getMessage());
            System.out.println(" 1: " + database.get(0).getTitleCaseName() + ", " + database.get(0).getAge());
        } else {
            System.out.println(MessageCli.PRINT_DB_POLICY_COUNT.getMessage(String.valueOf(database.size()), "s", ":"));
            System.out.println(MessageCli.PRINT_DB_PROFILE_HEADER_MINIMAL.getMessage());
            for (int i = 0; i < database.size(); i++) {
                System.out.println(" " + (i+1) + ": " + database.get(i).getTitleCaseName() + ", " + database.get(i).getAge());
            }
        }
    }

    public void createNewProfile(String userName, String age) {
        if (userName.length() < 3) {
            System.out.println(MessageCli.CREATE_PROFILE_INVALID_USERNAME.getMessage(userName));
            return;
        }
        int ageInt;
        try {
            ageInt = Integer.parseInt(age);
        } catch (NumberFormatException e) {
            System.out.println(MessageCli.CREATE_PROFILE_INVALID_AGE.getMessage(age, userName));
            return;
        }
        if (ageInt <= 0) {
            System.out.println(MessageCli.CREATE_PROFILE_INVALID_AGE.getMessage(age, userName));
            return;
        }
        for (ClientProfile profile : database) {
            if (profile.getName().equalsIgnoreCase(userName)) {
                System.out.println(MessageCli.CREATE_PROFILE_DUPLICATE_USERNAME.getMessage(userName));
                return;
            }
        }
        ClientProfile newProfile = new ClientProfile(userName);
        newProfile.setAge(ageInt);
        database.add(newProfile);
        System.out.println(MessageCli.CREATE_PROFILE_SUCCESS.getMessage(newProfile.getTitleCaseName(), newProfile.getAge()));
    }

    public void loadProfile(String userName) {
        // TODO: Complete this method.
    }

    public void unloadProfile() {
        // TODO: Complete this method.
    }

    public void deleteProfile(String userName) {
        // TODO: Complete this method.
    }

    public void createPolicy(PolicyType type, String[] options) {
        // TODO: Complete this method.
    }
}