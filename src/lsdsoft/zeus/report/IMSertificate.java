package lsdsoft.zeus.report;

import java.io.*;
import org.apache.xerces.dom.*;
import org.w3c.dom.*;
import lsdsoft.metrolog.*;
import java.util.*;
import lsdsoft.zeus.*;
import lsdsoft.util.*;
import java.text.*;

/**
 * <p>Title: </p>
 * <p>Description: ����� ��� ������������ ����������� ����������</p>
 * <p>Copyright: Copyright (c) Ural-Geo 2004-2010</p>
 * @author lsdsoft, de-nos
 * @version 1.0
 */

public class IMSertificate {
    private static final String HEADER =
        "<html><meta charset='windows-1251'><head><style>\n" +
        "body{font-family:Arial;font-size: 12pt;}\n" +
        "h2{margin: 0pt; font-size: 16pt;}\n" +
        "h3{margin: 10 0 0 0; font-size: 14pt;}\n" +
        "table{margin: 1px; padding: 0px; border-style: solid; border-width: 1px; border-color: #000000; text-align: center;font-size: 11pt;}\n" +
        "th{padding: 2 5 2 5;border-style: solid; border-width: 1px; border-color: #000000;font-size: 10pt;font-weight: bold;background-color: #FFFFFF;margin: 0px;}\n" +
        "td{background-color: #FFFFFF;align:center;border-style: solid; border-width: 1px; border-color: #000000;}\n" +
        "</style></head>\n" +
        "<body><center><h3>���������� ������� ����������<br>" +
        "$org$</h3><br>" +
        "<h2>����������</h2>" +
        "� ���������� �������� ���������</center><br><br>\n" +
        "<center><table width=$width$ style=\"border-width:0\"><tr><td style=\"border-width:0\"><p style=\"font-size:11pt; text-align:left;\">"+
        "������������: <b>����������� $tool.name$.</b><br>" +
        "��������� �����: <b>$tool.number$</b></p></td></tr></table></center>" +
        "<center><h3>���������� ����������:</h3></center>";
    private static final String ROTATE_HEADER =
        "<center>�� ������ �������� ����� (��� �������� ���� $zenith$&deg;), � ��������.<br></center>\n";
    private static final String ZENITH_HEADER =
        "<center>�� ������ �������� �����, � ��������.<br></center>\n";
    private static final String AZIMUTH_HEADER =
        "<center>�� ������ ������������ ����� (��� �������� ���� $rotate$&deg;), � ��������.<br></center>\n";
    private static final String TABLE_HEADER_ROTATE =
        "<center><table width=$width$ cellspacing=0 align='center'><tr>" +
        "<th>����������<br>��������</th>" +
        "<th>���������<br>��������</th>" +
        "<th>������<br>���. ����.</th>" +
        "<th>�������<br>���. ����.</th>" +
        "</tr>\n";
    private static final String TABLE_HEADER_ZENITH =
        "<center><table width=$width$ cellspacing=0 align='center'><tr>" +
        "<th>��������<br>����</th>" +
        "<th>����������<br>��������</th>" +
        "<th>���������<br>��������</th>" +
        "<th>������<br>���. ����.</th>" +
        "<th>�������<br>���. ����.</th>" +
        "</tr>\n";
    private static final String TABLE_HEADER_AZIMUTH =
        "<center><table width=$width$ cellspacing=0 align='center'><tr>" +
        "<th>��������<br>����</th>" +
        "<th>����������<br>��������</th>" +
        "<th>���������<br>��������</th>" +
        "<th>������<br>���. ����.</th>" +
        "<th>�������<br>���. ����.</th>" +
        "</tr>\n";
    private static final String TABLE_ROW_ROTATE =
        "<tr><td>$tool$</td><td>$accurate$</td><td>$delta$</td><td>&plusmn;$errorlimit$</td></tr>\n";
    private static final String TABLE_ROW_ZENITH =
        "<tr><td>$rotate$</td><td>$tool$</td><td>$accurate$</td><td>$delta$</td><td>&plusmn;$errorlimit$</td></tr>\n";
    private static final String TABLE_ROW_AZIMUTH =
        "<tr><td>$zenith$</td><td>$tool$</td><td>$accurate$</td><td>$delta$</td><td>&plusmn;$errorlimit$</td></tr>\n";
    private static final String TABLE_FOOTER = "</table></center>\n";
    private static final String FOOTER =
        "<center><table width=$width$ style=\"border-width:0\"><tr><td style=\"border-width:0\"><p style=\"font-size:9pt; text-align:left;\">" +
        "����������� ������� ($temperature$ &plusmn; 1)&deg;C.<br>" +
        "���������� ��������� �� ��������, ���������� � �� �� ����������� $tool.name$, � �������������� ��������� ���-��-���, " +
        "��������������� ���� � ��������� ���������� �����������: &plusmn;$acc_delta_rotate$&deg; �� ������ �������� �����; " +
        "&plusmn;$acc_delta_zenith$&deg; �� ������ �������� �����; &plusmn;$acc_delta_azimuth$&deg; �� " +
        "������ ������������ �����.<br>���������� � ���������� ��������� $sertif_num$.</p></td></tr>" +

