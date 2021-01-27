package main.java.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import java.sql.Timestamp;
import main.java.util.Database;
import main.java.model.BreezeCard;
import main.java.model.User;
import main.java.model.Conflict;
import javafx.scene.control.TextField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ManageCards extends MartaController {
    Database db;
    User user;

    @FXML private TableView<BreezeCard> breezeCardTable;
    @FXML private TextField addBreezeCardField;
    @FXML private TextField valueField;
    @FXML private TextField ccField;

    public ManageCards(MartaController previous) {
        super(previous);

        // make columns
        TableColumn<BreezeCard, String> breezeNumCol = new TableColumn("Number");
        breezeNumCol.setCellValueFactory(new PropertyValueFactory("number"));
        TableColumn<BreezeCard, String> breezeValCol = new TableColumn("Value");
        breezeValCol.setCellValueFactory(new PropertyValueFactory("value"));
        TableColumn<BreezeCard, Void> removeCol = new TableColumn("");
        removeCol.setCellFactory(tc -> new RemoveCell<>());
        breezeCardTable.getColumns().addAll(breezeNumCol, breezeValCol, removeCol);

        // get breezecards for the app user
        user = getApp().getUser();
        db = Database.getInstance();
        updateTable();
    }

    public void updateTable() {
        List<BreezeCard> breezeCards = db.getBreezeCards(
            user.getUsername(), null, -1,-1,false);
        breezeCardTable.setItems(FXCollections.observableArrayList(breezeCards));
    }

    @FXML
    private void addCard() {
        String number = addBreezeCardField.getText();
        number = number.replaceAll("\\s+","");
        // check for length
        if (number.length() == 0) {
              return;
        }
        if (number.length() != 16) {
            getApp().logError(getTitle(), "Incorrect Length - Breezecard must be 16 digit numeric value");
            return;
        }
        //make sure its numeric
        try {
            Double r = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            getApp().logError(getTitle(), "Non-numeric value - Breezecard must be 16 digit numeric value");
            return;
        }

        int code = breezeCardTakenCode(number, user.getUsername());
        if(code == 1) {
            // card is already assiged to user
        } else if(code == 2) {
            // breezecard conflict
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            db.addConflict(new Conflict(user.getUsername(), number, timestamp));
            return;
        } else if(code == 3) {
            // loose card to be assigned to user
            List<BreezeCard> cards = db.getBreezeCards(null,number,-1,-1,false);
            if (cards.size() == 1) {
                  BreezeCard original = cards.get(0);
                  BreezeCard updated = new BreezeCard(number,
                    original.getValue(), user.getUsername());
                  db.updateBreezeCard(original,updated);
            }
        } else {
            // new card -- add to table
            db.addBreezeCard(new BreezeCard(number,0.0,user.getUsername()));
        }
        updateTable();
        addBreezeCardField.clear();
    }

    public int breezeCardTakenCode(String cardnumber, String username) {
        List<BreezeCard> breezeCards = db.getBreezeCards();
        for (BreezeCard bc : breezeCards) {
            System.out.println(bc.getNumber());
              if (bc.getBelongsTo() != null) {
                  if (bc.getNumber().equals(cardnumber) && bc.getBelongsTo().equals(username)) {
                      getApp().logError(getTitle(), "You already own this breezecard");
                      return 1;
                  } else if (bc.getNumber().equals(cardnumber) && !bc.getBelongsTo().equals(username)) {
                      getApp().logError(getTitle(), "This breezecard already exists");
                      return 2;
                  }
              } else {
                  System.out.println("is null");
                  if(bc.getNumber().equals(cardnumber)) {
                        // loose card can be assigned to user
                        return 3;
                  }
              }
        }
        return 0;
  }

    @FXML
    private void addValue() {
        BreezeCard selected = breezeCardTable.getSelectionModel().getSelectedItem();
        String ccNumber = ccField.getText();
        ccNumber = ccNumber.replaceAll("\\s+","");

        // no row is selected
        if (selected == null) {
            getApp().logError(getTitle(), "Please Select a BreezeCard to add value to.");
            return;
        }

        // check for length
        if (ccNumber.length() == 0) {
            getApp().logError(getTitle(), "Please provide a Credit Card number.");
            return;
        }
        if (ccNumber.length() != 16) {
            getApp().logError(getTitle(), "Incorrect Length - Credit Card number must be 16 digit numeric value");
            return;
        }
        //make sure its numeric
        try {
            Double r = Double.parseDouble(ccNumber);
        } catch (NumberFormatException e) {
            getApp().logError(getTitle(), "Non-numeric value - Credit Card number must be 16 digit numeric value");
            return;
        }

        String ccValueString = valueField.getText();
        ccValueString = ccValueString.replaceAll("\\s+","");
        Double addedValue = 0.0;

        if (valueField.getText().isEmpty()) {
            getApp().logError(getTitle(), "Please provide a value to add.");
            return;
        }
        //make sure its numeric
        try {
            addedValue = Double.parseDouble(ccValueString);
        } catch (NumberFormatException e) {
            getApp().logError(getTitle(), "Non-numeric value - Added value must be a numeric value");
            return;
        }
        // caluculate new value in breezecard
        if (addedValue <= 0){
            getApp().logError(getTitle(), "Added value must be a positive number.");
            return;
        }
        Double newValue = selected.getValue() + addedValue;
        if (newValue > 1000) {
            getApp().logError(getTitle(), "BreezeCard value cannot exceed $1000");
            return;
        }
        BreezeCard updatedCard = selected.copy();
        updatedCard.setValue(newValue);
        db.updateBreezeCard(selected, updatedCard);
        updateTable();

        // clear the text fields
        ccField.clear();
        valueField.clear();
    }

    @Override
    public String getFXMLPath() {
        return "main/java/view/ManageCards.fxml";
    }

    @Override
    public String getTitle() {
        return "Manage Cards";
    }

    public class RemoveCell<T> extends TableCell<T, Void> {

        private final Hyperlink link;

        public RemoveCell() {
            link = new Hyperlink("Remove");
            link.setOnAction(evt -> {
                // remove row item from tableview
                BreezeCard selected = (BreezeCard)getTableRow().getItem();
                BreezeCard updated = selected.copy();
                updated.setBelongsTo(null);

                if (getTableView().getItems().size() > 1) {
                    db.updateBreezeCard(selected, updated);
                } else {
                    getApp().logError(getTitle(), "You cannot remove your last breezecard");
                }

                getTableView().getItems().remove(getTableRow().getIndex());
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : link);
        }

    }
}
