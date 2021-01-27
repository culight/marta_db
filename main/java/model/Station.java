package main.java.model;

import javafx.beans.property.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Station {

	private String name;
	private String stopID;
	private double fare;
	private Boolean isTrain;
	private Boolean isOpen;

	public Station() {}

	public Station(ResultSet resultSet) throws SQLException {
		this(
				resultSet.getString("name"),
				resultSet.getString("stopID"),
				resultSet.getDouble("fare"),
				resultSet.getBoolean("isTrain"),
				resultSet.getBoolean("isOpen")
		);
	}

	public Station(String name, String stopID, double fare, Boolean isTrain, Boolean isOpen) {
		this.name = name;
		this.stopID = stopID;
		this.fare = fare;
		this.isTrain = isTrain;
		this.isOpen = isOpen;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStopID() {
		return stopID;
	}

	public void setStopID(String stopID) {
		this.stopID = stopID;
	}

	public double getFare() {
		return fare;
	}

	public void setFare(double fare) {
		this.fare = fare;
	}

	public Boolean getTrain() {
		return isTrain;
	}

	public void setTrain(Boolean train) {
		isTrain = train;
	}

	public Boolean getOpen() {
		return isOpen;
	}

	public void setOpen(Boolean open) {
		isOpen = open;
	}
}
