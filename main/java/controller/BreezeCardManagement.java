package main.java.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.util.Database;
import main.java.model.BreezeCard;

import java.util.List;


public class BreezeCardManagement extends MartaController {
    private Database db;
    private List<BreezeCard> unsuspendedCards;
    @FXML private TableView<BreezeCard> breezeCardTable;
    @FXML private TextField ownerField;
    @FXML private TextField numberField;
    @FXML private TextField valueLoField;
    @FXML private TextField valueHiField;
    @FXML private CheckBox suspendedCheckBox;
    @FXML private TextField setValueField;
    @FXML private TextField transferField;

    public BreezeCardManagement(MartaController previous) {
        super(previous);
        TableColumn<BreezeCard, String> bcOwner = new TableColumn("Owner");
        bcOwner.setCellValueFactory(new PropertyValueFactory("belongsTo"));
        TableColumn<BreezeCard, String> bcNum = new TableColumn("Number");
        bcNum.setCellValueFactory(new PropertyValueFactory("number"));
        TableColumn<BreezeCard, Double> value = new TableColumn("Value");
        value.setCellValueFactory(new PropertyValueFactory("value"));
        breezeCardTable.getColumns().addAll(bcOwner, bcNum, value);

        db = Database.getInstance();
        unsuspendedCards  = db.getBreezeCards();
        if(unsuspendedCards.size() == 0) {
            getApp().logError(getTitle(), "Could not retrieve Breezecards, please try again.");
        } else {
            updateTable(FXCollections.observableArrayList(unsuspendedCards));
        }
    }

    public void updateTable(ObservableList<BreezeCard> breezeCards) {
        breezeCardTable.setItems(breezeCards);
    }

    @Override
    public String getFXMLPath() {
        return "main/java/view/BreezeCardManagement.fxml";
    }

    public String getTitle() {
        return "Breeze Card Management";
    }

    @FXML
    public void reset() {
        ownerField.clear();
        numberField.clear();
        valueLoField.clear();
        valueHiField.clear();
        suspendedCheckBox.setSelected(false);
    }

    @FXML
    public void update() {
        String owner = ownerField.getText();
        String number = numberField.getText();
        double valueLo = -1;
        double valueHi = -1;
        try {
            valueLo = Double.parseDouble(valueLoField.getText());
            valueHi = Double.parseDouble(valueHiField.getText());
        } catch (NumberFormatException e) { }
        boolean showSuspended = suspendedCheckBox.isSelected() ? true:false;
        List<BreezeCard> breezeCards = db.getBreezeCards(owner, number,valueLo,valueHi,showSuspended);
        if(showSuspended)
            for(BreezeCard bc : breezeCards) {
                if(!unsuspendedCards.contains(bc)) {
                    bc.setBelongsTo("Suspended");
                }
            }
        breezeCardTable.setItems(FXCollections.observableArrayList(breezeCards));
    }

    @FXML
    public void setValue() {
        BreezeCard selected = breezeCardTable.getSelectionModel().getSelectedItem();
        if(selected == null) {
            getApp().logError(getTitle(), "You must select a Breezecard first.");
        } else {
            BreezeCard updated = selected.copy();
            if(setValueField.getText().equals("")) {
                getApp().logError(getTitle(), "Value field must not be blank.");
            } else {
                try {
                    double value = Double.parseDouble(setValueField.getText());
                    if (value >= 0 && value <= 1000) {
                        updated.setValue(value);
                        db.updateBreezeCard(selected, updated);
                    } else {
                        getApp().logError(getTitle(), "Value must be between 0 and 1000 (inclusive).");
                    }
                    update();
                } catch (NumberFormatException e) {
                    getApp().logError(getTitle(),
                            "There was a problem parsing the value field,\n"
                                    + "make sure it is a valid floating point number.");
                }
            }
        }
    }

    @FXML
    public void transfer() {
        BreezeCard selected = breezeCardTable.getSelectionModel().getSelectedItem();
        if(selected == null) {
            getApp().logError(getTitle(), "You must select a Breezecard first.");
        } else {
            BreezeCard updated = selected.copy();
            String owner = transferField.getText();
            if (owner.equals("")) {
                getApp().logError(getTitle(), "Owner field cannot be empty.");
            } else {
                updated.setBelongsTo(owner);
                db.updateBreezeCard(selected, updated);
                update();
            }
        }
    }
}
