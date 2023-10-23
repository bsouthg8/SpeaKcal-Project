package com.example.speakcalproject;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoginTest {

    private Login loginActivity;

    @Before
    public void setUp() {
        // Initialize the Login activity or any required setup here
        loginActivity = new Login();
    }

    @After
    public void tearDown() {
        // Clean up if needed
        loginActivity = null;
    }

    @Test
    public void testLoginUser() {
        // Replace with actual valid username and password for testing
        String validUsername = "validUsername";
        String validPassword = "validPassword";

        boolean result = loginActivity.loginUser(validUsername, validPassword);

        assertTrue("Login should be successful", result);
    }
}

