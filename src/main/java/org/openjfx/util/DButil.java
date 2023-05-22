/**package org.openjfx.util;

import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;

public class DButil {

    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";

    private static Connection connect = null;

    private static final String connStr = "jdbc:oracle:thin:HR/HR@localhost:1521/xe";

    public static void dbConnect() throws SQLException, ClassNotFoundException {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }

        try {
            connect = DriverManager.getConnection(connStr);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void dbDisconnect() throws Exception {
        try {
            if (connect != null && !connect.isClosed()) {
                connect.close();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /*public static ResultSet dbExecuteQuery(String queryStmt) throws Exception {
        Statement stmt = null;
        ResultSet resultSet = null;
        CachedRowSet crs = null;
        try {
            dbConnect();
            stmt = connect.createStatement();
            resultSet = stmt.executeQuery(queryStmt);
            crs = new CachedRowSetImpl();
            crs.populate(resultSet);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            dbDisconnect();
        }
        return crs;
    }

    public static ResultSet dbSearchQuery(String queryStmt) throws Exception {
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        CachedRowSet crs = null;
        try {
            dbConnect();
            stmt = connect.prepareStatement(queryStmt);
            resultSet = stmt.executeQuery(queryStmt);
            crs = new CachedRowSetImpl();
            crs.populate(resultSet);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            dbDisconnect();
        }
        return crs;
    }

    public static void dbExecuteUpdate(String sqlStmt) throws Exception {
        Statement stmt = null;
        try {
            dbConnect();
            stmt = connect.prepareStatement(sqlStmt);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            dbDisconnect();
        }
    }
} */
