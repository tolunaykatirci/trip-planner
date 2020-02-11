package com.sudoers.travelagency.socket;

import com.sudoers.travelagency.database.DatabaseManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TA_HeartbeatClient implements Runnable{

    // heartbeat service for updating airlines' and hotels' status

    private String ip;
    private String clientName;
    private String clientType;
    private int port;

    // constructor
    public TA_HeartbeatClient(String ip, int port, String clientName, String clientType) {
        this.ip = ip;
        this.port = port;
        this.clientName = clientName;
        this.clientType = clientType;
    }

    @Override
    public void run() {
        try {
            // open socket
            Socket clientSocket = new Socket(ip,port);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);

            // create HTTP GET request for hotels and airlines
            out.print("GET /heartbeat HTTP/1.1\r\n");
            out.print("Host: "+ip+":"+port+"\r\n");
            out.print("Accept: */*\r\n");
            out.print("Cache-Control: no-cache\r\n");
            out.print("\r\n");
            out.flush();

            String response = in.readLine();
            if (response != null) {
                in.close();
                out.close();
                clientSocket.close();
                // update status according to HTTP Response comes from hotels and airlines
                new DatabaseManager().updateStatus(clientType, clientName, true);
            }

        } catch (Exception e){
            //e.printStackTrace();
            System.out.println("Couldn't connect to: " + clientName);
            new DatabaseManager().updateStatus(clientType, clientName, false);
        }
    }
}
