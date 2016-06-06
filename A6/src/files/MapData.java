/**
 * CS3310 - A5 
 * Author: Caleb Slater
 */
package files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class MapData {
    private final static int MAX_N = 200;
    private final static String mapFilePath = "src/data/MichiganMap.txt";
    private final static String FILE_MODE = "rw";
    
    private int N = 0; //next empty city number
    private int[][] roadDistances;
    private String[] cityNameList; // LP Cities
    private ArrayList upCityList; // UP Cities
    
    private UI ui;
    
    public MapData() {
        ui = new UI(false);
        
        roadDistances = new int[MAX_N][MAX_N];
        cityNameList = new String[MAX_N];
        upCityList = new ArrayList<>();
    }
    
    /**
     * Map data into memory
     * open/closes files
     */
    public void loadMapData() {
        //populate graph w/inifinities
        for( int i = 0; i < MAX_N; i++ ) {
            for( int j = 0; j < MAX_N; j++ ) {
                roadDistances[i][j] = Integer.MAX_VALUE;
            }
        }
        
        //populate diagonals w/zeros
        for( int i = 0, j = 0; i < MAX_N; i++, j++ ) {
            roadDistances[i][j] = 0;
        }
        
        //fill w/real distances
        File file = new File(mapFilePath);
        try {
            RandomAccessFile randAccessFile = new RandomAccessFile(file, FILE_MODE);
            String line;

            while( (line = randAccessFile.readLine()) != null ) {
                //invalid data
                if( line.length() == 0 ) continue;
                
                //comments
                char firstChar = line.charAt(0);
                if( firstChar == ' ' || firstChar == '%' ) continue;
                
                // valid data
                String[] parts = line.split(", ");
                                
                if( line.substring(0, 2).equals("up") && parts.length > 0 ) {
                    parts[0] = parts[0].replace("up([ ", "");
                    String lastElement = parts[ parts.length - 1 ];
                    parts[ parts.length - 1 ] = parts[ parts.length - 1 ].replace(" ]).", "");//lastElement.substring(0, lastElement.length() - 1 );
                    for( int i = 0; i < parts.length; i++ ) {
                        upCityList.add( parts[i] );
                    }
                } else if( line.substring(0, 4).equals("road") && parts.length == 3 ) {
                    String cityA = parts[0].replace("road(", "");
                    String cityB = parts[1];
                    int cityANum = storeCityName(cityA);
                    int cityBNum = storeCityName(cityB);
                    int distance = Integer.parseInt( parts[2].replace(").", "").trim() );
                    roadDistances[cityANum][cityBNum] = distance;
                    roadDistances[cityBNum][cityANum] = distance;
                } else {
                    //error 
                    ui.writeLine("Map File Load data error");
                }
            }
        } catch (FileNotFoundException ex ) {
            //error
        } catch (IOException ex ) {
            //error
        }
    }
    
    /**
     * get city num from name, -1 if invalid
     * @param cityName
     * @return 
     */
    public int getCityNumber(String cityName) {
        int num = -1;
        for( int i = 0; i < cityNameList.length; i++ ) {
            if ( cityNameList[i] != null && cityNameList[i].equals(cityName) ) {
                num = i;
                break;
            }
        }
        return num;
    }
    
    /**
     * get peninsula from city name
     * @param cityName
     * @return 
     */
    public String getCityPeninsula(String cityName) {
        String area = "LP";
        if( cityName.equals("theBridge") ) {
            area = "BRIDGE";
        } else { 
            for( int i = 0; i < upCityList.size(); i++ ) {
                if( cityName.equals( upCityList.get(i) ) ) {
                    area = "UP";
                    break;
                }
            }
        }
        return area;
    }
    
    /**
     * get city name by city number
     * @param cityNumber
     * @return 
     */
    public String getCityName(int cityNumber) {
        return cityNameList[cityNumber];
    }
    
    /**
     * get road distances between two cities
     * @param cityA
     * @param cityB
     * @return 
     */
    public int getRoadDistance(int cityA, int cityB) {
        return roadDistances[cityA][cityB];
    }
    
    /**
     * 
     * @param cityName
     * @return city number
     */
    private int storeCityName(String cityName) {
        int cityNumber = getCityNumber(cityName);
        if( cityNumber == -1 ) {
            cityNameList[N] = cityName;
            cityNumber = N;
            N++;
        }
        return cityNumber;
    }
    
    //------------GETTERS/SETTERS

    public int getN() {
        return N;
    }

    public void setN(int N) {
        this.N = N;
    }

    public int[][] getRoadDistances() {
        return roadDistances;
    }

    public void setRoadDistances(int[][] roadDistances) {
        this.roadDistances = roadDistances;
    }

    public String[] getCityNameList() {
        return cityNameList;
    }

    public void setCityNameList(String[] cityNameList) {
        this.cityNameList = cityNameList;
    }

    public ArrayList getUpCityList() {
        return upCityList;
    }

    public void setUpCityList(ArrayList upCityList) {
        this.upCityList = upCityList;
    }

    public UI getUi() {
        return ui;
    }

    public void setUi(UI ui) {
        this.ui = ui;
    }
    
}
