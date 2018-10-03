/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptedsearchserver.main;

import encryptedsearchserver.utilities.Config;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Jason
 */
public class EncryptedSearchServer {


    
    /**
     * @param args Arguments for what mode the server should launch into
     */
    public static void main(String[] args) {
        //Search indefinitely
        CloudSearcher searcher = new CloudSearcher();

       System.out.println("Server Side Running: Waiting for Search Term.....");
        searcher.ReceiveQuery();
        searcher.searchTermInIndex();
        searcher.sendResultsToClient();
    }
    



}
