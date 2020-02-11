package com.sudoers.airline.database;

import com.sudoers.airline.util.AppConfig;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {

    // static database connection
    private static Connection connection;

    // constructor
    public DatabaseManager() {
        try {
            // if connection is null, create new connection
            if (connection == null || connection.isClosed())
                connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void connect() {
        try {
            // get database path
            String path;
            if(AppConfig.projectPath != null)
                path = AppConfig.projectPath+"/"+ AppConfig.airlineProperties.getDatabasePath();
            else
                path = AppConfig.airlineProperties.getDatabasePath();
            // db parameters
            String url = "jdbc:sqlite:" + path;
            // create a connection to the database
            connection = DriverManager.getConnection(url);

            System.out.println("Connected to SQLite database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // create table
    private static void createTable(String sql) {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("Table already created!");
        }
    }

    // create initial tables
    public static void createInitialTables() {
        String seatSql = "CREATE TABLE IF NOT EXISTS seat (\n"
                + " seat_id integer PRIMARY KEY, \n"
                + " seat_name text \n"
                + " );";
        createTable(seatSql);
        int seatCount = AppConfig.airlineProperties.getSeatCount();

        String bookingSql = "CREATE TABLE booking (\n"
                + " book_id integer PRIMARY KEY, \n"
                + " seat_id integer, \n"
                + " customer_name text, \n"
                + " reservation_date date \n"
                + " );";
        createTable(bookingSql);

        // recreate seats
        dropSeats();
        for (int i = 1; i < seatCount+1; i++) {
            createSeat(i, "Seat_"+i);
        }
    }

    // create seat
    private static void createSeat(int seatId, String seatName) {
        String sql = "INSERT INTO seat(seat_id, seat_name) VALUES(?,?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, seatId);
            pstmt.setString(2, seatName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // drop all seats
    private static void dropSeats(){
        String sql = "DELETE FROM seat";
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Drop seats error!");
        }
    }

    // make reservation daily
    private void makeReservation(int seatId, String customerName, Date reservationDate){

        String sql = "INSERT INTO booking(seat_id, customer_name, reservation_date) VALUES(?,?,?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, seatId);
            pstmt.setString(2, customerName);
            pstmt.setDate(3, reservationDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // create reservation with specified values
    public void book(int seatId, String customerName, Date startDate, Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        long startTime = startDate.getTime();
        long endTime = endDate.getTime();

        // if seat is available, make reservation
        if (isSeatAvailable(seatId, startDate, endDate)) {
            long tempTime = startTime;
            while (tempTime <= endTime) {
                makeReservation(seatId, customerName, new Date(tempTime));
                tempTime += TimeUnit.DAYS.toMillis(1);
            }
            System.out.println("Reservation successfully created on seat: " + seatId);
        } else {
            System.out.println("Reservation couldn't created!" );
        }
    }

    // check if airline available on specified date intervals
    public List<Integer> isAvailable(Date startDate, Date endDate){
        List<Integer> availableSeats = new ArrayList<>();
        List<Integer> fullSeats = new ArrayList<>();

        String sql = "SELECT seat_id FROM booking WHERE reservation_date >= ? and reservation_date <= ?";

        try {

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int seatId = rs.getInt("seat_id");
                fullSeats.add(seatId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int seatCount = AppConfig.airlineProperties.getSeatCount();
        for (int i = 1; i < seatCount+1; i++) {
            if(!fullSeats.contains(i))
                availableSeats.add(i);
        }

        return availableSeats;
    }

    // check if room available on specified date
    private boolean isSeatAvailable(int seatId, Date startDate, Date endDate) {
        String sql = "SELECT * FROM booking WHERE seat_id = ? and reservation_date >= ? and reservation_date <= ?";

        try {

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, seatId);
            pstmt.setDate(2, startDate);
            pstmt.setDate(3, endDate);
            ResultSet rs = pstmt.executeQuery();

            return !rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
