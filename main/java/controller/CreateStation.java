package main.java.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;

import main.java.util.Database;
import main.java.model.Station;
import main.java.model.Bus;

public class CreateStation extends MartaController {
	private String name;
	private String stopID;
	private Double fare;
	private boolean isTrain = false;
	private boolean isOpen;
	private String nearestIntersection;
	private Database db;

	@FXML private TextField stationNameTextBox;
	@FXML private TextField stopIDTextBox;
	@FXML private TextField entryFareTextBox;
	@FXML private ToggleGroup stationToggleGroup;
	@FXML private TextField nearestIntersectionTextBox;
	@FXML private CheckBox isOpenCheckBox;

	public CreateStation(MartaController previous) {
		super(previous);
		db = Database.getInstance();
	}

	@Override
	public String getFXMLPath() {
		return "main/java/view/CreateStation.fxml";
	}

	@Override
	public String getTitle() {
		return "Create Station";
	}

	@FXML
	public void addStation() {
//		getApp().logError(getTitle(), "Could not retrieve Breezecards, please try again.");
		name = stationNameTextBox.getText();

		if (name.isEmpty()) {
			getApp().logError(getTitle(), "Error: Must enter valid name.");
			return;
		}

		stopID = stopIDTextBox.getText();

		if (stopID.isEmpty()) {
			getApp().logError(getTitle(), "Error: Must enter stop ID.");
			return;
		}

		try {
			fare = Double.parseDouble(entryFareTextBox.getText());

		} catch (NumberFormatException e) {
			getApp().logError(getTitle(), "Error: Fare must be a decimal value.");
			return;
//			e.getStackTrace();
		}



        //identify if the bus station radio button has been selected
		if (stationToggleGroup.getSelectedToggle() == null) {
			getApp().logError(getTitle(), "Error: Must select type of station.");
			return;
		} else {
			String selectedToggle = stationToggleGroup.getSelectedToggle().toString();

			String tss = "Train Station";

			if (selectedToggle.toLowerCase().contains(tss.toLowerCase())) {
				isTrain = true;
			}
		}



        //check if the station is open
        if (isOpenCheckBox.isSelected()) {
            isOpen = true;
        }

		// Add a train station, or even a bus station to the Station Table
		// Can put in the other properties of the station even if it is a bus station
		Station newStation = new Station(name, stopID, fare, isTrain, isOpen);

		//Write to the Station Table
		db.addStation(newStation);

		nearestIntersection = nearestIntersectionTextBox.getText();


		// Add a Bus Station --> if there is an intersection
        if (isTrain == false && !nearestIntersection.isEmpty()) {
            // Get the Nearest Intersection
		    Bus newBus = new Bus("'" + stopID  + "'", "'" + nearestIntersection + "'");
            // Add the new bus to the bus table
            db.addBusStation(newBus);
        }

        if (isTrain == false && nearestIntersection.isEmpty()) {
            // Get the Nearest Intersection
			Bus newBus = new Bus("'" + stopID  + "'", null);
            // Add the new bus to the bus table
            db.addBusStation(newBus);
        }

	}



}