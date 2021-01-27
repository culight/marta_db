package main.java.util;

import main.java.model.*;
import java.util.List;

public interface DBWrite {
    // Users
    boolean addUser(User user);
    boolean updateUser(User original, User updated);

    boolean addPassenger(Passenger passenger);
    boolean updatePassenger(Passenger original, Passenger updated);

    // BreezeCards
    boolean addBreezeCard(BreezeCard breezeCard);
    boolean updateBreezeCard(BreezeCard original, BreezeCard updated);

    // Stations
    boolean addStation(Station station);
    boolean updateStation(Station original, Station updated);

    boolean addBusStation(Bus busStation);
    boolean updateBusStation(Bus original, Bus updated);

    // Trip
    boolean addTrip(Trip trip);
    boolean updateTrip(Trip original, Trip updated);

    // Conflict
    boolean addConflict(Conflict conflict);
    boolean updateConflict(Conflict original, Conflict updated);
    boolean removeConflict(Conflict conflict);
}
