package com.example.mycarparksearch;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Entity class that represents a car park
 */
public class CarparkEntity {
    private HashMap<String, String> record;
    private HashMap<String, Integer> lotsAvailable;

    /**
     * Constructor
     * @param record
     * @param lotsAvailable
     */
    public CarparkEntity(HashMap<String, String> record, HashMap<String, Integer> lotsAvailable) {
        this.record = record;
        this.lotsAvailable = lotsAvailable;
    }

    /**
     * Constructor
     * @param record
     */
    public CarparkEntity(HashMap<String, String> record) {
        this.record = record;
        this.lotsAvailable = null;
    }

    /**
     * Pass in the key to get corresponding value (info) of the car park
     * @param key
     * @return a piece of info of the car park entity
     */
    public String getInformation(String key) {
        return record.get(key);
    }

    /**
     * Set the record (fixed info) of the car park entity
     * @param record
     */
    public void setRecord(HashMap<String, String> record) {
        this.record = record;
    }

    /**
     * Pass in the lot type to get the availability of the car park
     * @param lotType
     * @return lot availability
     */
    public int getLotsAvailable(String lotType) {
        return lotsAvailable.get(lotType);
    }

    /**
     * Set the lot availability info of the car park
     * @param lotsAvailable
     */
    public void setLotsAvailable(HashMap<String, Integer> lotsAvailable) {
        this.lotsAvailable = lotsAvailable;
    }

    /**
     * Overrides toString()
     * @return String
     */
    @Override
    public String toString() {
        return Arrays.asList(record).toString() + "\n"
                + Arrays.asList(lotsAvailable).toString();
    }

    /**
     * Overrides equals
     * @param o
     * @return whether equal to another car park entity or not
     */
    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) {
            return false;
        }
        if (((CarparkEntity)o).lotsAvailable == null && this.lotsAvailable == null &&
                this.record.equals(((CarparkEntity)o).record)) {
            return true;
        }
        if (((CarparkEntity)o).lotsAvailable == null || this.lotsAvailable == null) {
            return false;
        }
        if (this.lotsAvailable.equals(((CarparkEntity)o).lotsAvailable) &&
                this.record.equals(((CarparkEntity)o).record)) {
            return true;
        }
        return false;
    }
}