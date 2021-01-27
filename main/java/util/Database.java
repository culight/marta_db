package main.java.util;

import main.java.MartaApp;
import main.java.controller.MartaController;
import main.java.model.BreezeCard;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.LinkedList;
import java.util.List;
import main.java.model.*;
import java.sql.Timestamp;


public class Database implements DBWrite, DBRead {
    static Database instance;
    private Connection connection;
    private static MartaApp app;

    public static void setApp(MartaApp application) {
        if(application == null) System.out.println("db NULL???");
        else System.out.println("db Not null...");
        app = application;
    }

    public static Database getInstance() {
        if(instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private Database() {
        try {
            FileReader fr = new FileReader("db_info.txt");
            BufferedReader br = new BufferedReader(fr);
            String username = br.readLine();
            String password = br.readLine();
            String url = br.readLine();
            br.close();

            System.out.println("Connecting to Database...");
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + url + "/" + username,
              username,
              password);
            System.out.println("Database Successfully Connected!");
        } catch (IOException e) {
            app.logError("Database", "You do not have access to private database information.");
        } catch (SQLException e) {
            app.logError("Database", "There was a problem connecting to the Database:\n"+e.getMessage());
        } catch (ClassNotFoundException e) {
            app.logError("Database", "The application is missing the MySQL Driver library.");
        }
    }

    public ResultSet query(String sqlQuery) {
        try {
            System.out.println("Running query: " + sqlQuery);
            Statement statement = connection.createStatement();
            return statement.executeQuery(sqlQuery);
        } catch (SQLException e) {
            app.logError("Database",
                    "There was a problem running the query:\n"
                            +sqlQuery
                            +"\n\nError message:"
                            +e.getMessage());
            return null;
        }
    }

    public boolean update(String sqlUpdate) {
        try {
            System.out.println("Running update: " + sqlUpdate);
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sqlUpdate) > 0;
        } catch (SQLException e) {
            app.logError("Database",
                    "There was a problem running the update:\n"
                            +sqlUpdate
                            +"\n\nError message:"
                            +e.getMessage());
            return false;
        }
    }

    /* ---- Read Methods ---- */
    // Users
    @Override
    public List<User> getUsers(){
        return getUsers(null, null, null);
    }

