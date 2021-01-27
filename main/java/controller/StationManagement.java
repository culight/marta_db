package main.java.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.util.Database;
import main.java.model.Station;

import java.util.List;

public class StationManagement extends MartaController {

    @FXML private TableView<Station> stationTable;


    public StationManagement(MartaController previous) {
        super(previous);
        TableColumn<Station, String> stationName = new TableColumn("Station Name");
        stationName.setCellValueFactory(new PropertyValueFactory("name"));
        TableColumn<Station, String> stopID = new TableColumn("Stop ID");
        stopID.setCellValueFactory(new PropertyValueFactory("stopID"));
        TableColumn<Station, String> fare = new TableColumn("Fare");
        fare.setCellValueFactory(new PropertyValueFactory("fare"));
        TableColumn<Station, Boolean> status = new TableColumn("Status");
        status.setCellValueFactory(cellData ->
                new SimpleBooleanProperty(cellData.getValue().getOpen()));
        status.setCellFactory(col -> new TableCell<Station, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item ? "Open" : "Closed");
            }
        });
        stationTable.getColumns().addAll(stationName, stopID, fare, status);
        update();
    }
    @Override
    public String getFXMLPath() {
        return "main/java/view/StationManagement.fxml";
    }

    @Override
    public String getTitle() {
        return "Station Listing";
    }

    public void updateTable(ObservableList<Station> stations) {
        stationTable.setItems(stations);
    }

    @FXML
    public void createStation() {
        this.getApp().loadController(new CreateStation(this));
    }

    @FXML
    public void viewStation() {
        Station selected = stationTable.getSelectionModel().getSelectedItem();
        if(selected == null) {
            getApp().logError(getTitle(), "You must select a station to view it.");
        } else {
            this.getApp().loadController(new StationDetail(this, selected));
        }
    }

    @FXML
    public void back() {
        this.getApp().loadController(new AdminMain(this));
    }

    @Override
    public void update() {
        // Add stations here
        Database db = Database.getInstance();
        List<Station> stations = db.getStations();
        if(stations.size() == 0) {
            getApp().logError(getTitle(), "Could not retrieve stations, please try again.");
        } else {
            updateTable(FXCollections.observableArrayList(stations));
        }
    }
}
