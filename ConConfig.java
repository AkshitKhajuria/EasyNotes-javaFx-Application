package sample;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConConfig {
    public static Connection getConnection()
    {
        Connection connection_object = null;
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            final String url = "jdbc:mysql://localhost:3306/easynotes";
            final String user = "root";
            final String password = "admin";
            connection_object = DriverManager.getConnection(url, user, password);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return connection_object;
    }
}
