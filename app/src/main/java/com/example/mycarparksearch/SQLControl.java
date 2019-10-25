package com.example.mycarparksearch;

import android.util.Log;

import java.sql.*;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SQLControl {
    private static final int DEFAULT_SSH_PORT = 22;
    private static final int PORT = 53009;

    private String sshHost;
    private String sshUsername;
    private String sshPassword;
    private String dbHost;
    private int dbPort;
    private  String dbName;
    private String dbUsername;
    private String dbPassword;
    private Connection conn;
    private Session session;

    /* constructor
     */
    public SQLControl(String sshHost, String sshUsername, String sshPassword,
                      String dbHost, int dbPort, String dbName, String dbUsername,
                      String dbPassword) {
        this.sshHost = sshHost;
        this.sshUsername = sshUsername;
        this.sshPassword = sshPassword;
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.conn = null;
        this.session = null;
    }

    /*
    To set up SSH connection to the Linux server
     */
    private void setSSHConnection() throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(sshUsername, sshHost, DEFAULT_SSH_PORT);
        session.setPassword(sshPassword);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        session.setPortForwardingL(PORT, dbHost, dbPort);
    }

    /*
    To check whether SSH connection to the Linux server is established
     */
    private boolean isSSHConnected() {
        return session != null && session.isConnected();
    }

    /*
    To set up connection to the MySQL server
     */
    public boolean setDBConnection() {
        if (!isSSHConnected()) {
            try {
                setSSHConnection();
            } catch (JSchException e) {
                Log.d("CREATION", e.toString());
                close();
                return false;
            }
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + dbHost +":" + PORT + "/";
            try {
                conn = DriverManager.getConnection(url+dbName, dbUsername, dbPassword);
            } catch (SQLException e) {
                //Log.d("CREATION", e.getStackTrace().toString());
                Log.d("CREATION", "H");
                close();
                return false;
            }
            return true;
        } catch (ClassNotFoundException e) {
            Log.d("CREATION", "class");
            close();
            return false;
        }

    }

    /*
    To check whether connection to the MySQL server is established
     */
    public boolean isDBConnected() throws SQLException {
        return conn != null && conn.isValid(5);
    }

    /*
    To pass in a SQL query statement into MySQL and return a ResultSet object
     */
    public ResultSet query(String sql) throws SQLException {
        if (!isDBConnected()) {
            if (!setDBConnection()) {
                throw new SQLException("Connection to database failed!");
            }
        }
        Statement st = conn.createStatement();
        return st.executeQuery(sql);
    }

    /*
    To close connection to the Linux and the MySQL server
     */
    public void close() {
        if (session != null) {
            try {
                session.disconnect();
                session.delPortForwardingL(PORT);
            } catch (JSchException e) {}
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {}
        }
    }
}