package nz.ac.auckland.se281;

import static nz.ac.auckland.se281.Main.Command.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
  MainTest.Task1.class, //
  MainTest.Task2.class, // Uncomment this line when to start Task 2
  MainTest.Task3.class, // Uncomment this line when to start Task 3
  // MainTest.YourTests.class, // Uncomment this line to run your own tests
})
public class MainTest {

  public static class Task1 extends CliTest {

    public Task1() {
      super(Main.class);
    }

    @Test
    public void T1_01_empty_database() throws Exception {
      runCommands(PRINT_DB);
      assertContains("Database has 0 profiles.");
    }

    @Test
    public void T1_02_add_one_client() throws Exception {
      runCommands(CREATE_PROFILE, "Jordan", "21", PRINT_DB);
      assertContains("Database has 1 profile:");
      assertContains("New profile created for Jordan with age 21.");
      assertDoesNotContain("Database has 0 profiles", true);
    }

    @Test
    public void T1_xx_add_one_client_with_info() throws Exception {
      runCommands(CREATE_PROFILE, "Jordan", "21", PRINT_DB);
      assertContains("Database has 1 profile:");
      assertContains("New profile created for Jordan with age 21.");
      assertContains("1: Jordan, 21");
      assertDoesNotContain("Database has 0 profiles", true);
    }

    @Test
    public void T1_xx_ignore_short_name() throws Exception {
      runCommands(CREATE_PROFILE, "Jo", "21", PRINT_DB);
      assertContains("Database has 0 profiles.");
      assertContains(
          "'Jo' is an invalid username, it should be at least 3 characters long. No profile was"
              + " created.");
      assertDoesNotContain("Database has 1 profile", true);
      assertDoesNotContain("New profile created", true);
      assertDoesNotContain("21");
    }

    @Test
    public void T1_xx_ignore_short_name_to_titlecase() throws Exception {
      runCommands(CREATE_PROFILE, "aL", "21", PRINT_DB);
      assertContains("Database has 0 profiles.");
      assertContains(
          "'Al' is an invalid username, it should be at least 3 characters long. No profile was"
              + " created.");
      assertDoesNotContain("Database has 1 profile", true);
      assertDoesNotContain("New profile created", true);
      assertDoesNotContain("21");
    }

    @Test
    public void T1_xx_add_two_clients() throws Exception {
      runCommands(CREATE_PROFILE, "Jordan", "21", CREATE_PROFILE, "Tom", "25", PRINT_DB);
      assertContains("Database has 2 profiles:");
      assertContains("1: Jordan, 21");
      assertContains("2: Tom, 25");
      assertDoesNotContain("Database has 0 profiles", true);
      assertDoesNotContain("Database has 1 profile", true);
    }

    @Test
    public void T1_xx_add_five_clients() throws Exception {
      runCommands( //
          CREATE_PROFILE,
          "Jordan",
          "21", //
          CREATE_PROFILE,
          "Jenny",
          "22", //
          CREATE_PROFILE,
          "TOM",
          "23", //
          CREATE_PROFILE,
          "tOmmY",
          "24", //
          CREATE_PROFILE,
          "aLeX",
          "25", //
          PRINT_DB);

      assertContains("Database has 5 profiles:");
      assertContains("1: Jordan, 21");
      assertContains("2: Jenny, 22");
      assertContains("3: Tom, 23");
      assertContains("4: Tommy, 24");
      assertContains("5: Alex, 25");
    }

    @Test
    public void T1_xx_username_to_titlecase() throws Exception {
      runCommands(CREATE_PROFILE, "jorDan", "21", CREATE_PROFILE, "TOM", "25", PRINT_DB);
      assertContains("Database has 2 profiles:");
      assertContains("1: Jordan, 21");
      assertContains("2: Tom, 25");
      assertDoesNotContain("jorDan");
      assertDoesNotContain("TOM");
    }

    @Test
    public void T1_xx_add_ignore_duplicate() throws Exception {
      runCommands(CREATE_PROFILE, "Jordan", "21", CREATE_PROFILE, "Jordan", "35", PRINT_DB);
      assertContains("Database has 1 profile:");
      assertContains("1: Jordan, 21");

      assertContains("Usernames must be unique. No profile was created for 'Jordan'.");

      assertDoesNotContain("Database has 0 profiles", true);
      assertDoesNotContain("Database has 2 profiles", true);
      assertDoesNotContain("Jordan, 35", true);
    }

