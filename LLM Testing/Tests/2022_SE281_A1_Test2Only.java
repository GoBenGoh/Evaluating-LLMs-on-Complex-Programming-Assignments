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
  MainTest.Task2.class, // Uncomment this line when to start Task 2
})
public class MainTest {

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
}