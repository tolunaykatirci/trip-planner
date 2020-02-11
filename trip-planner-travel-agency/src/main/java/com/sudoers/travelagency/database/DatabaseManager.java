package com.sudoers.travelagency.database;

import com.sudoers.travelagency.database.model.Airline;
import com.sudoers.travelagency.database.model.Hotel;
import com.sudoers.travelagency.util.AppConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    public static Connection connection;

    // static connection for db operations
    public DatabaseManager() {
        try {
            // if connection null, create new connection
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
                path = AppConfig.projectPath+"/"+ AppConfig.travelAgencyProperties.getDatabasePath();
            else
                path = AppConfig.travelAgencyProperties.getDatabasePath();
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

    // create table in db
    private static void createTable(String sql) {
        try {
            Statement stmt = connection.createStatement();
            // execute statement
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // create initial tables with sql
    public static void createInitialTables() {
        String hotelSql = "CREATE TABLE IF NOT EXISTS hotel(\n"
                + " hotel_id integer PRIMARY KEY, \n"
                + " name text, \n"
                + " ip text, \n"
                + " port integer, \n"
                + " capacity integer, \n"
                + " status boolean \n"
                + " );";
        createTable(hotelSql);

        String airlineSql = "CREATE TABLE IF NOT EXISTS airline(\n"
                + " airline_id integer PRIMARY KEY, \n"
                + " name text, \n"
                + " ip text, \n"
                + " port integer, \n"
                + " capacity integer, \n"
                + " status boolean \n"
                + " );";
        createTable(airlineSql);
    }

    // add hotel to database, if exists, update properties
    public boolean addOrUpdateHotel(Hotel hotel) {
        boolean result = false;
        Hotel registeredHotel = findHotel(hotel.getIp(), hotel.getPort());
        // create hotel
        if (registeredHotel == null){
            String sql = "INSERT INTO hotel(name, ip, port, capacity, status) VALUES(?,?,?,?,?)";
            try {
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, hotel.getName());
                pstmt.setString(2, hotel.getIp());
                pstmt.setInt(3, hotel.getPort());
                pstmt.setInt(4, hotel.getCapacity());
                pstmt.setBoolean(5, true);
                pstmt.executeUpdate();
                System.out.println("Hotel: " + hotel.getName() + " successfully added!");
                result = true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                result = false;
            }
        } else {
            // update hotel
            String sql = "UPDATE hotel SET name = ? , capacity = ? WHERE ip = ? and port = ?";
            try {
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, hotel.getName());
                pstmt.setInt(2, hotel.getCapacity());
                pstmt.setString(3, hotel.getIp());
                pstmt.setInt(4, hotel.getPort());
                pstmt.executeUpdate();
                System.out.println("Hotel: " + hotel.getName() + " successfully updated!");
                result = true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                result = false;
            }
        }
        return result;
    }

    // find hotel by ip and port
    public Hotel findHotel(String ip, int port) {
        String sql = "SELECT * FROM hotel WHERE ip = ? and port = ? ";

        Hotel hotel = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, ip);
            pstmt.setInt(2, port);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                hotel = new Hotel();
                hotel.setHotelId(rs.getInt("hotel_id"));
                hotel.setName(rs.getString("name"));
                hotel.setIp(rs.getString("ip"));
                hotel.setPort(rs.getInt("port"));
                hotel.setCapacity(rs.getInt("capacity"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return hotel;
    }

    // find hotel by name
    public Hotel findHotelByName(String hotelName) {
        String sql = "SELECT * FROM hotel WHERE name = ? ";

        Hotel hotel = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, hotelName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                hotel = new Hotel();
                hotel.setHotelId(rs.getInt("hotel_id"));
                hotel.setName(rs.getString("name"));
                hotel.setIp(rs.getString("ip"));
                hotel.setPort(rs.getInt("port"));
                hotel.setCapacity(rs.getInt("capacity"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return hotel;
    }

    public List<Hotel> findAllHotels() {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT * FROM hotel";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Hotel hotel = new Hotel();
                hotel.setHotelId(rs.getInt("hotel_id"));
                hotel.setName(rs.getString("name"));
                hotel.setIp(rs.getString("ip"));
                hotel.setPort(rs.getInt("port"));
                hotel.setCapacity(rs.getInt("capacity"));

                hotels.add(hotel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hotels;
    }

    public List<Hotel> findAvailableHotels() {
        String sql = "SELECT * FROM hotel WHERE status = ? ";

        List<Hotel> hotels = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setBoolean(1, true);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Hotel hotel = new Hotel();
                hotel.setHotelId(rs.getInt("hotel_id"));
                hotel.setName(rs.getString("name"));
                hotel.setIp(rs.getString("ip"));
                hotel.setPort(rs.getInt("port"));
                hotel.setCapacity(rs.getInt("capacity"));

                hotels.add(hotel);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return hotels;
    }


    /**
     * make all hotel operations for airline also
     *
     */

    public boolean addOrUpdateAirline(Airline airline) {
        boolean result = false;
        Airline registeredAirline = findAirline(airline.getIp(), airline.getPort());
        if (registeredAirline == null){
            String sql = "INSERT INTO airline(name, ip, port, capacity, status) VALUES(?,?,?,?,?)";
            try {
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, airline.getName());
                pstmt.setString(2, airline.getIp());
                pstmt.setInt(3, airline.getPort());
                pstmt.setInt(4, airline.getCapacity());
                pstmt.setBoolean(5, true);
                pstmt.executeUpdate();

                result = true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                result = false;
            }
        } else {
            String sql = "UPDATE airline SET name = ? , capacity = ? WHERE ip = ? and port = ?";
            try {
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, airline.getName());
                pstmt.setInt(2, airline.getCapacity());
                pstmt.setString(3, airline.getIp());
                pstmt.setInt(4, airline.getPort());
                pstmt.executeUpdate();
                System.out.println("Airline: " + airline.getName() + " successfully updated!");
                result = true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                result = false;
            }
        }
        return result;
    }

    public Airline findAirline(String ip, int port) {
        String sql = "SELECT * FROM airline WHERE ip = ? and port = ? ";

        Airline airline = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, ip);
            pstmt.setInt(2, port);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                airline = new Airline();
                airline.setAirlineId(rs.getInt("airline_id"));
                airline.setName(rs.getString("name"));
                airline.setIp(rs.getString("ip"));
                airline.setPort(rs.getInt("port"));
                airline.setCapacity(rs.getInt("capacity"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return airline;
    }

    public Airline findAirlineByName(String airlineName) {
        String sql = "SELECT * FROM airline WHERE name = ?";

        Airline airline = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, airlineName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                airline = new Airline();
                airline.setAirlineId(rs.getInt("airline_id"));
                airline.setName(rs.getString("name"));
                airline.setIp(rs.getString("ip"));
                airline.setPort(rs.getInt("port"));
                airline.setCapacity(rs.getInt("capacity"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return airline;
    }

    public List<Airline> findAllAirlines() {
        List<Airline> airlines = new ArrayList<>();
        String sql = "SELECT * FROM airline ";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Airline airline = new Airline();
                airline.setAirlineId(rs.getInt("airline_id"));
                airline.setName(rs.getString("name"));
                airline.setIp(rs.getString("ip"));
                airline.setPort(rs.getInt("port"));
                airline.setCapacity(rs.getInt("capacity"));

                airlines.add(airline);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return airlines;
    }

    public List<Airline> findAvailableAirlines() {
        String sql = "SELECT * FROM airline WHERE status = ? ";

        List<Airline> airlines = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setBoolean(1, true);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Airline airline = new Airline();
                airline.setAirlineId(rs.getInt("airline_id"));
                airline.setName(rs.getString("name"));
                airline.setIp(rs.getString("ip"));
                airline.setPort(rs.getInt("port"));
                airline.setCapacity(rs.getInt("capacity"));
                airlines.add(airline);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return airlines;
    }

    // update hotel / airline status to true / false
    public boolean updateStatus(String tableName, String recordName, boolean status) {
        boolean result = false;
        String sql = "UPDATE "+tableName+" SET status = ? WHERE name = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setBoolean(1, status);
            pstmt.setString(2, recordName);
            pstmt.executeUpdate();
            System.out.println(tableName+": " + recordName + " successfully updated to: " + status);
            result = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            result = false;
        }
        return result;
    }



}
