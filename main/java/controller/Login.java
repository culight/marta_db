package main.java.controller;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.util.Database;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import main.java.model.User;
import main.java.util.Utils;

public class Login extends MartaController {

   @FXML private TextField username;
   @FXML private PasswordField password;
   Database db = null;

   public Login(MartaController previous) {
       super(previous);
       db = Database.getInstance();
   }

   @Override
   public String getFXMLPath() {
       return "main/java/view/Login.fxml";
   }

   @Override
   public String getTitle() {
       return "Login";
   }

   @FXML
   private void register() {
       this.getApp().loadController(new Register(this));
   }

   @FXML
   private void login() {
       // get username and password
       String uname = username.getText();
       String pwd = "";
       try {
           String rawPwd = password.getText();
           pwd = Utils.generateHash(rawPwd);
           User selectedUser;

           // verify size constraints
           if(uname.length() == 0 | rawPwd.length() == 0) {
               getApp().logError(getTitle(), "Please provide both username and password.");
               return;
           }

           List<User> users = db.getUsers(uname,pwd,null);

           // deal with none or multiple users
           if(users.size() == 1) {
               System.out.println("user is valid");
               selectedUser = users.get(0);
               getApp().setUser(selectedUser);
               if(selectedUser.getIsAdmin() == true) {
                   // admin
                   getApp().loadController(new AdminMain(this));
               } else {
                   // passenger
                   getApp().loadController(new WelcomeToMARTA(this));
               }
               username.clear();
               password.clear();
           } else {
               getApp().logError(getTitle(), "Username/Password Combination could not be found, please try again.");
           }
       } catch (NoSuchAlgorithmException e) {
           getApp().logError(getTitle(), "Unable to hash password!");
       }
   }

}