package lsdsoft.zeus.report;


import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import org.apache.xerces.dom.*;
import org.w3c.dom.*;
import lsdsoft.metrolog.*;
import java.util.*;
import lsdsoft.zeus.*;
import lsdsoft.util.*;
import java.text.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.0
 * Used variables in templates:
 * $mode.name$ - название режима: "калибровки" или "градуировки"
 * $tool.name$ - название типа аппаратуры
 * $tool.number$ - заводской номер аппаратуры
 * $channel.name$ - название канала обработки ("ГК" или "НГК")
 * $date$ - дата начала работы
 * $num$ - номер точки по порядку
 * $code$ - значение выходного сигнала прибора (код, имп/мин)
 * $tool$ - значение физической величины по прибору
 * $accurate$ - эталонное значение
 * $delta$ - оценка абсолютной погрешности
 * $valid$ - признак годности
 * $back$ - значение фона по прибору (код или физ. величина)
 * $K$ - чувствительность прибора (имп/мин)/(мкР/час)
 *
 */

public class GKProtocol {
    private String HEADER = "<html><meta charset='windows-1251'><head><style>" +
        "body{font-family:Arial;font-size: 14pt;}"+
        "table{text-align: center;background-color: #000000;font-size: 14pt;}"+
        "th{font-size: 10pt;font-weight: bold;background-color: #FFFFFF;margin: 0px;}"+
        "td{background-color: #FFFFFF;align:center;}"+
        "</style></head>" +
        "<body><center><h2>ПРОТОКОЛ</h2><br>" +
        "калибровки прибора $tool.name$ №$tool.number$<br>" +
        "по каналу ГК на установке УАК-ГК №__<br>"+
        "Дата: $date$<br></center>";
    private static String TABLE_HEADER1 =
        "<center><table cellspacing=1 align='center' width='600px'><tr>" +
        "<th width='5%'>п/п</th>" +
        "<th width='20%'>N прибора,<br>имп/мин</th>" +
        "<th width='20%'>МЭД по прибору,<br>мкР/ч</th>" +
        "<th width='20%'>МЭД по эталону,<br>мкР/ч</th>" +
        "<th width='15%'>Оц. абс.<br>погр-сти,<br>мкР/ч</th>" +
        //"<th>Возм. выход<br>за допуск,<br>%</th>" +
        "<th>Признак<br>годности<br></th>" +
        "</tr>";
    private static final String TABLE_ROW =
        "<tr><td align='center'>$num$</td>" +
        "<td align='center'>$code$</td><td align='center'>$tool$</td>"+
        "<td align='center'>$accurate$</td><td align='center'>$delta$</td>" +
        //"<td align='center'>$vvd$</td></tr>";
        "<td align='center'>$valid$</td></tr>";
    private static final String TABLE_FOOTER =
        "</table></center>";
    private static final String BACK =
        "<center><br>Фон по прибору: $back$ имп/мин</center>";
    private static final String GAMMA =
        "<br><center>" +
        "Чувствительность прибора: <b>$K$</b> (имп/мин)/(мкР/ч)<br>" +
        "<br>Градуировочная характеристика имеет вид: МЭД = N/K, где<br>" +
        "N - выходной сигнал прибора; К - чувствительность прибора.<br>" +
        "</center>";
    private static String FOOTER =
        "<center>" +
        "<br><br>Дата следующей калибровки: ___.___.______<br>" +
        "<br><br><br>Калибровщик __________________ " +
        "$operator$</center></body></html>";
    private Writer outs;
    private Properties props = new Properties();
    private Document doc = new DocumentImpl();
    private WorkMode wm;


    public GKProtocol( Writer outs ) {
        this.outs = outs;
        String root = Zeus.getInstance().getRootDir();
        try {
            // load document header
            String fileName = root + "etc/gk.protocol.header";
            FileInputStream fs = new FileInputStream( fileName );
            byte[] b = new byte[fs.available()];
            fs.read(b);
            HEADER = new String(b);
            // load footer
            fileName = root + "etc/gk.protocol.footer";
            fs = new FileInputStream( fileName );
            b = new byte[fs.available()];
            fs.read(b);
            FOOTER = new String(b);
        } catch ( Exception ex ) {
        }
    }