    @Test
    public void T1_xx_add_ignore_duplicate_added_later() throws Exception {
      runCommands(
          CREATE_PROFILE,
          "tom",
          "21", //
          CREATE_PROFILE,
          "jordan",
          "25", //
          CREATE_PROFILE,
          "Jenny",
          "23", //
          CREATE_PROFILE,
          "TOM",
          "32", //
          PRINT_DB);
      assertContains("Database has 3 profiles:");
      assertContains("1: Tom, 21");
      assertContains("2: Jordan, 25");
      assertContains("3: Jenny, 23");

      assertContains("Usernames must be unique. No profile was created for 'Tom'.");

      assertDoesNotContain("Database has 4 profiles", true);
      assertDoesNotContain("Tom, 32", true);
    }

    @Test
    public void T1_xx_ignore_invalid_age_negative() throws Exception {
      runCommands(CREATE_PROFILE, "Jordan", "-1", PRINT_DB);
      assertContains("Database has 0 profiles.");
      assertContains(
          "'-1' is an invalid age, please provide a positive whole number only. No profile was"
              + " created for Jordan.");
      assertDoesNotContain("Database has 1 profile", true);
      assertDoesNotContain("Jordan, -1", true);
      assertDoesNotContain("New profile created", true);
    }

        @Test
    public void T1_xx_add_success_after_invalid_age() throws Exception {
      runCommands(CREATE_PROFILE, "Jordan", "-1", CREATE_PROFILE, "Jordan", "20", PRINT_DB);
      assertContains(
          "'-1' is an invalid age, please provide a positive whole number only. No profile was"
              + " created for Jordan.");
      assertContains("Database has 1 profile:");
      assertContains("1: Jordan, 20");
      assertDoesNotContain("Database has 0 profiles", true);
      assertDoesNotContain("Jordan, -1", true);
    }
  }

  public static class Task2 extends CliTest {

    public Task2() {
      super(Main.class);
    }

    @Test
    public void T2_xx_load_profile_found() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Tom"));

      assertContains("Profile loaded for Tom.");
      assertDoesNotContain("No profile found for Tom. Profile not loaded.", true);
    }

    @Test
    public void T2_xx_load_profile_not_found() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Alex"));

