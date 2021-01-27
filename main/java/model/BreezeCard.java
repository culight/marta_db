package main.java.model;

import javafx.beans.property.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BreezeCard {
    private String number;
    private double value;
    private String belongsTo;

    public BreezeCard() {}

    public BreezeCard(ResultSet resultSet) throws SQLException{
        this(
                resultSet.getString("breezeNum"),
                resultSet.getDouble("value"),
                resultSet.getString("belongsTo")
        );
    }

    public BreezeCard(String number, double value, String username) {
        this.number = number;
        this.value = value;
        this.belongsTo = username;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(String belongsTo) {
        this.belongsTo = belongsTo;
    }

    public BreezeCard copy() {
        return new BreezeCard(
                this.number,
                this.value,
                this.belongsTo);
    }

    @Override
    public String toString() {
        return getNumber();
    }

    @Override
    public boolean equals(Object other) {
        BreezeCard otherBC = (BreezeCard) other;
        return this != null && other != null
            && (this.belongsTo == otherBC.belongsTo
                || (this.belongsTo != null
                && this.belongsTo.equals(otherBC.belongsTo)))
            && (this.number == otherBC.number
                || (this.number != null
                && this.number.equals(otherBC.number)))
            && this.value == otherBC.value;
    }
}
