package connection;

import java.io.*;
import java.sql.*;
import java.util.Properties;


public class ConnectionSingleton {
    private static Properties properties;
    private static final String propertiesPath = "src/main/resources/application.properties";
    private static Connection instance;

    /**
     * Loads the properties of the string such as url, username, and password
     */
    private static void loadProperties(){
        properties = new Properties();
        try{
            InputStream stream = new FileInputStream(new File(propertiesPath).getAbsolutePath());
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ConnectionSingleton(){

    }

    /**
     * Gets an instance of connection if one doesn't currently exist
     * @return If no connection exists, creates a connection, otherwise returns the current connection
     * @throws SQLException - Throws a sql exception if instance.isClosed() throws a sql exception
     */
    public static Connection getInstance() throws SQLException {
        if(properties == null){
            //loadProperties();
        }
        if(instance == null|| instance.isClosed()){
            try {
                Class.forName("org.postgresql.Driver");
                instance = DriverManager.getConnection(
                        "jdbc:postgresql://enterprise.cxxfrhuhokpa.us-east-1.rds.amazonaws.com:5432/postgres?currentSchema=aaron_litton_p1",
                        "p1ORMAdmin",
                        "toor123ORM123!");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        if(instance == null){
            System.out.println("Connection is still null");
        }
        return instance;
    }
}
