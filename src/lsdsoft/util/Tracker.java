package lsdsoft.util;

import java.util.*;
import lsdsoft.util.LasFile.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.*;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: Ural-Geo</p>
 *
 * @author lsdsoft
 * @version 1.0
 */
public class Tracker {
    public Vector points;
    protected int tracks;
    // for search
    protected int trkNum = -1;
    protected List searchItems = new ArrayList();
    private DecimalFormat df;

    public class RowComparator implements Comparator {
        private int track = 0;
        public RowComparator(int trk) {
            track = trk;
        }
        public void setTrack(int trk) {
            track = trk;
        }
        public int compare(Object obj1, Object obj2) {
            double[] row1 = (double[])obj1;
            double[] row2 = (double[])obj2;
            return (int)(row1[track]-row2[track]);
        }

    }
    private RowComparator rc = new RowComparator(0);

    public Tracker(int trck) {
        points = new Vector(128,128);
        tracks = trck;

    }
    //
    public Tracker(LasFile las) {
        long start = System.currentTimeMillis();
        df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
        df.applyPattern("#0.00");
        points = new Vector(128,128);
        LasSection section = las.getLastSection();
        // chacking amount of tracks
        String line = (String)section.lines.get(1);
        tracks = calcTracks(line);
        System.out.println( "## Creating tracker with " + tracks + " tracks" );
        int lineCount = section.lines.size();
        int i;
        for( i = 0; i < lineCount; i++) {
            appendData((String)section.lines.get(i));
        }
        long finish = System.currentTimeMillis();
        System.out.println( "## Created tracker with " + i + " rows (for " + (finish - start) + " ms)." );

    }

    public double[] getRow(int row) {
        if(row >= points.size() ) {
            return null;
        }
        return (double[])points.get(row);
    }
    public int getTrackCount() {
        return tracks;
    }
    public void selectTrackForSearch(int trk) {
        rc.setTrack(trk);
        trkNum = trk;
    }
    /**
     * поиск строки со значением в указанной колонке.
     * предположительно колонка отсортирована по возрастанию
     * @param value double
     * @param trk int
     * @return int
     */
    public int findRow( double value) {
        double[] val = new double[trkNum + 1];
        val[trkNum] = value;
        int res = Collections.binarySearch( points, val, rc );
        //System.out.println( res );
        return res;
    }
    public void sort(int trk) {
        Collections.sort(points, new RowComparator(trk));
    }

    private void appendData(String line) {
        String[] nums = line.split("\\s");
        double[] arr = new double[tracks];
        int index = 0;
        for( int i = 0; i < nums.length; i++) {
            if(nums[i].length() > 0) {
                try {
                    Number num = df.parse( nums[i] );
                    //System.out.println( num.doubleValue() );
                    arr[index++] = num.doubleValue();
                } catch ( ParseException ex ) {
                    System.err.println("Invalid number format in source line: " + line);
                }
            }
        }
        points.add(arr);

    }
    private int calcTracks(String line) {

        String[] nums = line.split("\\s");
        int count = 0;
        for( int i = 0; i < nums.length; i++) {
            if(nums[i].length() > 0) {
                try {
                    Number num = df.parse( nums[i] );
                    //System.out.println( num.doubleValue() );
                    count++;
                } catch ( ParseException ex ) {
                    System.err.println("Invalid number format in source line: " + line);
                }
            }
        }
        return count;
    }
}
