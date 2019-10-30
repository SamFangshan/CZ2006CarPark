package com.example.mycarparksearch;

import android.os.strictmode.SqliteObjectLeakedViolation;

import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class CarparkSQLControlTest {

    //Basis path testing
    @Test
    public void setDBConnection() {
        CarparkSQLControl con;

        // test case 1
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        assertEquals(con.setDBConnection(), true);
        con.close();

        // test case 2
        con = new CarparkSQLControl("172.21.148.175", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        assertEquals(con.setDBConnection(), false);
        con.close();

        // test case 3
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cx2006", "cz2006", "cz2006ala");
        assertEquals(con.setDBConnection(), false);
        con.close();
    }

    //Basis path testing
    @Test
    public void isDBConnected() {
        CarparkSQLControl con;
        boolean isDBConnected = false;

        // test case 1
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        con.setDBConnection();
        try {
            isDBConnected = con.isDBConnected();
        } catch (SQLException e) {}
        assertEquals(isDBConnected, true);
        con.close();

        // test case 2
        con = new CarparkSQLControl("172.21.148.175", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        con.setDBConnection();
        try {
            isDBConnected = con.isDBConnected();
        } catch (SQLException e) {}
        assertEquals(isDBConnected, false);
        con.close();

        // test case 3
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cx2006", "cz2006", "cz2006ala");
        con.setDBConnection();
        try {
            isDBConnected = con.isDBConnected();
        } catch (SQLException e) {}
        assertEquals(isDBConnected, false);
        con.close();

        // test case 4
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        try {
            isDBConnected = con.isDBConnected();
        } catch (SQLException e) {}
        assertEquals(isDBConnected, false);
        con.close();
    }

    @Test
    public void query() {
        CarparkSQLControl con;
        ResultSet resultSet = null;
        String expected = "";
        String output = "";
        boolean hasNext = true;

        // test case 1
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        con.setDBConnection();
        expected = "196 BOON LAY DR";
        try {
            resultSet = con.query("SELECT address FROM cz2006.HDBCarPark where carParkNo = 'BL22';");
            resultSet.next();
            output = resultSet.getString("address");
        } catch (SQLException e) { }
        assertEquals(expected, output);
        con.close();

        // test case 2
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        con.setDBConnection();
        expected = "4.5";
        try {
            resultSet = con.query("SELECT gantryHeight FROM cz2006.HDBCarPark where carParkNo = 'BM3';");
            resultSet.next();
            output = ((Double)resultSet.getDouble("gantryHeight")).toString();
        } catch (SQLException e) {}
        assertEquals(expected, output);
        con.close();

        // test case 3
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cx2006", "cz2006", "cz2006ala");
        expected = "Connection to database failed!";
        try {
            con.query("SELECT * FROM cz2006.HDBCarPark;");
        } catch (SQLException e) {
            output = e.getMessage();
        }
        assertEquals(expected, output);
        con.close();

        // test case 4
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        con.setDBConnection();
        try {
            resultSet = con.query("SELECT shortTermParking FROM cz2006.HDBCarPark where carParkNo = 'XXX';");
            hasNext = resultSet.next();
        } catch (SQLException e) {}
        assertEquals(false, hasNext);
        con.close();
    }

    @Test
    public void getAllCarparkLocations() {
        CarparkSQLControl con;
        String sql;
        ResultSet result;
        ArrayList<CarparkEntity> carparkListExpected = null;
        ArrayList<CarparkEntity> carparkListOutput = null;
        String expected = "";
        String output = "";

        // test case 1
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        try {
            sql = "SELECT carParkNo, xCoord, yCoord FROM HDBCarPark;";
            result = con.query(sql);
            carparkListExpected = new ArrayList<CarparkEntity>();
            while(result.next()) {
                HashMap<String, String> carMap = new HashMap<String, String>();
                carMap.put("carParkNo", result.getString("carParkNo"));
                carMap.put("xCoord", result.getString("xCoord"));
                carMap.put("yCoord", result.getString("yCoord"));
                CarparkEntity carparkEntity = new CarparkEntity(carMap);
                carparkListExpected.add(carparkEntity);
            }
            result.close();
            carparkListOutput = con.getAllCarparkLocations();
        } catch (SQLException e) { }
        assertEquals(carparkListExpected, carparkListOutput);

        // test case 2
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cx2006", "cz2006", "cz2006ala");
        expected = "Connection to database failed!";
        try {
            con.getAllCarparkLocations();
        } catch (SQLException e) {
            output = e.getMessage();
        }
        assertEquals(expected, output);
    }

    @Test
    public void queryCarparks() {
        CarparkSQLControl con;
        HashMap<String, String> carMap;
        CarparkEntity carparkEntity;
        ArrayList<CarparkEntity> carparkListExpected = null;
        ArrayList<CarparkEntity> carparkListOutput = null;
        String expected = "";
        String output = "";

        // test case 1
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        carparkListExpected = new ArrayList<CarparkEntity>();
        carMap = new HashMap<String, String>();
        carMap.put("carParkNo", "BL8");
        carMap.put("address", "BLK 221 BOON LAY PLACE");
        carparkEntity = new CarparkEntity(carMap);
        carparkListExpected.add(carparkEntity);
        carMap = new HashMap<String, String>();
        carMap.put("carParkNo", "BL8L");
        carMap.put("address", "BLK 221 BL8 BOON LAY PLACE");
        carparkEntity = new CarparkEntity(carMap);
        carparkListExpected.add(carparkEntity);
        try {
            carparkListOutput = con.queryCarparks("BL8");
        } catch (SQLException e) { }
        assertEquals(carparkListOutput, carparkListExpected);

        // test case 2
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        carparkListExpected = new ArrayList<CarparkEntity>();
        carMap = new HashMap<String, String>();
        carMap.put("carParkNo", "BL3");
        carMap.put("address", "BLK 174/179 BOON LAY DRIVE");
        carparkEntity = new CarparkEntity(carMap);
        carparkListExpected.add(carparkEntity);
        carMap = new HashMap<String, String>();
        carMap.put("carParkNo", "BL10");
        carMap.put("address", "BLK 188/191 BOON LAY DRIVE");
        carparkEntity = new CarparkEntity(carMap);
        carparkListExpected.add(carparkEntity);
        carMap = new HashMap<String, String>();
        carMap.put("carParkNo", "BL13");
        carMap.put("address", "BLK 198/206 BOON LAY DRIVE");
        carparkEntity = new CarparkEntity(carMap);
        carparkListExpected.add(carparkEntity);
        carMap = new HashMap<String, String>();
        carMap.put("carParkNo", "BL15");
        carMap.put("address", "BLK 257A BOON LAY DRIVE");
        carparkEntity = new CarparkEntity(carMap);
        carparkListExpected.add(carparkEntity);
        carMap = new HashMap<String, String>();
        carMap.put("carParkNo", "BL17");
        carMap.put("address", "BLK 268 BOON LAY DRIVE");
        carparkEntity = new CarparkEntity(carMap);
        carparkListExpected.add(carparkEntity);
        try {
            carparkListOutput = con.queryCarparks("BOON LAY DRIVE");
        } catch (SQLException e) { }
        assertEquals(carparkListOutput, carparkListExpected);

        // test case 3
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cx2006", "cz2006", "cz2006ala");
        expected = "Connection to database failed!";
        try {
            con.queryCarparks("nothing");
        } catch (SQLException e) {
            output = e.getMessage();
        }
        assertEquals(output, expected);

        // test case 4
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        carparkListExpected = new ArrayList<CarparkEntity>();
        try {
            carparkListOutput = con.queryCarparks("nothing");
        } catch (SQLException e) {
        }
        assertEquals(carparkListOutput, carparkListExpected);
    }

    @Test
    public void queryCarparkFullInfo() {
        CarparkSQLControl con;
        HashMap<String, String> carMap;
        HashMap<String, Integer> lotsAvailable;
        CarparkEntity carparkEntityExpected = null;
        CarparkEntity carparkEntityOutput = null;
        ResultSet resultSet = null;
        int carLotAvail = 0;
        int motorLotAvail = 0;
        int heavyLotAvail = 0;
        String expected = "";
        String output = "";

        // test case 1
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        carMap = new HashMap<String, String>();
        carMap.put("carParkNo", "B49");
        carMap.put("address", "BLK 549/551 BEDOK NORTH AVENUE 1");
        carMap.put("xCoord", "1.33219");
        carMap.put("yCoord", "103.927");
        carMap.put("carParkType", "SURFACE CAR PARK");
        carMap.put("typeOfParkingSystem", "ELECTRONIC PARKING");
        carMap.put("shortTermParking", "WHOLE DAY");
        carMap.put("freeParking", "SUN & PH FR 7AM-10.30PM");
        carMap.put("nightParking", "YES");
        carMap.put("carParkDecks", "0");
        carMap.put("gantryHeight", "4.5");
        carMap.put("carParkBasement", "N");
        carMap.put("carLotNum", "183");
        carMap.put("motorLotNum", "0");
        carMap.put("heavyLotNum", "0");
        carMap.put("carRates", "$0.60 per half-hour");
        carMap.put("motorRates", null);
        carMap.put("heavyRates", null);
        try {
            con.setDBConnection();
            resultSet = con.query("SELECT * FROM cz2006.HDBCarParkAvail where carParkNo = 'B49';");
            resultSet.next();
        } catch (SQLException e) { }
        lotsAvailable = new HashMap<String, Integer>();
        try {
            carLotAvail = resultSet.getInt("carLotAvail");
            motorLotAvail = resultSet.getInt("motorLotAvail");
            heavyLotAvail = resultSet.getInt("heavyLotAvail");
        } catch (SQLException e) { }
        lotsAvailable.put("carLotAvail", carLotAvail);
        lotsAvailable.put("motorLotAvail", motorLotAvail);
        lotsAvailable.put("heavyLotAvail", heavyLotAvail);
        carparkEntityExpected = new CarparkEntity(carMap, lotsAvailable);
        try {
            carparkEntityOutput = con.queryCarparkFullInfo("B49");
        } catch (SQLException e) { }
        assertEquals(carparkEntityOutput, carparkEntityExpected);

        // test case 2
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cx2006", "cz2006", "cz2006ala");
        expected = "Connection to database failed!";
        try {
            con.queryCarparkFullInfo("B49");
        } catch (SQLException e) {
            output = e.getMessage();
        }
        assertEquals(output, expected);

        // test case 3
        con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        expected = "Illegal operation on empty result set.";
        try {
            carparkEntityOutput = con.queryCarparkFullInfo("XXX");
        } catch (SQLException e) {
            output = e.getMessage();
        }
        assertEquals(output, expected);
    }
}