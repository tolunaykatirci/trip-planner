package com.sudoers.travelagency.service;

import com.sudoers.travelagency.database.DatabaseManager;
import com.sudoers.travelagency.database.model.Hotel;
import com.sudoers.travelagency.util.SocketManager;

import java.util.ArrayList;
import java.util.List;

public class HotelService {

    // Singleton Hotel service for database operations

    private static HotelService instance = null;
    private DatabaseManager databaseManager;

    // constructor
    private HotelService() {
        databaseManager = new DatabaseManager();
    }

    // service instance
    public static HotelService getInstance() {
        if (instance == null)
            instance = new HotelService();
        return instance;
    }

    // add or update hotel (same with db)
    public boolean addOrUpdateHotel(String name, String ip, String port, String capacity){
        boolean result = false;
        try {
            Hotel hotel = new Hotel();
            hotel.setName(name);
            hotel.setIp(ip);
            hotel.setPort(Integer.parseInt(port));
            hotel.setCapacity(Integer.parseInt(capacity));
            result = databaseManager.addOrUpdateHotel(hotel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // get active Hotels (where status = true)
    public List<Hotel> getAvailableHotels(){
        return databaseManager.findAvailableHotels();
    }

    // get all Hotels
    public List<Hotel> getAllHotels() {
        return databaseManager.findAllHotels();
    }

    // make hotel reservation with specified properties
    public boolean makeReservation(String startDate, String endDate, String customerName, int customerCount, String hotelName) {
        Hotel hotel = databaseManager.findHotelByName(hotelName);
        return SocketManager.makeReservation(hotel.getIp(), hotel.getPort(), startDate, endDate, customerName, customerCount);
    }

}
