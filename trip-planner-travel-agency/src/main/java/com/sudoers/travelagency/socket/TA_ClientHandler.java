package com.sudoers.travelagency.socket;

import com.sudoers.travelagency.database.model.Airline;
import com.sudoers.travelagency.database.model.Hotel;
import com.sudoers.travelagency.protocol.SUDORequest;
import com.sudoers.travelagency.protocol.SUDOResponse;
import com.sudoers.travelagency.service.AirlineService;
import com.sudoers.travelagency.service.HotelService;
import com.sudoers.travelagency.util.SocketManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class TA_ClientHandler implements Runnable {

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    private String clientIp;

    private static final String OUTPUT_HEADERS = "Content-Type: text/html";
    private static final String OUTPUT_HTTP_VERSION = "HTTP/1.1";

    /*
        HTTP Handler for TA-Hotel and TA-Airline communications
        SUDO Custom protocol handler for Travel Agency - Client communications
     */

    // constructor
    public TA_ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            // get input/output
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // client ip
            clientIp = clientSocket.getLocalAddress().getHostAddress();
            System.out.println("Client IP: " + clientSocket.getLocalAddress().getHostAddress());
            parseRequest();
            clientSocket.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseRequest() throws Exception{
        String initialLine = in.readLine();
        // if request starts with SUDO, it uses custom SUDO protocol, else it uses HTTP protocol
        if(initialLine.startsWith("SUDO")){
            // parse request
            SUDORequest sudoRequest = SUDORequest.parseRequest(in);
            // print request
            System.out.println(sudoRequest.toString());
            // define variables
            String startDate, endDate, hotelName, airlineName, travelerName;
            int travelerCount;

            SUDOResponse sudoResponse;
            // parse requests according to function
            switch (sudoRequest.getFunction()){
                case "getHotelList":
                    List<Hotel> hotelList = HotelService.getInstance().getAvailableHotels();
                    List<String> hotelNames = new ArrayList<>();
                    for (Hotel h : hotelList) hotelNames.add(h.getName());

                    // create response
                    sudoResponse = new SUDOResponse();
                    sudoResponse.setStatus(20);
                    sudoResponse.setMessage("Hotels successfully listed");
                    sudoResponse.setDataList(hotelNames);
                    respondSUDO(sudoResponse);
                    System.out.println("Response sent!");
                    System.out.println(sudoResponse);
                    break;
                case "getAirlineList":
                    List<Airline> airlineList = AirlineService.getInstance().getAvailableAirlines();
                    List<String> airlineNames = new ArrayList<>();
                    for (Airline a : airlineList) airlineNames.add(a.getName());

                    // create response
                    sudoResponse = new SUDOResponse();
                    sudoResponse.setStatus(20);
                    sudoResponse.setMessage("Airlines successfully listed");
                    sudoResponse.setDataList(airlineNames);
                    respondSUDO(sudoResponse);
                    break;
                case "getAvailableHotels":
                    startDate = sudoRequest.getDataMap().get("startDate");
                    endDate = sudoRequest.getDataMap().get("endDate");
                    travelerCount = Integer.parseInt(sudoRequest.getDataMap().get("travelerCount").trim());
                    List<String> availableHotels = SocketManager.getAvailableHotels(startDate, endDate, travelerCount);

                    // create response
                    sudoResponse = new SUDOResponse();
                    sudoResponse.setStatus(20);
                    sudoResponse.setMessage("Available hotels successfully listed");
                    sudoResponse.setDataList(availableHotels);
                    respondSUDO(sudoResponse);
                    break;
                case "getAvailableAirlines":
                    startDate = sudoRequest.getDataMap().get("startDate");
                    endDate = sudoRequest.getDataMap().get("endDate");
                    travelerCount = Integer.parseInt(sudoRequest.getDataMap().get("travelerCount").trim());
                    List<String> availableAirlines = SocketManager.getAvailableAirlines(startDate, endDate, travelerCount);

                    // create response
                    sudoResponse = new SUDOResponse();
                    sudoResponse.setStatus(20);
                    sudoResponse.setMessage("Available airlines successfully listed");
                    sudoResponse.setDataList(availableAirlines);
                    respondSUDO(sudoResponse);
                    break;
                case "makeReservation":
                    startDate = sudoRequest.getDataMap().get("startDate");
                    endDate = sudoRequest.getDataMap().get("endDate");
                    travelerName = sudoRequest.getDataMap().get("travelerName");
                    travelerCount = Integer.parseInt(sudoRequest.getDataMap().get("travelerCount").trim());
                    hotelName = sudoRequest.getDataMap().get("hotelName");
                    airlineName = sudoRequest.getDataMap().get("airlineName");

                    HotelService.getInstance().makeReservation(startDate, endDate, travelerName, travelerCount, hotelName);
                    AirlineService.getInstance().makeReservation(startDate, endDate, travelerName, travelerCount, airlineName);

                    HashMap<String, String> data = new HashMap<>();
                    data.put("result", "true");

                    sudoResponse = new SUDOResponse();
                    sudoResponse.setStatus(20);
                    sudoResponse.setMessage("Reservation successfully created!");
                    sudoResponse.setDataMap(data);
                    respondSUDO(sudoResponse);

                    break;
                default:
                    sudoResponse = new SUDOResponse();
                    sudoResponse.setStatus(20);
                    sudoResponse.setMessage("Application is running!");
                    respondSUDO(sudoResponse);
                    break;
            }

        } else {
            parseRequestHTTP(initialLine);
        }
    }


    private void parseRequestHTTP(String initialLine) throws IOException {
//        String initialLine = in.readLine();

        if(initialLine == null){
            respondHTTP(401, "Empty Request!", "Unable to parse request");
            return;
        }

        StringTokenizer tok = new StringTokenizer(initialLine);
        String[] components = new String[3];
        for (int i = 0; i < components.length; i++) {
            if (tok.hasMoreTokens())  {
                components[i] = tok.nextToken();
            } else  {
                respondHTTP(401, "Wrong Request!", "Unable to parse request");
                return;
            }
        }

        String method = components[0];
        String path = components[1];
        HashMap<String, String> headers = new HashMap<>();

        // Consume headers
        while (true)  {
            String headerLine = in.readLine();
//            System.out.println(headerLine);
            if (headerLine.length() == 0) {
                break;
            }

            int separator = headerLine.indexOf(":");
            if (separator == -1)  {
                respondHTTP(401, "Wrong Request!", "Unable to parse request");
                return;
            }
            headers.put(headerLine.substring(0, separator),
                    headerLine.substring(separator + 1));
        }
        clientSocket.shutdownInput();
        parsePath(path, headers);
    }

    private void parsePath(String path, HashMap<String, String> headers) throws IOException {
        boolean result;
        // HTTP requests for communication hotels / airlines
        switch (path) {
            case "/":
                respondHTTP(201, "Accepted!", "Application is running!");
                break;
            case "/addOrUpdateHotel":
                String hotelName = headers.get("hotelName").trim();
                String hotelIp = clientIp;
                String hotelPort = headers.get("hotelPort").trim();
                String hotelCapacity = headers.get("hotelCapacity").trim();
                result = HotelService.getInstance().addOrUpdateHotel(hotelName, hotelIp, hotelPort, hotelCapacity);
                if (result)
                    respondHTTP(200, "OK!", "Hotel successfully added!");
                else
                    respondHTTP(500, "Internal Server Error!", "Hotel couldn't added!");
                break;
            case "/addOrUpdateAirline":
                String airlineName = headers.get("airlineName").trim();
                String airlineIp = clientIp;
                String airlinePort = headers.get("airlinePort").trim();
                String seatCapacity = headers.get("seatCapacity").trim();
                result = AirlineService.getInstance().addOrUpdateAirline(airlineName, airlineIp, airlinePort, seatCapacity);
                if (result)
                    respondHTTP(200, "OK!", "Airline successfully added!");
                else
                    respondHTTP(500, "Internal Server Error!", "Airline couldn't added!");
                break;
            default:
                respondHTTP(404, "Not Found!", "Method Not Found!");
                break;
        }
    }

    // respond HTTP protocol
    private void respondHTTP(int statusCode, String msg, Object body) {
        String responseLine = OUTPUT_HTTP_VERSION + " " + statusCode + " " + msg + "\r\n";
        responseLine += OUTPUT_HEADERS + " \r\n\r\n";
        if(body != null)
            responseLine+=body;
        out.write(responseLine);
        out.flush();
        out.close();
    }

    // respond SUDO protocol
    private void respondSUDO(SUDOResponse sudoResponse) {
        String response = SUDOResponse.createResponse(sudoResponse);
        out.write(response);
        out.flush();
        out.close();
    }
}
