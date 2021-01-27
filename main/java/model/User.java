package main.java.model;

import javafx.beans.property.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
	private String username;
	private String password;
	private Boolean isAdmin;

	public User() {}

	public User(ResultSet resultSet) throws SQLException{
        this(
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getBoolean("isAdmin")
        );
    }

    public User(String username, String password, Boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsAdmin() {
		return isAdmin;
	}

	public boolean isAdminProperty() {
		return isAdmin;
	}

	public void setIsAdmin(boolean admin) {
		this.isAdmin = admin;
	}
}

