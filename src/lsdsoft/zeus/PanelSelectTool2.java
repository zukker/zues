package lsdsoft.zeus;


import javax.swing.*;
import java.awt.*;
import java.util.*;
import lsdsoft.welltools.*;
import java.awt.event.*;
import javax.swing.event.*;
import org.grlea.log.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.1
 */

public class PanelSelectTool2
    extends JPanel {

    private Properties zeusProps = Zeus.getInstance().getConfig();
    private String zeusNextStepClass = "wizard.nextstep";
    private JLabel lToolType = new JLabel();
    private Zeus zeus = Zeus.getInstance();
    JLabel lToolNumber = new JLabel();
    JComboBox cbTools = new JComboBox();
    JComboBox cbNumbers = new JComboBox();
    JComboBox cbChannels = new JComboBox();
    JLabel lChannel = new JLabel();

    public PanelSelectTool2() {
        try {
            zeusProps.setProperty( zeusNextStepClass,
                                   "lsdsoft.zeus.PanelSelectWorkMode" );
            jbInit();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setSize( new Dimension( 338, 200 ) );
        this.setVisible( true );
        lToolType.setText("�������� ��� ����������" );
        //jLabel1.setFont(new java.awt.Font("Dialog", 0, 14));
        lToolType.setBounds( new Rectangle( 25, 19, 293, 17 ) );
        this.setLayout( null );
        //cbTools.setFont(new java.awt.Font("Lucida Sans", Font.BOLD, 14));
        cbTools.setBounds( new Rectangle( 35, 41, 260, 24 ) );
        buildToolTypeList();
        buildNumberList();
        buildChannelList();

        cbTools.addActionListener(
            new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                 ToolTypeInfo item = ( ToolTypeInfo ) cbTools.getSelectedItem();
                if ( item != null ) {
                    zeus.setToolType( item.getID() );
                    zeus.setToolDataSourceID( item.getSourceName() );
                    Zeus.log.info("Selected tool: '" + item.getID() + "'");
                    buildNumberList();
                    buildChannelList();
                }
            }
        }
        );

        //Zeus.setWellToolType(((WellToolInfo)info[0]).getID());
        //cbTools.addItem("���-�7");
        //jLabel2.setFont(new java.awt.Font("Dialog", 0, 14));
        lToolNumber.setText( "�������� ��������� �����" );
        lToolNumber.setBounds( new Rectangle( 25, 89, 263, 16 ) );
        cbNumbers.setAutoscrolls( true );
        cbNumbers.setMaximumRowCount( 10 );
        cbNumbers.setBounds( new Rectangle( 35, 117, 260, 24 ) );
        cbNumbers.addItemListener( new java.awt.event.ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                cbNumbers_itemStateChanged( e );
            }
        } );
        cbNumbers.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //zeus.setToolNumber( ( ( ToolIndex.ToolIndexItem )
                //                      cbNumbers.getSelectedItem() ).number );
                //zeus.setToolNumber( cbNumbers.getSelectedItem().toString() );
            }
        } );
        lChannel.setText("�������� �����");
        lChannel.setBounds(new Rectangle(24, 144, 178, 20));
        cbChannels.setBounds(new Rectangle(35, 175, 260, 24));
        cbChannels.addActionListener(
            new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ToolChannel channel = ( ToolChannel ) cbChannels.getSelectedItem();
                if ( channel != null ) {
                    zeus.setToolChannel( channel.getID() );
                    zeus.log.info("Selected channel: '" + channel.getID() + "'");
                    //buildNumberList();
                    //buildChannelList();
                }
            }
        }
        );
        this.add( lToolType, null );
        this.add( cbTools, null );
        this.add( lToolNumber, null );
        this.add( cbNumbers, null );
        //this.add(lChannel, null);
        //this.add(cbChannels, null);

    }

    /**
     * �������� ������ ����� ������� ��� cbTools combobox
     */
    public void
        buildToolTypeList() {
        try {
            cbTools.removeAllItems();
            ToolTypeInfo[] info = DataFactory.getToolsInfo();
            String selectedTool = zeus.getToolType();
            for ( int i = 0; i < info.length; i++ ) {
                cbTools.addItem( ( ToolTypeInfo ) info[i] );
                String s = ( ( ToolTypeInfo ) info[i] ).getID();
                if ( selectedTool.equals( s ) ) {
                    cbTools.setSelectedIndex( i );

                }
            }
        } catch ( Exception ex ) {
            zeus.log.error( ex.getMessage() );
            System.err.println( ex.getMessage() );
        }
    }

    /**
     * ���������� ������ ������� �������� ��� cbNumbers combobox.
     */
    public void buildNumberList() {
        try {
            cbNumbers.removeAllItems();
            String selectedNumber = zeus.getToolNumber();
            String[] nums = DataFactory.getToolNumbers();
            for ( int i = 0; i < nums.length; i++ ) {
                cbNumbers.addItem( nums[i] );
                if ( selectedNumber.equals( nums[i] ) ) {
                    cbNumbers.setSelectedIndex( i );
                }
            }
            if(cbNumbers.getSelectedIndex() <= 0) {
                if(nums.length > 0) {
                    cbNumbers.setSelectedIndex( 0 );
                    //zeus.setToolNumber();
                } else {
                    zeus.setToolNumber("");
                }
            }

        } catch ( Exception ex ) {
            zeus.logex( ex );
            //zeus.log.dbe( DebugLevel.L3_WARN, ex.getCause() );
            System.err.println( ex.getMessage() );
        }
    }
    public void buildChannelList() {
        try {
            cbChannels.removeAllItems();
            String selectedTool = zeus.getToolType();
            String selectedChannel = zeus.getToolChannel();
            ToolTypeInfo info = DataFactory.getToolInfo(selectedTool);
            ToolChannels channels = info.getChannels();
            for ( int i = 0; i < channels.size(); i++ ) {
                ToolChannel ch = ( ToolChannel )channels.get(i);
                cbChannels.addItem( ch );
                String s = ch.getID();
                if ( selectedChannel.equals( s ) ) {
                    cbChannels.setSelectedIndex( i );

                }
            }

        } catch ( Exception ex ) {
            zeus.logex( ex );
            //zeus.log.dbe( DebugLevel.L3_WARN, ex.getCause() );
            System.err.println( ex.getMessage() );
        }
    }

    void cbNumbers_itemStateChanged( ItemEvent e ) {
        if(cbNumbers.getSelectedIndex() < 0) {
            return;
        }
        //String num = ( ( ToolIndex.ToolIndexItem )
        //                      cbNumbers.getSelectedItem() ).number;
        String num = cbNumbers.getSelectedItem().toString();
        Zeus.log.info( "Select tool number '" + num + "'");
        zeus.setToolNumber( num );
    }

}
