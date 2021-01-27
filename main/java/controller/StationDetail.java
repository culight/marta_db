package main.java.controller;

import java.text.DecimalFormat;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import main.java.model.Station;
import main.java.model.Bus;
import main.java.util.Database;
import java.util.List;

public class StationDetail extends MartaController {

    @FXML private Label stationName;
    @FXML private Label intersection;
    @FXML private Label stopName;
    @FXML private TextField fareTextBox;
    @FXML private CheckBox openStationCheck;

    private Station station;
    private Database db;
    private Bus bus;

    public StationDetail(MartaController previous, Station selected) {
        super(previous);
        db = Database.getInstance();
        station = selected;
        setStationName(station.getName());
        setStopName(String.valueOf(station.getStopID()));
        setFare(station.getFare());
        setOpenStatus(station.getOpen());
        if (!station.getTrain()) {
           List<Bus> buses = db.getBuses(station.getStopID(), null);
           if (buses.size() == 1) {
              setIntersection(buses.get(0).getIntersection());
           } else {
             // no bus station found
           }
        }
    }

    @Override
    public String getFXMLPath() {
        return "main/java/view/StationDetail.fxml";
    }

    public void setStationName(String text) {
        stationName.setText(text);
    }

    public String getStationName() {
        return stationName.getText();
    }

    public void setStopName(String text) {
        stopName.setText("Stop " + text);
    }

    public String getStopName() {
        return stopName.getText();
    }

    public void setIntersection(String text) {
        intersection.setText(text);
    }

    public String getIntersection() {
        return intersection.getText();
    }

    public void setOpenStatus(Boolean openStatus) {
        openStationCheck.setSelected(openStatus);
    }

    public void setFare(Double fare) {
        fareTextBox.clear();
        DecimalFormat df = new DecimalFormat("#.00");
        fareTextBox.setText("$" + df.format(fare).toString());
    }

    public double getFare() {
        String value = fareTextBox.getText().replace("$", "");
        if (value.isEmpty())
            return -1;
        return Double.parseDouble(value);
    }

    public String getTitle() {
        return "Station Detail - " + station.getName();
    }

    @FXML
    public void updateFare() {
        Double newFare = getFare();
        if (newFare < 0 || newFare > 50) {
            getApp().logError(getTitle(), "Fare must be between $0 and $50");
            return;
        }
        db.updateStation(station, new Station(
            station.getName(),
            station.getStopID(),
            newFare,
            station.getTrain(),
            station.getOpen()
        ));
        station.setFare(newFare);
    }

    @FXML
    public void updateOpenStatus() {
        db.updateStation(station, new Station(
            station.getName(),
            station.getStopID(),
            station.getFare(),
            station.getTrain(),
            openStationCheck.isSelected()
        ));
        station.setOpen(openStationCheck.isSelected());
    }

    @FXML
    public void back() {
        updateOpenStatus();
        this.getApp().loadController(new StationManagement(this));
    }

}
