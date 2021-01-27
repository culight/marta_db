package main.java.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.util.Database;
import main.java.model.Trip;
import main.java.model.BreezeCard;

import java.util.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class TripHistory extends MartaController {
    private Database db;
    private List<BreezeCard> breezeCards;
    @FXML private TableView tripTable;
    @FXML private TextField startTime;
    @FXML private TextField endTime;

    SimpleDateFormat inputFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a");
    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

    public TripHistory(MartaController previous, List<BreezeCard> breezeCards) {
        super(previous);
        this.breezeCards = breezeCards;
        TableColumn<Trip, String> time = new TableColumn("Time");
        time.setCellValueFactory(trip -> {
            StringProperty date = new SimpleStringProperty();
            date.set(inputFormat.format(trip.getValue().getTime_start()));
            return date;
        });
        TableColumn<Trip, String> source = new TableColumn("Source");
        source.setCellValueFactory(new PropertyValueFactory("startID"));
        TableColumn<Trip, String> destination = new TableColumn("Destination");
        destination.setCellValueFactory(new PropertyValueFactory("endID"));
        TableColumn<Trip, Double> fairPaid = new TableColumn("Fare Paid");
        fairPaid.setCellValueFactory(new PropertyValueFactory("current_fare"));
        TableColumn<Trip, String> cardNumber = new TableColumn("Card Number");
        cardNumber.setCellValueFactory(new PropertyValueFactory("breezeNum"));
        tripTable.getColumns().addAll(time, source, destination, fairPaid, cardNumber);

        update();

    }


    @Override
    public String getFXMLPath() {
        return "main/java/view/TripHistory.fxml";
    }

    @Override
    public String getTitle() {
        return "Trip History";
    }

     public void updateTable(ObservableList<Trip> trips) {
        tripTable.getItems().setAll(trips);
    }

    @FXML
    public void update() {
        Database db = Database.getInstance();
        String startTimeStr = startTime.getText();
        String endTimeStr = endTime.getText();
        try {
            Timestamp start = null;
            Timestamp end = null;
            if(!startTimeStr.equals("")) {
                Date inputStartDate = inputFormat.parse(startTimeStr);
                System.out.println(outputFormat.format(inputStartDate));
                start = Timestamp.valueOf(outputFormat.format(inputStartDate));
            }
            if(!endTimeStr.equals("")) {
                Date inputEndDate = inputFormat.parse(endTimeStr);
                end = Timestamp.valueOf(outputFormat.format(inputEndDate));
            }
            List<Trip> trips = new LinkedList<>();
            for(BreezeCard bc : breezeCards) {
                trips.addAll(db.getTrips(bc.getNumber(), start, end,
                        -1, null, null));
            }
            if(trips.size() > 0) {

            }
            updateTable(FXCollections.observableArrayList(trips));
        } catch (ParseException e) {
            getApp().logError(getTitle(), "Date filters must be of format mm/dd/yyyy hh:mm:ss AM/PM");
        }
    }


    @FXML
    public void reset() {
         startTime.clear();
         endTime.clear();
    }

}

