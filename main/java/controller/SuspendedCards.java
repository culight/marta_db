package main.java.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.util.Database;
import main.java.model.BreezeCard;
import main.java.model.Conflict;

import java.util.List;
import java.util.Random;

public class SuspendedCards extends MartaController {
	private Database db;

	@FXML private TableView<Conflict> suspendedCardsTable;

	public SuspendedCards(MartaController previous) {
		super(previous);

		TableColumn<Conflict, String> cardNumber = new TableColumn("Card #");
        cardNumber.setCellValueFactory(new PropertyValueFactory("breezeNum"));

        TableColumn<Conflict, String> newOwner = new TableColumn("New Owner");
        newOwner.setCellValueFactory(new PropertyValueFactory("username"));

        TableColumn<Conflict, String> dateSuspended = new TableColumn("Date Suspended");
        dateSuspended.setCellValueFactory(new PropertyValueFactory("date_suspended"));

        TableColumn<Conflict, String> previousOwner = new TableColumn("Previous Owner");
        previousOwner.setCellValueFactory(new PropertyValueFactory("oldUser"));

        suspendedCardsTable.getColumns().addAll(cardNumber, newOwner, dateSuspended, previousOwner);

		db = Database.getInstance();
		List<Conflict> suspendedCards = db.getConflicts();

		if (suspendedCards.size() == 0) {
		    System.out.println("No conflicts returned!");
        }

        updateTable(FXCollections.observableArrayList(suspendedCards));

	}

    public void updateTable(ObservableList<Conflict> suspendedCards) {
        suspendedCardsTable.setItems(suspendedCards);
    }

	@Override
	public String getFXMLPath() {
		return "main/java/view/SuspendedCards.fxml";
	}

	@Override
	public String getTitle() {
		return "Suspended Cards";
	}

	@FXML
	public void assignToNewOwner() {
	    Conflict selectedItem = suspendedCardsTable.getSelectionModel().getSelectedItem();

        //Assign old breeze card to new owner
        List<BreezeCard> oldBreezeCards = db.getBreezeCards("", selectedItem.getBreezeNum(),
                -1, -1, true);

        if(oldBreezeCards.size() == 0) {
            getApp().logError(getTitle(), "There was an error finding that card in the database.");
        } else {
            BreezeCard oldBreezeCard = oldBreezeCards.get(0);

            String oldName = oldBreezeCard.getBelongsTo();
            String oldNumber = oldBreezeCard.getNumber();
            double oldValue = oldBreezeCard.getValue();

            System.out.println(oldName);
            System.out.println(oldNumber);
            System.out.println(oldValue);

            String newName = selectedItem.getUsername();

            BreezeCard updatedBreezeCard = new BreezeCard(oldNumber, oldValue, newName);

            //Assign breeze card to new user
            db.updateBreezeCard(oldBreezeCard, updatedBreezeCard);

            //Generate new breeze card for old owner and assign to them
            boolean hasUpdated = false;

            BreezeCard newBreezeCard;

            while (!hasUpdated) {
                Random rnd = new Random();
                String oldNum = "" + generateNumber();
                //Put new breeze card for old user
                newBreezeCard = new BreezeCard(oldNum, 0.0, oldName);
                boolean check = db.addBreezeCard(newBreezeCard);

                if (check) {
                    hasUpdated = true;
                }
            }


            //Delete From conflict table
            db.removeConflict(selectedItem);
            suspendedCardsTable.getItems().remove(selectedItem);
        }
    }


    @FXML
    public void assignToOldOwner() {
	    Conflict selectedItem = suspendedCardsTable.getSelectionModel().getSelectedItem();

	    //Assign old breeze card to old owner
        List<BreezeCard> oldBreezeCards = db.getBreezeCards(selectedItem.getOldUser(), selectedItem.getBreezeNum(),
                -1, -1, true);

        BreezeCard oldBreezeCard = oldBreezeCards.get(0);

        String oldName = oldBreezeCard.getBelongsTo();
        String oldNumber = oldBreezeCard.getNumber();
        double oldValue = oldBreezeCard.getValue();

        System.out.println(oldName);
        System.out.println(oldNumber);
        System.out.println(oldValue);

        BreezeCard updatedBreezeCard = new BreezeCard(oldNumber, oldValue, oldName);

        //Assign breeze card to old user
        db.updateBreezeCard(oldBreezeCard, updatedBreezeCard);

        String newName = selectedItem.getUsername();

        //Generate new breeze card for new owner and assign to them
        boolean hasUpdated = false;

        BreezeCard newBreezeCard;

        while (!hasUpdated) {
            Random rnd = new Random();
            String newNum = "" + generateNumber();
            //Put new breeze card for old user
            newBreezeCard = new BreezeCard(newNum, 0.0, newName);
            boolean check = db.addBreezeCard(newBreezeCard);

            if (check) {
                hasUpdated = true;
            }
        }



        //Delete From conflict table
        db.removeConflict(selectedItem);
        suspendedCardsTable.getItems().remove(selectedItem);


    }


    public static String generateNumber() {
        Random rand = new Random();
        long accumulator = 1 + rand.nextInt(9); // ensures that the 16th digit isn't 0
        for(int i = 0; i < 15; i++) {
            accumulator *= 10L;
            accumulator += rand.nextInt(10);
        }

        String returnString = Long.toString(accumulator);
        return returnString;
    }

}