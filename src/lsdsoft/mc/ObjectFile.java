package lsdsoft.mc;

import java.io.InputStream;
import java.util.*;

public class ObjectFile {
  protected ArrayList records;
  ObjectFile() {
    records = new ArrayList(10);
  }

  public void combineRecords() {
    // find chain of records
    int first = 0;
    int last = first + 1;
    while(last < records.size()) {
      ObjectRecord rec1 = (ObjectRecord)(records.get(last - 1));
      ObjectRecord rec2 = (ObjectRecord)(records.get(last));
      int address1 = rec1.getAddress() + rec1.getLength();
      if( address1 == rec2.getAddress() ) {
        last++;
      } else {
        if(last - first > 1) { // found at least two "good" records
          // need combine recors from 'first' to 'last'-1
          rec1 = (ObjectRecord)(records.get(first));
          rec2 = (ObjectRecord)(records.get(last - 1));
          int newAddress = rec1.getAddress();
          int newLength = rec2.getAddress() - newAddress + rec2.getLength();
          ObjectRecord newRec = new ObjectRecord(ObjectRecord.TYPE_STANDART, newLength);
          byte[] newData = newRec.getData();
          int pos = 0;
          // copy data
          for(int i = first; i < last; i++) {
            rec2 = (ObjectRecord)(records.get(i));
            System.arraycopy(rec2.getData(), 0, newData, pos, rec2.getLength());
            pos += rec2.getLength();
            // remove records
          }
          for(int i = last - 1; i >= first; i--)
            records.remove(i);
          records.add(newRec);
          records.trimToSize();
        } else {
          first++;
        }
      }
    }
  }
  public void load(InputStream ins) throws Exception {

  }
}