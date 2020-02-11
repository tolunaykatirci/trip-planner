package com.sudoers.travelagency.socket;

import com.sudoers.travelagency.protocol.SUDORequest;
import com.sudoers.travelagency.util.AppConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TA_Client implements Runnable {

    // test customer client for testing is custom protocol running

    private String ip;
    private int port;
    private String function;

    // constructor
    public TA_Client(String function) {
        this.ip = "127.0.0.1";
        this.port = AppConfig.travelAgencyProperties.getPort();
        this.function = function;
    }

    @Override
    public void run() {
        try {
            // open socket and create SUDORequest
            Socket clientSocket = new Socket(ip,port);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);

            // create SUDORequest
            SUDORequest sudoRequest = new SUDORequest();
            sudoRequest.setHost("127.0.0.1");
            sudoRequest.setPort(8080);
            sudoRequest.setVersion("VER_1.0");
            sudoRequest.setFunction(function);

            // input
            out.write(SUDORequest.createRequest(sudoRequest));
            out.flush();

            // output
            String response = in.readLine();
            while(response != null) {
                System.out.println(response);
                response = in.readLine();
            }

            // close socket and other connections
            in.close();
            out.close();
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
