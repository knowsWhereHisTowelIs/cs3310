/**
 * CS3310 - A4
 * Author: Caleb Slater
 */
package controllers;

import files.HeapEntity;
import files.InputStream;
import files.PriorityQueque;
import files.TaskList;
import files.UI;
import funcs.Strings;

/**
 *
 * @author mini
 */
public class Main {
    private UI ui;
    private InputStream inputStream;
    private TaskList taskList;
    private PriorityQueque priorityQueue;
    
    public static void main(String[] args) {
        Main main = new Main();
        if( main.taskList.isErrored() == false && main.inputStream.isErrored() == false ) {
            String line;
            
            while ( (line = main.taskList.readLine() ) != null ) {
                String[] parts = line.split(",");
                if ( parts.length > 0 ) {
                    String action = parts[0];
                    main.ui.writeLine(line);
                    switch(action) {
                        case "BUILD":
                            if( parts.length == 3 ) {
                                main.buildAction(parts);
                            } else {
                                main.ui.writeLine("Build action error");
                            }
                            break;
                        case "SNAPSHOT":
                            main.snapshotAction(parts);
                            break;
                        case "EMPTY":
                            main.emptyAction(parts);
                            break;
                        case "ADD":
                            main.addAction(parts);
                            break;
                        case "REMOVE":
                            main.removeAction(parts);
                            break;
                    }
                }
            }
        }
        main.inputStream.deconstruct();
        main.taskList.deconstruct();
        main.ui.deconstruct();
    }
    
    public Main() {
        ui = new UI(true);  
        inputStream = new InputStream();
        taskList = new TaskList();
        priorityQueue = new PriorityQueque();
    }
    
    /**
     * add continents until different country is seen
     * @param parts 
     */
    public void buildAction(String[] parts) {
        String stopOn = parts[2];
        String line;
        int count = 0;
        while ( (line = inputStream.readLine() ) != null ) {
            String[] lineParts = line.split(",");
            long currentOffset = inputStream.getCurrentOffset();
            if( lineParts.length == 4 ) {
                String name = lineParts[0];
                String country = lineParts[1];
                //String region = lineParts[2];
                int population = Integer.parseInt( lineParts[3] );
                if ( ! stopOn.equals( country ) ) {
                    //set offset back by one line for next iteration
                    inputStream.setOffset(currentOffset);
                    break;
                } else {
                    priorityQueue.add(name, population);
                }
                count++;
            }
        }
        ui.writeLine( "\t>>>> "+count+" countries STORED in PQ" );
    }
    
    /**
     * print out heap NOT SORTED
     * @param parts 
     */
    public void snapshotAction(String[] parts) {
        ui.writeLine( "\tN is " + priorityQueue.getN() );
        if( priorityQueue.getN() == -1 ) {
            //nothing in here
        } else { 
            int current = 0;
            HeapEntity tmp;
            while ( current < priorityQueue.getN() ) { //root
                tmp = priorityQueue.getHeapEntity(current);
                if( tmp != null ) {
                    String popStr = String.format("%,d", tmp.getPopulation());
                    ui.writeLine( String.format("\t[%02d] %s > %s", 
                            current,
                            Strings.forceStrLength(30, tmp.getKeyValue()),
                            Strings.padStrLength(11, popStr)
                    ) );
                } else {
                    System.out.println("\tERROR" + current);
                }
                current++;
            }
        }
    }
    
    /**
     * Remove all properties
     * @param parts 
     */
    public void emptyAction(String[] parts) {
        ui.writeLine( String.format("\t>>>> %d countries REMOVED from PQ (in this order)", priorityQueue.getN()) );
        int i = 1;
        HeapEntity row;
        while( (row = priorityQueue.delete()) != null ) {
            i++;
            String popStr = String.format("%,d", row.getPopulation());
            ui.writeLine( String.format("\t%02d > %s > %s", 
                    i, 
                    Strings.forceStrLength(30, row.getKeyValue()),
                    Strings.padStrLength(11, popStr)
            ) );
        }
    }
    
    /**
     * Add single entity
     * @param parts 
     */
    public void addAction(String[] parts) {
        String stopOn = parts[2];
        String line;
        int count = 0;
        while ( (line = inputStream.readLine() ) != null ) {
            String[] lineParts = line.split(",");
            long currentOffset = inputStream.getCurrentOffset();
            if( lineParts.length == 4 ) {
                String name = lineParts[0];
                String region = lineParts[2];
                int population = Integer.parseInt( lineParts[3] );
                if ( ! stopOn.equals( region ) ) {
                    //set offset back by one line for next iteration
                    inputStream.setOffset(currentOffset);
                    break;
                } else {
                    priorityQueue.add(name, population);
                }
                count++;
            }
        }
        ui.writeLine( "\t>>>> "+count+" countries STORED in PQ" );
    }
    
    /**
     * remove entity
     * @param parts 
     */
    public void removeAction(String[] parts) {
        if( parts.length == 2 ) {
            int items = Integer.parseInt( parts[1] );
            ui.writeLine( String.format("\t>>>> %d countries REMOVED from PQ (in this order)", items) );
            for( int i = 0; i < items; i++ ) {
                HeapEntity row = priorityQueue.delete();
                if( row != null ) {
                    String popStr = String.format("%,d", row.getPopulation());
                    ui.writeLine( String.format("\t%02d > %s > %s", 
                            i, 
                            Strings.forceStrLength(30, row.getKeyValue()),
                            Strings.padStrLength(11, popStr)
                    ) );
                } else {
                    ui.writeLine( String.format("\t%02d > Error removing country", i) );
                }
            }
        } else {
            ui.writeLine("REMOVE ACTION Error");
        }
    }
}
