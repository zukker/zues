package lsdsoft.test;

import lsdsoft.mc.*;
import java.lang.*;
import java.io.FileInputStream;

/**

 */
public class TestDown {
  private ObjectFile hex;
  public TestDown() {
    hex = new IntelHexFile();
  }
  public void run() {
    try {
      hex.load(new FileInputStream("d:/projects/drivers/motordc/motordrv.hex"));
      hex.combineRecords();
    } catch (Exception e) { e.printStackTrace(); }
  }
  public static void main(String[] args) {
   TestDown test = new TestDown();
   test.run();
  }

}