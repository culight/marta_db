package main.java.util;

import main.java.model.*;
import java.util.List;
import java.sql.Timestamp;

public interface DBRead {
    // Users
    List<User> getUsers(); // get all users
    List<User> getUsers(String username, String password, Boolean isAdmin);

    List<Passenger> getPassengers(); // get all users
    List<Passenger> getPassengers(String username, String password,
        String email);

    // BreezeCards
    List<BreezeCard> getBreezeCards();
    List<BreezeCard> getBreezeCards(String owner, String number,
        double valLow, double valHigh, boolean isSuspended);

    // Stations
    List<Station> getStations(); // get all stations
    List<Station> getStations(String name, String stopID,
        double fare, Boolean isTrain, Boolean isOpen);

    List<Bus> getBuses(); // get all stations
    List<Bus> getBuses(String stopID, String intersection);

    // Trip
    List<Trip> getTrips(); // get all trips
    List<Trip> getTrips(String breezeNum, Timestamp startTime, Timestamp endTime,
        double current_fare, String startID, String endID);

    // Conflict
    List<Conflict> getConflicts(); // get all conflicts
    List<Conflict> getConflicts(String username, String breezeNum,
        Timestamp date_suspended, String oldOwner);

    List<FlowReport> getFlowReports(Timestamp start, Timestamp end);
        
}
