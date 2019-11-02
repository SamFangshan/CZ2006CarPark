package com.example.mycarparksearch;

import java.util.Arrays;
import java.util.HashMap;

public class CarparkEntity {
    private HashMap<String, String> record;
    private HashMap<String, Integer> lotsAvailable;

    public CarparkEntity(HashMap<String, String> record, HashMap<String, Integer> lotsAvailable) {
        this.record = record;
        this.lotsAvailable = lotsAvailable;
    }

    public CarparkEntity(HashMap<String, String> record) {
        this.record = record;
        this.lotsAvailable = null;
    }

    public String getInformation(String key) {
        return record.get(key);
    }

    public void setRecord(HashMap<String, String> record) {
        this.record = record;
    }

    public int getLotsAvailable(String lotType) {
        return lotsAvailable.get(lotType);
    }

    public void setLotsAvailable(HashMap<String, Integer> lotsAvailable) {
        this.lotsAvailable = lotsAvailable;
    }

    @Override
    public String toString() {
        return Arrays.asList(record).toString() + "\n"
                + Arrays.asList(lotsAvailable).toString();
    }

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