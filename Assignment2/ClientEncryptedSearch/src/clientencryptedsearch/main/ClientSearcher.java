/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientencryptedsearch.main;

import clientencryptedsearch.utilities.Config;
import clientencryptedsearch.utilities.StopwordsRemover;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client Semantic Searcher.
 * Responsible for:
 *  Expanding the query into the modified query set.
 *  Weighting.
 *  Sending the query over to the server.
 * @author jason
 */
public class ClientSearcher {
    String originalQuery;


    StopwordsRemover stop;
    StopwordsRemover wikiStop;


    ArrayList<String> searhTerm  = new ArrayList<String>();
    Socket sock;
    ArrayList<String> searchResults;


    /**
     * Constructor.
     * Sets up required objects, maps, and variables.
     * Does not do any of the semantic query modification yet.
     * @param query The original user query
     */
    public ClientSearcher(String query) {
        originalQuery = query.toLowerCase();

        stop = new StopwordsRemover("stopwords_en.txt");
        wikiStop = new StopwordsRemover("wiki_stopwords_en.txt");

        constructQuery();
       searchResults = new ArrayList<>();

    }

    //--------------------QUERY PROCESSING-------------------------

    /**
     * Constructs the query vector to be sent to the server.
     * The entire process builds up the multiple weights.
     * Splits the query into its individual components and weights them.
     * Adds synonyms for each term in the queryWeights map, and weights them.
     * Adds the wikipedia terms for each term in the queryWeights map, and weights them.
     */
    public void constructQuery() {
        // Log time to process the query
        long begin = System.currentTimeMillis();
        splitQuery();
        long end = System.currentTimeMillis();
    }

    /**
     * Splits and weights the original query.
     * Adds terms to the queryWeights, splitting based on config method.
     * Pre: Original query has been set.
     * Post: queryWeights has all required data.
     */
    public void splitQuery() {
        String[] subQueries;
        subQueries = originalQuery.split(" "); //Just split by spaces
        for (String term : subQueries) {
             searhTerm.add(term);
        }


    }



    //---------------------SEARCHING AND RESULTS--------------------

    /**
     * Send Search Query To The Server To Perform Search.
     * Does several things:
     * Consolidates and encrypts the query.
     * Loads in the abstracts to be compared against the query.
     * Ranks the abstracts against the query.
     * Sends the query and abstract choice to the server
     */
    public void search() {
        //First things first (I'm the realest) we have to fill allWeights with the encrypted dat

        boolean scanning = true;
        while(scanning) {
            //Now send allWeights over a socket.
            try {
                sock = new Socket(Config.cloudIP, Config.socketPort);

                DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

                sock.setKeepAlive(true);
                sock.setSoTimeout(10000);


                //Just write how many entries we need to write so the cloud knows.
                dos.writeInt(searhTerm.size());

                //Start writing to it.  One entry at a time.
                for (String term : searhTerm) {
                    //Send the term, then the weight
                    dos.writeUTF(term);

                }

                scanning = false;

            } catch (IOException ex) {
                System.err.println(ClientSearcher.class.getName() + ": Error in Input data. trying again");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(ClientSearcher.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }



    public String acceptResults()  {
        System.out.println("Waiting for file list from server...");

        String searchResults = null;
        DataInputStream dis = null;
        // Scan for connection
        boolean scanning = true;
        while(scanning) {
            try {
                sock = new Socket(Config.cloudIP, Config.socketPort);
                dis = new DataInputStream(sock.getInputStream());
                searchResults = dis.readUTF();
                System.out.println("Results From Server");
                System.out.println(searchResults);
                scanning = false;
            } catch (IOException ex) {
                System.err.println("Connect failed, waiting and will try again.");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(ClientSearcher.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }


        try {
            dis.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        return searchResults;
    }

}