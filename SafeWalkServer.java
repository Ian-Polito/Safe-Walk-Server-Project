/*
 Project 5
 Safe Walk Server
 Ian Polito & Kurt Sermersheim
 CS 180
 11/10/14
 */

import java.util.Scanner;
import java.net.*;
import java.io.*;

public class SafeWalkServer {
    
    int portNumber = 8888;
    ServerSocket ss;
    
    public SafeWalkServer(int port) throws SocketException, IOException { //constructs the server if given a port #
        if (port >= 1025 && 65535 >= port) {
            ServerSocket ss = new ServerSocket(port);
            portNumber = port;
            ss.setReuseAddress(true);
            System.out.println("Connected using port " + portNumber + ".");
        }
        else {
            System.out.println("Port invalid, using free port 8888.");
            ServerSocket ss = new ServerSocket(portNumber);
            ss.setReuseAddress(true);
        }
    }
    
    public SafeWalkServer() throws SocketException, IOException { //constructs the server if not given a port #
        System.out.println("Port not specified, using free port 8888.");
        ServerSocket ss = new ServerSocket(portNumber);
        ss.setReuseAddress(true);
    }
    
    public int getLocalPort() { // returns the port number we are on
        return portNumber;
    }
    
    public void run() throws SocketException, IOException { //Start a loop to accept incoming connections
        while (true) {
            Socket client = ss.accept();
        }
    }
    
    public static void main(String[] args) throws SocketException, IOException {
        if (args.length == 0) {
            SafeWalkServer sws = new SafeWalkServer();
            sws.run();
        }
        if (args[0].length() != 0) {
            int p = 0;
            String s = args[0];
            Scanner scan = new Scanner(s);
            while (scan.hasNextInt()) {
                p += scan.nextInt();
            }
            SafeWalkServer sws = new SafeWalkServer(p);
            sws.run();
        }
    }
}
