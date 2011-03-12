package lsdsoft.zeus.methods;


import java.lang.reflect.*;
import java.lang.Character;
import java.text.*;
import java.util.*;
import java.util.Timer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.lsdsoft.math.*;
import lsdsoft.metrolog.*;
import lsdsoft.metrolog.unit.*;
import lsdsoft.units.*;
import lsdsoft.util.*;
import lsdsoft.zeus.*;
import lsdsoft.zeus.ui.*;


/**
 * <p>Title: Градуировка приборов ИОН-1</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 * @todo: split methods viewer on MethodsPerformer and Viewer
 */

public class ION1ManualGradViewer
    extends AbstractMethodsViewer
    implements SignalEventListener, ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6365381020066402573L;


	class TreeNodeVector<E> extends Vector<E> {
		  /**
		 * 
		 */
		private static final long serialVersionUID = 2313508365282017221L;
		String name;

		  TreeNodeVector(String name) {
		    this.name = name;
		  }

		  TreeNodeVector(String name, E elements[]) {
		    this.name = name;
		    for (int i = 0, n = elements.length; i < n; i++) {
		      add(elements[i]);
		    }
		  }

		  public String toString() {
		    return "[" + name + "]";
		  }
		}



	/*
	void execCommand(ION1GradCommand.Command command) {
		final Class[] params = {};
		try {
			Method method = this.getClass().getMethod(command.method, params);
			commandArgument1 = command.arg1;
			commandArgument2 = command.arg2;
			if (method != null) {
			    method.invoke(viewer, (Object[]) null);
			}
		} catch (Exception e) {
			addMessage("Ошибка выполнения команды: " + command.description);
			e.printStackTrace();
		} 
	}
	*/
	Vector<ION1GradCommand.Command> gradAz = new TreeNodeVector<ION1GradCommand.Command>(
			"Градуировка оси Az-Hxy", ION1GradCommand.commandsAz);
	Vector<ION1GradCommand.Command> gradAx = new TreeNodeVector<ION1GradCommand.Command>(
			"Градуировка оси Ax-Hyz", ION1GradCommand.commandsAx);
	Vector<ION1GradCommand.Command> gradAy = new TreeNodeVector<ION1GradCommand.Command>(
			"Градуировка оси Ay-Hzx", ION1GradCommand.commandsAy);
	Vector<ION1GradCommand.Command> gradZeroAx = new TreeNodeVector<ION1GradCommand.Command>(
			"Определение нулевых кодов датчиков Ax, Ay...",
			ION1GradCommand.commandsZeroAx);
	Vector<ION1GradCommand.Command> gradErrorAz = new TreeNodeVector<ION1GradCommand.Command>(
			"Определение погрешности датчика Az",
			ION1GradCommand.commandsErrorAz);

	//Object[] ion1GradSteps={gradAz, gradAx, gradAy, gradZeroAx, gradErrorAz};
	Object[] ion1GradSteps = {
			new TreeNodeVector<ION1GradCommand.Command>(
					"Градуировка оси Az-Hxy", ION1GradCommand.commandsAz),
			new TreeNodeVector<ION1GradCommand.Command>(
					"Градуировка оси Ax-Hyz", ION1GradCommand.commandsAx),
			new TreeNodeVector<ION1GradCommand.Command>(
					"Градуировка оси Ay-Hzx", ION1GradCommand.commandsAy),
			new TreeNodeVector<ION1GradCommand.Command>(
					"Определение нулевых кодов датчиков Ax, Ay...",
					ION1GradCommand.commandsZeroAx),
			new TreeNodeVector<ION1GradCommand.Command>(
					"Определение погрешности датчика Az",
					ION1GradCommand.commandsErrorAz) };
    ///////////////////////////////////////////////////////////////////////////
    ION1ManualGradViewer viewer = this;
    Zeus zeus = Zeus.getInstance();
    //String[][] actions={{"run", "doRun"},{"stop", "doStop"},{"config","doConfig"}};
    //Hashtable<String,String> actionsTable = new
    // index of sensor values in channel source
    static final int ION1_AX = 12;
    static final int ION1_AY = 13;
    static final int ION1_AZ = 14;
    static final int ION1_HXY = 15;
    static final int ION1_HYZ = 16;
    static final int ION1_HZX = 17;
    // набег
    static final int ION1_AXN = 6;
    static final int ION1_AYN = 7;
    static final int ION1_AZN = 8;
    static final int ION1_HXYN = 9;
    static final int ION1_HYZN = 10;
    static final int ION1_HZXN = 11;
    static final char DEGREE_SIGN = '\u00B0';
    static final char PLUS_MINUS_SIGN = '\u00B1';

    double commandArgument1, commandArgument2;
    double ion1Sensors[]= new double[20];
    int selectedCommand = 1;
    private Timer buttonTimer = new Timer();
    private RemainderTask task = new RemainderTask( );

    DecimalFormat angleFormat = new DecimalFormat();
    static int FILTER_SIZE = 9;
    MedianFilter mfilter1 = new MedianFilter( FILTER_SIZE );
    MedianFilter mfilter2 = new MedianFilter( FILTER_SIZE );
    UAKSI2CheckUnit unit = null;
    InclinometerAngles accAngles = new InclinometerAngles();
    ChannelDataSource toolSource = null;
    DigitalDisplay displayAX = new DigitalDisplay( 6, 2 );
    DigitalDisplay displayAY = new DigitalDisplay( 6, 2 );
    DigitalDisplay displayAZ = new DigitalDisplay( 6, 2 );
    ImageIcon iconRun = Zeus.createImageIcon( "images/play.24.png" );
    ImageIcon iconStop = Zeus.createImageIcon( "images/stop_red.png" );
    ImageIcon iconNext = Zeus.createImageIcon( "images/next.24.png" );
    ImageIcon iconConfig = Zeus.createImageIcon( "images/gear.24.png" );
    

    CommandExecuter executer;
    // панель показаний УАКСИ
    JPanel panelDisplay = new JPanel();
    JLabel lZenith = new JLabel();
    JLabel lRotate = new JLabel();
    JLabel lAzimuth = new JLabel();
    JImage imgDigAX = new JImage( displayAX.getImage() );
    JImage imgDigAY = new JImage( displayAY.getImage() );
    JImage imgDigAZ = new JImage( displayAZ.getImage() );
    TitledBorder titledBorderAcc;
    //
    Border lineBorder; // black 1px line border
    // панель шагов методики
    JPanel panelSteps = new JPanel();
    JTree treeSteps;
    // панель показаний прибора ИОН1
    JPanel panelIon1 = new JPanel();
    JTextArea logger = new JTextArea();
    TitledBorder titledBorder3;
    ButtonGroup bgWhat = new ButtonGroup();
    TitledBorder titledBorder4;
    JLabel jLabel9 = new JLabel();
    JButton bConfig = new JButton();
    JButton bRun = new JButton();
    JButton bStop = new JButton();
    JButton bNext = new JButton();
    JLabel lMessage1 = new JLabel();
    //JLabel jLabel12 = new JLabel();
    //JLabel jLabel13 = new JLabel();
    //JLabel jLabel14 = new JLabel();
    //JLabel jLabel15 = new JLabel();
    //JLabel jLabel16 = new JLabel();
    //JLabel jLabel17 = new JLabel();
    JLabel lIon1[]=new JLabel[24];
    //JLabel lcAx = new JLabel();
    //JLabel jLabel19 = new JLabel();
    //JLabel jLabel110 = new JLabel();
    //JLabel jLabel111 = new JLabel();
    //JLabel jLabel112 = new JLabel();
    //JLabel jLabel113 = new JLabel();
    //JLabel jLabel114 = new JLabel();
    //JLabel jLabel115 = new JLabel();
    //JLabel jLabel116 = new JLabel();
    //JLabel jLabel117 = new JLabel();
    //JLabel jLabel118 = new JLabel();
    //JLabel jLabel119 = new JLabel();
    //JLabel jLabel1110 = new JLabel();
    //JLabel jLabel120 = new JLabel();
    //JLabel jLabel1111 = new JLabel();
    //JLabel jLabel121 = new JLabel();
    //JLabel jLabel122 = new JLabel();
    //JLabel jLabel123 = new JLabel();
    JLabel lAx1 = new JLabel();
    JLabel lAy1 = new JLabel();
    JLabel lAz1 = new JLabel();
    JLabel lAx2 = new JLabel();
    JLabel lAy2 = new JLabel();
    JLabel lAz2 = new JLabel();
    JLabel lAx = new JLabel();
    JLabel lAy = new JLabel();
    JLabel lAz = new JLabel();
    JLabel lAxn = new JLabel();
    JLabel lAyn = new JLabel();
    JLabel lAzn = new JLabel();
    JLabel lHxy1 = new JLabel();
    JLabel lHyz1 = new JLabel();
    JLabel lHzx1 = new JLabel();
    JLabel lHxy2 = new JLabel();
    JLabel lHyz2 = new JLabel();
    JLabel lHzx2 = new JLabel();
    JLabel lHxy = new JLabel();
    JLabel lHyz = new JLabel();
    JLabel lHzx = new JLabel();
    JLabel lHxyn = new JLabel();
    JLabel lHyzn = new JLabel();
    JLabel lHzxn = new JLabel();



    public ION1ManualGradViewer() {
        try {
            //jbInit();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void setProperties( Properties props ) {
        properties = ( Properties ) props.clone();
    }
    /**
     * Выполняет метод класса по заданной команде.
     * формирует имя метода путем добавления к имени команды
     * "command" и делая первую букву команды заглавной.
     * Например для команды "stop" будет выдзван метод "commandStop" 
     * @param command
     */
    public void execCommand(String command){
    	String mname;
    	char c = Character.toUpperCase(command.charAt(0));
    	mname = "command" + c + command.substring(1);
    	System.out.println("Exec method: " + mname);
    	execMethod(mname);
    }
    
    public void execMethod(String methodName){
    	try {
			Method method = viewer.getClass().getMethod( methodName, (Class[])null );
			if ( method != null ) {
			    method.invoke( viewer, (Object[])null );
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.err.println("Метод не определен: " + methodName+"()");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void actionPerformed(ActionEvent e) {
    	String cmd = e.getActionCommand();
    	execCommand(cmd);
      }

    public void commandRun() {
    	System.out.println("Executing commandRun()");
    	DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        treeSteps.getLastSelectedPathComponent();

    	if (node == null) return;

    	Object nodeInfo = node.getUserObject();
    	if (node.isLeaf()) {
    		ION1GradCommand.Command cmd = (ION1GradCommand.Command)nodeInfo;
    		System.out.println(cmd.method);
    		execCommand(cmd);
    	} 
    }
    
    public void commandStop() {
    	System.out.println("Executing commandStop()");
    	if ( executer != null ) {
            if ( executer.isAlive() ) {
                try {
                    unit.stop();
                } catch ( Exception ex ) {
                }
                //executer.interrupt();
                executer.stop();
                //executer.destroy();

            }
        }
    	
    }
    public void commandConfig() {
    	System.out.println("Executing commandConfig()");
    	// TODO запуск обозревателей через пул 
    	zeus.startViewer("uaksi2","","tune");
    }
    static final int PLANE_AX = 3;
    static final int PLANE_AY = 4;
    static final int PLANE_AZ = 5;
    
    
    public void goTo(int plane, double point, double acc) {
    	final String[] names={"Азимут", "Зенит", "Визир", "Ax", "Ay", "Az"};
    	final char[] planes={'x', 'y', 'z', 'a', 'b', 'c'};
		String mess = "Выход на точку: " + names[plane] + " "
				+ point + DEGREE_SIGN + PLUS_MINUS_SIGN
				+ (int)acc + "'";
    	addMessage(mess);
    	double uaksiPosition;
    	try {
    		if(plane>2){
    			goToSensor(plane, point, acc);
    		} else {
    			acc /= 60.0;
    			do {
    				uaksiPosition = unit.getPosition(planes[plane]);
    				if(Math.abs(point-uaksiPosition)<=acc) {
    					break;
    				}
    				unit.goToPoint(planes[plane], point);
    				doWaitComplete( planes[plane] );
    				Util.delay(400);
    			}while(true);
    		}
		} catch (Exception e) {
			e.printStackTrace();
			addMessage("Ошибка: "+e.getMessage());
		}
    }

    public void goToSensor(int plane, double point, double acc) {
    	final int sensIndex[] = {0, 0, 0, ION1_AX, ION1_AY, ION1_AZ };
    	final char uaksiPlanes[]= {'x', 'y', 'z', 'y', 'y', 'z'};
    	final byte positiveDir[]={ 1, 1, 1, 1, -1, 1, };
    	byte dir = positiveDir[plane];
    	char uaksiPlane = uaksiPlanes[plane];
    	int sensorIndex = sensIndex[plane];
    	double uaksiPosition = 0;
    	
    	acc /= 60.0; // переводим из минут в доли градуса
		do {
			double sens = ion1Sensors[sensorIndex];
			uaksiPosition = unit.getPosition(uaksiPlane);
			double delta = point - sens;
			double absD = Math.abs(delta);
			double delta3 = point - (sens-360.0);
			double absD3 = Math.abs(delta3);
			double dl = Math.min(absD3, absD);
			if (dl<acc) {
				break;
			}
			double newPos = uaksiPosition + dl;
			//char dir = 
			if (absD > absD3) {
				newPos = uaksiPosition + dir * delta3;
			} else {
				newPos = uaksiPosition + dir * delta;
			}
			System.out.println("S="+formatAngle(sens)+
					"; delta=" + formatAngle(delta)+
					"; delta3=" + formatAngle(delta3)+
					"; mdl=" + formatAngle(dl)+
					"; pos=" + formatAngle(uaksiPosition)+
					"; newpos=" + formatAngle(newPos)
					);
			try {
				//unit.setRotateDirection(dir);
				unit.goTo(uaksiPlane, newPos);
				doWaitComplete(uaksiPlane);
				//unit.rotate(uaksiPlane, speed);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				addMessage("Ошибка: "+e.getMessage());
			}
			Util.delay(2200);
			//delta = point - sens;
		} while (true);

    }
    public void goToAzimuth() {
    	goTo(0, commandArgument1, commandArgument2);
     }
    public void goToZenith() {
    	goTo(1, commandArgument1, commandArgument2);
    }
    public void goToAbsZenith() {
    	InclinometerAngles ang = unit.getAccurateDelta();
        ang.zenith.setAngle( 0 );
    	goTo(1, commandArgument1, commandArgument2);
    }
    public void goToVizir() {
    	goTo(2, commandArgument1, commandArgument2);
    }

    
    public void goToSensorAx() {
    	goTo(PLANE_AX, commandArgument1, commandArgument2);
    }
    public void goToSensorAy() {
    	goTo(PLANE_AY, commandArgument1, commandArgument2);
    }
    public void goToSensorAz() {
    	goTo(PLANE_AZ, commandArgument1, commandArgument2);
    }
        
    public void addMessage(String message){
    	logger.append(message + "\n");
    	logger.setCaretPosition(logger.getText().length());
    }

    public void doJoinVizirAz(){
    	addMessage("Совмещение визира с показанием датчика Az");
    	InclinometerAngles ang = unit.getAccurateDelta();
        ang.rotate.setAngle( 0 );
        Util.delay( 200 );
        double val = ion1Sensors[ION1_AZ];
        if(Math.abs(360 - val)<val) {
        	val -= 360;
        }
        ang.rotate.setAngle( accAngles.rotate.getValue()-val );
    }
    public void doJoinZenithAx(){
    	addMessage("Совмещение зенита с показанием датчика Ax");
    	InclinometerAngles ang = unit.getAccurateDelta();
        ang.zenith.setAngle( 0 );
        Util.delay( 200 );
        double val = ion1Sensors[ION1_AX];
        if(Math.abs(360 - val)<val) {
        	val -= 360;
        }
        ang.zenith.setAngle( accAngles.zenith.getValue()-val );
    }

    public void doJoinZenithAy(){
    	addMessage("Совмещение зенита с показанием датчика Ax");
    	InclinometerAngles ang = unit.getAccurateDelta();
        ang.zenith.setAngle( 0 );
        Util.delay( 200 );
        double val = ion1Sensors[ION1_AX];
        if(Math.abs(360 - val)<val) {
        	val -= 360;
        }
        ang.zenith.setAngle( accAngles.zenith.getValue()-val );
    }

    private void redrawAccurateValues() {
        if ( unit.isConnected() ) {
            displayAX.render( accAngles.azimuth.getValue() );
            displayAY.render( accAngles.zenith.getValue() );
            displayAZ.render( accAngles.rotate.getValue() );
        } else {
            displayAX.renderClear();
            displayAY.renderClear();
            displayAZ.renderClear();
        }
        imgDigAX.repaint();
        imgDigAY.repaint();
        imgDigAZ.repaint();
    }
    /**
     *
     */

    public String formatAngle( double value ) {
    	//long seconds = Math.round(value*3600.0);
    	long minutes = Math.round(value*60.0);
        String str = String.valueOf(minutes/60)+DEGREE_SIGN+(minutes%60) + '\'';
        return str;
    }

    public void refreshToolValues() {
        Channel chan;
        chan = toolSource.getChannel( "sensors" );
        if( chan != null ) {
            lAx1.setText( Integer.toString(chan.getValue(0).getAsInteger()));
            lAx2.setText( Integer.toString(chan.getValue(1).getAsInteger()));
            lAy1.setText( Integer.toString(chan.getValue(2).getAsInteger()));
            lAy2.setText( Integer.toString(chan.getValue(3).getAsInteger()));
            lAz1.setText( Integer.toString(chan.getValue(4).getAsInteger()));
            lAz2.setText( Integer.toString(chan.getValue(5).getAsInteger()));
            lHxy1.setText( Integer.toString(chan.getValue(6).getAsInteger()));
            lHxy2.setText( Integer.toString(chan.getValue(7).getAsInteger()));
            lHyz1.setText( Integer.toString(chan.getValue(8).getAsInteger()));
            lHyz2.setText( Integer.toString(chan.getValue(9).getAsInteger()));
            lHzx1.setText( Integer.toString(chan.getValue(10).getAsInteger()));
            lHzx2.setText( Integer.toString(chan.getValue(11).getAsInteger()));
            //lAx1.setText( Integer.toString(chan.getValue(0).getAsInteger()));
            //lAx1.setText( Integer.toString(chan.getValue(0).getAsInteger()));
        }

        chan = toolSource.getChannel( "angles2" );
        if( chan != null ) {
        	for(int i=0; i<18; i++){
        		ion1Sensors[i]=chan.getValue(i).getAsDouble();
        	}
        	ion1Sensors[ION1_AX]= 360.0 - ion1Sensors[ION1_AX];
        	ion1Sensors[ION1_AY]= 360.0 - ion1Sensors[ION1_AY];
        	
        	updateIon1();
        	/*
            lAx.setText( formatAngle(ion1Sensors[12]));
            lAy.setText( formatAngle(chan.getValue(13).getAsDouble()));
            lAz.setText( formatAngle(chan.getValue(14).getAsDouble()));
            lAxn.setText( formatAngle(chan.getValue(6).getAsDouble()));
            lAyn.setText( formatAngle(chan.getValue(7).getAsDouble()));
            lAzn.setText( formatAngle(chan.getValue(8).getAsDouble()));
            lHxy.setText( formatAngle(chan.getValue(15).getAsDouble()));
            lHyz.setText( formatAngle(chan.getValue(16).getAsDouble()));
            lHzx.setText( formatAngle(chan.getValue(17).getAsDouble()));
            lHxyn.setText( formatAngle(chan.getValue(9).getAsDouble()));
            lHyzn.setText( formatAngle(chan.getValue(10).getAsDouble()));
            lHzxn.setText( formatAngle(chan.getValue(11).getAsDouble()));
			*/
        }

    }
    public void signalEvent( SignalEvent ev ) {
        SignalSource src = ev.getSource();
        if ( src.equals( toolSource ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                refreshToolValues();
            }
            if ( ev.getSignal() == SignalEvent.SIG_TIMEOUT ) {
                UiUtils.showError( this, "Нет связи с прибором" );
            }

        } else
        if ( src.equals( unit ) ) {
            if ( ev.getSignal() == SignalEvent.SIG_DATA_ARRIVED ) {
                Channel chan = unit.getChannel( "angles" );
                if ( chan != null ) {
                    accAngles.azimuth.setAngle( chan.getValue( 0 ).getAsDouble() );
                    accAngles.zenith.setAngle( chan.getValue( 1 ).getAsDouble() );
                    accAngles.rotate.setAngle( chan.getValue( 2 ).getAsDouble() );
                    redrawAccurateValues();
                }

            }

        }

    }

    public void start() {
        boolean quit = false;
        try {
            Zeus zeus = Zeus.getInstance();
            workState.setWorkMode( zeus.getWorkMode() );
            unit = (UAKSI2CheckUnit)DataFactory.createCheckUnit();
            unit.addSignalListener( this );
            try {
                toolSource = DataFactory.createToolDataSource();
            } catch ( Exception ex ) {
                UiUtils.showError( this,
                                   ex.getLocalizedMessage() );
            }
            if ( toolSource == null ) {
                int res = UiUtils.showConfirmError( this,
                    "<html><center>Не удалось создать источник данных прибора" +
                    "<br>Продолжить?" );
                quit = res != 0;
            } else {
                toolSource.addSignalListener( this );
            }
            jbInit();


            UiUtils.toScreenCenter( this );
            redrawAccurateValues();
        } catch ( Exception ex ) {
            UiUtils.showError( this, ex.getLocalizedMessage() );
            ex.printStackTrace();
        }
        if ( !quit ) {
            this.setVisible( true );
        }
    }

    public void updateIon1() {
    	lAx.setText(formatAngle(ion1Sensors[ION1_AX]));
    	lAy.setText(formatAngle(ion1Sensors[ION1_AY]));
    	lAz.setText(formatAngle(ion1Sensors[ION1_AZ]));
    	lHxy.setText(formatAngle(ion1Sensors[ION1_HXY]));
    	lHyz.setText(formatAngle(ion1Sensors[ION1_HYZ]));
    	lHzx.setText(formatAngle(ion1Sensors[ION1_HZX]));
    	lAxn.setText(formatAngle(ion1Sensors[ION1_AXN]));
    	lAyn.setText(formatAngle(ion1Sensors[ION1_AYN]));
    	lAzn.setText(formatAngle(ion1Sensors[ION1_AZN]));
    	lHxyn.setText(formatAngle(ion1Sensors[ION1_HXYN]));
    	lHyzn.setText(formatAngle(ion1Sensors[ION1_HYZN]));
    	lHzxn.setText(formatAngle(ion1Sensors[ION1_HZXN]));
    }
    
    private void jbInit() throws Exception {
        this.getContentPane().setLayout( null );
        this.setSize( 680, 605 );
        this.setResizable(false);
        this.setState( Frame.NORMAL );
        this.setTitle( "Градуировка прибора " + workState.getToolName() + " № " +
                       workState.getToolNumber());
        lineBorder = BorderFactory.createLineBorder( Color.black, 1 );

        //this.setExtendedState( 6 );
        //tabbedTables.addTab("Угол поворота", table);
        Font font = new Font( "Dialog", 0, 14 );
        // init tableX
        //scrollPaneY.setAutoscrolls( true );
        //scrollPaneY.setDebugGraphicsOptions( 0 );
        // панель показаний УАКСИ
        titledBorderAcc = new TitledBorder( lineBorder, "Показания УАКСИ" );
        panelDisplay.setLayout(null);
        panelDisplay.setBorder( titledBorderAcc );
        panelDisplay.setBounds( new Rectangle( 400, 10, 266, 150 ) );
        lAzimuth.setText( "АЗИМУТ" );
        lAzimuth.setBounds( new Rectangle( 10, 30, 60, 15 ) );
        lAzimuth.setHorizontalAlignment( SwingConstants.RIGHT );
        
        lZenith.setHorizontalAlignment( SwingConstants.RIGHT );
        lZenith.setText( "ЗЕНИТ" );
        lZenith.setBounds( new Rectangle( 10, 70, 60, 15 ) );
        lRotate.setHorizontalAlignment( SwingConstants.RIGHT );
        lRotate.setText( "ВИЗИР" );
        lRotate.setBounds( new Rectangle( 10, 110, 60, 15 ) );

        imgDigAX.setBounds( new Rectangle( 90, 20, 162, 35 ) );
        imgDigAY.setBounds( new Rectangle( 90, 60, 162, 35 ) );
        imgDigAZ.setBounds( new Rectangle( 90, 100, 162, 35 ) );
        panelDisplay.add( lRotate, null );
        panelDisplay.add( lZenith, null );
        panelDisplay.add( lAzimuth, null );
        panelDisplay.add( imgDigAY, null );
        panelDisplay.add( imgDigAX, null );
        panelDisplay.add( imgDigAZ, null );
        this.getContentPane().add( panelDisplay, null );
        // ---- конец.панель показаний УАКСИ
        // панель показаний ИОН1
        panelIon1.setLayout(null);
        panelIon1.setBorder( new TitledBorder( lineBorder, "Показания ИОН1" ) );
        panelIon1.setBounds( new Rectangle( 400, 205, 266, 155 ) );
        this.getContentPane().add( panelIon1, null );
        // ---- конец.панель показаний ИОН-1
        bConfig.setBounds(new Rectangle(465, 165, 200, 36) );
        bConfig.setText( "Настройка УАК-СИ..." );
        bConfig.setIcon(iconConfig);
        bConfig.setActionCommand( "config" );
        bConfig.addActionListener( this );
        this.getContentPane().add( bConfig, null );
        
     // панель методики
        treeSteps = new JTree(ion1GradSteps);
		treeSteps.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
        //treeSteps.setPreferredSize(new Dimension(350, 200));
        panelSteps.setLayout( new BorderLayout());
        panelSteps.setBorder( new TitledBorder( lineBorder, "Шаги методики" ) );
        panelSteps.setBounds( new Rectangle( 10, 10, 375, 350 ) );
        panelSteps.add( new JScrollPane(treeSteps) );
        this.getContentPane().add( panelSteps, null );
        JPanel jp1 = new JPanel();
        jp1.setLayout(new BorderLayout());
        panelSteps.add(jp1, BorderLayout.SOUTH);
        // -------
        bRun.setPreferredSize(new Dimension(150,36));
        bRun.setText( "Выполнить" );
        bRun.setIcon(iconRun);
        bRun.addActionListener( this );
        bRun.setActionCommand( "run" );
        //bRun.setEnabled(false);
        jp1.add(bRun, BorderLayout.EAST);
        // --------
        bStop.setPreferredSize(new Dimension(150,36) );
        bStop.setText( "Останов" );
        bStop.setIcon(iconStop);
        bStop.setActionCommand("stop");
        bStop.addActionListener(this);
        bStop.setEnabled(false);
        jp1.add(bStop, BorderLayout.WEST);
        // ---------
        //lMessage1.setText( "..." );
        //lMessage1.setBounds( new Rectangle( 7, 403, 500, 22 ) );
        //lMessage1.setFont(new Font("Tahoma",Font.PLAIN,13));
        //this.getContentPane().add(lMessage1, null);
        //
		JScrollPane scrollPane = new JScrollPane(logger,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		logger.setEditable(false);
        logger.setFont(new Font("Tahoma",Font.PLAIN,12));
        scrollPane.setBounds(10, 372, 600, 200);
        this.getContentPane().add(scrollPane, null);
        // ------
        Font fontSans = new Font("SansSerif", 1, 12 );
        //jLabel12.setBounds( new Rectangle( 30, 445, 25, 15 ) );
        //jLabel12.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        //jLabel12.setText( "Ax\'" );
        //jLabel14.setBounds(new Rectangle(30, 465, 25, 15));
        //jLabel14.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        //jLabel14.setText("Ay\'");
        //jLabel16.setBounds(new Rectangle(30, 485, 25, 15));
        //jLabel16.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        //jLabel16.setText("Az\'");
        //jLabel13.setBounds( new Rectangle( 130, 445, 25, 15 ) );
        //jLabel13.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        //jLabel13.setText("Ax\'\'");
        //jLabel15.setBounds(new Rectangle(130, 465, 25, 15));
        //jLabel15.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        //jLabel15.setText("Ay\'\'");
        //jLabel17.setBounds(new Rectangle(130, 485, 25, 15));
        //jLabel17.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        //jLabel17.setText("Az\'\'");
        final String[] iNames = {"Ax", "Ay", "Az", "Hyz", "Hzx", "Hxy",
        		"Axn", "Ayn", "Azn", "Hyzn", "Hzxn", "Hxyn",
        		};
        for(int i=0; i<iNames.length; i++){
        	lIon1[i] = new JLabel();
        	lIon1[i].setText(iNames[i]);
        	lIon1[i].setFont( fontSans );
            panelIon1.add(lIon1[i]);
        }
        lIon1[0].setBounds(new Rectangle(20, 20, 25, 15));
        lIon1[1].setBounds(new Rectangle(20, 40, 25, 15));
        lIon1[2].setBounds(new Rectangle(20, 60, 25, 15));
        lIon1[3].setBounds(new Rectangle(130, 20, 25, 15));
        lIon1[4].setBounds(new Rectangle(130, 40, 25, 15));
        lIon1[5].setBounds(new Rectangle(130, 60, 25, 15));
        
        lIon1[6].setBounds(new Rectangle(20, 80, 25, 15));
        lIon1[7].setBounds(new Rectangle(20, 100, 25, 15));
        lIon1[8].setBounds(new Rectangle(20, 120, 25, 15));
        lIon1[9].setBounds(new Rectangle(130, 80, 36, 15));
        lIon1[10].setBounds(new Rectangle(130, 100, 50, 15));
        lIon1[11].setBounds(new Rectangle(130, 120, 39, 15));

        //jLabel116.setBounds(new Rectangle(20, 110, 25, 15));
        //jLabel116.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        //jLabel116.setText("Hxy\'");
        //jLabel118.setBounds(new Rectangle(30, 525, 25, 15));
        //jLabel118.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        //jLabel118.setText("Hyz\'");
        //jLabel119.setBounds(new Rectangle(30, 545, 25, 15));
        //jLabel119.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        //jLabel119.setText("Hzx\'");
        //jLabel120.setBounds(new Rectangle(130, 505, 37, 15));
        //jLabel120.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        //jLabel120.setText("Hxy\'\'");
        //jLabel123.setBounds(new Rectangle(130, 525, 41, 15));
        ///jLabel123.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        //jLabel123.setText("Hyz\'\'");
        //jLabel122.setBounds(new Rectangle(130, 545, 38, 15));
        //jLabel122.setFont( new java.awt.Font( "SansSerif", 1, 12 ) );
        //jLabel122.setText("Hzx\'\'");
        /*
        lAx1.setBounds(new Rectangle(60, 445, 38, 15));
        lAx1.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAx1.setText("0000");
        lAy1.setBounds(new Rectangle(60, 465, 38, 15));
        lAy1.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAy1.setText("0000");
        lAz1.setBounds(new Rectangle(60, 485, 38, 15));
        lAz1.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAz1.setText("0000");
        lAx2.setBounds(new Rectangle(160, 445, 38, 15));
        lAx2.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAx2.setText("0000");
        lAy2.setBounds(new Rectangle(160, 465, 38, 15));
        lAy2.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAy2.setText("0000");
        lAz2.setBounds(new Rectangle(160, 485, 38, 15));
        lAz2.setFont(new java.awt.Font("Monospaced", 0, 14));
        lAz2.setText("0000");
        */
        Font fontMono = new java.awt.Font("Monospaced", 0, 14);
        final JLabel[] ionV={lAx, lAy, lAz, lHyz, lHzx, lHxy, lAxn, lAyn, lAzn, lHyzn, lHzxn, lHxyn,};
        
        lAx.setBounds(new Rectangle(60, 20, 60, 15));
        lAy.setBounds(new Rectangle(60, 40, 60, 15));
        lAz.setBounds(new Rectangle(60, 60, 60, 15));
        lHyz.setBounds(new Rectangle(170, 20, 60, 15));
        lHzx.setBounds(new Rectangle(170, 40, 60, 15));
        lHxy.setBounds(new Rectangle(170, 60, 60, 15));
        lAxn.setBounds(new Rectangle(60, 80, 60, 15));
        lAyn.setBounds(new Rectangle(60, 100, 60, 15));
        lAzn.setBounds(new Rectangle(60, 120, 60, 15));
        lHyzn.setBounds(new Rectangle(170, 80, 60, 15));
        lHzxn.setBounds(new Rectangle(170, 100, 60, 15));
        lHxyn.setBounds(new Rectangle(170, 120, 60, 15));
        for(int i=0;i<ionV.length;i++){
        	ionV[i].setFont(fontMono);
        	ionV[i].setHorizontalAlignment(SwingConstants.RIGHT);
            panelIon1.add(ionV[i], null);
        }

        
        /*
        lHxy1.setBounds(new Rectangle(60, 505, 38, 15));
        lHxy1.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHxy1.setText("0000");
        lHyz1.setBounds(new Rectangle(60, 525, 38, 15));
        lHyz1.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHyz1.setText("0000");
        lHzx1.setBounds(new Rectangle(60, 545, 38, 15));
        lHzx1.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHzx1.setText("0000");
        lHxy2.setBounds(new Rectangle(160, 505, 38, 15));
        lHxy2.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHxy2.setText("0000");
        lHyz2.setBounds(new Rectangle(160, 525, 38, 15));
        lHyz2.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHyz2.setText("0000");
        lHzx2.setBounds(new Rectangle(160, 545, 38, 15));
        lHzx2.setFont(new java.awt.Font("Monospaced", 0, 14));
        lHzx2.setText("0000");
        */


        //this.getContentPane().add(bStop, null);
        

        displayAX.renderClear();
        displayAY.renderClear();
        displayAZ.renderClear();
        updateIon1();
        
        //this.getContentPane().add( graph, null );
        //this.getContentPane().add(jLabel12, null);
        //this.getContentPane().add(jLabel14, null);
        //this.getContentPane().add(jLabel16, null);
        //this.getContentPane().add(jLabel116, null);
        //this.getContentPane().add(jLabel118, null);
        //this.getContentPane().add(jLabel119, null);
        //this.getContentPane().add(jLabel111, null);
        //this.getContentPane().add(jLabel112, null);
        //this.getContentPane().add(jLabel113, null);
        //this.getContentPane().add(jLabel117, null);
        //this.getContentPane().add(jLabel114, null);
        //this.getContentPane().add( jLabel1111, null );
        //this.getContentPane().add( jLabel19, null );
        //this.getContentPane().add( jLabel18, null );
        //this.getContentPane().add( jLabel110, null );
        //this.getContentPane().add( jLabel115, null );
        //this.getContentPane().add( jLabel1110, null );
        //this.getContentPane().add( jLabel121, null );
        //this.getContentPane().add( jLabel17, null );
        //this.getContentPane().add( jLabel13, null );
        //this.getContentPane().add( jLabel15, null );
        //this.getContentPane().add( jLabel120, null );
        //this.getContentPane().add( jLabel122, null );
        //this.getContentPane().add( jLabel123, null );
        //this.getContentPane().add(jLabel9, null);
        /*
        this.getContentPane().add( lAx1, null );
        this.getContentPane().add( lAy1, null );
        this.getContentPane().add( lAz1, null );
        this.getContentPane().add( lAx2, null );
        this.getContentPane().add( lAy2, null );
        this.getContentPane().add( lAz2, null );
        this.getContentPane().add( lHxy1, null );
        this.getContentPane().add( lHyz1, null );
        this.getContentPane().add( lHzx1, null );
        this.getContentPane().add( lHxy2, null );
        this.getContentPane().add( lHyz2, null );
        this.getContentPane().add( lHzx2, null );
        */
        /*
        this.getContentPane().add( lAx, null );
        this.getContentPane().add( lAy, null );
        this.getContentPane().add( lAz, null );
        this.getContentPane().add( lAxn, null );
        this.getContentPane().add( lAyn, null );
        this.getContentPane().add( lAzn, null );
        this.getContentPane().add( lHxy, null );
        this.getContentPane().add( lHyz, null );
        this.getContentPane().add( lHzx, null );
        this.getContentPane().add( lHxyn, null );
        this.getContentPane().add( lHyzn, null );
        this.getContentPane().add( lHzxn, null );
		*/
        //scrollPaneX.getViewport().add( tables[0].table, null );
        //scrollPaneZ.getViewport().add( tables[2].table, null );
        //tabbedTables.setSelectedComponent( panelX );
        this.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        buttonTimer.schedule( new RemainderTask(), 0, 100 );
    }


    void waitForValue( double value, char plane ) {
        for ( ; ; ) {

            try {
                double val = Double.parseDouble( unit.getValue( plane ).
                                                 toString() );
                if ( Math.abs( val - value ) < 0.5 ) {
                    break;
                }
                //Thread.currentThread();
				Thread.sleep( 500 );
            } catch ( Exception ex ) {
                System.err.println( ex );
            }
        }
    }

    
    private int planeToIndex( char plane ) {
        int subchan = 0;
        if ( plane == 'y' ) {
            subchan = 1;
        } else if ( plane == 'z' ) {
            subchan = 2;
        }
        return subchan;
    }


    double doMeasureTool( char plane ) {
        double value = 0;
        int subchan = planeToIndex( plane );
        Channel chan = toolSource.getChannel( "angles" );
        if ( chan == null ) {
            /** @todo throw exce */
            return 0;
        }
        for ( int i = 0; i < 4; i++ ) {
            toolSource.waitNewData();
            double v1 = chan.getValue( subchan ).getAsDouble();
            if(v1 > 350 )
                v1 -= 360;
            value += v1;
        }
        value /= 4.0;
        return value;
    }

    void doWaitComplete( char plane ) {
        unit.waitNewData();
        unit.waitNewData();
        unit.waitNewData();
        unit.waitNewData();

        try {
            while ( !unit.isComplete( plane ) ) {
                Thread.sleep( 500 );
            }
        } catch ( InterruptedException ex ) {
        }
    }

    /**
     * Совмещение показаний угла поворота установки и прибора.
     * Совмещение производтся при зените 90 градусов.
     */
    public void doJoinRotates() throws Exception {
        lMessage1.setText( "Совмещение углов поворота" );
        UAKSI2CheckUnit unit = ( UAKSI2CheckUnit )this.unit;
        // сбрасываем поправку по повороту
        InclinometerAngles ang = unit.getAccurateDelta();
        ang.rotate.setAngle( 0 );
        // выходим на 90 градусов по зениту
        unit.goToPoint( 'y', 90 );
        doWaitComplete( 'y' );
        ang.rotate.setAngle( accAngles.rotate.getAngle() );

    }

    char nameToPlane( String tableName ) {
        char plane = 'z';
        if ( tableName.equals( "azimuth" ) ) {
            plane = 'x';
        } else
        if ( tableName.equals( "zenith" ) ) {
            plane = 'y';
        }
        return plane;
    }

    void doGoTo( char plane, Double value ) {
        try {
            String mess = "Задание азимутального угла ";
            if ( plane == 'y' ) {
                mess = "Задание зенитного угла ";
            }
            if ( plane == 'z' ) {
                mess = "Задание угла поворота ";
            }
            unit.goToPoint( plane, value );
            doWaitComplete( plane );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

    }


     /**
     * Выполнение градуировки датчиков Ax, Hyz, Ay, Hzx
     * Методика: установить азимут 180+-1 град, зенит 90+-1 град
     * установить угол поворота по показаниям датчика Az = 0+-0.5 град.
     * Снять характеристику пары датчиков Ax Hyz вращая раму относительно
     * зенитной оси
     * Задать угол датчика Az = 270+-0,5 град.
     * Аналогично снять характеристику пары датчиков Ay Hzx
     */

    public void doAxHyz() {

    }
     protected void startProcess() {
        //execCommand(( Command ) cbDo.getSelectedItem() );
        System.out.println( "Process started" );
    }
    protected void execCommand(ION1GradCommand.Command command) {
        if ( executer != null ) {
            if ( executer.isAlive() ) {
                return;
            }
        }
        executer = new CommandExecuter( command );
        executer.start();

    }
    void stopProcess( ActionEvent e ) {
        if ( executer != null ) {
            if ( executer.isAlive() ) {
                try {
                    unit.stop();
                } catch ( Exception ex ) {
                }
                //executer.interrupt();
                executer.stop();
                //executer.destroy();

            }
        }

    }


    void switchOff() {
        if ( unit != null ) {
            unit.removeSignalListeners();
            unit.disconnect();
        }
    }

    void quit() {
        if ( JOptionPane.showConfirmDialog( this, "Выйти?",
                                            "Выход из модуля",
                                            JOptionPane.YES_NO_OPTION ) == 0 ) {
            switchOff();
            dispose();
        }

    }


    void switchConnect() {
        try {
            if ( !unit.isConnected() ) {
                unit.connect();
            }
        } catch ( Exception ex ) {
            UiUtils.showError( this,
                               "Не удалось подключиться: " + ex.getMessage() );
            //System.err.println( ex.getMessage() );
        }
        try {
            if ( !toolSource.isConnected() ) {
                toolSource.connect();
            }
        } catch ( Exception ex ) {
            UiUtils.showError( this,
                               "Не удалось подключиться к источку данных прибора: " + ex.getMessage() );
            //System.err.println( ex.getMessage() );
        }
    }


    private class CommandExecuter
        extends Thread {
        private ION1GradCommand.Command command;
        public CommandExecuter( ION1GradCommand.Command command ) {
            this.command = command;
        }

        public void run() {

            try {
        		try {
        			Method method = viewer.getClass().getMethod(command.method, (Class[])null);
        			commandArgument1 = command.arg1;
        			commandArgument2 = command.arg2;
        			if (method != null) {
        			    method.invoke(viewer, (Object[]) null);
        			}
        		} catch (Exception e) {
        			addMessage("Ошибка выполнения команды: " + command.description);
        			e.printStackTrace();
        		} 

                //command.execute();
            } catch ( Exception ex ) {
                System.err.println( ex.getCause().getMessage() );
            }
            finally {
            }
        }
    }






    protected void processWindowEvent( WindowEvent e ) {
        super.processWindowEvent( e );
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
            quit();
            //System.exit( 0 );
        }
    }


	protected class RemainderTask extends TimerTask {
		JButton bRun, bStop;
		public RemainderTask() {
			bRun = ION1ManualGradViewer.this.bRun;
			bStop = ION1ManualGradViewer.this.bStop;
		}

		public void run() {
			CommandExecuter executer = ION1ManualGradViewer.this.executer;
			if(executer ==  null) {
				bStop.setEnabled(false);
				bRun.setEnabled(true);
			}else {
				bStop.setEnabled(executer.isAlive());
				bRun.setEnabled(!executer.isAlive());
			}
		}
	}

}
