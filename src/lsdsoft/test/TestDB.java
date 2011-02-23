package lsdsoft.test;

import lsdsoft.metrolog.mdb.*;
import java.sql.*;
import java.io.*;

public class TestDB {

  public TestDB() {
  }
  public void run() {
    MetrologDB db = new MetrologDB();
    try {
      db.connect("lsd", "some");
      Connection conn = db.getConnection();
      DatabaseMetaData meta = conn.getMetaData();
      System.out.println("Server: " + meta.getDatabaseProductName());
      System.out.println("Version: " + meta.getDatabaseProductVersion());
      System.out.println("Driver: " + meta.getDriverName());
      conn.close();
    } catch(Exception e) {
      System.err.print(e.getMessage());
    }
  }
}