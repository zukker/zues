package lsdsoft.test;
import lsdsoft.metrolog.*;
import java.lang.*;
import java.awt.*;

public class TestGraph {
  private DataSource source;
  public TestGraph() {
  }
  public void run() {
    try {
        GraphicsEnvironment ge = GraphicsEnvironment.
  getLocalGraphicsEnvironment();
  GraphicsDevice[] gs = ge.getScreenDevices();
  for (int j = 0; j < gs.length; j++) {
     GraphicsDevice gd = gs[j];
     System.out.println("=== "+gd.getIDstring());
     System.out.println("=== "+gd.getIDstring());

     //GraphicsConfiguration[] gc = gd.getConfigurations();
         DisplayMode[] dm = gd.getDisplayModes();
         for(int l = 0; l<dm.length;l++) {
             System.out.print("Mode #"+l+": ");
             System.out.println( dm[l].getWidth()+"x"+dm[l].getHeight()+"x"+dm[l].getBitDepth() );
         }
         DisplayMode d1=new DisplayMode(640,480,8,60);
         gd.setDisplayMode(d1);

  }


    } catch (Exception e) { e.printStackTrace(); }
  }
}
