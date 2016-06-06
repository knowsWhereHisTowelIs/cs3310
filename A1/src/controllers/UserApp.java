/**
 * CS3310 - A1
 * Author: Caleb Slater
 */
package controllers;

import files.CountryData;
import files.TransactionData;
import files.UI;
import funcs.Strings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class UserApp {
    private TransactionData transactionData;
    private UI ui;
    private CountryData countryData;
    
    /**
     * Get transaction data then execute commands
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UserApp userApp = new UserApp();
        
        //userApp.ui.writeLine("--------------------USER APP----------------------");
        
        String line;
        int count = 0;
        while( (line = userApp.transactionData.getNextTransaction()) != null ) {
            //System.out.println(line);
            count++;
            
            String[] parts = line.split(",");
            userApp.ui.writeLine(line);
            String action = parts[0];
            switch( action ) {
                case "LI":
                    //list all countries
                    userApp.listAllCountries();
                    break;
                case "QI":
                    //query county by id
                    userApp.queryCountry(parts);
                    break;
                case "IN":
                    //insert country
                    userApp.insertCountry(parts);
                    break;
                case "DI":
                    //delete country by id
                    userApp.deleteCountry(parts);
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
        ui = new UI(true); //must be first so appends
        transactionData = new TransactionData();
        countryData = new CountryData();
    }
    
    /**
     * get list then format output ignore empty
     */
    public void listAllCountries() {
        ArrayList<String> list = countryData.getListOfAllCountries();
        ui.writeLine("ID CODE NAME              CONTINENT      SIZE    YEAR  POPULATION L.EX");
        for( String line : list ) {
            ui.writeLine(countryData.formatCountryStr(line));
        }
        int count = list.size();
        ui.writeLine("+ + + + + + + + + + + END OF DATA – " + count + " countries + + + + + + + + + + + +");
    }
    
    /**
     * Get country data or say if empty
     * @param parts 
     */
    public void queryCountry(String[] parts) {
        if ( parts.length > 1 ) {
            int id = Integer.valueOf( parts[1] );
            String country;
            try { 
                country = countryData.queryCountry(id);
            } catch (IOException ex ) {
                country = "";
            }
            if( country.equals("") ) {
                ui.writeLine("** ERROR: no country with id " + id);
            } else {
                //ui.writeLine("ID:"+id + " '" + country + "'");
                ui.writeLine(countryData.formatCountryStr(country));
            }
        } else {
            ui.writeLine("Query country error. Too few parts");
        }
    }
    
    /**
     * insert country data into "DB"
     * @param parts 
     */
    public void insertCountry(String[] parts) {
        if( parts.length > 1 ) {
            String[] countryFields = Arrays.copyOfRange(parts, 1, parts.length);
            String line = Strings.implode(",", countryFields);
            countryData.insertCountry(line);
        } else {
            ui.writeLine("Insert Country error");
        }
    }
    
    /**
     * Delete country form "DB"
     * @param parts 
     */
    public void deleteCountry(String[] parts) {
        int id = Integer.valueOf( parts[1] );
        countryData.deleteCountry(id);
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
