package main.java.controller;

import main.java.util.Database;
import javafx.fxml.FXML;

import java.util.Random;
import javafx.scene.control.*;
import java.util.List;
import main.java.model.BreezeCard;
import main.java.model.Passenger;
import main.java.model.User;
import main.java.model.Conflict;
import main.java.util.Utils;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static main.java.util.Utils.generateHash;

public class Register extends MartaController {
    private Database db;
    public static final String SALT = "marta123";
    @FXML private TextField username;
    @FXML private TextField emailAddress;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmPassword;
    @FXML private TextField cardNumber;
    @FXML private ToggleGroup breezecardToggleGroup;

    List<BreezeCard> breezecards = null;
    List<Passenger> passengers = null;

    public Register (MartaController previous) {
        super(previous);
        db = Database.getInstance();
        passengers = db.getPassengers();
        breezecards = db.getBreezeCards();
    }

    //function that checks if breezecard number is already in the database
    public boolean breezecardConflictChecker(String cardnumber) {
        for (BreezeCard bc : breezecards) {
            if (bc.getNumber().equals(cardnumber) && bc.getBelongsTo() != null) {
                return true;
            }

        }
        return false;
    }

    public boolean nullOwnerChecker(String cardnumber) {
        for (BreezeCard bc: breezecards) {
            if (bc.getNumber().equals(cardnumber) && bc.getBelongsTo() == null) {
                return true;
                }
            }
            return false;
    }


    @Override
    public String getFXMLPath() {
        return "main/java/view/Register.fxml";
    }

    @FXML
    private void createAccount() {
        //get username, email address, password, confirm password from entered text fields
        String newUname = username.getText();
        String currentEmailAddress = emailAddress.getText();
        String pwd = password.getText();
        String cfmpwd = confirmPassword.getText();
        String cardnumber = cardNumber.getText();
        Passenger newPassenger = new Passenger();
        boolean isConflict = false;
        String cardnumberConflict = "";
        boolean ownerNull = false;


        //verify all fields were filled in
        if (newUname.isEmpty() || newUname == null || currentEmailAddress.isEmpty() ||  emailAddress == null ||
             pwd.isEmpty() || pwd == null || cfmpwd.isEmpty() || cfmpwd == null ) {
            getApp().logError(getTitle(), "All fields must be filled.");
            return;
        }

        //verify username is unique
        for (Passenger pg : passengers) {
            if (pg.getUsername().equals(newUname)) {
                getApp().logError(getTitle(), "This username already exists, please choose another.");
                return;
            } else if (pg.getEmail().equals(currentEmailAddress)) {
                getApp().logError(getTitle(), "This email is already registered, please choose another.");
                return;
            }
        }

        //verify email is valid
        if (!Utils.validate(currentEmailAddress))
        {
            getApp().logError(getTitle(), "This is not a valid email, please use a valid email.");
            return;
        }

        //verify new password is at least 8 characters
        if (pwd.length() < 8) {
            getApp().logError(getTitle(),"Your password must be at least 8 characters");
            return;
        }

        //verify new password and confirm password match
        if (!Objects.equals(pwd, cfmpwd)) {
            getApp().logError(getTitle(),"Your password and confirm password do not match.");
            return;
        }

        //checks to make sure a button is pushed
        if (breezecardToggleGroup.getSelectedToggle()== null) {
            getApp().logError(getTitle(), "Please selected one of the breezecard choices.");
            return;
        }

        String selectedToggle = breezecardToggleGroup.getSelectedToggle().toString();
        String useExisting = "Use my existing Breezecard";
        String getNew = "Get a new breezecard";
        Timestamp timestamp;
        //if use my existing card is selected
        if (selectedToggle.toLowerCase().contains(useExisting.toLowerCase())) {
            //check if the number already exists
            if (breezecardConflictChecker(cardnumber) == true && nullOwnerChecker(cardnumber) == false) {
                //boolean for conflict
                System.out.println("Recognizes there is a conflict");
                isConflict = true;
                ownerNull = false;
                cardnumberConflict = cardnumber;
                //generate random breezcard
                cardnumber = Utils.generateNumber();
            }

            if (nullOwnerChecker(cardnumber) == true) {
                ownerNull = true;
                isConflict = false;
            }


            while (breezecardConflictChecker(cardnumber) == true) {
                // randomly generate new number and assign to card number
                cardnumber = Utils.generateNumber();
                //check if new generated number is in database
                breezecardConflictChecker(cardnumber);
            }

        }


        //if get new breezecard is selected
         if (selectedToggle.toLowerCase().contains(getNew.toLowerCase())) {
            //randomly generate 16 digit number -
            isConflict = false;
            cardnumber = Utils.generateNumber();
            // check if it exists in database already
            while (breezecardConflictChecker(cardnumber) == true) {
                cardnumber = Utils.generateNumber();
                breezecardConflictChecker(cardnumber);
            }
        }


        //convert inputted password string to hashed password
        String saltedPassword = SALT + pwd;
        String hashedPassword = "";
        try {
             hashedPassword = generateHash(saltedPassword);
        } catch (NoSuchAlgorithmException e) {
            getApp().logError(getTitle(), "Unable to hash password!");
        }

        //stores new passenger information into database
        User registeredUser = new User(newUname, hashedPassword, false);
        Passenger newRegisteredPassenger = new Passenger(newUname, currentEmailAddress);
        BreezeCard newBreezeNum = new BreezeCard(cardnumber, 0, newUname);

        db.addUser(registeredUser);
        db.addPassenger(newRegisteredPassenger);


        if (ownerNull == true)
        {
         List<BreezeCard> bc = db.getBreezeCards(null, cardnumber, -1, -1, false);
         BreezeCard original = bc.get(0);
         BreezeCard updated = new BreezeCard(cardnumber, original.getValue(), newUname);
         db.updateBreezeCard(original,updated);
        }

        if (isConflict == true) {
            timestamp = new Timestamp(System.currentTimeMillis());
            db.addConflict(new Conflict(newUname, cardnumberConflict, timestamp));
        }

         if (ownerNull == false) {
            db.addBreezeCard(newBreezeNum);
         }

        //load Welcome to MARTA screen
        getApp().setUser(registeredUser);
        getApp().loadController(new WelcomeToMARTA(this));
    }


    // @FXML
    // private void back() {
    //     // go back
    // }

     public String getTitle() {
        return "Register";
    }

}
