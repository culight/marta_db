package main.java.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FlowReport {
    private String station;
    private int passengersIn;
    private int passengersOut;
    private int flow;
    private double revenue;

    public FlowReport(ResultSet resultSet) throws SQLException {

        this (
                resultSet.getString("name"),
                resultSet.getInt("passIn"),
                resultSet.getInt("passOut"),
                resultSet.getDouble("revenue")
        );
    }

    public FlowReport(String station, int passengersIn, int passengersOut, double revenue) {
        this.station = station;
        this.passengersIn = passengersIn;
        this.passengersOut = passengersOut;
        this.flow = passengersIn - passengersOut;
        this.revenue = revenue;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public int getPassengersIn() {
        return passengersIn;
    }

    public void setPassengersIn(int passengersIn) {
        this.passengersIn = passengersIn;
    }

    public int getPassengersOut() {
        return passengersOut;
    }

    public void setPassengersOut(int passengersOut) {
        this.passengersOut = passengersOut;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}
