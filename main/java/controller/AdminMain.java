package main.java.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;

import javafx.scene.layout.VBox;
import main.java.MartaApp;
import main.java.model.Station;

public class AdminMain extends MartaController {

    public AdminMain(MartaController previous) {
        super(previous);
    }

    @Override
    public String getFXMLPath() {
        return "main/java/view/AdminMain.fxml";
    }

    @Override
    public String getTitle() {
        return "Administrator";
    }

    @FXML
    private void navToStationMgmnt() {
        this.getApp().loadController(new StationManagement(this));
    }

    @FXML
    private void navToSuspCards() {
        this.getApp().loadController(new SuspendedCards(this));
    }

    @FXML
    private void navToBreezecardMgmnt() {
        this.getApp().loadController(new BreezeCardManagement(this));
    }

    @FXML
    private void navToPassFlowReport() {
        this.getApp().loadController(new PassengerFlow(this));
    }

    @FXML
    private void logOut() {
        // Log out procedure
        this.getApp().loadController(new Login(this));
        getApp().setUser(null);
    }

}
