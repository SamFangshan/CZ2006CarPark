package com.example.mycarparksearch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class CarparkSQLControl extends SQLControl {
    public CarparkSQLControl(String sshHost, String sshUsername, String sshPassword,
                             String dbHost, int dbPort, String dbName, String dbUsername,
                             String dbPassword) {
        super(sshHost, sshUsername, sshPassword, dbHost, dbPort, dbName, dbUsername, dbPassword);
    }

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
}