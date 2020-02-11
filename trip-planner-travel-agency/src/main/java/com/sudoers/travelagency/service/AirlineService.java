package com.sudoers.travelagency.service;

import com.sudoers.travelagency.database.DatabaseManager;
import com.sudoers.travelagency.database.model.Airline;
import com.sudoers.travelagency.database.model.Hotel;
import com.sudoers.travelagency.util.SocketManager;

import java.util.ArrayList;
import java.util.List;

public class AirlineService {

    // Singleton Airline service for database operations

    private static AirlineService instance = null;
    private DatabaseManager databaseManager;

    // constructor
    private AirlineService() {
        databaseManager = new DatabaseManager();
    }

    // service instance
    public static AirlineService getInstance() {
        if( instance == null)
            instance = new AirlineService();
        return instance;
    }

    // add or update airline (same with db)
    public boolean addOrUpdateAirline(String name, String ip, String port, String capacity) {
        boolean result = false;
        try {
            Airline airline = new Airline();
            airline.setName(name);
            airline.setIp(ip);
            airline.setPort(Integer.parseInt(port));
            airline.setCapacity(Integer.parseInt(capacity));
            result = databaseManager.addOrUpdateAirline(airline);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // get active Airlines (where status = true)
    public List<Airline> getAvailableAirlines() {
        return databaseManager.findAvailableAirlines();
    }

    // get all Airlines
    public List<Airline> getAllAirlines() {
        return databaseManager.findAllAirlines();
    }

    // make airline reservation with specified properties
    public boolean makeReservation(String startDate, String endDate, String customerName, int customerCount, String airlineName) {
        Airline airline = databaseManager.findAirlineByName(airlineName);
        return SocketManager.makeReservation(airline.getIp(), airline.getPort(), startDate, endDate, customerName, customerCount);
    }
}
