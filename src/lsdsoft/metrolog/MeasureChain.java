package lsdsoft.metrolog;

import java.util.*;
import lsdsoft.util.*;
import org.w3c.dom.*;
import java.text.*;

/**
 * <p>Title: ѕоследовательность замеров в одной точке</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class MeasureChain
    implements XMLStorable {
    private static final String TAG_REP = "rep";
    private static final String TAG_DELTA = "delta";
    private static final String TAG_SKO = "sko";
    private static final String TAG_TOOL_AVERAGE = "toolav";
    private static final String TAG_TOOL_CODE = "toolcode";
    private static final String TAG_ACC_AVERAGE = "accav";
    private static final String TAG_ACC_DELTA = "accdelta";
    private static final String TAG_TOOL_DELTA = "tooldelta";
    private static final String TAG_VALID = "valid";

    private static final String TAG_MEASURE = "measure";
    private static final String TAG_TOOL = "tool";
    private static final String TAG_ACCURATE = "acc";
    private static final String ATTR_TOOL = "tool";
    private static final String ATTR_ACCURATE = "acc";
    private static final String ATTR_CODE = "code";
    private static final String ATTR_ACC_DELTA = "accd";
    private static final String ATTR_TOOL_DELTA = "toold";
    private static final String ATTR_TIME = "time";

    private static final String TAG_TOOLAV = "toolav";

    private static final int MINIMUM_POINTS = 4;
    /**
     * ¬оспроизводимое значение (значение, которое желательно выставить)
     */
    protected double reproductionValue;
    /**
     * —реднее значение по эталону
     */
    protected Value accAverage = new Value();
    /**
     * —реднее значение по прибору
     */
    protected Value toolAverage = new Value();
    /**
     * —реднее значение показаний прибора в условных единицах
     */
    protected double codeAverage;
    /**
     * ќценка абсолютной погрешности (
     */
    protected double delta;
    protected double SKO;
    protected double quality;
    /**
     * Ќормированный предел погрешности в этой точке
     */
    protected double errorLimit = 0;

    protected Vector points = new Vector( MINIMUM_POINTS, MINIMUM_POINTS );

    public MeasureChain() {
    }
    public MeasureChain(int pointsCount) {
        points = new Vector(pointsCount);
        for(int i = 0; i < pointsCount; i++) {
            points.add(new MeasurePoint());
        }
    }
    /**
     * «адание значени€ нормированного предела абсолютной погрешности
     * @param limit
     */
    public void setToolErrorLimit(double limit) {
        errorLimit = Math.abs(limit);
    }
    public double getToolErrorLimit() {
        return toolAverage.delta;
    }

    /**
     * ѕроверка на годность прибора в этой точке
     * @return истина если прибор годен
     */
    public boolean isValid() {
        //return Math.abs(delta) < errorLimit;
        return Math.abs(delta) < toolAverage.delta;
    }
    /**
     * ¬ычисление веро€тности годности
     * @return процент веро€тности
     */
    public double getValid() {
        //final double acc_delta = 1.1;
        double ret;
        //System.out.println(accAverage.delta);
        ret = Math.abs(delta) - toolAverage.delta + accAverage.delta;
        ret /= accAverage.delta;
        ret = 100 - 50 * ret;
        return ret;
    }
    public String getValidString() {
        double valid = getValid();
        String valid_str;
        if (valid >= 100) {
            valid_str = "100";
        } else if (valid <= 0) {
            valid_str = "0";
        } else {
            valid_str = String.valueOf((int)Math.round(valid));
        }
        return valid_str;
    }
    public MeasurePoint getPoint(int index) {
        return (MeasurePoint) points.get(index);
    }
    public void addPoint() {
        points.add(new MeasurePoint());
    }
    public void ensureSize(int size) {
        int toAdd = size - points.size();
        if(toAdd > 0) {
            for(int i = 0; i < toAdd; i++) {
                points.add(new MeasurePoint());
            }
        }
    }
    public void calc() {
        int size = points.size();
        for(int i = 0; i < size; i++) {
            ((MeasurePoint) points.get(i)).calc();
        }
        calcAverage();
        calcSKO();
        //???????????
        if( toolAverage.delta != 0.0 ) {
            quality = ( Math.abs( delta ) + accAverage.delta -
                        toolAverage.delta ) /
                toolAverage.delta * 100.0;
        } else {
            quality = 0;
        }
        //delta = accurateAverage - toolAverage;
    }
    /**
     * ¬ычисление среднеквадратичного отклонени€
     */
    protected void calcSKO() {
        int size = points.size();
        SKO = 0;
        for ( int i = 0; i < size; i++ ) {
            MeasurePoint point = ( MeasurePoint ) points.get( i );
            if(point.isEmpty()) {
                continue;
            }
            double val = toolAverage.value - point.tool.value;
            val *= val;
            SKO += val;
        }
        SKO = Math.sqrt(SKO);
    }
    /** @todo separate calc average and delta */
    protected void calcAverage() {
        int size = points.size();
        double count = 0;
        double maxDelta = 0;
        accAverage.value = 0;
        accAverage.delta = 0;
        toolAverage.value = 0;
        toolAverage.delta = 0;
        codeAverage = 0;
        delta = 0;
        for ( int i = 0; i < size; i++ ) {
            MeasurePoint point = ( MeasurePoint ) points.get( i );
            if(point.isEmpty()) {
                continue;
            }
            accAverage.add( point.accurate );
            toolAverage.add( point.tool );
            codeAverage += point.toolCode;
            point.calc();
            count += 1.0;
            /*double d = point.getDelta();
            if(Math.abs(d) > maxDelta) {
                maxDelta = Math.abs(d);
                delta = d;
            } */
        }
        if ( count == 0 ) {
            return;
        }
        accAverage.value /= count;
        accAverage.delta /= count;

        toolAverage.value /= count;
        toolAverage.delta /= count;

        codeAverage /= count;
        delta = toolAverage.value - accAverage.value;
    }
    protected void calcDelta() {

    }
    public boolean isEmpty() {
        boolean empty = true;
        int size = points.size();
        for(int i = 0; i < size; i++) {
            empty = empty && ((MeasurePoint) points.get(i)).isEmpty();
        }
        return empty;
    }
    public Value getAccurateValue() {
        return accAverage;
    }
    public Value getToolValue() {
        return toolAverage;
    }
    public double getCodeAverage() {
        return codeAverage;
    }
    public double getReproductionValue() {
        return reproductionValue;
    }
    public void setReproductionValue( double value) {
        reproductionValue = value;
    }
    public double getDelta() {
        return delta;
    }
    public double getQuality() {
        return quality;
    }
    public double getSKO() {
        return SKO;
    }
    public int size() {
        return points.size();
    }
    public void load(Node parentNode) {
        String s = XMLUtil.getTextTag(parentNode, TAG_REP);
        try {
            reproductionValue = Double.parseDouble( s );
        } catch ( NumberFormatException ex ) {
            System.err.println(ex);
        }
        NodeList list = ((Element) parentNode).getElementsByTagName(TAG_MEASURE);
        int size = list.getLength();
        points.clear();
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        for(int i = 0; i < size; i++) {
            MeasurePoint point = new MeasurePoint();
            point.accurate = XMLUtil.getValueAttribute((Element)list.item(i), TAG_ACCURATE);
            //point.accurateValue = point.accurate.value;
            //point.accurateDelta = point.accurate.delta;
            point.tool = XMLUtil.getValueAttribute((Element)list.item(i), ATTR_TOOL);
            //point.toolValue = point.tool.value;
            //point.toolDelta = point.tool.delta;
            point.toolCode = (int)XMLUtil.getDoubleAttribute((Element)list.item(i), ATTR_CODE);
            String tm = XMLUtil.getStringAttribute( ( Element )
                    list.item( i ), ATTR_TIME );
            try {
                point.time = TextUtil.stringToDate( tm );
            } catch ( Exception ex1 ) {
                System.out.println( "#error parsing time:" + ex1.getLocalizedMessage() + " ("+ tm );

            }
            //point.toolDelta = (int)XMLUtil.getDoubleAttribute((Element)list.item(i), ATTR_TOOL_DELTA);
            points.add(point);
        }
    }
    /** —охранение значений в родительский узел
     * @param parentNode родительский узел
     */
    public void save(Node parentNode) {
        Document doc = parentNode.getOwnerDocument();
        Element elem;
        int size = points.size();
        XMLUtil.addTextTagAsDouble(parentNode, TAG_REP, reproductionValue );
        XMLUtil.addTextTagAsValue(parentNode, TAG_ACC_AVERAGE, accAverage );
        XMLUtil.addTextTagAsValue(parentNode, TAG_TOOL_AVERAGE, toolAverage );
        XMLUtil.addTextTagAsDouble(parentNode, TAG_TOOL_CODE, codeAverage );
        XMLUtil.addTextTagAsDouble(parentNode, TAG_DELTA, delta );
        XMLUtil.addTextTagAsDouble(parentNode, TAG_SKO, SKO );
        XMLUtil.addTextTag(parentNode, TAG_VALID, Boolean.toString( isValid() ) );

        for(int i = 0 ; i < size; i++) {
            MeasurePoint point = (MeasurePoint) points.get(i);
            if(point.isEmpty()) {
                continue;
            }
            elem = doc.createElement( TAG_MEASURE );
            XMLUtil.setValueAttribute( elem, ATTR_ACCURATE, point.accurate );
            XMLUtil.setValueAttribute( elem, ATTR_TOOL, point.tool );
            XMLUtil.setDoubleAttribute( elem, ATTR_CODE, point.toolCode );
            XMLUtil.setStringAttribute( elem, ATTR_TIME,
                                        TextUtil.dateToString( point.time ));

            parentNode.appendChild(elem);
        }
    }

}
