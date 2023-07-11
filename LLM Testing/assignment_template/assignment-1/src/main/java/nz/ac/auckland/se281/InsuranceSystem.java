package nz.ac.auckland.se281;

import nz.ac.auckland.se281.Main.PolicyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsuranceSystem {
    private final Map<String, Profile> profiles;
    private Profile loadedProfile;

    public InsuranceSystem() {
        profiles = new HashMap<>();
        loadedProfile = null;
    }

    public void printDatabase() {
        int profileCount = profiles.size();
        String profileText = profileCount == 1 ? "profile" : "profiles";
        String endText = profileCount == 1 ? ":" : ".";

        System.out.println("Database has " + profileCount + " " + profileText + endText);

        if (profileCount > 0) {
            List<Profile> profileList = new ArrayList<>(profiles.values());

            for (int i = 0; i < profileList.size(); i++) {
                Profile profile = profileList.get(i);
                String rank = (i + 1) + "";
                String username = profile.getUsername();
                String age = profile.getAge() + "";

                System.out.println(" " + rank + ": " + username + ", " + age);
            }
        }
    }

    public void createNewProfile(String userName, String age) {
        if (userName.length() < 3) {
            System.out.println("'" + userName + "' is an invalid username, it should be at least 3 characters long. No profile was created.");
            return;
        }

        if (profiles.containsKey(userName)) {
            System.out.println("Usernames must be unique. No profile was created for '" + userName + "'.");
            return;
        }

        int ageInt;

        try {
            ageInt = Integer.parseInt(age);
        } catch (NumberFormatException e) {
            System.out.println("'" + age + "' is an invalid age, please provide a positive whole number only. No profile was created for " + userName + ".");
            return;
        }

        if (ageInt <= 0) {
            System.out.println("'" + age + "' is an invalid age, please provide a positive whole number only. No profile was created for " + userName + ".");
            return;
        }

        Profile newProfile = new Profile(userName, ageInt);
        profiles.put(userName, newProfile);

        System.out.println("New profile created for " + newProfile.getUsername() + " with age " + newProfile.getAge() + ".");
    }

    public void loadProfile(String userName) {
        if (loadedProfile != null) {
            System.out.println("Cannot load a profile. First unload the profile for " + loadedProfile.getUsername() + ".");
            return;
        }

        Profile profile = profiles.get(userName);

        if (profile == null) {
            System.out.println("No profile found for " + userName + ". Profile not loaded.");
            return;
        }

        loadedProfile = profile;

        System.out.println("Profile loaded for " + loadedProfile.getUsername() + ".");
    }

    public void unloadProfile() {
        if (loadedProfile == null) {
            System.out.println("No profile is currently loaded.");
            return;
        }

        System.out.println("Profile unloaded for " + loadedProfile.getUsername() + ".");

        loadedProfile = null;
    }

    public void deleteProfile(String userName) {
        Profile profile = profiles.get(userName);

        if (profile == null) {
            System.out.println("No profile found for " + userName + ". No profile was deleted.");
            return;
        }

        if (loadedProfile != null && loadedProfile.getUsername().equals(userName)) {
            System.out.println("Cannot delete profile for " + userName + " while loaded. No profile was deleted.");
            return;
        }

        profiles.remove(userName);

        System.out.println("Profile deleted for " + userName + ".");
    }

    public void createPolicy(PolicyType type, String[] options) {
        if (loadedProfile == null) {
            System.out.println("Need to load a profile in order to create a policy.");
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

    private void createHomePolicy(String[] options) {
        String sumInsured = options[0];
        String propertyAddress = options[1];
        String rental = options[2];

        HomePolicy policy = new HomePolicy(sumInsured, propertyAddress, rental.equals("y"));

        loadedProfile.addPolicy(policy);

        System.out.println("New home policy created for " + loadedProfile.getUsername() + ".");
    }

    private void createCarPolicy(String[] options) {
        String sumInsured = options[0];
        String makeAndModel = options[1];
        String licensePlate = options[2];
        String mechanicalWarranty = options[3];

        CarPolicy policy = new CarPolicy(sumInsured, makeAndModel, licensePlate, mechanicalWarranty.equals("y"));

        loadedProfile.addPolicy(policy);

        System.out.println("New car policy created for " + loadedProfile.getUsername() + ".");
    }

    private void createLifePolicy(String[] options) {
        String sumInsured = options[0];

        if (loadedProfile.hasLifePolicy()) {
            System.out.println(loadedProfile.getUsername() + " already has a life policy. No new policy was created.");
            return;
        }

        if (loadedProfile.getAge() > 65) {
            System.out.println(loadedProfile.getUsername() + " is over the age limit. No policy was created.");
            return;
        }

        LifePolicy policy = new LifePolicy(sumInsured);

        loadedProfile.addPolicy(policy);

        System.out.println("New life policy created for " + loadedProfile.getUsername() + ".");
    }

    private static class Profile {
        private final String username;
        private final int age;
        private final List<Policy> policies;

        public Profile(String username, int age) {
            this.username = toTitleCase(username);
            this.age = age;
            this.policies = new ArrayList<>();
        }

        public String getUsername() {
            return username;
        }

        public int getAge() {
            return age;
        }

        public void addPolicy(Policy policy) {
            policies.add(policy);
        }

        public boolean hasLifePolicy() {
            for (Policy policy : policies) {
                if (policy instanceof LifePolicy) {
                    return true;
                }
            }

            return false;
        }

        private static String toTitleCase(String str) {
            String[] words = str.split(" ");
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < words.length; i++) {
                String word = words[i];

                if (i > 0) {
                    sb.append(" ");
                }

                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }

            return sb.toString();
        }
    }

    private abstract static class Policy {
        private final String sumInsured;

        public Policy(String sumInsured) {
            this.sumInsured = sumInsured;
        }

        public String getSumInsured() {
            return sumInsured;
        }
    }

    private static class HomePolicy extends Policy {
        private final String propertyAddress;
        private final boolean isRental;

        public HomePolicy(String sumInsured, String propertyAddress, boolean isRental) {
            super(sumInsured);
            this.propertyAddress = propertyAddress;
            this.isRental = isRental;
        }

        public String getPropertyAddress() {
            return propertyAddress;
        }

        public boolean isRental() {
            return isRental;
        }
    }

    private static class CarPolicy extends Policy {
        private final String makeAndModel;
        private final String licensePlate;
        private final boolean hasMechanicalWarranty;

        public CarPolicy(String sumInsured, String makeAndModel, String licensePlate, boolean hasMechanicalWarranty) {
            super(sumInsured);
            this.makeAndModel = makeAndModel;
            this.licensePlate = licensePlate;
            this.hasMechanicalWarranty = hasMechanicalWarranty;
        }

        public String getMakeAndModel() {
            return makeAndModel;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public boolean hasMechanicalWarranty() {
            return hasMechanicalWarranty;
        }
    }

    private static class LifePolicy extends Policy {
        public LifePolicy(String sumInsured) {
            super(sumInsured);
        }
    }
}