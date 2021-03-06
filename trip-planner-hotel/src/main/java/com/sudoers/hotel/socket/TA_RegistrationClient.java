package com.sudoers.hotel.socket;

import com.sudoers.hotel.util.AppConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TA_RegistrationClient implements Runnable {

    private Socket clientSocket = null;
    private String ip;
    private int port;

    // HTTP registration client for beginning of service


    public TA_RegistrationClient() {
        this.ip = AppConfig.hotelProperties.getTravelAgencyIP();
        this.port = AppConfig.hotelProperties.getTravelAgencyPort();
    }

    @Override
    public void run() {
        try {
            // open socket
            Socket clientSocket = new Socket(ip,port);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);

            // create GET request
            out.print("GET /addOrUpdateHotel HTTP/1.1\r\n");
            out.print("Host: "+ip+":"+port+"\r\n");
            out.print("Accept: */*\r\n");
            out.print("Cache-Control: no-cache\r\n");
            out.print("Accept-Encoding: gzip,deflate\r\n");
            out.print("hotelName: " + AppConfig.hotelProperties.getHotelName() +"\r\n");
            out.print("hotelPort: " + AppConfig.hotelProperties.getPort() +"\r\n");
            out.print("hotelCapacity: " + AppConfig.hotelProperties.getRoomCount() +"\r\n");
            out.print("\r\n");
            out.flush();

            // get response
            String response = in.readLine();
            while(response != null) {
                System.out.println(response);
                response = in.readLine();
            }

            // close connections
            in.close();
            out.close();
            clientSocket.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
