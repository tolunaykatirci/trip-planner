package com.sudoers.tripplanner.util;

import com.sudoers.tripplanner.protocol.SUDORequest;
import com.sudoers.tripplanner.protocol.SUDOResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TripPlannerService {

    private static final String TRAVEL_AGENCY_IP = "127.0.0.1";
    private static final int TRAVEL_AGENCY_PORT = 8080;

    // get all active hotels
    public List<String> getAllHotels() {
        List<String> hotelNames = new ArrayList<>();
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(TRAVEL_AGENCY_IP, TRAVEL_AGENCY_PORT), 6000);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);

            // create request
            SUDORequest sudoRequest = new SUDORequest();
            sudoRequest.setHost(TRAVEL_AGENCY_IP);
            sudoRequest.setPort(TRAVEL_AGENCY_PORT);
            sudoRequest.setVersion("VER_1.0");
            sudoRequest.setFunction("getHotelList");

            // input
            out.write(SUDORequest.createRequest(sudoRequest));
            out.flush();

            // parse response
            SUDOResponse sudoResponse = SUDOResponse.parseResponse(in);
            System.out.println(sudoResponse);
            hotelNames = sudoResponse.getDataList();

            // close connections
            in.close();
            out.close();
            clientSocket.close();


        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Socket Error!");
        }

        //return active hotels
        return hotelNames;
    }

    // get all active airlines
    public List<String> getAllAirlines() {
        List<String> airlineNames = new ArrayList<>();
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(TRAVEL_AGENCY_IP, TRAVEL_AGENCY_PORT), 6000);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);

            SUDORequest sudoRequest = new SUDORequest();
            sudoRequest.setHost(TRAVEL_AGENCY_IP);
            sudoRequest.setPort(TRAVEL_AGENCY_PORT);
            sudoRequest.setVersion("VER_1.0");
            sudoRequest.setFunction("getAirlineList");

            // input
            out.write(SUDORequest.createRequest(sudoRequest));
            out.flush();

            SUDOResponse sudoResponse = SUDOResponse.parseResponse(in);
            System.out.println(sudoResponse);
            airlineNames = sudoResponse.getDataList();

            in.close();
            out.close();
            clientSocket.close();


        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Socket Error!");
        }

        return airlineNames;
    }

    // get available hotels in specified date intervals with properties
    public List<String> getAvailableHotels(String startDate, String endDate, int travelerCount) {
        List<String> hotelNames = new ArrayList<>();

        HashMap<String, String> data = new HashMap<>();
        data.put("startDate", startDate);
        data.put("endDate", endDate);
        data.put("travelerCount", String.valueOf(travelerCount));
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(TRAVEL_AGENCY_IP, TRAVEL_AGENCY_PORT), 6000);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);

            // create request
            SUDORequest sudoRequest = new SUDORequest();
            sudoRequest.setHost(TRAVEL_AGENCY_IP);
            sudoRequest.setPort(TRAVEL_AGENCY_PORT);
            sudoRequest.setVersion("VER_1.0");
            sudoRequest.setFunction("getAvailableHotels");
            sudoRequest.setDataMap(data);

            // input
            out.write(SUDORequest.createRequest(sudoRequest));
            out.flush();

            // parse response
            SUDOResponse sudoResponse = SUDOResponse.parseResponse(in);
            System.out.println(sudoResponse);
            hotelNames = sudoResponse.getDataList();

            // close connections
            in.close();
            out.close();
            clientSocket.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return hotelNames;
    }

    // get available airlines in specified date intervals with properties
    public List<String> getAvailableAirlines(String startDate, String endDate, int travelerCount){
        List<String> airlineNames = new ArrayList<>();

        HashMap<String, String> data = new HashMap<>();
        data.put("startDate", startDate);
        data.put("endDate", endDate);
        data.put("travelerCount", String.valueOf(travelerCount));
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(TRAVEL_AGENCY_IP, TRAVEL_AGENCY_PORT), 6000);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);

            SUDORequest sudoRequest = new SUDORequest();
            sudoRequest.setHost(TRAVEL_AGENCY_IP);
            sudoRequest.setPort(TRAVEL_AGENCY_PORT);
            sudoRequest.setVersion("VER_1.0");
            sudoRequest.setFunction("getAvailableAirlines");
            sudoRequest.setDataMap(data);

            // input
            out.write(SUDORequest.createRequest(sudoRequest));
            out.flush();

            SUDOResponse sudoResponse = SUDOResponse.parseResponse(in);
            System.out.println(sudoResponse);
            airlineNames = sudoResponse.getDataList();

            in.close();
            out.close();
            clientSocket.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return airlineNames;
    }

    // make reservation with selected hotel and airline
    public boolean makeReservation(String startDate, String endDate, String registrationName, String travelerCount, String selectedHotel, String selectedAirline) {
        boolean result = false;
        // update data
        HashMap<String, String> data = new HashMap<>();
        data.put("startDate", startDate);
        data.put("endDate", endDate);
        data.put("travelerName", registrationName);
        data.put("travelerCount", travelerCount);
        data.put("hotelName", selectedHotel);
        data.put("airlineName", selectedAirline);
        try {
            // create socket
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(TRAVEL_AGENCY_IP, TRAVEL_AGENCY_PORT), 6000);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);

            // create request
            SUDORequest sudoRequest = new SUDORequest();
            sudoRequest.setHost(TRAVEL_AGENCY_IP);
            sudoRequest.setPort(TRAVEL_AGENCY_PORT);
            sudoRequest.setVersion("VER_1.0");
            sudoRequest.setFunction("makeReservation");
            sudoRequest.setDataMap(data);

            // input
            out.write(SUDORequest.createRequest(sudoRequest));
            out.flush();

            // parse response
            SUDOResponse sudoResponse = SUDOResponse.parseResponse(in);
            System.out.println(sudoResponse);
            result = Boolean.parseBoolean(sudoResponse.getDataMap().get("result"));

            // close connections
            in.close();
            out.close();
            clientSocket.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