    protected String filter( String template ) {
        StringBuffer buf = new StringBuffer( 128 );
        int size = template.length();
        for ( int i = 0; i < size; i++ ) {
            if ( template.charAt( i ) != '$' ) {
                buf.append( template.charAt( i ) );
            } else {
                int start = i + 1;
                do {
                    i++;
                }
                while ( i < size && template.charAt( i ) != '$' );
                if ( i < size && template.charAt( i ) == '$' ) {
                    String name = template.substring( start, i );
                    buf.append( props.getProperty( name, "" ) );
                }
            }
        }
        return buf.toString();
    }
    protected String formatNumber(double num) {
        NumberFormat nf = DecimalFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(num);
    }

    protected void generateTable(MeasureTable table) throws Exception {
        if(table.isEmpty()) {
            return;
        }
        double errorlimit = table.getChain(0).getToolErrorLimit();
        props.setProperty("errorlimit", formatNumber(errorlimit));
        //String s;
        outs.write( filter(TABLE_HEADER1) );
        for(int i = 0; i < table.size(); i++) {
            MeasureChain chain = table.getChain(i);
            if(chain.isEmpty()) {
                continue;
            }
            Value val = chain.getToolValue();
            String toolVal = (wm.getWorkMode() == WorkMode.MODE_CALIB)?
                val.toHtml(1):
                formatNumber(val.value);
            props.setProperty("num", formatNumber(chain.getReproductionValue()));
            props.setProperty("code", formatNumber(chain.getCodeAverage()));
            props.setProperty("tool", toolVal);
            props.setProperty("accurate", chain.getAccurateValue().toHtml(2));
            props.setProperty("delta", formatNumber(chain.getDelta()));
            props.setProperty("vvd", formatNumber(chain.getQuality()));
            props.setProperty("valid", chain.isValid()?"ГОДЕН":"<b>НЕ ГОДЕН</b>");
            outs.write( filter(TABLE_ROW) );

        }
        outs.write( filter(TABLE_FOOTER) );
    }
    public void generate( MeasureDatas datas ) throws Exception {
        WorkState state = datas.getWorkState();
        wm = state.getWorkMode();
        String toolChannel = state.getToolChannel();
        if(toolChannel == null) {
            toolChannel = "gk";
        }
        props.setProperty( "tool.name", state.getToolName() );
        props.setProperty( "tool.number", state.getToolNumber() );
        props.setProperty( "mode.name", (wm.getWorkMode() == WorkMode.MODE_CALIB)?"калибровки":"градуировки");
        props.setProperty( "channel.name", (toolChannel.equals("gk"))?"ГК":"НГК");
        props.setProperty( "date",
                           TextUtil.dateToString( state.getStartDate(), "dd.MM.yyyy" ) );
        MeasureChain ch = datas.selectTable("name","back").getChain(0);
        ch.calc();
        props.setProperty("back",
                          formatNumber( ch.getCodeAverage() ));
        //StringBuffer buf = new StringBuffer( 100 );
        //String s;
        outs.write( filter(HEADER) );
        //for(int i = 0; i < datas.size(); i++) {
        generateTable(datas.selectTable("name", "gk"));
        int K = (int)Double.parseDouble( datas.getProperties().getProperty("K",""));
        props.setProperty("K", String.valueOf(K));
        outs.write( filter(BACK) );
        outs.write( filter(GAMMA) );
        outs.write( filter(FOOTER) );
        //outs.write( s );

        /*
        TransformerFactory tFactory = TransformerFactory.newInstance();

        Transformer transformer = tFactory.newTransformer
            ( new StreamSource( "foo.xsl" ) );
        transformer.transform
            ( new StreamSource( "foo.xml" ),
              new StreamResult( new FileOutputStream( "foo.out" ) ) );
            */

    }
}
