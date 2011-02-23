package lsdsoft.test;

import org.apache.xerces.parsers.*;

public class tester{
  private static DOMParser parser;
  //private TestXml test1 = new TestXml();
  //private TestReg test2 = new TestReg();
  //private TestDB test3 = new TestDB();
  //private TestImmn73 test4 = new TestImmn73("d:/zeus/test/im73_043.clb");
  //private TestINS60 test5 = new TestINS60("d:/zeus/data/tables/ins60/ins-303.clb");
  //private TestApprox test6 = new TestApprox();
  private TestGraph testGr = new TestGraph();
  public tester() {
   //test1.run();
   //test2.run();
   //test3.run();
   //test6.run();
   testGr.run();
  }
  public static void main(String[] args) {
   parser = new DOMParser();
   tester tst = new tester();

  }
}
