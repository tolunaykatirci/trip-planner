package com.sudoers.travelagency.util;

import com.sudoers.travelagency.database.DatabaseManager;
import com.sudoers.travelagency.database.model.Airline;
import com.sudoers.travelagency.database.model.Hotel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketManager {

    // checks selected airline / hotel is running
    public static boolean isRunning(String ip, int port){
        boolean status = false;
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(ip, port), 2000);
            clientSocket.setSoTimeout(2000);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);

            // create GET request
            out.print("GET /isRunning HTTP/1.1\r\n");
            out.print("Host: "+ip+":"+port+"\r\n");
            out.print("Accept: */*\r\n");
            out.print("Cache-Control: no-cache\r\n");
            out.print("Accept-Encoding: gzip,deflate\r\n");
            out.print("\r\n");
            out.flush();


            String lastResponse = "false";
            String response = in.readLine();
            while (response != null){
                lastResponse = response;
                response = in.readLine();
            }
//            System.out.println(lastResponse);
            status = Boolean.parseBoolean(lastResponse);

            in.close();
            out.close();
            clientSocket.close();

        } catch (Exception e){
//            e.printStackTrace();
            System.out.println("error! port: " + port);
        }
        return status;
    }

    // finds available hotels
    public static List<String> getAvailableHotels(String startDate, String endDate, int customerCount) {
        DatabaseManager db = new DatabaseManager();
        // active hotels
        List<Hotel> activeHotels = db.findAvailableHotels();

        List<String> availableHotels = new ArrayList<>();
        for (Hotel hotel:activeHotels) {
            boolean isAvailable = isAvailable(hotel.getIp(), hotel.getPort(), startDate, endDate, customerCount);
            if(isAvailable)
                // add to available hotels
                availableHotels.add(hotel.getName());
        }
        return availableHotels;
    }

    // finds available airlines
    public static List<String> getAvailableAirlines(String startDate, String endDate, int customerCount) {
        DatabaseManager db = new DatabaseManager();
        List<Airline> activeAirlines = db.findAvailableAirlines();

        List<String> availableAirlines = new ArrayList<>();
        for (Airline airline:activeAirlines) {
            boolean isAvailable = isAvailable(airline.getIp(), airline.getPort(), startDate, endDate, customerCount);
            if(isAvailable)
                availableAirlines.add(airline.getName());
        }
        return availableAirlines;
    }

    // is hotel / airline available in specified date
    public static boolean isAvailable(String ip, int port, String startDate, String endDate, int customerCount){
        boolean status = false;
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(ip, port), 5000);
            clientSocket.setSoTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);

            // create GET request
            out.print("GET /isAvailable HTTP/1.1\r\n");
            out.print("Host: "+ip+":"+port+"\r\n");
            out.print("Accept: */*\r\n");
            out.print("Cache-Control: no-cache\r\n");
            out.print("Accept-Encoding: gzip,deflate\r\n");
            out.print("startDate: " + startDate +"\r\n");
            out.print("endDate: " + endDate +"\r\n");
            out.print("customerCount: " + customerCount +"\r\n");
            out.print("\r\n");
            out.flush();

            String lastResponse = "false";
            String response = in.readLine();
            while (response != null){
                lastResponse = response;
                response = in.readLine();
            }
            status = Boolean.parseBoolean(lastResponse);

            in.close();
            out.close();
            clientSocket.close();

        } catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }

    // make hotel / airline reservation with specified properties
    public static boolean makeReservation(String ip, int port, String startDate, String endDate, String customerName, int customerCount){
        boolean status = false;
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(ip, port), 5000);
            clientSocket.setSoTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);

            // create GET request
            out.print("GET /makeReservation HTTP/1.1\r\n");
            out.print("Host: "+ip+":"+port+"\r\n");
            out.print("Accept: */*\r\n");
            out.print("Cache-Control: no-cache\r\n");
            out.print("Accept-Encoding: gzip,deflate\r\n");
            out.print("startDate: " + startDate +"\r\n");
            out.print("endDate: " + endDate +"\r\n");
            out.print("customerName: " + customerName +"\r\n");
            out.print("customerCount: " + customerCount +"\r\n");
            out.print("\r\n");
            out.flush();

            String lastResponse = "false";
            String response = in.readLine();
            while (response != null){
                lastResponse = response;
                response = in.readLine();
            }
            status = Boolean.parseBoolean(lastResponse);

            in.close();
            out.close();
            clientSocket.close();

        } catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }

}
