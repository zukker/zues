package lsdsoft.zeus;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.event.*;
import java.beans.*;
import lsdsoft.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */
public class SetupToolsViewer2
    extends AbstractMethodsViewer
    implements ActionListener{

    JLabel lTypes = new JLabel();
    JLabel lNumbers = new JLabel();
    JList liNumbers = new JList();
    JList liTypes = new JList();
    JButton bAddNumber = new JButton();
    //JPopupMenu tablePopup = new JPopupMenu();
    //JMenuItem miChange = new JMenuItem();
    //JMenuItem miMeasure = new JMenuItem();
    private DefaultListModel listModel = new DefaultListModel();
    private DefaultListModel typesListModel = new DefaultListModel();
    private String[] types = null;
    private String selectedTool = "";
    JScrollPane scTools = new JScrollPane();
    JScrollPane scNumbers = new JScrollPane();

    //private Zeus zeus = Zeus.getInstance();

    public SetupToolsViewer2() {
        try {
            jbInit();
            //buildTypesList();
            //buildNumbersList();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

    }

    public void start() {
        try {
            //jbInit();
            buildTypesList();
            buildNumbersList();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

    }

    protected void buildTypesList() {
        try {

            //String selectedNumber = zeus.getToolNumber();
            types = DataFactory.getDataStorage().getToolsList();
            String[] names = DataFactory.getDataStorage().getToolsNamesList();
            //liNumbers = new JList(nums);
            typesListModel.removeAllElements();
            for ( int i = 0; i < types.length; i++ ) {
                typesListModel.addElement(names[i]);

            }
            int index = liTypes.getSelectionModel().getLeadSelectionIndex();
            if( index >= 0) {
                selectedTool = types[index];
            }
        } catch ( Exception ex ) {
            //zeus.logex( ex );
            //zeus.log.dbe( DebugLevel.L3_WARN, ex.getCause() );
            System.err.println( ex.getMessage() );
        }
    }
    protected void buildNumbersList() {
        try {
            listModel.removeAllElements();
            //String selectedNumber = zeus.getToolNumber();
            Zeus.getInstance().setToolType(selectedTool);
            String[] nums = DataFactory.getToolNumbers();
            //liNumbers = new JList(nums);
            for ( int i = 0; i < nums.length; i++ ) {
                listModel.addElement(nums[i]);

            }
        } catch ( Exception ex ) {
            //zeus.logex( ex );
            //zeus.log.dbe( DebugLevel.L3_WARN, ex.getCause() );
            System.err.println( ex.getMessage() );
        }
    }

    void jbInit() throws Exception {
        lTypes.setText( "Типы аппаратуры" );
        lTypes.setBounds( new Rectangle( 19, 13, 165, 15 ) );
        lNumbers.setText( "Заводские номера" );
        lNumbers.setBounds( new Rectangle( 207, 9, 166, 19 ) );
        liNumbers.setModel(listModel);
        liTypes.setModel(typesListModel);
        bAddNumber.setBounds( new Rectangle( 206, 199, 163, 28 ) );
        bAddNumber.setText( "Новый номер" );
        bAddNumber.setActionCommand("new");
        bAddNumber.addActionListener(this);

        this.getContentPane().setLayout( null );
        scTools.setBorder( BorderFactory.createEtchedBorder() );
        scTools.setBounds( new Rectangle( 13, 41, 162, 139 ) );
        scNumbers.setBorder( BorderFactory.createEtchedBorder() );
        scNumbers.setBounds( new Rectangle( 210, 41, 162, 139 ) );
        liTypes.getSelectionModel().addListSelectionListener(
                            new SharedListSelectionHandler());
        //this.getContentPane().add( liNumbers );
        this.getContentPane().add( bAddNumber );
        this.getContentPane().add( lTypes, null );
        this.getContentPane().add( lNumbers, null );
        this.getContentPane().add( scTools );
        scTools.setViewportView( liTypes );
        this.getContentPane().add( scNumbers );
        scNumbers.setViewportView( liNumbers );
        this.setResizable(false);
        this.setBounds( 0,0, 390, 270);
        UiUtils.toScreenCenter(this);
        this.setVisible(true);
    }
    public void newNumber() {
        CustomDialog dialog = new CustomDialog(this);
        dialog.pack();
        dialog.setVisible(true);
        if(dialog.typedText != null ) {
            DataFactory.addToolNumber(selectedTool, dialog.typedText);
            buildNumbersList();
        }
    }
    public void actionPerformed(ActionEvent e) {
        if ("new".equals(e.getActionCommand())) {
            newNumber();

        } else {
        }
    }
    class SharedListSelectionHandler
        implements ListSelectionListener {
        public void valueChanged( ListSelectionEvent event ) {
            ListSelectionModel lsm = ( ListSelectionModel )event.getSource();

            //int firstIndex = event.getFirstIndex();
            //int lastIndex = event.getLastIndex();
            //boolean isAdjusting = event.getValueIsAdjusting();

            if ( !lsm.isSelectionEmpty() ) {
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                //int maxIndex = lsm.getMaxSelectionIndex();
                selectedTool = types[minIndex];
                buildNumbersList();
            }
        }
    }


    class CustomDialog
        extends JDialog {
        String typedText = null;

        private JOptionPane optionPane;

        public String getValidatedText() {
            return typedText;
        }

        public CustomDialog( Frame aFrame ) {
            super( aFrame, true );
            //final JFrame dd = parent;

            setTitle( "Задание номера" );

            final String msgString1 = "Введите новый номер аппаратуры";
            final JTextField textField = new JTextField( 10 );
            Object[] array = {
                msgString1, textField};

            final String btnString1 = "Добавить";
            final String btnString2 = "Отмена";
            Object[] options = {
                btnString1, btnString2};

            optionPane = new JOptionPane( array,
                                          JOptionPane.QUESTION_MESSAGE,
                                          JOptionPane.YES_NO_OPTION,
                                          null,
                                          options,
                                          options[0] );
            setContentPane( optionPane );
            setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
            addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent we ) {
                    /*
                     * Instead of directly closing the window,
                     * we're going to change the JOptionPane's
                     * value property.
                     */
                    optionPane.setValue( new Integer(
                        JOptionPane.CLOSED_OPTION ) );
                }
            } );
            //setLocationRelativeTo(aFrame);
            UiUtils.toScreenCenter(this);
            setSize(new Dimension(300, 150));
            //setSize(300, 100);

            textField.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    optionPane.setValue( btnString1 );
                }
            } );

            optionPane.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent e ) {
                    String prop = e.getPropertyName();

                    if ( isVisible()
                         && ( e.getSource() == optionPane )
                         && ( prop.equals( JOptionPane.VALUE_PROPERTY ) ||
                              prop.equals( JOptionPane.INPUT_VALUE_PROPERTY ) ) ) {
                        Object value = optionPane.getValue();

                        if ( value == JOptionPane.UNINITIALIZED_VALUE ) {
                            //ignore reset
                            return;
                        }

                        // Reset the JOptionPane's value.
                        // If you don't do this, then if the user
                        // presses the same button next time, no
                        // property change event will be fired.
                        optionPane.setValue(
                            JOptionPane.UNINITIALIZED_VALUE );

                        if ( value.equals( btnString1 ) ) {
                            typedText = textField.getText();
                            setVisible( false );
                        } else { // user closed dialog or clicked cancel
                            typedText = null;
                            setVisible( false );
                        }
                    }

                }} );
        }
    }

}
