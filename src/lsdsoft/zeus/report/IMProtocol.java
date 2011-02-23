package lsdsoft.zeus.report;

import java.io.*;
import lsdsoft.metrolog.*;
import java.util.*;
import lsdsoft.zeus.*;
import lsdsoft.util.*;
import java.text.*;

/**
 * <p>Title: </p>
 * <p>Description: ����� ��� ������������ ��������� ����������</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft, de-nos
 * @version 1.1
 */

public class IMProtocol {
    private static final String HEADER =
        "<html><meta charset='windows-1251'><head><style>\n" +
        "h2{margin: 0pt; font-size: 16pt;}\n"+
        "h3{margin: 10 0 0 0; font-size: 14pt;}\n"+
        "body{font-family:Arial;font-size: 12pt;}\n"+
        "table{margin: 1px; padding: 0px; border-style: solid; border-width: 1px; border-color: #000000; text-align: center;font-size: 11pt;}\n"+
        "th{padding: 2 5 2 5;border-style: solid; border-width: 1px; border-color: #000000;font-size: 10pt;font-weight: bold;background-color: #FFFFFF;margin: 0px;}\n"+
        "td{background-color: #FFFFFF;align:center;border-style: solid; border-width: 1px; border-color: #000000;}\n"+
        "</style></head>\n" +
        "<body><center><h3>$org$</h3><br>" +
        "<h2>��������</h2>" +
        "���������� ������� $tool.name$ N $tool.number$<br>����: $date$<br>" +
        "���� ����������� �������: $table.date$ </center>\n"
        ;
    private static final String ROTATE_HEADER =
        "<center><br>�� ������ �������� ����� (��� �������� ���� $zenith$&deg;), ����.<br></center>\n";
    private static final String ZENITH_HEADER =
        "<center><br>�� ������ �������� ����� (��� �������� ���� $rotate$&deg;), ����.<br></center>\n";
    private static final String AZIMUTH_HEADER =
        "<center><br>�� ������ ������������ ����� (��� �������� ���� $zenith$&deg; � �������� ���� $rotate$&deg;), ����.<br></center>\n";
    private static final String TABLE_HEADER = "<center><table width=$width$ cellspacing=0 align='center'><tr>" +
        "<th>����������<br>��������</th>" +
        "<th>���������<br>��������</th>" +
        "<th>������<br>���. ����.</th>" +
        "<th>�������<br>���. ����.</th>" +
        "<th>�����������<br>��������, %</th>" +
        "</tr>\n";
    private static final String TABLE_ROW =
        "<tr><td>$tool$</td><td>$accurate$</td><td>$delta$</td><td>&plusmn;$errorlimit$</td><td>$valid$</td></tr>\n";
    private static final String TABLE_FOOTER = "</table></center>\n";
    private static final String FOOTER =
        "<center><table width=$width$ style=\"border-width:0\"><tr><td style=\"border-width:0\"><p style=\"font-size:9pt; text-align:left;\">" +
        "����������� ������� ($temperature$ &plusmn; 1)&deg;C.<br>" +
        "���������� ��������� �� ��������, ���������� � �� �� ����������� $tool.name$, � �������������� ��������� ���-��-���, " +
        "��������������� ���� � ��������� ���������� �����������: &plusmn;$acc_delta_rotate$&deg; �� ������ �������� �����; " +
        "&plusmn;$acc_delta_zenith$&deg; �� ������ �������� �����; &plusmn;$acc_delta_azimuth$&deg; �� " +
        "������ ������������ �����.<br>���������� � ���������� ��������� $sertif_num$.</p></td></tr>" +
        "</table></center>" +

        "<center><table width=$width$ style=\"border-width:0\">" +
        "<tr><td style=\"border-width:0\"><p style=\"font-size:12pt; text-align:left;\">�����������</p></td>" +
        "<td style=\"border-width:0\"><p style=\"font-size:12pt; text-align:right;\">$operator$</p></td></tr></table></center>" +
        "</body></html>";
    private Writer outs;
    private Properties props = new Properties();
    public IMProtocol( Writer outs ) {
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

    protected void generateTable(MeasureTable table) throws Exception {
        if(table.isEmpty()) {
            return;
        }

        String s = ZENITH_HEADER;
        if(table.getProperty("type").equals("rotate")) {
            s = ROTATE_HEADER;
        }
        if(table.getProperty("type").equals("azimuth")) {
            s = AZIMUTH_HEADER;
        }
        if(table.getProperty("zenith").length() > 0) {
            props.setProperty("zenith", table.getProperty("zenith"));
        }
        if(table.getProperty("rotate").length() > 0) {
            props.setProperty("rotate", table.getProperty("rotate"));
        }
        outs.write( filter(s) );
        outs.write( filter(TABLE_HEADER) );
        for(int i = 0; i < table.size(); i++) {
            MeasureChain chain = table.getChain(i);
            if(chain.isEmpty()) {
                continue;
            }
            double errorlimit = chain.getToolErrorLimit();
            props.setProperty("errorlimit", formatNumber(errorlimit));
            props.setProperty("reprod", formatNumber(chain.getReproductionValue()));
            props.setProperty("tool", formatNumber(chain.getToolValue().value));
            props.setProperty("accurate", formatNumber(chain.getAccurateValue().value));
            props.setProperty("delta", formatNumber(chain.getDelta()));
            props.setProperty("sko", formatNumber(chain.getSKO()));
            props.setProperty("valid", chain.getValidString());
            outs.write( filter(TABLE_ROW) );
        }
        outs.write( filter(TABLE_FOOTER) );
    }

    public void generate( MeasureDatas datas ) throws Exception {
        WorkState state = datas.getWorkState();
        props.setProperty( "width", "540" );
        props.setProperty( "org", Zeus.getInstance().getProperty("org","�� ������ �����������") );
        props.setProperty( "operator", Zeus.getInstance().getProperty("operator_edited", "") );
        props.setProperty( "temperature", Zeus.getInstance().getProperty("temperature_edited", "") );
        props.setProperty( "tool.name", state.getToolName() );
        props.setProperty( "tool.number", state.getToolNumber() );
        props.setProperty( "table.date", datas.getProperties().getProperty("table.date","�� ��������") );
        props.setProperty( "date",
                           TextUtil.dateToString( state.getStartDate(), "dd.MM.yyyy" ) );
        props.setProperty("acc_delta_rotate", Zeus.getInstance().getProperty("rotate.delta", "1.0"));
        props.setProperty("acc_delta_zenith", Zeus.getInstance().getProperty("zenith.delta", "0.08"));
        props.setProperty("acc_delta_azimuth", Zeus.getInstance().getProperty("azimuth.delta", "0.5"));
        props.setProperty("sertif_num", Zeus.getInstance().getProperty("sertificate.number", "<�����������>"));
        outs.write( filter(HEADER) );
        for(int i = 0; i < datas.size(); i++) {
            generateTable(datas.getTable(i));
        }
        outs.write( filter(FOOTER) );
    }
}
