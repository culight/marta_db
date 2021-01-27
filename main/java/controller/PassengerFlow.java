package main.java.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.util.Database;
import main.java.model.FlowReport;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PassengerFlow extends MartaController {

    @FXML private TableView<FlowReport> flowTable;
    @FXML private TextField startTime;
    @FXML private TextField endTime;

    public PassengerFlow(MartaController previous) {
        super(previous);
        TableColumn<FlowReport, String> stationName = new TableColumn("Station Name");
        stationName.setCellValueFactory(new PropertyValueFactory("station"));
        TableColumn<FlowReport, Integer> passengersIn = new TableColumn("Passengers In");
        passengersIn.setCellValueFactory(new PropertyValueFactory("passengersIn"));
        TableColumn<FlowReport, Integer> passengersOut = new TableColumn("Passengers Out");
        passengersOut.setCellValueFactory(new PropertyValueFactory("passengersOut"));
        TableColumn<FlowReport, Integer> flow = new TableColumn("Flow");
        flow.setCellValueFactory(new PropertyValueFactory("flow"));
        TableColumn<FlowReport, Double> revenue = new TableColumn("Revenue");
        revenue.setCellValueFactory(new PropertyValueFactory("revenue"));
        flowTable.getColumns().addAll(stationName, passengersIn, passengersOut, flow, revenue);

        update();
    }

    public void updateTable(ObservableList<FlowReport> flows) {
        flowTable.getItems().setAll(flows);
    }

    @Override
    public String getFXMLPath() {
        return "main/java/view/PassengerFlow.fxml";
    }

    public String getTitle() {
        // get station name
        return "Passenger Flow Report";
    }

    @FXML
    public void update() {
        Database db = Database.getInstance();
        String startTimeStr = startTime.getText();
        String endTimeStr = endTime.getText();

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            List<FlowReport> flowReports = db.getFlowReports(start, end);
            updateTable(FXCollections.observableArrayList(flowReports));
        } catch (ParseException e) {
            getApp().logError(getTitle(), "Date filters must be of format mm/dd/yyyy hh:mm:ss AM/PM");
        }
    }

    @FXML
    public void reset() {

    }
}