    @Override
    public List<User> getUsers(String username, String password, Boolean isAdmin) {
        String sql = "SELECT * FROM user";
        String[] varNames = {"username", "password", "isAdmin"};
        sql += addWhereClause(varNames, username, password, isAdmin) + ";";
        ResultSet results = query(sql);
        List<User> users = new LinkedList<>();
        try {
            while (results.next()) {
                users.add(new User(results));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return users;
    }

    @Override
    public List<Passenger> getPassengers() {
        return getPassengers(null, null, null);
    }

    @Override
    public List<Passenger> getPassengers(String username, String password, String email) {
        String sql = "SELECT * FROM passenger";
        String[] varNames = {"username", "password", "email"};
        sql += addWhereClause(varNames, username, password, email);
        ResultSet results = query(sql);
        List<Passenger> passengers = new LinkedList<>();
        try {
            while (results.next()) {
                passengers.add(new Passenger(results));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return passengers;
    }

    // BreezeCards
    @Override
    public List<BreezeCard> getBreezeCards(){
        return getBreezeCards(null, null,
                -1, -1, false);
    }

    @Override
    public List<BreezeCard> getBreezeCards(String owner, String number, double valLow,
                                           double valHigh, boolean showSuspended) {
        List<BreezeCard> breezeCards = new LinkedList<>();
        String sql = "SELECT * FROM breezecard";
        if(!showSuspended) { sql += " AS bc"; }
        String[] varNames = {"belongsTo", "breezeNum", "value>", "value<"};
        String where = addWhereClause(varNames, owner, number, valLow, valHigh);
        if(!showSuspended) {
            if(where.equals("")) {
                where += " WHERE";
            } else {
                where += " AND";
            }
            where += " bc.breezeNum NOT IN (SELECT breezeNum FROM conflict)";
        }
        sql += where + ";";
        ResultSet results = query(sql);
        try {
            while (results.next()) {
                breezeCards.add(new BreezeCard(results));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return breezeCards;
    }


    // Stations
    @Override
    public List<Station> getStations(){
        return getStations(null, null, -1, null, null);
    }
    @Override
    public List<Station> getStations(String name, String stopID, double fare,
                                  Boolean isTrain, Boolean isOpen){
        String sql = "SELECT * FROM station";
        String[] varNames = {"name", "stopID", "fare", "isTrain", "isOpen"};
        sql += addWhereClause(varNames, name, stopID, fare, isTrain, isOpen) + ";";
        ResultSet resultSet = query(sql);
        List<Station> stations = new LinkedList<>();
        try {
            while (resultSet.next()) {
                stations.add(new Station(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return stations;
    }

    @Override
    public List<Bus> getBuses(){
      return getBuses(null, null);
    }

    @Override
    public List<Bus> getBuses(String stopID, String intersection){
        String sql = "SELECT * FROM bus";
        String[] varNames = {"stopID", "intersection"};
        sql += addWhereClause(varNames, stopID, intersection) + ";";
        ResultSet resultSet = query(sql);
        List<Bus> buses = new LinkedList<>();
        try {
            while (resultSet.next()) {
                buses.add(new Bus(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return buses;
    }

    // Conflict
    @Override
    public List<Conflict> getConflicts(){return getConflicts(null, null, null, null);}
    @Override
    public List<Conflict> getConflicts(String username, String breezeNum,
        Timestamp date_suspended, String oldUser){

        String sql = "SELECT * FROM breezecard NATURAL JOIN conflict";
        String[] varNames = {"username", "breezeNum", "date_suspended", "belongsTo"};
        sql += addWhereClause(varNames, username, breezeNum, date_suspended, oldUser) + ";";
        ResultSet resultSet = query(sql);

        List<Conflict> conflicts = new LinkedList<>();

        try {
            while (resultSet.next()) {
                conflicts.add(new Conflict(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return conflicts;
    }

    public List<BreezeCard> getSuspendedBreezeCards(String newOwner, String number, String dateSuspended) {
        List<BreezeCard> suspendedBreezeCards = new LinkedList<>();

        String sql = "SELECT * FROM conflict";
        String[] varNames = {"username", "breezeNum", "date_suspended"};
        sql += addWhereClause(varNames, newOwner, number, dateSuspended) + ";";
        ResultSet resultSet = query(sql);

        try {
            while (resultSet.next()) {
                suspendedBreezeCards.add(new BreezeCard((resultSet)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return suspendedBreezeCards;

    }


    public User getPrevOwnerFromBreezeCardNum(String breezeCardnum) {
        String sql = "SELECT * FROM breezecard WHERE breezeNum=" + breezeCardnum;

        ResultSet resultSet = query(sql);
        String prevOwnerName = "";

        try {
            prevOwnerName = resultSet.getString("belongsTo");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        String prevOwnerPassword = "";
        boolean prevOwnerisAdmin = false;

        if (prevOwnerName.equals("")) {
            return null;
        }

        String sql_two = "SELECT * FROM user WHERE username=" + prevOwnerName;
        ResultSet resultSet1 = query(sql_two);

        try {
            prevOwnerPassword = resultSet1.getString("password");
            prevOwnerisAdmin = resultSet1.getBoolean("isAdmin");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        User prevOwner = new User(prevOwnerName, prevOwnerPassword, prevOwnerisAdmin);

        return prevOwner;
    }

    // SELECT FROM breezecard * conflict;

    @Override
    public List<FlowReport> getFlowReports(Timestamp start, Timestamp end) {
        String dateFilter = "";
        if(start != null) {
            dateFilter += "WHERE trip.time_start >= '" + start.toString()+"'";
        }
        if(end != null) {
            if(!dateFilter.equals("")) {
                dateFilter += " AND ";
            } else {
                dateFilter += "WHERE ";
            }
            dateFilter += "trip.time_start <= '" + end.toString()+"'";
        }
        String sql = "SELECT t1.name, t2.name, passIn, passOut, revenue FROM\n" +
                "(SELECT name, breezeNum, COUNT(breezeNum) AS passIn, SUM(current_fare) AS revenue\n" +
                "FROM (SELECT * FROM trip " + dateFilter+") AS trip1\n" +
                "JOIN station ON trip1.startID = station.stopID\n" +
                "GROUP BY name) AS t1\n" +
                "JOIN\n" +
                "(SELECT name, breezeNum, COUNT(breezeNum) AS passOut\n" +
                "FROM (SELECT * FROM trip " + dateFilter+") AS trip2\n" +
                "JOIN station ON trip2.endID = station.stopID\n" +
                "GROUP BY name) AS t2\n" +
                "ON t1.name = t2.name\n" +
                "GROUP BY t1.name;";
        ResultSet results = query(sql);
        List<FlowReport> flowReports = new LinkedList<>();
        try {
            while(results.next()) {
                flowReports.add(new FlowReport(results));
            }
        } catch (SQLException e) {
            app.logError("Database", e.toString());
        }
        return flowReports;
    }

    // Trip
    @Override
    public List<Trip> getTrips(){return getTrips(null,
            null, null, -1, null, null);}
    @Override
    public List<Trip> getTrips(String breezeNum, Timestamp startTime, Timestamp endTime,
                               double current_fare, String startID, String endID) {
        String sql = "SELECT * FROM trip";
        String[] varNames = {"breezeNum", "current_fare", "startID", "endID"};
        String where = addWhereClause(varNames, breezeNum, current_fare, startID, endID);

        if(startTime != null) {
            if(where.equals("")) { where += " WHERE ";}
            else { where += "AND ";}
            where += "time_start >= '" + startTime.toString() + "'";
        }
        if(endTime != null) {
            if(where.equals("")) { where += " WHERE ";}
            else { where += "AND "; }
            where += "time_start <= '" + endTime.toString() + "'";
        }
        if(!where.equals(" WHERE ")) { sql += where; }
        sql += ";";
        ResultSet resultSet = query(sql);
        List<Trip> trips = new LinkedList<>();
        try {
            while(resultSet.next()) {
                trips.add(new Trip(resultSet));
            }
        } catch (SQLException e) {
            app.logError("Database", e.toString());
        }
        return trips;
    }


    /* ---- Write Methods ---- */

    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO user (username, password, isAdmin) VALUES (";
        sql += "'" + user.getUsername() + "'" + "," +
                "'" + user.getPassword() + "'" + ", "
                + (user.getIsAdmin() ? "TRUE" : "FALSE");
        sql += ");";
        return update(sql);
    }

    @Override
    public boolean updateUser(User original, User updated) {
        return false;
    }

    @Override
    public boolean addPassenger(Passenger passenger) {
        String sql = "INSERT INTO passenger (username, email) VALUES (";
        sql += "'" + passenger.getUsername() + "'" + "," +
               "'" + passenger.getEmail() + "'" + ");";
        return update(sql);
    }

    @Override
    public boolean updatePassenger(Passenger original, Passenger updated) {
        return false;
    }

    @Override
    public boolean addBreezeCard(BreezeCard breezeCard) {
        String sql = "INSERT INTO breezecard (breezeNum, value, belongsTo) VALUES (";
        sql += breezeCard.getNumber() + ", "
          + breezeCard.getValue() + ", ";
        if (breezeCard.getBelongsTo() == null) {
            // add breezecard with no user
            sql += "NULL";
        } else {
            sql += "'" + breezeCard.getBelongsTo() + "'";
        }
        sql += ");";
        return update(sql);
    }

    @Override
    public boolean updateBreezeCard(BreezeCard original, BreezeCard updated) {
        String sql = "UPDATE breezecard SET ";
        String values = "";
        if(!original.getNumber().equals(updated.getNumber())) {
            values += "breezeNum = '" + updated.getNumber()+"'";
        }
        if(original.getValue() != updated.getValue()) {
            if(!values.equals("")) {values += ", ";}
            values += String.format("value = %.2f", updated.getValue());
        }
        if(original.getBelongsTo() != updated.getBelongsTo()) {
            if(!values.equals("")) {values += ", ";}
            if(updated.getBelongsTo() == null) {
                // dissociate user from breezecard
                values += "belongsTo=NULL";
            } else {
                values += "belongsTo = '" + updated.getBelongsTo()+ "'";
            }
        }

        String[] varNames = {"breezeNum","value","belongsTo"};
        String where = addWhereClause(varNames, original.getNumber(),
                original.getValue(), original.getBelongsTo());
        if(!values.equals("") && !where.equals("")) {
            sql += values + where + ";";
            return update(sql);
        } else {
            return false;
        }
    }


    @Override
    public boolean addStation(Station station) {
        String sql = "INSERT INTO station (name, stopID, fare, isTrain, isOpen) VALUES (";
        sql += "'" + station.getName() + "', "
                + "'" + station.getStopID() + "', "
                + String.format("%.2f",station.getFare()) + ", "
                + (station.getTrain() ? "TRUE" : "FALSE") + ", "
                + (station.getOpen() ? "TRUE" : "FALSE");
        sql += ");";
        return update(sql);
    }

    @Override
    public boolean updateStation(Station original, Station updated) {
        String sql = "UPDATE station SET ";
        String values = "";
        if(!original.getName().equals(updated.getName())) {
            values += "name = '" + updated.getName() + "'";
        }
        if(!original.getStopID().equals(updated.getStopID())) {
            values += "stopID = '" + updated.getStopID() + "'";
        }
        if(original.getFare() != updated.getFare()
          && updated.getFare() > -1) {
            if(!values.equals("")) {values += ", ";}
            values += "fare = '" + updated.getFare() + "'";
            // String.format("fare = %.2f", updated.getFare());
        }
        if(original.getTrain() != updated.getTrain()) {
            values += "isTrain = '" + (updated.getTrain() ? 1:0) + "'";
        }
        if(original.getOpen() != updated.getOpen()) {
            values += "isOpen = '" + (updated.getOpen() ? 1:0) + "'";
        }
        String[] varNames = {"name","stopID","fare","isTrain","isOpen"};
        String where = addWhereClause(varNames, original.getName(),
          original.getStopID(), original.getFare(), (original.getTrain() ? 1:0),
          (original.getOpen() ? 1:0));
        if(!values.equals("") && !where.equals("")) {
            sql += values + where + ";";
            return update(sql);
        } else {
            return false;
        }
    }

    public boolean addBusStation(Bus busStation) {
        String sql = "INSERT INTO bus (stopID, intersection) VALUES (";
         sql += busStation.getStopID() ;

         if(busStation.getIntersection() == null) {
                sql +=  ", " + "intersection=NULL" + ");";
            } else {
                sql += ", " + busStation.getIntersection() + ");";
            }
            return update(sql);
    }

    @Override
    public boolean updateBusStation(Bus original, Bus updated) {
        String sql = "UPDATE bus SET ";
        String values = "";
        if(!original.getStopID().equals(updated.getStopID())) {
            values += "stopID = '" + updated.getStopID() + "'";
        }
        if(!original.getIntersection().equals(updated.getIntersection())) {
            values += "intersection = '" + updated.getIntersection() + "'";
        }
        String[] varNames = {"stopID","intersection"};
        String where = addWhereClause(varNames, original.getStopID(),
                original.getIntersection());
        if(!values.equals("") && !where.equals("")) {
            sql += values + where + ";";
            return update(sql);
        } else {
            return false;
        }
    }

    @Override
    public boolean addTrip(Trip trip) {
        String sql = "INSERT INTO trip (breezeNum, current_fare, startID) VALUES (";
        sql += "'"+trip.getBreezeNum() + "', " + String.format("'%.2f'", trip.getCurrent_fare());
        sql += ", '" + trip.getStartID() + "');";
        return update(sql);
    }
    @Override
    public boolean updateTrip(Trip original, Trip updated) {
        String sql = "UPDATE trip SET ";
        String values = "";
        if(!original.getBreezeNum().equals(updated.getBreezeNum())) {
            values += "breezeNum = '" + updated.getBreezeNum() + "'";
        }
        if(original.getCurrent_fare() != updated.getCurrent_fare()) {
            if(!values.equals("")) { values += " , "; }
            values += "current_fare = " + String.format("'%.2f'", updated.getCurrent_fare());
        }
        if(!original.getTime_start().equals(updated.getTime_start())) {
            if(!values.equals("")) { values += " , "; }
            values += "time_start = '" + updated.getTime_start() + "'";
        }
        if(!original.getStartID().equals(updated.getStartID())) {
            if(!values.equals("")) { values += " , "; }
            values += "startID = '" + updated.getStartID() + "'";
        }
        if(original.getEndID() == null || !original.getEndID().equals(updated.getEndID())) {
            if(!values.equals("")) { values += " , "; }
            values += "endID = '" + updated.getEndID() + "'";
        }
        String[] varName = {"breezeNum", "current_fare", "time_start", "startID", "endID"};
        String where = addWhereClause(varName,
                original.getBreezeNum(),
                original.getCurrent_fare(),
                original.getTime_start(),
                original.getStartID(),
                original.getEndID()
                );
        if (where != "" && values != "") {
            sql += values + where + ";";
            return update(sql);
        }
        return false;
    }

    @Override
    public boolean addConflict(Conflict conflict) {
        String sql = "INSERT INTO conflict (username, breezeNum, date_suspended) VALUES (";
        sql += "'" + conflict.getUsername() + "'" + ", "
          + "'" + conflict.getBreezeNum() + "'" + ", "
          + "'" + conflict.getDate_suspended() + "'" + ");";
        return update(sql);
    }


    @Override
    public boolean updateConflict(Conflict original, Conflict updated) {
        return false;
    }

    @Override
    public boolean removeConflict(Conflict conflict) {
        String sql = "DELETE FROM conflict";
        String[] varNames = {"username", "breezeNum", "value>", "value<"};

        sql += addWhereClause(varNames, conflict.getUsername(), conflict.getBreezeNum()) + ";";

        return update(sql);
    }

    private String addWhereClause(String[] varNames, Object... vars) {
        String conditions = "";
        int count = 0;
        int validCount = 0;
        for (Object var : vars) {
            Boolean isValid = true;

            if (var instanceof Integer) {
                if ((Integer)var < 0)
                    isValid = false;
            }
            else if (var instanceof Double) {
                if ((Double)var < 0.0 || (Double)var >1000.00)
                    isValid = false;
            }
            else if (var instanceof String) {
                if (var == null || var.equals(""))
                    isValid = false;
            }
            else {
                if (var == null)
                    isValid = false;
            }

            if(isValid) {
                if (validCount > 0) {
                    conditions += " AND ";
                } else {
                    conditions += " WHERE ";
                }

                conditions += varNames[count]
                    + "='" + var + "'";

                validCount++;
            }
            count++;
        }
        return conditions;
    }
}
