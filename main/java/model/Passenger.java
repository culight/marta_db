package main.java.model;

import javafx.beans.property.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Passenger {
	private String username;
	private String email;

	public Passenger() {}

	public Passenger(ResultSet resultSet) throws SQLException{
        this(
                resultSet.getString("username"),
                resultSet.getString("email")
        );
    }

    public Passenger(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}