package com.sudoers.airline;

import com.sudoers.airline.database.DatabaseManager;
import com.sudoers.airline.socket.A_SocketServer;
import com.sudoers.airline.socket.TA_RegistrationClient;
import com.sudoers.airline.util.AppConfig;

public class AirlineApplication {

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
        A_SocketServer socketServer = new A_SocketServer(AppConfig.airlineProperties.getPort());
        Thread socketThread = new Thread(socketServer);
        socketThread.start();
    }

}
