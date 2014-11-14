/*
 Project 5
 Safe Walk Server
 Ian Polito ipolito@purdue.edu
 CS 180
 11/10/14
 */

import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

interface Runnable {
}

public class SafeWalkServer implements Runnable {
    
    int portNumber = 8888; //default listening port
    ServerSocket ss; //the server socket
    ArrayList requests = new ArrayList(); //list of requests for SafeWalk
    ArrayList clients = new ArrayList(); //list of client objects
    PrintWriter out; //output to client
    BufferedReader in; //input from client
    boolean isStopped = false; //variable used to stop the 'run' loop if necessary
    
    public SafeWalkServer(int port) throws SocketException, IOException { //constructs the server if given a port #
        if (port >= 1025 && 65535 >= port) {
            ss = new ServerSocket(port);
            portNumber = port;
            ss.setReuseAddress(true); //allows the port to be reusable once server is closed
            System.out.println("Connected using port " + portNumber + ".");
        }
        else {
            System.out.println("Port invalid, using free port 8888.");
            new ServerSocket(portNumber);
            ss.setReuseAddress(true); //allows the port to be reusable once server is closed
        }
    }
    
    public SafeWalkServer() throws SocketException, IOException { //constructs the server if not given a port #
        System.out.println("Port not specified, using free port 8888.");
        ss = new ServerSocket(portNumber);
        ss.setReuseAddress(true); //allows the port to be reusable once server is closed
    }
    
    public int getLocalPort() { // returns the port number we are on
        return portNumber;
    }
    
    public void run() throws SocketException, IOException { //Start a loop to accept incoming connections
        while (!isStopped) {
            Socket client = ss.accept();
            //new Thread(client).start();
            out = new PrintWriter(client.getOutputStream(), true);
            out.flush();
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            Client c = new Client(in.readLine(), client);
        }
    }
    
    //Here begins the methods that will be invoked via 'commands'------------------------------------------------------
    
    public void listRequests() { //will need some sort of input to tell which client it needs to send to
        //iterate through the arraylist and print the requests
        for (int i = 0; i < requests.size(); i++) {
            System.out.println(requests.get(i));
        }
    }
    
    public void reset() { //will need some sort of input to tell which client it needs to send to
        //close up the clients' request and tell them that an error occured with the connection
        
    }
    
    public void shutdown() { //will need some sort of input to tell which client it needs to send to
        //close up, similar to the reset command, but exit the run loop, shuting down the server
    }
    //Here ends the 'command' methods----------------------------------------------------------------------------------
    
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

class Client {
    
    String command;
    Socket client;
    
    public Client(String command, Socket client) {
        this.command = command;
        this.client = client;
    }
}
