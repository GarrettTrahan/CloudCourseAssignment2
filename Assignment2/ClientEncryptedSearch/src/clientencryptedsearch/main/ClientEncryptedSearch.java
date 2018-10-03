/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientencryptedsearch.main;



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

                search(args[0]);
    }

    private static String[] getUserInput() {
        String[] args = new String[1];
        System.out.println("Welcome to Search Data in the Cloud.");
        //Get input
        Scanner scan;
        scan = new Scanner(System.in);
        System.out.println("Enter search query: ");
        args[0] = scan.nextLine();
        return args;
    }
    

    
    
    public void search(String query)  {
        // Start timing
        ClientSearcher searcher = new ClientSearcher(query); //Constructor just initializes

        //Search!
        searcher.search();
        searcher.acceptResults();

    }
}
