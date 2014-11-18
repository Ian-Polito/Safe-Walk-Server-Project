/*
 Project 5
 Safe Walk Server
 @author Ian Polito ipolito 801
 @author Kurt Sermersheim ksermer lab section
 CS 180
 */

import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class SafeWalkServer implements Runnable {
    
    int portNumber = 8888; //default listening port
    ServerSocket ss; //the server socket
    ArrayList<List> requests = new ArrayList<>(); //list of arrayLists ???
    List<String> elements; //list of elements of requests
    ArrayList clients = new ArrayList(); //list of client objects
    PrintWriter out; //output to client
    BufferedReader in; //input from client
    boolean isStopped = false; //variable used to stop the 'run' loop if necessary
    
    Socket difclient; //just initializes the client
    final ArrayList<Socket> clientsConnected = new ArrayList<>();
    
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
    
    public SafeWalkServer(Socket difclient) {
    	this.difclient = difclient;
    }
    
    public SafeWalkServer() throws SocketException, IOException { //constructs the server if not given a port #
        System.out.println("Port not specified, using free port 8888.");
        ss = new ServerSocket(portNumber);
        ss.setReuseAddress(true); //allows the port to be reusable once server is closed
    }
    
    public int getLocalPort() { // returns the port number we are on
        return ss.getLocalPort();
    }
    
    public void run() throws SocketException, IOException { //Start a loop to accept incoming connections
        while (!isStopped) {
            Socket client = ss.accept();
            clientsConnected.add(client);
            System.out.println("Client Connected.");
            out = new PrintWriter(client.getOutputStream(), true);
            out.flush();
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String command = in.readLine();
            
            if (command.startsWith(":")) {
            	if (command.equals(":LIST_PENDING_REQUESTS")) { //lists pending requests
            		listRequests(client);
            	}
            
            	if (command.equals(":SHUTDOWN")) { //shuts down the server
            		shutdown(client);
            	}
            
            	if (command.equals(":RESET")) { //resets the server
            		reset(client);
            	}
            	else {
            		out.println("ERROR: invalid request"); //sends a message saying it was an invalid command
            		client.close();
            	}
            }
            //Thread thread = new Thread(ss2);
            //thread.start();
            else {
            	elements = Arrays.asList(command.split(","));
            	if (elements.size() != 4) {
            		out.println("ERROR: invalid request");
            		client.close();
            	}
            	else if (!elements.get(1).equals("CL50") | !elements.get(1).equals("EE") | !elements.get(1).equals("LWSN") 
            			| !elements.get(1).equals("PMU") | !elements.get(1).equals("PUSH") | elements.get(1).equals("*")) {
            		out.println("ERROR: invalid request");
            		client.close();
            	}
            }
        }
    }
    
    //Here begins the methods that will be invoked via 'commands'------------------------------------------------------
    
    public void listRequests(Socket client) throws SocketException, IOException { //will need some sort of input to tell which client it needs to send to
        //iterate through the arraylist and print the requests
        out.println(requests);
        out.flush();
        client.close();
    }
    
    public void reset(Socket client) throws SocketException, IOException { //will need some sort of input to tell which client it needs to send to
        //close up the clients' request and tell them that an error occured with the connection
    	out.println("RESPONSE: success");
    	out.flush();
    	client.close();
    	for (int i = 0; i < clientsConnected.size(); i++) {
    		out = new PrintWriter(clientsConnected.get(i).getOutputStream(), true);
    		out.println("ERROR: connection reset");
    		clientsConnected.get(i).close();
    	}
    	requests.clear();
    	elements.clear();
    	clientsConnected.clear();
    }
    
    public void shutdown(Socket client) throws SocketException, IOException { //will need some sort of input to tell which client it needs to send to
        //close up, similar to the reset command, but exit the run loop, shuting down the server
    	reset(client);
    	isStopped = true;
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
//might need this class still? Who knows, this project is a holy mess.
//class Client {
    
    //String command;
    //Socket client;
    
    //public Client(Socket client, String command) {
        //this.command = command;
        //this.client = client;
    //}
//}
