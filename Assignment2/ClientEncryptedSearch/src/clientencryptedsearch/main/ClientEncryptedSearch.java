/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientencryptedsearch.main;

import clientencryptedsearch.utilities.ClientMetrics;
import clientencryptedsearch.utilities.Config;
import java.io.File;
import java.util.Scanner;

/**
 *
 * @author jason
 */
public class ClientEncryptedSearch {

    /**
     * @param args search arguments.
     * If this has been called from outside with arguments, it won't ask for input.
     */
    public static void main(String[] args) {


        if (args.length == 0) {//No args
            //Then we should get args from the user
            args = getUserInput();
        }

        ClientEncryptedSearch esc = new ClientEncryptedSearch(args);
    }
    
    public ClientEncryptedSearch(String[] args) {
        //Load properties
        Config.loadProperties();
        
        //Determine what the user wants to do
        switch (args[0]) {

            case "-s":
                //Begin timing for search
                long begin = System.currentTimeMillis();
                search(args[1]);
                long end = System.currentTimeMillis();
                break;
        }
    }

    private static String[] getUserInput() {
        String[] args = new String[2];
        System.out.println("Welcome to Search Data in the Cloud.");
        System.out.println("Client Version: For Search press -s");

        //Get input
        Scanner scan;
        scan = new Scanner(System.in);

        String choice = scan.nextLine();
        args[0] = choice;

        switch (choice) {

            case "-s": //Search
                System.out.println("Enter search query: ");
                args[1] = scan.nextLine();
                break;

            default:
                System.out.println("I'm sorry, I do not recognize that input");
                break;
        }

        return args;
    }
    

    
    
    public void search(String query) {
        // Start timing
        ClientSearcher searcher = new ClientSearcher(query); //Constructor just initializes
        //Rank our abstracts based on the query and send it over.

        Config.loadProperties();


        //Search!
        searcher.search();

     //   searcher.acceptResults();
     //   searcher.processResults();

    }
}
