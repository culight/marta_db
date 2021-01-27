package main.java.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import main.java.util.Database;
import main.java.model.BreezeCard;
import main.java.model.Station;
import main.java.model.Trip;
import main.java.model.User;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.*;
import java.text.DecimalFormat;
import java.util.Map;

public class WelcomeToMARTA extends MartaController {
    private final String SEPARATOR = " - ";
    Database db;
    List<BreezeCard> breezeCards;
    Trip currentTrip;
    User currentUser;
    Map<String, Station> stationMap;
    @FXML private Label valueLable;
    @FXML private ChoiceBox breezeCardList;
    @FXML private ChoiceBox startStationList;
    @FXML private ChoiceBox endStationList;

    public WelcomeToMARTA(MartaController previous) {
        super(previous);
        db = Database.getInstance();
        this.currentUser = getApp().getUser();
        update();
        breezeCardList.getSelectionModel()
          .selectedItemProperty()
          .addListener( (options,oldValue,newValue) -> {
            List<BreezeCard> bc = db.getBreezeCards(null,newValue.toString(),-1,-1,false);
            setValue(bc.get(0).getValue());
        });
    }

    public void setValue(Double value) {
        DecimalFormat df = new DecimalFormat("#.00");
        String valueText = "$" + df.format(value).toString();
        valueLable.setText(valueText);
    }

    @Override
    public void update() {
        stationMap = new HashMap<>();
        breezeCards = db.getBreezeCards(currentUser.getUsername(), null,
                -1,-1,false);
        List<Station> stations = db.getStations("","", -1, null, true);
        breezeCards.size();
        stations.size();

        if(breezeCards.size() == 0 || stations.size() == 0) {
            getApp().logError(getTitle(), "Could not retrieve Breezecards and Stations, please try again.");
        } else {
            for(Station station : stations) {
                stationMap.put(station.getStopID(), station);
                startStationList.getItems().add(station.getStopID() + SEPARATOR +
                        String.format("%.2f",station.getFare()));
            }
            breezeCardList.setItems(FXCollections.observableArrayList(breezeCards));
            breezeCardList.getSelectionModel().selectFirst();
            setValue(breezeCards.get(0).getValue());
            startStationList.getSelectionModel().selectFirst();
            endStationList.setDisable(true);

        }
    }

    @Override
    public String getFXMLPath() {
        return "main/java/view/WelcomeToMARTA.fxml";
    }

    @Override
    public String getTitle() {
        return "Welcome To MARTA";
    }

    @FXML
    public void manageCards() {
        this.getApp().loadController(new ManageCards(this));
    }

    @FXML
    public void viewTrips() {this.getApp().loadController(new TripHistory(this, breezeCards));}

    @FXML
    public void logout() {
        // reset app user
        getApp().setUser(null);
        this.getApp().loadController(new Login(this));
    }

    @FXML
    public void start() {
        if(currentTrip == null) {
            String stationString = (String) startStationList.getSelectionModel().getSelectedItem();
            String[] id_fare = stationString.split(SEPARATOR);
            String stopID = id_fare[0];
            double fare = Double.parseDouble(id_fare[1]);
            BreezeCard bc = (BreezeCard) breezeCardList.getSelectionModel().getSelectedItem();
            if(bc.getValue() >= fare) {
                BreezeCard updated = bc.copy();
                updated.setValue(bc.getValue()-fare);
                setValue(updated.getValue());
                db.updateBreezeCard(bc, updated);
                // round to nearest second for db storage
                long millis = 1000 * Math.round(System.currentTimeMillis() / 1000.0);
                Timestamp now = new Timestamp(millis);
                currentTrip = new Trip(bc.getNumber(), now, fare, stopID, null);
                if (!db.addTrip(currentTrip)) {
                    getApp().logError(getTitle(), "Failed to start the trip, please try again.");
                } else {
                    startStationList.setDisable(true);
                    endStationList.setDisable(false);
                    breezeCardList.setDisable(true);
                    Station start = stationMap.get(stopID);
                    List<Station> stations = new LinkedList<>(stationMap.values());
                    for (Station station : stations) {
                        if (start.getTrain() == station.getTrain()) {
                            endStationList.getItems().add(station.getStopID());
                        }
                    }
                    endStationList.getSelectionModel().selectFirst();
                }
            } else {
                getApp().logError(getTitle(), "You do not have enough value on your BreezeCard.");
            }
        }
    }

    @FXML
    public void end() {
        if(currentTrip != null) {
            String stopID = (String) endStationList.getSelectionModel().getSelectedItem();
            Trip newTrip = currentTrip.copy();
            newTrip.setEndID(stopID);
            if (db.updateTrip(currentTrip, newTrip)) {
                currentTrip = null;
                startStationList.setDisable(false);
                endStationList.setDisable(true);
                breezeCardList.setDisable(false);
                endStationList.getItems().clear();
            } else {
                getApp().logError(getTitle(), "Failed to end the current trip, please try again.");
            }
        }
    }
}