      assertContains("No profile found for Alex. Profile not loaded.");
      assertDoesNotContain("Profile loaded for Alex.", true);
    }

    @Test
    public void T2_xx_load_profile_found_display() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Tom", //
              PRINT_DB));

      assertContains("Profile loaded for Tom.");

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21");
      assertContains("*** 2: Tom, 25");
      assertContains("3: Jenny, 23");
    }

    @Test
    public void T2_xx_load_profile_not_found_display() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Alex", //
              PRINT_DB));

      assertContains("No profile found for Alex. Profile not loaded.");
      assertDoesNotContain("Profile loaded for Alex.", true);

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21");
      assertContains("2: Tom, 25");
      assertContains("3: Jenny, 23");

      assertDoesNotContain("***");
    }

    @Test
    public void T2_xx_load_profile_found_ignore_case_display() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "tom", //
              PRINT_DB));

      assertContains("Profile loaded for Tom.");

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21");
      assertContains("*** 2: Tom, 25");
      assertContains("3: Jenny, 23");
    }

    @Test
    public void T2_xx_load_profile_switch_profiles() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "tom", //
              LOAD_PROFILE,
              "jenny", //
              PRINT_DB));

      assertContains("Profile loaded for Tom.");
      assertContains("Profile loaded for Jenny.");

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21");
      assertContains("2: Tom, 25");
      assertContains("*** 3: Jenny, 23");
      assertDoesNotContain("*** 1: Jordan, 21", true);
      assertDoesNotContain("*** 2: Tom, 25", true);
    }

    @Test
    public void T2_xx_load_profile_ignore_invalid_switch() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "jordan", //
              LOAD_PROFILE,
              "unknown", //
              PRINT_DB));

      assertContains("Profile loaded for Jordan.");
      assertContains("No profile found for Unknown. Profile not loaded.");

      assertContains("Database has 3 profiles:");
      assertContains("*** 1: Jordan, 21");
      assertContains("2: Tom, 25");
      assertContains("3: Jenny, 23");

      assertDoesNotContain("Profile loaded for unknown", true);
      assertDoesNotContain("*** 2: Tom, 25", true);
      assertDoesNotContain("*** 3: Jenny, 23", true);
    }

    @Test
    public void T2_xx_unload_profile() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Jenny", //
              UNLOAD_PROFILE, //
              PRINT_DB));

      assertContains("Profile loaded for Jenny.");
      assertContains("Profile unloaded for Jenny.");

      assertContains("1: Jordan, 21");
      assertContains("2: Tom, 25");
      assertContains("3: Jenny, 23");

      assertDoesNotContain("*** 1: Jordan, 21", true);
      assertDoesNotContain("*** 2: Tom, 25", true);
      assertDoesNotContain("*** 3: Jenny, 23", true);
    }

    @Test
    public void T2_xx_unload_invalid_profile() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "jen", //
              UNLOAD_PROFILE, //
              PRINT_DB));

      assertContains("No profile is currently loaded.");

      assertContains("1: Jordan, 21");
      assertContains("2: Tom, 25");
      assertContains("3: Jenny, 23");

      assertDoesNotContain("*** 1: Jordan, 21", true);
      assertDoesNotContain("*** 2: Tom, 25", true);
      assertDoesNotContain("*** 3: Jenny, 23", true);
    }

    @Test
    public void T2_xx_cannot_create_profile_while_loaded() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "TOM", //
              CREATE_PROFILE,
              "Who",
              19, //
              PRINT_DB));

      assertContains("1: Jordan, 21");
      assertContains("*** 2: Tom, 25");
      assertContains("3: Jenny, 23");

      assertContains("Cannot create a new profile. First unload the profile for Tom.");

      assertDoesNotContain("*** 1: Jordan, 21", true);
      assertDoesNotContain("*** 3: Jenny, 23", true);
      assertDoesNotContain("Who, 19", true);
    }

    @Test
    public void T2_xx_can_create_profile_after_unloading() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "TOM", //
              UNLOAD_PROFILE, //
              CREATE_PROFILE,
              "who",
              19, //
              PRINT_DB));

      assertContains("1: Jordan, 21");
      assertContains("2: Tom, 25");
      assertContains("3: Jenny, 23");
      assertContains("4: Who, 19");

      assertDoesNotContain("Cannot create a new profile. First unload the profile for Tom.", true);

      assertDoesNotContain("*** 1: Jordan, 21", true);
      assertDoesNotContain("*** 2: Tom, 25", true);
      assertDoesNotContain("*** 3: Jenny, 23", true);
    }

    @Test
    public void T2_xx_delete_profile_found() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              DELETE_PROFILE,
              "jordan", //
              PRINT_DB));

      assertContains("Profile deleted for Jordan.");
      assertContains("Database has 2 profiles:");
      assertContains("1: Tom, 25");
      assertContains("2: Jenny, 23");
      assertDoesNotContain("Jordan, 21", true);
    }

    @Test
    public void T2_xx_delete_profile_not_found() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              DELETE_PROFILE,
              "alex", //
              PRINT_DB));

      assertDoesNotContain("Profile deleted for Alex", true);
      assertContains("No profile found for Alex. No profile was deleted.");

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21");
      assertContains("2: Tom, 25");
      assertContains("3: Jenny, 23");
    }

    @Test
    public void T2_xx_delete_profile_while_loaded() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Jenny", //
              DELETE_PROFILE,
              "jenny", //
              PRINT_DB));

      assertContains("Profile loaded for Jenny.");

      assertContains("Cannot delete profile for Jenny while loaded. No profile was deleted.");
      assertDoesNotContain("Profile deleted for Jenny", true);

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21");
      assertContains("2: Tom, 25");
      assertContains("3: Jenny, 23");
    }

    @Test
    public void T2_xx_delete_profile_while_another_is_loaded() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Jenny", //
              DELETE_PROFILE,
              "tom", //
              PRINT_DB));

      assertContains("Profile loaded for Jenny.");
      assertContains("Profile deleted for Tom.");

      assertContains("Database has 2 profiles:");
      assertContains("1: Jordan, 21");
      assertContains("2: Jenny, 23");
      assertDoesNotContain("Tom, 25", true);
    }

    @Test
    public void T2_xx_delete_profile_after_unloaded() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "tom", //
              UNLOAD_PROFILE, //
              DELETE_PROFILE,
              "TOM", //
              PRINT_DB));

      assertContains("Profile loaded for Tom.");
      assertContains("Profile unloaded for Tom.");
      assertContains("Profile deleted for Tom.");

      assertContains("Database has 2 profiles:");
      assertContains("1: Jordan, 21");
      assertContains("2: Jenny, 23");
      assertDoesNotContain("Tom, 25", true);
    }
  }

  public static class Task3 extends CliTest {

    public Task3() {
      super(Main.class);
    }

    @Test
    public void T3_xx_cannot_add_policy_without_loaded_profile() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              POLICY_HOME,
              options("1000000", "20 Symonds Street", "yes")));

      assertContains("Need to load a profile in order to create a policy.");
      assertDoesNotContain("New home policy created", true);
    }

    @Test
    public void T3_xx_add_home_policy_loaded_profile() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Jenny", //
              POLICY_HOME,
              options("1000000", "20 Symonds Street", "yes"), //
              PRINT_DB));

      assertContains("Profile loaded for Jenny.");

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21, 0 policies");
      assertContains("2: Tom, 25, 0 policies");
      assertContains("*** 3: Jenny, 23, 1 policy");

      assertContains("New home policy created for Jenny.");

      assertContains(
          "Home Policy (20 Symonds Street, Sum Insured: $1000000, Premium: $20000 -> $20000)");
    }

    @Test
    public void T3_xx_add_car_policy_loaded_profile() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Tom", //
              POLICY_CAR,
              options("55000", "Subaru Impreza", "SUB123", "yes"), //
              PRINT_DB));

      assertContains("Profile loaded for Tom.");
      assertContains("New car policy created for Tom.");

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21, 0 policies");
      assertContains("*** 2: Tom, 25, 1 policy");
      assertContains("3: Jenny, 23, 0 policies");

      assertContains("Car Policy (Subaru Impreza, Sum Insured: $55000, Premium: $5580 -> $5580)");
    }

    @Test
    public void T3_xx_add_life_policy_loaded_profile() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Jordan", //
              POLICY_LIFE,
              options("80000"), //
              PRINT_DB));

      assertContains("Profile loaded for Jordan.");
      assertContains("New life policy created for Jordan.");

      assertContains("Database has 3 profiles:");
      assertContains("*** 1: Jordan, 21, 1 policy");
      assertContains("2: Tom, 25, 0 policies");
      assertContains("3: Jenny, 23, 0 policies");

      assertContains("Life Policy (Sum Insured: $80000, Premium: $968 -> $968)");
    }

    @Test
    public void T3_xx_two_different_policies_one_profile() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Jenny", //
              POLICY_HOME,
              options("1000000", "20 Symonds Street", "yes"), //
              POLICY_CAR,
              options("55000", "Subaru Impreza", "SUB123", "no"), //
              PRINT_DB));

      assertContains("Profile loaded for Jenny.");
      assertContains("New home policy created for Jenny.");
      assertContains("New car policy created for Jenny.");

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21, 0 policies");
      assertContains("2: Tom, 25, 0 policies");
      assertContains("*** 3: Jenny, 23, 2 policies");

      assertContains(
          "Home Policy (20 Symonds Street, Sum Insured: $1000000, Premium: $20000 -> $18000)");
      assertContains("Car Policy (Subaru Impreza, Sum Insured: $55000, Premium: $8250 -> $7425)");
    }

    @Test
    public void T3_xx_two_different_policies_home_life_one_profile() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Jenny", //
              POLICY_HOME,
              options("1000000", "20 Symonds Street", "yes"), //
              POLICY_LIFE,
              options("1000000"), //
              PRINT_DB));

      assertContains("Profile loaded for Jenny.");
      assertContains("New home policy created for Jenny.");
      assertContains("New life policy created for Jenny.");

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21, 0 policies");
      assertContains("2: Tom, 25, 0 policies");
      assertContains("*** 3: Jenny, 23, 2 policies");

      assertContains(
          "Home Policy (20 Symonds Street, Sum Insured: $1000000, Premium: $20000 -> $18000)");
      assertContains("Life Policy (Sum Insured: $1000000, Premium: $12300 -> $11070)");
    }

    @Test
    public void T3_xx_three_policies_one_profile() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Jenny", //
              POLICY_HOME,
              options("1000000", "20 Symonds Street", "yes"), //
              POLICY_HOME,
              options("1000000", "20 Queen Street", "no"), //
              POLICY_LIFE,
              options("1000000"), //
              PRINT_DB));

      assertContains("Profile loaded for Jenny.");
      assertContains("New home policy created for Jenny.");
      assertContains("New life policy created for Jenny.");

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21, 0 policies");
      assertContains("2: Tom, 25, 0 policies");
      assertContains("*** 3: Jenny, 23, 3 policies");

      assertContains(
          "Home Policy (20 Symonds Street, Sum Insured: $1000000, Premium: $20000 -> $16000)");
      assertContains(
          "Home Policy (20 Queen Street, Sum Insured: $1000000, Premium: $10000 -> $8000)");
      assertContains("Life Policy (Sum Insured: $1000000, Premium: $12300 -> $9840)");
    }

    @Test
    public void T3_xx_two_policies_two_profiles() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Tom", //
              POLICY_HOME,
              options("1000000", "20 Symonds Street", "yes"), //
              UNLOAD_PROFILE, //
              LOAD_PROFILE,
              "Jenny", //
              POLICY_CAR,
              options("55000", "Subaru Impreza", "SUB123", "no"), //
              UNLOAD_PROFILE, //
              PRINT_DB));

      assertContains("Profile loaded for Tom.");
      assertContains("Profile loaded for Jenny.");
      assertContains("New home policy created for Tom.");
      assertContains("New car policy created for Jenny.");

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21, 0 policies");
      assertContains("2: Tom, 25, 1 policy");
      assertContains("3: Jenny, 23, 1 policy");

      assertContains(
          "Home Policy (20 Symonds Street, Sum Insured: $1000000, Premium: $20000 -> $20000)");
      assertContains("Car Policy (Subaru Impreza, Sum Insured: $55000, Premium: $8250 -> $8250)");
    }

    @Test
    public void T3_xx_two_life_policies_one_profile() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Jenny", //
              POLICY_LIFE,
              options("100000"), //
              POLICY_LIFE,
              options("500000"), //
              UNLOAD_PROFILE, //
              PRINT_DB));

      assertContains("Profile loaded for Jenny.");
      assertContains("New life policy created for Jenny.");
      assertContains("Jenny already has a life policy. No new policy was created.");

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21, 0 policies");
      assertContains("2: Tom, 25, 0 policies");
      assertContains("3: Jenny, 23, 1 policy");

      assertContains("Life Policy (Sum Insured: $100000, Premium: $1230 -> $1230)");
      assertDoesNotContain("Life Policy (Sum Insured: $500000", true);
    }

    @Test
    public void T3_xx_two_life_policies_two_profiles() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Jenny", //
              POLICY_LIFE,
              options("100000"), //
              UNLOAD_PROFILE, //
              LOAD_PROFILE,
              "Tom", //
              POLICY_LIFE,
              options("500000"), //
              UNLOAD_PROFILE, //
              PRINT_DB));

      assertContains("Profile loaded for Jenny.");
      assertContains("New life policy created for Jenny.");
      assertContains("Profile loaded for Tom.");
      assertContains("New life policy created for Tom.");

      assertDoesNotContain("already has a life policy", true);

      assertContains("Database has 3 profiles:");
      assertContains("1: Jordan, 21, 0 policies");
      assertContains("2: Tom, 25, 1 policy");
      assertContains("3: Jenny, 23, 1 policy");

      assertContains("Life Policy (Sum Insured: $100000, Premium: $1230 -> $1230)");
      assertContains("Life Policy (Sum Insured: $500000, Premium: $6250 -> $6250)");
    }

    @Test
    public void T3_xx_life_policy_over_age_limit() throws Exception {
      runCommands(
          CREATE_PROFILE,
          "Jenny",
          101, //
          LOAD_PROFILE,
          "Jenny", //
          POLICY_LIFE,
          options("100000"), //
          UNLOAD_PROFILE, //
          PRINT_DB);

      assertContains("Profile loaded for Jenny.");
      assertContains("Jenny is over the age limit. No policy was created.");

      assertContains("Database has 1 profile:");
      assertContains("1: Jenny, 101, 0 policies");

      assertDoesNotContain("New life policy created for Jenny.", true);
      assertDoesNotContain("Life Policy (Sum Insured", true);
    }

    @Test
    public void T3_xx_two_policies_two_profiles_deleted_profile_total_costs() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Jordan", //
              POLICY_HOME,
              options("500000", "Queen Street", "yes"), //
              POLICY_HOME,
              options("500000", "Another Street", "yes"), //
              UNLOAD_PROFILE, //
              LOAD_PROFILE,
              "Tom", //
              POLICY_HOME,
              options("1000000", "20 Symonds Street", "yes"), //
              UNLOAD_PROFILE, //
              LOAD_PROFILE,
              "Jenny", //
              POLICY_CAR,
              options("55000", "Subaru Impreza", "SUB123", "no"), //
              UNLOAD_PROFILE, //
              DELETE_PROFILE,
              "Jordan", //
              PRINT_DB));

      assertDoesNotContain("2 policies", true);
      assertContains("1: Tom, 25, 1 policy for a total of $20000");
      assertContains("2: Jenny, 23, 1 policy for a total of $8250");

      assertContains("New home policy created for Jordan.");
      assertDoesNotContain("Home Policy (Queen Street");

      assertContains(
          "Home Policy (20 Symonds Street, Sum Insured: $1000000, Premium: $20000 -> $20000)");
      assertContains("Car Policy (Subaru Impreza, Sum Insured: $55000, Premium: $8250 -> $8250)");
    }

    @Test
    public void T3_xx_two_policies_one_profile_ignore_zero_policy_total_costs() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Tom", //
              POLICY_HOME,
              options("1000000", "20 Symonds Street", "yes"), //
              POLICY_CAR,
              options("55000", "Subaru Impreza", "SUB123", "no"), //
              UNLOAD_PROFILE, //
              LOAD_PROFILE,
              "Jenny", //
              POLICY_CAR,
              options("55000", "Subaru Impreza", "SUB123", "no"), //
              UNLOAD_PROFILE, //
              PRINT_DB));

      assertContains("2: Tom, 25, 2 policies for a total of $22950");
      assertContains("3: Jenny, 23, 1 policy for a total of $8250");

      assertContains(
          "Home Policy (20 Symonds Street, Sum Insured: $1000000, Premium: $20000 -> $18000)");
      assertContains("Car Policy (Subaru Impreza, Sum Insured: $55000, Premium: $5500 -> $4950)");
      assertContains("Car Policy (Subaru Impreza, Sum Insured: $55000, Premium: $8250 -> $8250)");
    }

    @Test
    public void T3_xx_five_policies_two_profiles_total_costs() throws Exception {
      runCommands(
          unpack( //
              CREATE_SOME_CLIENTS, //
              LOAD_PROFILE,
              "Tom", //
              POLICY_HOME,
              options("1000000", "20 Symonds Street", "yes"), //
              POLICY_CAR,
              options("55000", "Subaru Impreza", "SUB123", "no"), //
              POLICY_CAR,
              options("50000", "Toyota Prius", "TOY456", "yes"), //
              UNLOAD_PROFILE, //
              LOAD_PROFILE,
              "Jenny", //
              POLICY_CAR,
              options("55000", "Subaru Forester", "SUB456", "no"), //
              POLICY_CAR,
              options("55000", "Toyota Camry", "TOY987", "no"), //
              UNLOAD_PROFILE, //
              PRINT_DB));

      assertContains("1: Jordan, 21, 0 policies for a total of $0");
      assertContains("2: Tom, 25, 3 policies for a total of $24464");
      assertContains("3: Jenny, 23, 2 policies for a total of $14850");

      assertContains(
          "Home Policy (20 Symonds Street, Sum Insured: $1000000, Premium: $20000 -> $16000)");
      assertContains("Car Policy (Subaru Impreza, Sum Insured: $55000, Premium: $5500 -> $4400)");
      assertContains("Car Policy (Toyota Prius, Sum Insured: $50000, Premium: $5080 -> $4064)");
      assertContains("Car Policy (Subaru Forester, Sum Insured: $55000, Premium: $8250 -> $7425)");
      assertContains("Car Policy (Toyota Camry, Sum Insured: $55000, Premium: $8250 -> $7425)");
    }
  }

  public static class YourTests extends CliTest {

    public YourTests() {
      super(Main.class);
    }

    @Test
    public void TY_01_your_own_test() throws Exception {
      // Write your own test here, in the same format as the other tests.
      runCommands(PRINT_DB);
      assertContains("");
    }

    @Test
    public void TY_02_your_own_test() throws Exception {
      // Write your own test here, in the same format as the other tests.
      runCommands(PRINT_DB);
      assertContains("");
    }
  }

  private static final Object[] CREATE_SOME_CLIENTS =
      new Object[] { //
        CREATE_PROFILE, "Jordan", "21", //
        CREATE_PROFILE, "Tom", "25", //
        CREATE_PROFILE, "Jenny", "23", //
      };

  private static Object[] unpack(Object[] commands, Object... more) {
    List<Object> all = new ArrayList<Object>();
    all.addAll(List.of(commands));
    all.addAll(List.of(more));
    return all.toArray(new Object[all.size()]);
  }

  private static String[] options(String... options) {
    List<String> all = new ArrayList<String>();
    all.addAll(List.of(options));
    return all.toArray(new String[all.size()]);
  }
}
