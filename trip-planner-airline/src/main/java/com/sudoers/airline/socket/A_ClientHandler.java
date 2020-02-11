package com.sudoers.airline.socket;

import com.sudoers.airline.service.BookingService;
import com.sudoers.airline.util.AppConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;

public class A_ClientHandler implements Runnable{

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    // HTTP values
    private static final String OUTPUT_HEADERS = "Content-Type: text/html";
    private static final String OUTPUT_HTTP_VERSION = "HTTP/1.1";

    // constructor
    public A_ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            System.out.println("Client IP: " + clientSocket.getLocalAddress());
            // parse request
            parseRequest();
            // close close
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseRequest() throws IOException {
        // read and parse request
        String initialLine = in.readLine();
        if(initialLine == null){
            respond(401, "Empty Request!", "Unable to parse request");
            return;
        }

        StringTokenizer tok = new StringTokenizer(initialLine);
        String[] components = new String[3];
        for (int i = 0; i < components.length; i++) {
            if (tok.hasMoreTokens())  {
                components[i] = tok.nextToken();
            } else  {
                respond(401, "Wrong Request!", "Unable to parse request");
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
                respond(401, "Wrong Request!", "Unable to parse request");
                return;
            }
            headers.put(headerLine.substring(0, separator),
                    headerLine.substring(separator + 1));
        }
        clientSocket.shutdownInput();
        parsePath(path, headers);

    }

    private void parsePath(String path, HashMap<String, String> headers) throws IOException {
        String startDate, endDate, customerName, customerCount;
        boolean result;
        switch (path) {
            case "/":
                respond(201, "Accepted!", "Application is running!");
                break;
            case "/isRunning":
                // is program running
                respond(200, "OK!", true);
                break;
            case "/getSeatCount":
                // get seat count in db
                int seatCount = AppConfig.airlineProperties.getSeatCount();
                respond(200, "OK!", seatCount);
                break;
            case "/isAvailable":
                // is airline available in specified dates
                startDate = headers.get("startDate").trim();
                endDate = headers.get("endDate").trim();
                customerCount = headers.get("customerCount").trim();
                result = BookingService.getInstance().isAvailable(customerCount, startDate, endDate);

                respond(200, "OK!", result);
                break;
            case "/makeReservation":
                // make reservation with specified values
                startDate = headers.get("startDate").trim();
                endDate = headers.get("endDate").trim();
                customerName = headers.get("customerName").trim();
                customerCount = headers.get("customerCount").trim();

                // make reservation
                result = BookingService.getInstance().makeReservation(customerName, customerCount, startDate, endDate);
                if (result)
                    respond(200, "OK!", true);
                else
                    respond(410, "Reservation Error!", false);
                break;
            case "/heartbeat":
                // heartbeat OK
                respond(200, "OK!", "Heartbeat OK!");
                break;
            default:
                respond(404, "Not Found!", "Method Not Found!");
                break;
        }
    }

    // respond HTTP Service
    private void respond(int statusCode, String msg, Object body) throws IOException  {
        String responseLine = OUTPUT_HTTP_VERSION + " " + statusCode + " " + msg + "\r\n";
        responseLine += OUTPUT_HEADERS + " \r\n\r\n";
        if(body != null)
            responseLine+=body;
        out.write(responseLine);
        out.flush();
        out.close();
    }
}
