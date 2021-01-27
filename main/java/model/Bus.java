package main.java.model;

import javafx.beans.property.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Bus {
    private String stopID;
    private String intersection;

    public Bus() {}

    public Bus(ResultSet resultSet) throws SQLException{
        this(
                resultSet.getString("stopID"),
                resultSet.getString("intersection")
        );
    }

    public Bus(String stopID, String intersection) {
        this.stopID = stopID;
        this.intersection = intersection;
    }

    public String getStopID() {
        return stopID;
    }

    public void setStopID(String stopID) {
        this.stopID = stopID;
    }

    public String getIntersection() {
        return intersection;
    }

    public void setIntersection(String intersection) {
        this.intersection = intersection;
    }
}