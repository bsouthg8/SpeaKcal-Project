package com.example.speakcalproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LoginTest {

    private Login loginActivity;

    @Before
    public void setUp() {
        // Initialize the activity
        loginActivity = Robolectric.buildActivity(Login.class).create().get();
    }

    @Test
    public void testLoginWithValidCredentials() {
        // Set valid username and password
        loginActivity.usernameEditText.setText("validUsername");
        loginActivity.passwordEditText.setText("validPassword");

        boolean result = loginActivity.loginUser("validUsername", "validPassword");

        assertTrue("Login should be successful", result);

        // Add additional assertions for expected behavior after login
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        // Set invalid username and password
        loginActivity.usernameEditText.setText("invalidUsername");
        loginActivity.passwordEditText.setText("invalidPassword");

        boolean result = loginActivity.loginUser("invalidUsername", "invalidPassword");

        assertTrue("This test will fail intentionally", false); // This test will fail

        // Add additional assertions for expected behavior after a failed login
    }

    @Test
    public void testOpenSignupActivity() {
        loginActivity.openSignupActivity(new View(loginActivity));

        // Verify that the Signup activity is started
        Intent nextStartedActivity = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        assertEquals(Signup.class, nextStartedActivity.getComponent().getClassName());
    }
}
