/*
 Project 5
 Safe Walk Server
 @author Ian Polito ipolito 810
 @author Kurt Sermersheim ksermers LN5
 CS 180
 */

import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.AbstractList;

public class SafeWalkServer {
    
    int portNumber = 0; //default listening port
    ServerSocket ss; //the server socket
    ArrayList<ArrayList> requests = new ArrayList<>(); //list of array lists
    ArrayList<String> elements; //list of elements of requests
    ArrayList<String> elements2 = new ArrayList<>(); //temporary list of strings
    PrintWriter out; //output to client
    BufferedReader in; //input from client
    boolean isStopped = false; //variable used to stop the 'run' loop if necessary
    Socket difclient; //just initializes the client
    final ArrayList<Socket> clientsConnected = new ArrayList<>(); //Arraylist of client sockets
    
    public SafeWalkServer(int port) throws SocketException, IOException { //constructs the server if given a port #
        if (port >= 1025 && 65535 >= port) {
            ss = new ServerSocket(port);
            portNumber = port;
            ss.setReuseAddress(true); //allows the port to be reusable once server is closed
            System.out.println("Connected using port " + portNumber + ".");
        }
        else {
            System.out.println("Port invalid, using free port.");
            new ServerSocket(portNumber);
            ss.setReuseAddress(true); //allows the port to be reusable once server is closed
        }
    }
    
    public SafeWalkServer(Socket difclient) {
        this.difclient = difclient;
    }
    
    public SafeWalkServer() throws SocketException, IOException { //constructs the server if not given a port #
        System.out.println("Port not specified, using free port.");
        ss = new ServerSocket(portNumber);
        ss.setReuseAddress(true); //allows the port to be reusable once server is closed
    }
    
    public int getLocalPort() { // returns the port number we are on
        return ss.getLocalPort();
    }
    
    public void run() throws SocketException, IOException { //Start a loop to accept incoming connections
        while (!isStopped) {
            Socket client = ss.accept();
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
            //begin testing to see if request is valid ----------------------------------------------------------------
            else {
                elements = new ArrayList<String>(Arrays.asList(command.split("\\s*,\\s*")));
                if (elements.size() == 4) {
                 if ((elements.get(1).equals("CL50")) | (elements.get(1).equals("EE")) | 
                   (elements.get(1).equals("LWSN")) | (elements.get(1).equals("PMU")) | 
                   (elements.get(1).equals("PUSH"))) {
                  if (!elements.get(1).equals("*")) {
                   if ((elements.get(2).equals("CL50")) | (elements.get(2).equals("EE")) | 
                     (elements.get(2).equals("LWSN")) | (elements.get(2).equals("PMU")) | 
                     (elements.get(2).equals("PUSH")) | (elements.get(2).equals("*"))) {
                    if (!elements.get(1).equals(elements.get(2))) {
                     //request is good, do this
                                    requests.add(elements);
                                    elements2.add(command);
                                    clientsConnected.add(client);
                                    System.out.println("Request Added.");
                                    elements.clear();
                    }
                                else {
                                    out.println("ERROR: invalid request");
                                    client.close();
                                    elements.clear();
                                }
                   }
                            else {
                                out.println("ERROR: invalid request");
                                client.close();
                                elements.clear();
                            }
                  }
                        else {
                            out.println("ERROR: invalid request");
                            client.close();
                            elements.clear();
                        }
                 }
                    else {
                        out.println("ERROR: invalid request");
                        client.close();
                        elements.clear();
                    }
                }
                else {
                    out.println("ERROR: invalid request");
                    client.close();
                    elements.clear();
                }
            }
            //stop testing --------------------------------------------------------------------------------------------
            
            //begin checking for matches ------------------------------------------------------------------------------
            //should check if FROM's are the same & TO's are not both stars
            if (requests.size() > 1) {
                List temp = (Arrays.asList((elements2.get(elements2.size()- 1)).split("\\s*,\\s*")));
                List temp2;
                for (int i = 0; i < elements2.size() - 1; i++) {
                    temp2 = (Arrays.asList(elements2.get(i).split("\\s*,\\s*")));
                    if ((temp.get(1)).equals(temp2.get(1))) {
                        if ((temp.get(2)).equals(temp2.get(2)) | ((temp.get(2)).equals("*")) | ((temp2.get(2)).equals("*"))) {
                         if (!(((temp.get(2)).equals("*")) && ((temp2.get(2)).equals("*")))) {
                                //the matches are correct, do this stuff
                                String s = ("RESPONSE: "); //the string being sent to the match
                                String s2 = ("RESPONSE: "); //the string being sent to the requester
                                s += temp.get(0) + "," + temp.get(1) + "," + temp.get(2) + "," + temp.get(3);
                                s2 += temp2.get(0) + "," + temp2.get(1) + "," + temp2.get(2) + "," + temp2.get(3);
                                out = new PrintWriter(clientsConnected.get(i).getOutputStream(), true);
                                out.println(s);
                                out.flush();
                                
                                out = new PrintWriter(clientsConnected.get
                                (requests.size() - 1).getOutputStream(), true);
                                
                                out.println(s2);
                                out.flush();
                                //needs to remove clients from clientsConnected list and requests ArrayList
                                clientsConnected.get(i).close();
                                clientsConnected.get(requests.size() - 1).close();
                                
                                requests.remove(i);
                                requests.remove(requests.size() - 1);
                                
                                clientsConnected.remove(i);
                                clientsConnected.remove(clientsConnected.size() - 1);
                            }
                        }
                    }
                }
            }
            //stop checking for matches -------------------------------------------------------------------------------
        }
    }
    
    //Here begins the methods that will be invoked via 'commands'------------------------------------------------------
    public void listRequests(Socket client) throws SocketException, IOException {
        //print the list of requests to the client
        System.out.println(requests);
        out.println(requests);
        out.flush();
        client.close();
    }
    
    public void reset(Socket client) throws SocketException, IOException {
        //close up the clients' request and tell them that an error occured with the connection
        out.println("RESPONSE: success");
        out.flush();
        client.close();
        for (int i = 0; i < clientsConnected.size(); i++) {
            out = new PrintWriter(clientsConnected.get(i).getOutputStream(), true);
            out.println("ERROR: connection reset");
            clientsConnected.get(i).close();
        }
        if (requests.size() > 0) {
            requests.clear();
        }
        if (elements != null) {
            if (elements.size() > 0) {
                elements.clear();
            }
        }
        if (clientsConnected.size() > 0) {
            clientsConnected.clear();
        }
    } 
    
    public void shutdown(Socket client) throws SocketException, IOException {
        //similar to the reset command, but exit the run loop and close the server socket; shutting down the server
        reset(client);
        ss.close();
        isStopped = true;
    }
    //Here ends the 'command' methods----------------------------------------------------------------------------------
    
    public static void main(String[] args) throws SocketException, IOException {
        boolean done = false;
        
        if (args.length == 0) {
            SafeWalkServer sws = new SafeWalkServer();
            sws.run();
            done = true;
        }
        if (done != true) {
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
}
