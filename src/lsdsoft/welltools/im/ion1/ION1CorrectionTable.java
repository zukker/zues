package lsdsoft.welltools.im.ion1;

import java.io.*;

import lsdsoft.zeus.*;
import java.util.*;
import lsdsoft.util.*;

/**
 * <p>Title: </p>
 * <p>Description:
 * At first try loading text format table, if false, load binary table.
 * Table loaded from path located by property 'ion1.path.tables'.
 * Zeus may have property 'ion1.table.format' for table saving output format.
 * Value mat be 'text' or 'binary'</p>
 * <p>Copyright: Copyright (c) 2004, 2005</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 1.1
 */
public class ION1CorrectionTable {
    private final static int ION_TABLE_SIZE = 1027 * 7;
    public final static int FORMAT_TEXT = 1;
    public final static int FORMAT_BINARY = 2;
    private int format;
    // значения алгоритмических углов датчиков Ax Ay Az Hxy Hyz Hzx
    protected int[] algorithmicAngle = new int[6];
    // нулевые коды датчиков
    protected int[] zeroCode = new int[6];
    // неперпендикулярность выставки датчика Az
    protected int[] nonprependicularityAz = new int[2];

    protected int[] table = new int[ION_TABLE_SIZE];
    private String number; // factory namber
    private boolean loaded = false;
    private String fileName = "";
    private Date date = new Date(); // date of table by deafaul is today
    private Zeus zeus = null;

    public ION1CorrectionTable() {
        number = "111111";
        zeus = Zeus.getInstance();
    }
    public ION1CorrectionTable(String num) {
        zeus = Zeus.getInstance();
        number = num;
    }
    /**
     * Формирование имени файла поправочной таблицы.
     * Формируется имя файла из заводского номера прибора и каталога, содержащего
     * все поправочные таблицы. Путь к этому каталогу указывается в свойствах Zeus с ключом
     * "ion1.path.tables"
     * @return Сформированное имя файла
     * @see lsdsoft.Zeus
     */
    private String buildFileName() {
        Properties props = new Properties();
        try {
            zeus.loadProperties( props, "ion1.properties" );
        } catch ( Exception ex ) {
            System.err.println("Unable to load 'ion1.properties'");
        }
        String fileName = props.getProperty("path.tables");
        String root;
        if(fileName.startsWith(".")){
        	root = zeus.getWorkDir();
        } else {
        	root = "";
        }
        File fn = new File(root, fileName);
        try {
			String ff = fn.getCanonicalPath();
			fileName = ff;
			
		} catch (IOException e) {
			System.err.println("#ERROR: invalid path to ion tables: " + fileName);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if( ! fileName.endsWith(File.separator) )
            fileName += File.separatorChar;
        fileName += number;
		System.out.println("#INFO: ion table file: "+ fileName);
        return fileName;
    }
    public void setNumber(String num) {
        number = num;
    }
    /**
     * Загрузка поправочной таблицы из файла. Имя файла формируется методом {@link #buildFileName buildFileName }
     * @throws Exception
     */
    public void load() throws Exception {
        loaded = false;
        String name = buildFileName();
        File file = new File(name);
        File filebin = new File(name + ".ion");
        if(file.exists()) {
            loadAsText( file.getCanonicalPath() );
            fileName = file.getCanonicalPath();
            date = new Date(file.lastModified());
        } else
            if(filebin.exists()) {
                loadAsBinary( filebin.getCanonicalPath() );
                fileName = filebin.getCanonicalPath();
                date = new Date(filebin.lastModified());
            } else {
                throw new Exception("Не удалось найти ни один из файлов:"+file.getCanonicalPath()+","+filebin.getCanonicalPath());
            }

    }
    /**
     * Load ION1 binary tables (*.ion )
     * File must be 1536 bytes long. Contains ????
     * @param fileName
     * @throws Exception
     */
    void loadAsBinary(String fileName) throws Exception {
        File file = new File(fileName);
        if(file.length() != 1536 ) {
            throw new Exception("Broken table:"+ fileName);
        }
        byte[] items = new byte[1536];
        FileInputStream ins = new FileInputStream(file);
        ins.read(items);
        String line = null;
        int index = 0;

        //while ((line = br.readLine()) != null) {
        //    br.
        //    StringTokenizer st = new StringTokenizer(line);
        //    while (st.hasMoreTokens()) {
        //        table[index] = Integer.parseInt(st.nextToken());
        //        index++;
        //    }
        //}
        loaded = false;
        format = FORMAT_TEXT;
    }
    /**
     *
     * @param fileName
     * @throws Exception
     */
    void loadAsText(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line = null;
        int index = 0;
        while ((line = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line);
            while (st.hasMoreTokens()) {
                table[index] = Integer.parseInt(st.nextToken());
                index++;
            }
        }
        br.close();
        for ( int i = 0; i < 6; i++ ) {
            algorithmicAngle[i] = table[i];
            zeroCode[i] = table[i + 7];
        }
        nonprependicularityAz[0] = table[6];
        nonprependicularityAz[1] = table[13];
        loaded = true;
        format = FORMAT_TEXT;
    }
    public void save() throws Exception {
        Zeus.getInstance().getProperty("ion1.table.format");
    }
    public void saveAsBinary() throws Exception {

    }
    public void saveAsText() throws Exception {

    }

    public String getNumber() {
        return number;
    }
    /**
     *
     * @return
     */
    public int[] getTable() {
        return table;
    }
    public String getFileName() {
        return fileName;
    }
    public String getDate() {
        return TextUtil.dateToString( date, "dd.MM.yyyy" );
    }
    public int getFormat() {
        return format;
    }
    public void setFormat( int form ) {
        if(form == FORMAT_TEXT || form == FORMAT_BINARY )
            format = form;
    }
    public boolean isLoaded() {
        return loaded;
    }
    public String toString() {
        return fileName + date.toString();
    }

}
