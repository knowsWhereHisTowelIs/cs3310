/**
 * CS3310 - A5
 * Author: Caleb Slater
 */
package controller;

import files.CodeIndex;
import files.CountryData;
import files.TransactionData;
import files.UI;
import java.io.IOException;
import java.util.Scanner;

public class UserApp {
    private TransactionData transactionData;
    private UI ui;
    private CountryData countryData;
    private CodeIndex codeIndex;
    
    /**
     * Get transaction data then execute commands
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UserApp userApp = new UserApp();
        
        userApp.ui.writeLine("Please input the dataset you want to use (1,2,3):");
        int dataSet = -1;
        boolean isValidInput = false;
        Scanner scan = new Scanner(System.in);
        while( isValidInput == false ) {
            try{ 
                dataSet = scan.nextInt();
                if( dataSet < 1 || dataSet > 3 ) {
                    userApp.ui.writeLine("Sorry data set must be between 1 and 3:"+dataSet);
                } else {
                    isValidInput = true;
                }
            } catch ( Exception ex ) {
                String invalid = scan.nextLine();
                userApp.ui.writeLine("Sorry invalid input:"+invalid);
            }
        }
        userApp.ui.writeLine("Dataset chosen:" + dataSet);
        userApp.loadDataSet(dataSet);
        
        userApp.ui.writeLine("%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
                "PROCESSING TransDataA5_"+dataSet+".csv\n");
        
        String line;
        int count = 0;
        while( (line = userApp.transactionData.getNextTransaction()) != null ) {
            //System.out.println(line);
            count++;
            
            String[] parts = line.split(",");
            String lineNoNewLine = line.split("\r\n")[0];
            userApp.ui.write(lineNoNewLine + " >>> ");
            
            String action = parts[0];
            switch( action ) {
                case "QC":
                    //list all countries
                    userApp.queryCountryByCode(parts);
                    break;
                default: 
                    //ERROR
                    userApp.ui.writeLine("Illegal Transaction: " + action);
                    break;
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
        ui = new UI(false);
    }
    
    /**
     * specifies which files to load
     * must be called before any other functions can be used
     * @param dataSet 
     */
    public void loadDataSet(int dataSet) {
        transactionData = new TransactionData(dataSet);
        countryData = new CountryData(dataSet);
        codeIndex = new CodeIndex(dataSet);
    }
    
    /**
     * Get country data or say if empty
     * @param parts 
     */
    public void queryCountryByCode(String[] parts) {
        if ( parts.length > 1 ) {
            int[] info = codeIndex.queryByCode(parts[1]);
            int id = info[0];
            int nodes = info[1];
            int dataRecords = info[2];
            
            String country;
            try {
                country = countryData.queryCountry( id );
            } catch (IOException ex ) {
                country = "";
            }
            if( country.equals("") ) {
                ui.write( String.format("CODE NOT FOUND          [NODES: %2d, DATA RECORDS: %2d]\n", nodes, dataRecords));
            } else {
                ui.write( String.format("%s [NODES: %2d, DATA RECORDS: %2d]\n", country, nodes, dataRecords));
            }
        } else {
            ui.writeLine("Query country error. Too few parts");
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
