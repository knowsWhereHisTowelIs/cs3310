/**
 * CS3310 - A3
 * Author: Caleb Slater
 */
package controllers;

import files.CountryData;
import files.NameIndex;
import files.TransactionData;
import files.UI;
import funcs.BstNode;
import funcs.Strings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class UserApp {
    private TransactionData transactionData;
    private UI ui;
    private CountryData countryData;
    private NameIndex nameIndex;
    
    /**
     * Get transaction data then execute commands
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UserApp userApp = new UserApp();
        
        String line;
        int count = 0;
        while( (line = userApp.transactionData.getNextTransaction()) != null ) {
            if( line.charAt(0) != '%' ) {
                //not a comment
                count++;

                String[] parts = line.split(",");
                userApp.ui.writeLine(line);
                String action = parts[0];
                switch( action ) {
                    case "QN":
                        userApp.queryCountryByName(parts[1]);
                        break;
                    case "LN":
                        userApp.listCountiesByName();
                        break;
                    default: 
                        //ERROR
                        userApp.ui.writeLine("Illegal Transaction: " + action);
                        break;
                }
            }
        }
        
        userApp.ui.writeLine("UserApp done - " + count + " transactions processed");
        userApp.transactionData.deconstruct();
        userApp.countryData.deconstruct();
        userApp.ui.deconstruct();
    }
    
    /**
     * constructor
     */
    public UserApp() {
        ui = new UI(true); //must be first so appends
        transactionData = new TransactionData();
        countryData = new CountryData();
        nameIndex = new NameIndex(false);
    }
    
    private void queryCountryByName(String name) {
        Object[] obj = nameIndex.getNodeDataByName(name);
        BstNode node = (BstNode) obj[0];
        int nodesVisited = (int) obj[1];
        
        if( node != null ) {
            try {
                String country = countryData.queryCountry( node.getDataPtr() );
                if ( country != null ) {
                    ui.writeLine( countryData.formatCountryStr(country) );
                } else {
                    ui.writeLine("** ERROR: no country with name " + name);
                }
            } catch( IOException ex ) {
                ui.writeLine("** ERROR: no country with name " + name);
            }
        } else {
            ui.writeLine("** ERROR: no country with name " + name);
        }
        
        ui.writeLine( String.format("  [%d BST nodes visited]", nodesVisited));
    }

    private void listCountiesByName() {
        ArrayList<BstNode> nodes = nameIndex.getInOrderTraversal( nameIndex.getRootPtr() );
        ui.writeLine("CDE NAME-------------- CONTINENT---- ---POPULATION L.EX");
        System.out.println(nodes.size());
        for( BstNode node : nodes ) {
            if ( node != null ) {
                try {
                    String country = countryData.queryCountry( node.getDataPtr() );
                    if ( country != null ) {
                        ui.writeLine( countryData.formatCountryStr(country) );
                    }
                } catch( IOException ex ) {
                    //error
                }
            }
        }
    }
    
    //------------GET/SETTERS--------------

    public TransactionData getTransactionData() {
        return transactionData;
    }

    public void setTransactionData(TransactionData transactionData) {
        this.transactionData = transactionData;
    }

    public UI getUi() {
        return ui;
    }

    public void setUi(UI ui) {
        this.ui = ui;
    }

    public CountryData getCountryData() {
        return countryData;
    }

    public void setCountryData(CountryData countryData) {
        this.countryData = countryData;
    }
}
