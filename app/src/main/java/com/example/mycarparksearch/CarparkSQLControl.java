package com.example.mycarparksearch;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class CarparkSQLControl extends SQLControl {
    private Context context;
    public CarparkSQLControl(String sshHost, String sshUsername, String sshPassword,
                             String dbHost, int dbPort, String dbName, String dbUsername,
                             String dbPassword, Context context) {
        super(sshHost, sshUsername, sshPassword, dbHost, dbPort, dbName, dbUsername, dbPassword);
        this.context = context;
    }

    /*
    To get all CarParkNo and car park coordinates for displaying car park locations on Google Maps
    Return an ArrayList of CarparkEntity
     */
    public ArrayList<CarparkEntity> getAllCarparkLocations() throws SQLException {
        if (!isDBConnected()) {
            if (!setDBConnection()) {
                throw new SQLException("Connection to database failed!");
            }
        }
        String sql = "SELECT carParkNo, xCoord, yCoord FROM HDBCarPark;";
        ResultSet result = query(sql);
        ArrayList<CarparkEntity> carparkList = new ArrayList<CarparkEntity>();
        while(result.next()) {
            HashMap<String, String> carMap = new HashMap<String, String>();
            carMap.put(context.getString(R.string.carParkNo), result.getString(context.getString(R.string.carParkNo)));
            carMap.put(context.getString(R.string.xCoord), result.getString(context.getString(R.string.xCoord)));
            carMap.put(context.getString(R.string.yCoord), result.getString(context.getString(R.string.yCoord)));
            CarparkEntity carparkEntity = new CarparkEntity(carMap);
            carparkList.add(carparkEntity);
        }
        result.close();
        close();

        return carparkList;
    }

    public ArrayList<CarparkEntity> queryCarparks(String keywords) throws SQLException {
        if (!isDBConnected()) {
            if (!setDBConnection()) {
                throw new SQLException("Connection to database failed!");
            }
        }
        String matcher = "%";
        StringTokenizer st = new StringTokenizer(keywords);
        while (st.hasMoreTokens()) {
            matcher += st.nextToken() + "%";
        }

        String sql = "SELECT carParkNo, address FROM cz2006.HDBCarPark WHERE carParkNo LIKE '" + matcher +
                "' OR address LIKE '"+ matcher + "'";
        sql += ";";

        ResultSet result = query(sql);

        ArrayList<CarparkEntity> carparkList = new ArrayList<CarparkEntity>();
        while(result.next()) {
            HashMap<String, String> carMap = new HashMap<String, String>();
            carMap.put(context.getString(R.string.carParkNo), result.getString(context.getString(R.string.carParkNo)));
            carMap.put(context.getString(R.string.address), result.getString(context.getString(R.string.address)));
            CarparkEntity carparkEntity = new CarparkEntity(carMap);
            carparkList.add(carparkEntity);
        }
        result.close();
        close();

        return carparkList;
    }

    public ArrayList<CarparkEntity> queryCarparks(String keywords,
                                                             String type, String system,
                                                             String short_term, String free,
                                                             String night) throws SQLException {
        if (!isDBConnected()) {
            if (!setDBConnection()) {
                throw new SQLException("Connection to database failed!");
            }
        }
        String matcher = "%";
        StringTokenizer st = new StringTokenizer(keywords);
        while (st.hasMoreTokens()) {
            matcher += st.nextToken() + "%";
        }

        String sql = "SELECT carParkNo, address FROM cz2006.HDBCarPark WHERE (carParkNo LIKE '" + matcher +
                "' OR address LIKE '"+ matcher + "')";
        if (type != null) {
            sql += " AND carParkType = '" + type + "'";
        }
        if (system != null) {
            sql += " AND typeOfParkingSystem = '" + system + "'";
        }
        if (short_term != null) {
            if (short_term.equals(context.getString(R.string.no))) {
                sql += " AND shortTermParking = 'NO'";
            } else if (short_term.equals(context.getString(R.string.yes))) {
                sql += " AND shortTermParking != 'NO'";
            }
        }
        if (free != null) {
            if (free.equals(context.getString(R.string.no))) {
                sql += " AND freeParking = 'NO'";
            } else if (free.equals(context.getString(R.string.yes))) {
                sql += " AND freeParking != 'NO'";
            }
        }
        if (night != null) {
            sql += " AND nightParking = '" + night + "'";
        }
        sql += ";";

        ResultSet result = query(sql);

        ArrayList<CarparkEntity> carparkList = new ArrayList<CarparkEntity>();
        while(result.next()) {
            HashMap<String, String> carMap = new HashMap<String, String>();
            carMap.put(context.getString(R.string.carParkNo), result.getString(context.getString(R.string.carParkNo)));
            carMap.put(context.getString(R.string.address), result.getString(context.getString(R.string.address)));
            CarparkEntity carparkEntity = new CarparkEntity(carMap);
            carparkList.add(carparkEntity);
        }
        result.close();
        close();

        return carparkList;
    }

<<<<<<< Updated upstream
    /*
    To get detailed car park information of a car park with a specific carParkNo
    Return a CarparkEntity
=======
    public CarparkEntity findNearestCarpark(LatLng here) throws SQLException {
        if (!isDBConnected()) {
            if (!setDBConnection()) {
                throw new SQLException("Connection to database failed!");
            }
        }
        String sql = "SELECT HDBCarPark.carParkNo, ((xCoord - " +  here.latitude + ") *  (xCoord - " +  here.latitude + ") + " +
                "(yCoord - " + here.longitude + ")" + "*(yCoord - " + here.longitude + ")) AS squareDiff" +
                " FROM HDBCarPark WHERE HDBCarPark.carParkNo IN (SELECT carParkNo FROM HDBCarParkAvail) " +
                " ORDER BY squareDiff";
        ResultSet result = query(sql);
        ArrayList<CarparkEntity> carparkList = new ArrayList<CarparkEntity>();
        while (true) {
            if (!result.next()) break;
            HashMap<String, String> carMap = new HashMap<String, String>();
            carMap.put(context.getString(R.string.carParkNo), result.getString(context.getString(R.string.carParkNo)));
            carMap.put("context.getString(R.string.squareDiff))", result.getString(context.getString(R.string.squareDiff)));
            return new CarparkEntity(carMap);
        }
        throw new SQLException("No data found!");
    }

    /**
     * To get detailed car park information of a car park with a specific carParkNo
     * @param carParkNo
     * @return carpark entity
     * @throws SQLException
>>>>>>> Stashed changes
     */
    public CarparkEntity queryCarparkFullInfo(String carParkNo) throws SQLException {
        if (!isDBConnected()) {
            if (!setDBConnection()) {
                throw new SQLException("Connection to database failed!");
            }
        }

        String sql;
        ResultSet result;
        ResultSetMetaData resultMD;

        sql = "SELECT * FROM cz2006.HDBCarPark WHERE carParkNo = '" + carParkNo + "';";
        result = query(sql);
        resultMD = result.getMetaData();
        result.next();
        HashMap<String, String> carMap = new HashMap<String, String>();
        for (int i = 1; i <= resultMD.getColumnCount(); i++) {
            carMap.put(resultMD.getColumnName(i), result.getString(resultMD.getColumnName(i)));
        }
        result.close();

        sql = "SELECT * FROM cz2006.HDBCarParkAvail WHERE carParkNo = '" + carParkNo + "';";
        result = query(sql);
        resultMD = result.getMetaData();
        result.next();
        HashMap<String, Integer> lotMap = new HashMap<String, Integer>();
        lotMap.put(context.getString(R.string.carLotAvail), result.getInt(context.getString(R.string.carLotAvail)));
        lotMap.put(context.getString(R.string.motorLotAvail), result.getInt(context.getString(R.string.motorLotAvail)));
        lotMap.put(context.getString(R.string.heavyLotAvail), result.getInt(context.getString(R.string.heavyLotAvail)));
        result.close();

        CarparkEntity carparkEntity = new CarparkEntity(carMap, lotMap);
        close();
        return carparkEntity;
    }
}