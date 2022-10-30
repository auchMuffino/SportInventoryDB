package javaresources.lr11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection("jdbc:MySQL://localhost:3306/SportInventory","root","11111111");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void closeConnection(){
        try {
            if(!connection.isClosed()){
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void statementExecute(String query){
        try {
            if (!connection.isClosed()) {
                (connection.createStatement()).executeUpdate(query);
            }
            else{
                System.out.println("No connection");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static ResultSet statementExecuteQuery(String query){
        try {
            if (!connection.isClosed()) {
                return (connection.createStatement()).executeQuery(query);
            }
            else{
                System.out.println("No connection");
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
