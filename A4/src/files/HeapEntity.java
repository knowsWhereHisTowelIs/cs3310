/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

/**
 *
 * @author CallMeTim
 */
public class HeapEntity {
    private String keyValue;
    private int population;
    
    public HeapEntity(String keyValue, int population) {
        this.keyValue = keyValue;
        this.population = population;
    }
    
    /**
     * key value is name
     * @return 
     */
    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }    
}
