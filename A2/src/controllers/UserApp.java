/**
 * CS3310 - A2
 * Author: Caleb Slater
 */
package controllers;

import files.CountryData;
import files.TransactionData;
import files.UI;
import funcs.ArrayFuncs;
import funcs.Strings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            //System.out.println("------------------------this is not in ui---------------------------");
            userApp.ui.writeLine(line);
            String action = parts[0];
            
            switch( action ) {
                case "LI": //list all countries
                    // `Dummy stub`... print stupid error msg
                    userApp.listAllCountriesById();
                    break;
                case "QI": //query county by id
                    //`Dummy stub`... print stupid error msg
                    userApp.queryCountryById(parts);
                    break;
                case "DI": //delete country by id
                    //`Dummy stub`... print stupid error msg
                    userApp.deleteCountryById(parts);
                    break;
                    
                case "LC":
                    // `Dummy stub`... print stupid error msg
                    userApp.listAllCountriesByCode();
                    break;
                    
                case "QC": //query by country code
                    userApp.queryCountryByCode(parts);
                    break;
                case "DC": //delete by country code
                    userApp.deleteCountryByCode(parts);
                    break;
                
                case "IN": //insert country
                    //TODO update?
                    userApp.insertCountry(parts);
                    break;
                    
                default: 
                    //ERROR
                    userApp.ui.writeLine("Illegal Transaction: " + action);
                    break;
            }
        }
        
        System.out.println("\n------------------------this is not in ui---------------------------\n");
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
     * Dummy Stub since hash file
     */
    public void listAllCountriesById() {
        ui.writeLine("** SORRY: list all by id not yet operational");
    }
    
    /**
     * Dummy Stub since hash file
     * @param parts 
     */
    public void queryCountryById(String[] parts) {
        ui.writeLine("** SORRY: query by id not yet operational");
    }
    
    /**
     * Dummy Stub since hash file
     * @param parts 
     */
    public void deleteCountryById(String[] parts) {
        ui.writeLine("** SORRY: delete by id not yet operational");
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
     * Dummy Stub since hash file
     */
    public void listAllCountriesByCode() {
        ui.writeLine("** SORRY: list all by code is an invalid option");
    }
    
    /**
     * Get country from code
     * @param parts 
     */
    private void queryCountryByCode(String[] parts) {
         if ( parts.length > 1 ) {
            String qCode = String.valueOf(parts[1]).trim();
            String country = null;
            int pathLength = -1;
            byte[] bytes;
            try {
                bytes = countryData.queryCountryByCode(qCode);
                
                pathLength = ArrayFuncs.toInt(Arrays.copyOfRange(bytes, 0, countryData.getSIZE_OF_INT()) );
                if( countryData.getSIZE_OF_INT() != bytes.length ) {
                    //country is returned
                    country = ArrayFuncs.toString( Arrays.copyOfRange(bytes, countryData.getSIZE_OF_INT(), bytes.length) );
                }
            } catch (IOException ex ) {
                System.out.println("Error in queryCountryByCode ");
                country = null;
            }
            
            //System.out.printf("code:%s length:%d country:%s\n", qCode, pathLength, country);
            
            if( country == null || pathLength == -1 || ArrayFuncs.isByteArrByte( country.getBytes(), (byte) '\0') ) {
                ui.writeLine("** ERROR: no country with code " + qCode);
            } else {
                ui.writeLine(country);
            }
            ui.writeLine(String.format("     search path:    %d", pathLength));
        } else {
            ui.writeLine("Query country error. Too few parts");
        }
    }

    /**
     * Delete country by code
     * @param parts 
     */
    private void deleteCountryByCode(String[] parts) {
        String code = parts[1];
        String name = null;
        
        byte[] bytes = countryData.getRecordInfoByCode(code);
        int bytesOffset = 0;
        long offset = ArrayFuncs.toLong(Arrays.copyOfRange(bytes, bytesOffset, bytesOffset + countryData.getSIZE_OF_LONG()));
        bytesOffset += countryData.getSIZE_OF_LONG();
        int pathLength = ArrayFuncs.toInt(Arrays.copyOfRange(bytes, bytesOffset, bytesOffset + countryData.getSIZE_OF_INT()));
        
        try {
            bytes = countryData.readRecord(offset);
            int nameStart = countryData.getSIZE_OF_FIELD_ID() + countryData.getSIZE_OF_FIELD_CODE();
            name = ArrayFuncs.toString( Arrays.copyOfRange(bytes, nameStart, nameStart + countryData.getSIZE_OF_FIELD_NAME() ) );
            //System.out.println(countryData.formatCountryStr(bytes));
        } catch (IOException ex) {
            System.out.println("Error in deleteCountryByCode");
        }
        
        countryData.deleteCountryByCode(code);
        ui.writeLine("OK, " + name + " deleted");
        ui.writeLine(String.format("     search path:    %d", pathLength));
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
