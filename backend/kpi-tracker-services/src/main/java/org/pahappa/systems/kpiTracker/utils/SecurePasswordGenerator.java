package org.pahappa.systems.kpiTracker.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for generating secure passwords using SecureRandom
 */
public class SecurePasswordGenerator {

    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    // Combined character set for password generation
    private static final String ALL_CHARS = UPPER_CASE + LOWER_CASE + NUMBERS + SPECIAL_CHARS;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Password requirements
    private static final int MIN_LENGTH = 12;
    private static final int MAX_LENGTH = 16;
    private static final int MIN_UPPER_CASE = 2;
    private static final int MIN_LOWER_CASE = 2;
    private static final int MIN_NUMBERS = 2;
    private static final int MIN_SPECIAL_CHARS = 1;

    /**
     * Generates a secure password that meets all security requirements
     *
     * @return A secure password string
     */
    public static String generateSecurePassword() {
        int passwordLength = MIN_LENGTH + SECURE_RANDOM.nextInt(MAX_LENGTH - MIN_LENGTH + 1);

        List<Character> password = new ArrayList<>();

        // Ensure minimum requirements are met
        addRequiredCharacters(password, UPPER_CASE, MIN_UPPER_CASE);
        addRequiredCharacters(password, LOWER_CASE, MIN_LOWER_CASE);
        addRequiredCharacters(password, NUMBERS, MIN_NUMBERS);
        addRequiredCharacters(password, SPECIAL_CHARS, MIN_SPECIAL_CHARS);

        // Fill remaining length with random characters
        int remainingLength = passwordLength - password.size();
        for (int i = 0; i < remainingLength; i++) {
            password.add(ALL_CHARS.charAt(SECURE_RANDOM.nextInt(ALL_CHARS.length())));
        }

        // Shuffle the password to randomize character positions
        Collections.shuffle(password, SECURE_RANDOM);

        // Convert to string
        StringBuilder passwordBuilder = new StringBuilder();
        for (Character c : password) {
            passwordBuilder.append(c);
        }

        return passwordBuilder.toString();
    }

    /**
     * Adds the required number of characters from a specific character set
     *
     * @param password The password character list
     * @param charSet  The character set to choose from
     * @param count    The number of characters to add
     */
    private static void addRequiredCharacters(List<Character> password, String charSet, int count) {
        for (int i = 0; i < count; i++) {
            password.add(charSet.charAt(SECURE_RANDOM.nextInt(charSet.length())));
        }
    }

    /**
     * Validates if a password meets security requirements
     *
     * @param password The password to validate
     * @return true if password meets requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            return false;
        }

        int upperCaseCount = 0;
        int lowerCaseCount = 0;
        int numberCount = 0;
        int specialCharCount = 0;

        for (char c : password.toCharArray()) {
            if (UPPER_CASE.indexOf(c) >= 0) {
                upperCaseCount++;
            } else if (LOWER_CASE.indexOf(c) >= 0) {
                lowerCaseCount++;
            } else if (NUMBERS.indexOf(c) >= 0) {
                numberCount++;
            } else if (SPECIAL_CHARS.indexOf(c) >= 0) {
                specialCharCount++;
            }
        }

        return upperCaseCount >= MIN_UPPER_CASE &&
                lowerCaseCount >= MIN_LOWER_CASE &&
                numberCount >= MIN_NUMBERS &&
                specialCharCount >= MIN_SPECIAL_CHARS;
    }

    /**
     * Generates a temporary password for first-time login
     * This password should be changed on first login
     *
     * @return A temporary secure password
     */
    public static String generateTemporaryPassword() {
        return generateSecurePassword();
    }

    /**
     * Main method for testing password generation
     * This can be used to verify password generation works correctly
     */

}
