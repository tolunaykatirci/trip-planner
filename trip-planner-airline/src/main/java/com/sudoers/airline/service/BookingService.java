package com.sudoers.airline.service;

import com.sudoers.airline.database.DatabaseManager;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    // static instance for singleton service
    private static BookingService instance = null;

    //database manager
    private DatabaseManager databaseManager;

    // constructor
    private BookingService() {
        databaseManager = new DatabaseManager();
    }

    // create instance if null
    public static BookingService getInstance() {
        if (instance == null)
            instance = new BookingService();
        return instance;
    }

    // checks if airline is available in specified date intervals (same with db)
    public boolean isAvailable(String customerCount, String startDate, String endDate) {
        boolean result = false;
        try {
            int customerCnt = Integer.parseInt(customerCount);

            // get date format
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            long startTime = sdf.parse(startDate).getTime();
            long endTime = sdf.parse(endDate).getTime();

            // get acailable seats
            List<Integer> availableSeats = databaseManager.isAvailable(new Date(startTime), new Date(endTime));
            result = availableSeats.size() >= customerCnt;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    // make reservation with specified values
    public boolean makeReservation(String customerName, String customerCount, String startDate, String endDate) {
        boolean result = false;
        try {
            int customerCnt = Integer.parseInt(customerCount.trim());

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            long startTime = sdf.parse(startDate).getTime();
            long endTime = sdf.parse(endDate).getTime();

            List<Integer> availableSeats = databaseManager.isAvailable(new Date(startTime), new Date(endTime));
            List<Integer> seatIdList = new ArrayList<>();

            for (int i = 0; i <customerCnt; i++) {
                seatIdList.add(availableSeats.get(i));
            }

            if(customerCnt == seatIdList.size()){
                for (int seatId: seatIdList) {
                    // create reservation
                    databaseManager.book(seatId, customerName, new Date(startTime), new Date(endTime));
                }
                String res = "Reservation successfully completed."
                        + " Name: " + customerName
                        + ", count: " + customerCount
                        + ", startDate: " + startDate
                        + ", endDate: " + endDate;
                System.out.println(res);
                result = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
