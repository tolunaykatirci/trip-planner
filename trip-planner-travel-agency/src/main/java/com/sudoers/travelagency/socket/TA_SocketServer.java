package com.sudoers.travelagency.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TA_SocketServer implements Runnable{

    private int serverPort;
    private ServerSocket serverSocket = null;
    private boolean isStopped = false;
    private Thread runningThread = null;

    // create socket server
    public TA_SocketServer(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        // multithreaded socket server
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }

        openServerSocket();

        while (!isStopped) {
            Socket clientSocket = null;
            try {
                // accept socket request
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }

            // send socket to handler
            new Thread(new TA_ClientHandler(clientSocket)).start();
            System.out.println("Server Stopped.");
        }
    }

    // stop server
    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    // start server
    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
            System.out.println("Server started at port: " + this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port "+ this.serverPort, e);
        }
    }
}
