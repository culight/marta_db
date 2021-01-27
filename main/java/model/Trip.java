package main.java.model;

import javafx.beans.property.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Trip {
    private String breezeNum;
    private Timestamp time_start;
    private Double current_fare;
    private String startID;
    private String endID;

    public Trip() { }

    public Trip(ResultSet resultSet) throws SQLException{
        this(
                resultSet.getString("breezeNum"),
                resultSet.getTimestamp("time_start"),
                resultSet.getDouble("current_fare"),
                resultSet.getString("startID"),
                resultSet.getString("endID")
        );
    }

    public Trip(String breezeNum, Timestamp time_start, Double current_fare, String startID, String endID) {
        this.breezeNum = breezeNum;
        this.time_start = time_start;
        this.current_fare = current_fare;
        this.startID = startID;
        this.endID = endID;
    }

    public String getBreezeNum() {
        return breezeNum;
    }

    public void setBreezeNum(String breezeNum) {
        this.breezeNum = breezeNum;
    }

    public Timestamp getTime_start() {
        return time_start;
    }

    public void setTime_start(Timestamp time_start) {
        this.time_start = time_start;
    }

    public Double getCurrent_fare() {
        return current_fare;
    }

    public void setCurrent_fare(Double current_fare) {
        this.current_fare = current_fare;
    }

    public String getStartID() {
        return startID;
    }

    public void setStartID(String startID) {
        this.startID = startID;
    }

    public String getEndID() {
        return endID;
    }

    public void setEndID(String endID) {
        this.endID = endID;
    }

    public Trip copy() {
        return new Trip(
                breezeNum,
                time_start,
                current_fare,
                startID,
                endID);
    }
}
