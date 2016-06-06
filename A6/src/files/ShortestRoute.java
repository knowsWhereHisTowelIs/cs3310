/**
 * CS3310 - A5 
 * Author: Caleb Slater
 */
package files;

import java.io.IOException;

public class ShortestRoute {
    public int N;
    public boolean[] included;
    public int[] distance;
    public int[] predecessor; // aka PATH

    private MapData map;
    private UI ui;
    private final int infinity = Integer.MAX_VALUE;

    public ShortestRoute(UI ui, MapData map, int N) {
        this.N = N;
        this.ui = ui;
        this.map = map;
        included = new boolean[N];
        distance = new int[N];
        predecessor = new int[N];
    }

    /**
     * find route between two cities
     * @param start
     * @param destination
     * @throws IOException 
     */
    public void findRoute(int start, int destination) throws IOException {
        initializeArrays(start);
        searchForPath(start, destination);
        int shortest = distance[destination];
        String list = "";
        list = reportAnswers(list, destination);
        ui.writeLine("\n\nTOTAL DISTANCE:  " + shortest + " miles");
        ui.writeLine("SHORTEST ROUTE:  " + list + "\n");
    }

    /**
     * setup arrays to find shortest route
     * @param start 
     */
    private void initializeArrays(int start) {
        for (int i = 0; i < distance.length; i++) {
            included[i] = false;
            if (start != i) {
                distance[i] = infinity;
            } else {
                distance[i] = 0;
            }
            predecessor[i] = -1;
        }
    }

    /**
     * find the shortest search path between cities
     * using Dijkstra's Algorithm
     * @param start
     * @param destination 
     */
    private void searchForPath(int start, int destination) {
        if (start == destination) {
            ui.write(map.getCityName(start) + " ");
            distance[start] = 0;
        } else {
            while ( included[destination] == false ) {
                int target = selectTarget();
                if (target == -1) {
                    continue;
                }
                
                ui.write(map.getCityName(target) + " ");

                included[target] = true;
                
                for (int i = 0; i < N; i++) {
                    int edge = getEdgeWeight(target, i);
                    if (!included[i] && isValidEdgeWeight(target)) {
                        int newDistance = distance[target] + edge;
                        if ( newDistance > 0 && newDistance < distance[i] ) {
                            distance[i] = newDistance;
                            predecessor[i] = target;
                        }
                    }
                }
            }
        }
    }

    /**
     * Reports the answers to the total distance and shortest route from start
     * to destination.
     *
     * @param list
     * @param dest
     * @return
     * @throws IOException
     */
    private String reportAnswers(String list, int dest) throws IOException {
        if (predecessor[dest] == -1) {
            list = list + map.getCityName(dest);
        } else {
            list = reportAnswers(list, predecessor[dest]);
            list = list + (" > " + map.getCityName(dest));
        }
        return list;
    }

    /**
     * Get min distance
     * @return 
     */
    private int selectTarget() {
        int min = infinity;
        int subscript = -1;
        for (int i = 0; i < N; i++) {
            if ( !included[i] && distance[i] < min ) {
                min = distance[i];
                subscript = i;
            }
        }
        return subscript;
    }
    
    /**
     * find distance between two cities
     * @param target
     * @param i
     * @return 
     */
    private int getEdgeWeight(int target, int i) {
        return map.getRoadDistance(target, i);
    }
    
    /**
     * is weight not 0, or infinity
     * @param weight
     * @return 
     */
    private boolean isValidEdgeWeight(int weight) {
        return weight != 0 && weight != infinity;
    }
}
