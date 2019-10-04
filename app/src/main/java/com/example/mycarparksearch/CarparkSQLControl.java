package com.example.mycarparksearch;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class CarparkSQLControl extends SQLControl {
    public CarparkSQLControl(String sshHost, String sshUsername, String sshPassword,
                             String dbHost, int dbPort, String dbName, String dbUsername,
                             String dbPassword) {
        super(sshHost, sshUsername, sshPassword, dbHost, dbPort, dbName, dbUsername, dbPassword);
    }

    // to be used for displaying on map
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
            carMap.put("carParkNo", result.getString("carParkNo"));
            carMap.put("xCoord", result.getString("xCoord"));
            carMap.put("yCoord", result.getString("yCoord"));
            CarparkEntity carparkEntity = new CarparkEntity(carMap);
            carparkList.add(carparkEntity);
        }
        result.close();
        close();

        return carparkList;
    }

    // to be used for query result list
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
                "' OR address LIKE '"+ matcher + "';";
        ResultSet result = query(sql);

        ArrayList<CarparkEntity> carparkList = new ArrayList<CarparkEntity>();
        while(result.next()) {
            HashMap<String, String> carMap = new HashMap<String, String>();
            carMap.put("carParkNo", result.getString("carParkNo"));
            carMap.put("address", result.getString("address"));
            CarparkEntity carparkEntity = new CarparkEntity(carMap);
            carparkList.add(carparkEntity);
        }
        result.close();
        close();

        return carparkList;
    }

    // to get the full detailed info of a car park
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
        lotMap.put("carLotAvail", result.getInt("carLotAvail"));
        lotMap.put("motorLotAvail", result.getInt("motorLotAvail"));
        lotMap.put("heavyLotAvail", result.getInt("heavyLotAvail"));
        result.close();

        CarparkEntity carparkEntity = new CarparkEntity(carMap, lotMap);
        close();
        return carparkEntity;
    }
}