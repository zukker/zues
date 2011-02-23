package lsdsoft.test;
import lsdsoft.metrolog.*;
import java.lang.*;

public class TestReg {
  private DataSource source;
  private Registrator tm251;
  public TestReg() {
    tm251 = new SpendMeterUralRegistrator();
    source = new RegistratorDataSource(tm251);
  }
  public void run() {
    try {
      for(int i = 0; i < 10; i++)
        System.out.println(tm251.getByte());
      //System.out.println(source.getData());
    } catch (Exception e) { e.printStackTrace(); }
  }
}