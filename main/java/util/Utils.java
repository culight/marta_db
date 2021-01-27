package main.java.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    //randomly  generated number function for 8 digit breezecards
    public static String generateNumber() {
        Random rand = new Random();
        long accumulator = 1 + rand.nextInt(9); // ensures that the 16th digit isn’t 0
        for(int i = 0; i < 15; i++) {
            accumulator *= 10L;
            accumulator += rand.nextInt(10);
        }

        String returnString = Long.toString(accumulator);
        return returnString;
    }

    //function to validate emails
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
                    "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                    Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailAddress) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailAddress);
        return matcher.find();
    }

    //function that creates hashed password
    public static String generateHash(String password) throws NoSuchAlgorithmException{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = sha.digest(password.getBytes());
        String hash = Base64.getEncoder().encodeToString(hashedBytes);
        return hash.substring(0,15);
    }
}