        "<tr><td style=\"border-width:0\"><p style=\"font-size:12pt; text-align:center;\">" +
        "<b>��������� ���������� �������� �� ������� $date_next$</b></center></p></td></tr>" +
        "</table></center>" +

        "<center><table width=$width$ style=\"border-width:0\">" +
        "<tr><td style=\"border-width:0\"><p style=\"font-size:12pt; text-align:left;\">��������</p></td>" +
        "<td style=\"border-width:0\"><p style=\"font-size:12pt; text-align:right;\">$operator$</p></td></tr></table></center>" +
        "<center><table width=$width$ style=\"border-width:0\">" +
        "<tr><td style=\"border-width:0\"><p style=\"font-size:12pt; text-align:left;\">����: $date$</p></td></tr>" +
        "</table></center>" +
        "</body></html>";
    Writer outs;
    Properties props = new Properties();
    Document doc = new DocumentImpl();
    public static int calibrationInterval = 3;
    public IMSertificate( Writer outs ) {
        this.outs = outs;
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

    public void generate( MeasureDatas datas ) throws Exception {
        WorkState state = datas.getWorkState();
        props.setProperty( "width", "540" );
        props.setProperty( "org", Zeus.getInstance().getProperty( "org", "" ) );
        props.setProperty( "operator",
                           Zeus.getInstance().getProperty( "operator_edited" ) );
        props.setProperty( "temperature",
                           Zeus.getInstance().getProperty( "temperature_edited" ) );
        props.setProperty( "tool.name", state.getToolName() );
        props.setProperty( "tool.number", state.getToolNumber() );
        props.setProperty( "acc_delta_rotate",
                           Zeus.getInstance().getProperty( "rotate.delta", "1.0" ) );
        props.setProperty( "acc_delta_zenith",
                           Zeus.getInstance().getProperty( "zenith.delta", "0.08" ) );
        props.setProperty( "acc_delta_azimuth",
                           Zeus.getInstance().getProperty( "azimuth.delta", "0.5" ) );
        props.setProperty( "sertif_num",
                           Zeus.getInstance().getProperty( "sertificate.number",
            "<�����������>" ) );
        props.setProperty( "date",
                           TextUtil.dateToString( state.getStartDate(), "dd.MM.yyyy" ) );
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime( state.getStartDate() );
        gc.add( GregorianCalendar.MONTH, calibrationInterval );
        props.setProperty( "date_next",
                           TextUtil.dateToString( gc.getTime(), "dd.MM.yyyy" ) );
        outs.write( filter( HEADER ) );

        generateTableRotate( datas );
        generateTableZenith( datas );
        generateTableAzimuth( datas );

        outs.write( filter( FOOTER ) );
    }
    // �������� ������� �� ������ ������������ �����.
    private void generateTableAzimuth(MeasureDatas datas) throws Exception{
        outs.write( filter(AZIMUTH_HEADER) );
        outs.write( filter(TABLE_HEADER_AZIMUTH) );
        MeasureTable table;
        for(int i = 0; i < datas.size(); i++) {
            table = datas.getTable(i);
            if (table.isEmpty()) continue;
            if (!table.getProperty("type").equals("azimuth")) continue;
            MeasureChain chain = table.getMaxDelta();

            if (chain.isEmpty()) continue;
            double errorlimit = chain.getToolErrorLimit();
            props.setProperty("errorlimit", formatNumber(errorlimit));
            props.setProperty("zenith", table.getProperty("zenith", "."));
            props.setProperty("rotate", table.getProperty("rotate", "."));
            props.setProperty( "reprod",
                               formatNumber( chain.getReproductionValue() ) );
            props.setProperty( "tool",
                               formatNumber( chain.getToolValue().value ) );
            props.setProperty( "accurate",
                               formatNumber( chain.getAccurateValue().value ) );
            props.setProperty( "delta", formatNumber( chain.getDelta() ) );
            props.setProperty( "sko", formatNumber( chain.getSKO() ) );
            props.setProperty( "valid", chain.getValidString() );
            if ( table.getProperty( "zenith" ).length() > 0 ) {
                props.setProperty( "zenith", table.getProperty( "zenith" ) );
            }
            outs.write( filter( TABLE_ROW_AZIMUTH ) );
        }
        outs.write( filter(TABLE_FOOTER) );
    }

    // �������� ������� �� ������ �������� �����.
    private void generateTableZenith(MeasureDatas datas) throws Exception{
        outs.write( filter(ZENITH_HEADER) );
        outs.write( filter(TABLE_HEADER_ZENITH) );
        MeasureTable table;
        for(int i = 0; i < datas.size(); i++) {
            table = datas.getTable(i);
            if (table.isEmpty()) continue;
            if (!table.getProperty("type").equals("zenith")) continue;
            MeasureChain chain = table.getMaxDelta();

            if (chain.isEmpty()) continue;
            double errorlimit = table.getChain(0).getToolErrorLimit();
            props.setProperty("errorlimit", formatNumber(errorlimit));
            props.setProperty("rotate", table.getProperty("rotate", "."));
            props.setProperty( "reprod",
                               formatNumber( chain.getReproductionValue() ) );
            props.setProperty( "tool",
                               formatNumber( chain.getToolValue().value ) );
            props.setProperty( "accurate",
                               formatNumber( chain.getAccurateValue().value ) );
            props.setProperty( "delta", formatNumber( chain.getDelta() ) );
            props.setProperty( "sko", formatNumber( chain.getSKO() ) );
            props.setProperty( "valid", chain.getValidString() );
            if ( table.getProperty( "zenith" ).length() > 0 ) {
                props.setProperty( "zenith", table.getProperty( "zenith" ) );
            }
            outs.write( filter( TABLE_ROW_ZENITH ) );
        }
        outs.write( filter(TABLE_FOOTER) );
    }

    // �������� ������� �� ������ �������� �����.
    private void generateTableRotate(MeasureDatas datas) throws Exception{
        MeasureTable table = datas.getTable( "rotate" );
        if( table == null) {
            return;
        }
        MeasureChain chain = table.getMaxDelta();
        if ( !chain.isEmpty() ) {
            double errorlimit = table.getChain(0).getToolErrorLimit();
            props.setProperty("errorlimit", formatNumber(errorlimit));
            props.setProperty( "reprod",
                               formatNumber( chain.getReproductionValue() ) );
            props.setProperty( "tool",
                               formatNumber( chain.getToolValue().value ) );
            props.setProperty( "accurate",
                               formatNumber( chain.getAccurateValue().value ) );
            props.setProperty( "delta", formatNumber( chain.getDelta() ) );
            props.setProperty( "sko", formatNumber( chain.getSKO() ) );
            props.setProperty( "valid", chain.getValidString() );
            if(table.getProperty("zenith").length() > 0) {
                props.setProperty("zenith", table.getProperty("zenith"));
            }
            outs.write( filter(ROTATE_HEADER) );
            outs.write( filter(TABLE_HEADER_ROTATE) );
            outs.write( filter(TABLE_ROW_ROTATE) );
            outs.write( filter(TABLE_FOOTER) );
        }
    }
}
