/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchserver.main;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
    private  String searchResultForClient = null;
    public static String indexFileLocation = ".." + File.separator + "index";

    //Name of the index file
    public static String indexFileName = "Index.txt";

    //Uhhhh....
    public CloudSearcher() {
        query = new ArrayList<String>();
       }

    public void ReceiveQuery() {
        //Open up the sockets and let the data flow in.
        try {
            serv = new ServerSocket(1350);
            sock = serv.accept();
            sock.setKeepAlive(true);
            sock.setSoTimeout(10000);

        } catch (IOException ex) {
            System.err.println(CloudSearcher.class.getName() + ": Error opening port");
        }

        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());

            int numTerms = dis.readInt();

            for (int i = 0; i < numTerms; i++) {
                String term = dis.readUTF();
                      query.add(term);
            }

            dis.close();
            sock.close();
            serv.close();
        } catch (IOException ex) {
            System.err.println(CloudSearcher.class.getName() + " Error getting query from client.");
        }

    }

    public void searchTermInIndex(){
        for(String searchName: query){
            File file = new File(indexFileLocation + File.separator + indexFileName);

            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String st;
                while ((st = br.readLine()) != null) {

                String [] spiltWords =  st.split("\\|.\\|");
                  if(spiltWords[0].equals(searchName)){
                      System.out.println("Match Find on Index File!");
                      System.out.println("Sending following result to the client side:");
                      System.out.println(st);
                      searchResultForClient = st;
                  }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    public void sendResultsToClient() {
        System.out.println("\nSending search results to client...");
        // Uses the same connection from before.  Maybe bad?
        try {
            serv = new ServerSocket(1350);
            sock = serv.accept();
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            dos.writeUTF(searchResultForClient);
            dos.close();
            sock.close();
            serv.close();
        } catch (IOException ex) {
            Logger.getLogger(CloudSearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}



