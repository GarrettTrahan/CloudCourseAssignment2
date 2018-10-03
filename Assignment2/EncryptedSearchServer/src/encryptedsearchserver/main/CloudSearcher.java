/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptedsearchserver.main;

import encryptedsearchserver.utilities.Config;
import encryptedsearchserver.utilities.Constants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Receive search queries, initiate ranking, and send results to client.
 * @author Jason
 */
public class CloudSearcher {

    private ArrayList<String> query;
    private ArrayList<String> searchedClusterNames;
    private ArrayList<String> searchResults;
    private ServerSocket serv;
    private Socket sock;
    private long searchTime;

    //Uhhhh....
    public CloudSearcher() {
        query = new ArrayList<String>();
       }


    /**
     * Receive the query info as sent from the client.
     * Post-conditions:
     *  query will be filled with the query and weight data
     * After this, the system is set to perform ranking.
     */
    public void ReceiveQuery() {
        //Open up the sockets and let the data flow in.
        try {
            serv = new ServerSocket(Config.socketPort);
            sock = serv.accept();

            sock.setKeepAlive(true);
            sock.setSoTimeout(10000);
            System.out.println("\nNow awaiting Search...");

        } catch (IOException ex) {
            System.err.println(CloudSearcher.class.getName() + ": Error opening port");
        }

        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());

            int numTerms = dis.readInt();

            for (int i = 0; i < numTerms; i++) {
                String term = dis.readUTF();
                      query.add(term);
                      System.out.print(term);
            }

            dis.close();
            sock.close();
            serv.close();
        } catch (IOException ex) {
            System.err.println(CloudSearcher.class.getName() + " Error getting query from client.");
        }

        //Now the query holds the Q' with its weights

        //System.out.println("Query: " + query);

    }

    public void searchTermInIndex(){

        final Scanner scanner = new Scanner(Constants.indexFileLocation + File.separator + Constants.indexFileName);


        for(String searchName: query){
            while (scanner.hasNextLine()) {
                final String lineFromFile = scanner.nextLine();
                if(lineFromFile.contains(query.toString())) {
                    // a match!
                    System.out.println("I found " +query+ " in file " + lineFromFile);
                    break;
                }
            }
        }



    }




    public void sendResultsToClient() {
        System.out.println("\nSending search results to client...");
        // Uses the same connection from before.  Maybe bad?
        try {
            serv = new ServerSocket(Config.socketPort);
            sock = serv.accept();
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

            int numSearchResults = Math.min(Config.numSearchResults, searchResults.size());
            dos.writeInt(numSearchResults);

            for (int i = 0; i < numSearchResults; i++) {
                dos.writeUTF(searchResults.get(i));
            }

            // If we're taking metrics, send the time info back to the client.
            if (Config.calcMetrics)
                dos.writeLong(searchTime);

            dos.close();
            sock.close();
            serv.close();
        } catch (IOException ex) {
            Logger.getLogger(CloudSearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}



