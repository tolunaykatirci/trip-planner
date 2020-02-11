package com.sudoers.hotel.database;

import com.sudoers.hotel.util.AppConfig;

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
                path = AppConfig.projectPath+"/"+ AppConfig.hotelProperties.getDatabasePath();
            else
                path = AppConfig.hotelProperties.getDatabasePath();
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
        String roomSql = "CREATE TABLE IF NOT EXISTS room (\n"
                + " room_id integer PRIMARY KEY, \n"
                + " room_name text \n"
                + " );";
        createTable(roomSql);
        int roomCount = AppConfig.hotelProperties.getRoomCount();

        String bookingSql = "CREATE TABLE booking (\n"
                + " book_id integer PRIMARY KEY, \n"
                + " room_id integer, \n"
                + " customer_name text, \n"
                + " reservation_date date \n"
                + " );";
        createTable(bookingSql);

        // recreate rooms
        dropRooms();
        for (int i = 1; i < roomCount+1; i++) {
            createRoom(i, "Room_"+i);
        }
    }

    // create room
    private static void createRoom(int roomId, String roomName) {
        String sql = "INSERT INTO room(room_id, room_name) VALUES(?,?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, roomId);
            pstmt.setString(2, roomName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // drop all rooms
    private static void dropRooms(){
        String sql = "DELETE FROM room";
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Drop rooms error!");
        }
    }

    // make reservation daily
    private void makeReservation(int roomId, String customerName, Date reservationDate){

        String sql = "INSERT INTO booking(room_id, customer_name, reservation_date) VALUES(?,?,?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, roomId);
            pstmt.setString(2, customerName);
            pstmt.setDate(3, reservationDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // create reservation with specified values
    public void book(int roomId, String customerName, Date startDate, Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        long startTime = startDate.getTime();
        long endTime = endDate.getTime();

        // if room is available, make reservation
        if (isRoomAvailable(roomId, startDate, endDate)) {
            long tempTime = startTime;
            while (tempTime <= endTime) {
                makeReservation(roomId, customerName, new Date(tempTime));
                tempTime += TimeUnit.DAYS.toMillis(1);
            }
            System.out.println("Reservation successfully created on room: " + roomId);
        } else {
            System.out.println("Reservation couldn't created!" );
        }
    }

    // check if hotel available on specified date intervals
    public List<Integer> isAvailable(Date startDate, Date endDate){
        List<Integer> availableRooms = new ArrayList<>();
        List<Integer> fullRooms = new ArrayList<>();

        String sql = "SELECT room_id FROM booking WHERE reservation_date >= ? and reservation_date <= ?";

        try {

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int roomId = rs.getInt("room_id");
                fullRooms.add(roomId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int roomCount = AppConfig.hotelProperties.getRoomCount();
        for (int i = 1; i < roomCount+1; i++) {
            if(!fullRooms.contains(i))
                availableRooms.add(i);
        }

        return availableRooms;
    }

    // check if room available on specified date
    private boolean isRoomAvailable(int roomId, Date startDate, Date endDate) {
        String sql = "SELECT * FROM booking WHERE room_id = ? and reservation_date >= ? and reservation_date <= ?";

        try {

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, roomId);
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
