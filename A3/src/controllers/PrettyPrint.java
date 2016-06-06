/**
 * CS3310 - A3
 * Author: Caleb Slater
 */
package controllers;

import files.CountryData;
import files.NameIndex;
import files.UI;
import funcs.BstNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Quick and dirty way of debuging CountryData.csv
 */
public class PrettyPrint {
    private RandomAccessFile randAccessFile;
    private UI ui;
    private NameIndex nameIndex;
    
    private boolean errored = false;
    
    /**
     * Setup for input
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PrettyPrint prettyPrint = new PrettyPrint();
        
        prettyPrint.ui.writeLine("open CountryData file");
        if( prettyPrint.errored == false ) {
            prettyPrint.printLines();
        } else {
            System.out.println("Failed to open files");
        }
        
        prettyPrint.ui.writeLine("close CountryData file");
        prettyPrint.ui.writeLine("PrettyPrint done");
        prettyPrint.deconstruct();
    }
    
    /**
     * Constructor
     */
    public PrettyPrint() {
        ui = new UI(true); //must be first so appends
        nameIndex = new NameIndex(false);
    }
    
    
    /**
     * Read line by line and output debug info
     */
    public void printLines() {
        BstNode node;
        ArrayList<BstNode> nodes = nameIndex.getNodes();
        ui.writeLine( String.format("RootPtr is %d,  N is %d", nameIndex.getRootPtr(), nameIndex.getN()) );
        ui.writeLine("[SUB] LCH NAME-------------- DRP RCH");
        for ( int i = 0; i < nodes.size(); i++ ) {
            node = nodes.get(i);
            ui.writeLine( String.format("[%03d] %03d %s %03d %03d ", i, node.getLeftChildPtr(), node.getKeyValue(), node.getDataPtr(), node.getRightChildPtr()) );
        }
    }

    /**
     * close files
     */
    private void deconstruct() {
        nameIndex.deconstruct();
        ui.deconstruct();
    }
}