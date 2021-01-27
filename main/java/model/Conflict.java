package main.java.model;

import javafx.beans.property.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

public class Conflict {
    private String username;
    private String breezeNum;
    private Timestamp date_suspended;
    private String oldUser;

    public Conflict() { }

    public Conflict(ResultSet resultSet) throws SQLException {
        this(
                resultSet.getString("username"),
                resultSet.getString("breezeNum"),
                resultSet.getTimestamp("date_suspended"),
                resultSet.getString("belongsTo")
        );
    }

    public Conflict(String username, String breezeNum, Timestamp date_suspended) {
        this.username = username;
        this.breezeNum = breezeNum;
        this.date_suspended = date_suspended;

    }

    public Conflict(String username, String breezeNum, Timestamp date_suspended, String oldUser) {
        this.username = username;
        this.breezeNum = breezeNum;
        this.date_suspended = date_suspended;
        this.oldUser = oldUser;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBreezeNum() {
        return breezeNum;
    }

    public void setBreezeNum(String breezeNum) {
        this.breezeNum = breezeNum;
    }

    public Timestamp getDate_suspended() {
        return date_suspended;
    }

    public void setDate_suspended(Timestamp date_suspended) {
        this.date_suspended = date_suspended;
    }
    public String getOldUser() {
        return oldUser;
    }
    public void setOldUser(String oldUser) {
        this.oldUser = oldUser;
    }
}
