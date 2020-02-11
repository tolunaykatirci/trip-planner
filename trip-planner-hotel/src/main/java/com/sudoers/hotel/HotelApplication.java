package com.sudoers.hotel;

import com.sudoers.hotel.database.DatabaseManager;
import com.sudoers.hotel.socket.H_SocketServer;
import com.sudoers.hotel.socket.TA_RegistrationClient;
import com.sudoers.hotel.util.AppConfig;

public class HotelApplication {

    public static void main(String[] args) {
        // get application properties from file
        AppConfig.getApplicationProperties();
        // connect to database
        DatabaseManager.connect();
        // create initial tables
        DatabaseManager.createInitialTables();

        // register to travel agency
        TA_RegistrationClient client = new TA_RegistrationClient();
        new Thread(client).start();

        // run socket server
        runSocketServer();

    }

    private static void runSocketServer() {
        // run socket server on another thread
        H_SocketServer socketServer = new H_SocketServer(AppConfig.hotelProperties.getPort());
        Thread socketThread = new Thread(socketServer);
        socketThread.start();
    }

}
