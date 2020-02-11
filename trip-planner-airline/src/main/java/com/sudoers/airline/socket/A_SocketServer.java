package com.sudoers.airline.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class A_SocketServer implements Runnable{

    private int serverPort;
    private ServerSocket serverSocket = null;
    private boolean isStopped = false;
    private Thread runningThread = null;

    public A_SocketServer(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        // run multithread
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }

        // open socket server
        openServerSocket();

        while (!isStopped) {
            Socket clientSocket = null;
            try {
                // accept request and send to handler
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }

            // handle request in background
            new Thread(new A_ClientHandler(clientSocket)).start();
            System.out.println("Server Stopped.");
        }
    }

    // stop socket server
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

    // start socket server
    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
            System.out.println("Server started at port: " + this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port "+ this.serverPort, e);
        }
    }
}
