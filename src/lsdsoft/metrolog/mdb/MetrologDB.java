package lsdsoft.metrolog.mdb;
import java.sql.*;
import java.util.*;

public class MetrologDB {
  private Connection conn = null;
  public MetrologDB() {
  }
  public void connect(String user, String password) throws Exception{
    Properties info = new Properties();
    info.put("user", user);
    info.put("password", password);
    try {
    Class.forName("com.mysql.jdbc.Driver").newInstance();

    conn = DriverManager.getConnection("jdbc:mysql://localhost/MIS", info);
    }
    catch(Exception e) {
      throw new Exception("Error during connection to db", e);
    }
  }
  public Connection getConnection() {
    return conn;
  }
}