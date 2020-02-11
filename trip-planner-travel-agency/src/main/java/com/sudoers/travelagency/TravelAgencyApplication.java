package com.sudoers.travelagency;

import com.sudoers.travelagency.database.DatabaseManager;
import com.sudoers.travelagency.database.model.Airline;
import com.sudoers.travelagency.database.model.Hotel;
import com.sudoers.travelagency.service.AirlineService;
import com.sudoers.travelagency.service.HotelService;
import com.sudoers.travelagency.socket.TA_Client;
import com.sudoers.travelagency.socket.TA_HeartbeatClient;
import com.sudoers.travelagency.socket.TA_SocketServer;
import com.sudoers.travelagency.util.AppConfig;

import java.util.List;

public class TravelAgencyApplication {

    public static void main(String[] args) {
        // get application properties
        AppConfig.getApplicationProperties();
        // connect to database
        DatabaseManager.connect();
        // create initial tables
        DatabaseManager.createInitialTables();
        // run socket server
        runSocketServer();
        // run heartbeat service
        runHeartbeatService();
    }

    private static void runSocketServer() {
        // run socket server on another thread
        TA_SocketServer socketServer = new TA_SocketServer(AppConfig.travelAgencyProperties.getPort());
        Thread socketThread = new Thread(socketServer);
        socketThread.start();

//        TA_Client client = new TA_Client("getAirlineList");
//        Thread clientThread = new Thread(client);
//        clientThread.start();

    }

    private static void runHeartbeatService() {
        while (true) {
            try {
                // run thread every 30 secs
                Thread.sleep(30000);
                // get airlines and hotels
                List<Airline> airlines = AirlineService.getInstance().getAllAirlines();
                List<Hotel> hotels = HotelService.getInstance().getAllHotels();

                // send heartbeat to all airlines
                for (Airline a:airlines) {
                    try {
                        TA_HeartbeatClient heartbeat = new TA_HeartbeatClient(a.getIp(), a.getPort(), a.getName(), "airline");
                        Thread heartbeatThread = new Thread(heartbeat);
                        heartbeatThread.start();
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }
                // send heartbeat to all hotels
                for (Hotel h:hotels) {
                    try {
                        TA_HeartbeatClient heartbeat = new TA_HeartbeatClient(h.getIp(), h.getPort(), h.getName(), "hotel");
                        Thread heartbeatThread = new Thread(heartbeat);
                        heartbeatThread.start();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
