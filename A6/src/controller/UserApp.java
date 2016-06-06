/**
 * CS3310 - A5 
 * Author: Caleb Slater
 */
package controller;

import files.MapData;
import files.ShortestRoute;
import files.UI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class UserApp {

    private UI ui;
    private MapData mapData;

    /**
     * Find shortest route
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UserApp userApp = new UserApp();
        try {
            userApp.dispatcher();
        } catch (IOException ex) {
            userApp.ui.writeLine("Fatal Error");
        }
        userApp.deconstruct();
    }

    /**
     * constructor
     */
    public UserApp() {
        ui = new UI(false);
        mapData = new MapData();
    }

    /**
     * create map data then find shortest route between cities
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void dispatcher() throws FileNotFoundException, IOException {
        String fileName = "src/data/CityPairsTestPlan.txt";
        File file = new File(fileName);

        MapData map = new MapData();
        map.loadMapData();
        ShortestRoute shortestRoute = new ShortestRoute(ui, map, map.getN());
        Scanner input = new Scanner(file);
        
        int start, dest;
        String cityAPeninsula, cityBPeninsula;

        while (input.hasNextLine()) {
            String line = input.nextLine().trim();
            String[] cityPair = line.split(" ");
            
            if( cityPair.length != 2 ) continue;
            
            String cityA = cityPair[0];
            String cityB = cityPair[1];
            
            if ( !cityA.startsWith("%") && cityA.length() > 0 ) {
                cityAPeninsula = map.getCityPeninsula(cityA);
                cityBPeninsula = map.getCityPeninsula(cityB);

                start = map.getCityNumber(cityA);
                dest = map.getCityNumber(cityB);

                ui.writeLine("%   %   %   %   %   %   %   %   %   %   %   %   %   %   %\n");

                if (start == -1) {
                    ui.writeLine(String.format("START:  %s", cityA));
                    ui.writeLine(String.format(">>> ERROR >>> city not in Michigan Map ", "%"));
                } else if (dest == -1) {
                    ui.writeLine(String.format("START:  %s (%d - %s)", cityA, start, cityAPeninsula));
                    ui.writeLine(String.format("DESTINATION:  %s ", cityB));
                    ui.writeLine(String.format(">>> ERROR >>> city not in Michigan Map ", "%"));
                } else {
                    ui.writeLine(String.format("START:  %s (%d - %s)", cityA, start, cityAPeninsula));
                    ui.writeLine(String.format("DESTINATION:  %s (%d - %s)", cityB, dest, cityBPeninsula));
                    if (cityAPeninsula.equals(cityBPeninsula) || cityAPeninsula.equals("BRIDGE") || cityBPeninsula.equals("BRIDGE")) {
                        ui.write("\nTRACE OF TARGETS: ");
                        shortestRoute.findRoute(start, dest);
                    } else {
                        ui.writeLine(">>> NOTE >>> 2 different peninsulas, so 2 partial routes ");
                        
                        ui.write("\nTRACE OF TARGETS: ");
                        shortestRoute.findRoute(start, map.getCityNumber("theBridge"));

                        ui.write("\nTRACE OF TARGETS: ");
                        shortestRoute.findRoute(map.getCityNumber("theBridge"), dest);
                    }
                }
                ui.writeLine("");
            }
        }

        ui.writeLine("%   %   %   %   %   %   %   %   %   %   %   %   %   %   %");
        input.close();
    }

    /**
     * save files
     */
    public void deconstruct() {
        ui.deconstruct();
    }
}
