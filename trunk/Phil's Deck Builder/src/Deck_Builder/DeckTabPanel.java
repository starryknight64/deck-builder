/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DeckTabPanel.java
 *
 * Created on Nov 2, 2010, 8:35:05 PM
 */

package Deck_Builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import Deck_Builder.CardTable.DeckTableModel;
import Deck_Builder.CardTable.PricesTableModel;
import Deck_Builder.c_Deck.CardLoading;
import Deck_Builder.c_Deck.WhichHalf;
import Deck_Builder.c_Price.PriceType;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Phillip
 */
public class DeckTabPanel extends JPanel implements ActionListener {

    private enum DeckInfo {
        NAME,
        COLORS,
        CREATIONDATE,
        MODIFICATIONS,
        SIZE,
        SIDEBOARDSIZE,
        PLANESWALKERS,
        CREATURES,
        ARTIFACTS,
        ENCHANTMENTS,
        SORCERIES,
        INSTANTS,
        LANDS;
    }
    private JTextField[] m_DeckInfoText;

    private enum DeckTab {
        MAINDECK,
        SIDEBOARD,
        DECKINFO,
        MISC
    }

    private EventListenerList m_listeners = new EventListenerList();

    private DeckTableModel m_deckCTM = new DeckTableModel();
    private DeckTableModel m_sbCTM = new DeckTableModel();
    private PricesTableModel m_priceCTM = new PricesTableModel();
    private c_CardDB m_db;
    private c_PriceDB m_priceDB;

    private Boolean m_isSaved = true;
    private Boolean m_isLoading = false;
    private String m_filename = "";

    private c_Deck m_deck = new c_Deck();

    @Override
    public void finalize() throws Throwable {
        m_listeners = null;
        m_deckCTM = null;
        m_sbCTM = null;
        m_priceCTM = null;
        m_db = null;
        m_priceDB = null;
        super.finalize();
    }

    /** Creates new form DeckTabPanel */
    public DeckTabPanel() {
        m_db = new c_CardDB();
        m_priceDB = new c_PriceDB();
        initComponents();
        init();
        m_deck.addActionListener( this );
    }
    public DeckTabPanel( c_CardDB db, c_PriceDB pdb ) {
        m_db = db;
        m_priceDB = pdb;
        initComponents();
        init();
        m_deck.addActionListener( this );
    }
    public DeckTabPanel( String deckfilepath, c_CardDB db, c_PriceDB pdb ) {
        m_db = db;
        m_priceDB = pdb;
        initComponents();
        init();
        m_deck.addActionListener( this );
        m_deck.loadDeckFromFile( deckfilepath );
        m_filename = deckfilepath;
    }
    
    private void init() {
        m_DeckInfoText = new JTextField[] {
            m_Properties_DeckNameText,
            m_Properties_ColorsText,
            m_Properties_CreationDateText,
            m_Properties_ModificationsText,
            m_Properties_SizeText,
            m_Properties_SideboardSizeText,
            m_Properties_PlaneswalkersText,
            m_Properties_CreaturesText,
            m_Properties_ArtifactsText,
            m_Properties_EnchantmentsText,
            m_Properties_SorceriesText,
            m_Properties_InstantsText,
            m_Properties_LandsText
        };
//        this.m_MainDeck_Table.setAutoCreateRowSorter(true);
//        this.m_Sideboard_Table.setAutoCreateRowSorter(true);
//        this.m_Misc_PricesTable.setAutoCreateRowSorter(true);

        updateDeckInfo();
    }

    public c_Deck getDeck() {
        return m_deck;
    }

    public boolean saveDeck( String filepath ) {
        m_isSaved = m_deck.saveDeck( filepath, m_db );
        if( m_isSaved ) {
            m_filename = filepath;
        }
        return m_isSaved;
    }

    public boolean isSaved() {
        return m_isSaved;
    }

    public String getFilename() {
        return m_filename;
    }
    
    public void addCard( c_Card card, Integer amount ) {
        boolean toDeck = (m_DeckTabPane.getSelectedIndex() != DeckTab.SIDEBOARD.ordinal());
        addCard( card, amount, toDeck );
    }
    public void addCard( c_Card card, Integer amount, boolean toDeck ) {
        DeckTableModel dtm = toDeck ? m_deckCTM : m_sbCTM;
        Integer row = dtm.getMIDRow( card.MID );//.findValueInColumn( card.MID, DeckTableModel.DeckCols.MID.val );
        Integer cur_amt = m_deck.getAmountOfCard( card.MID, toDeck ? WhichHalf.DECK : WhichHalf.SB );
        Integer new_amt = amount + cur_amt;
        
        if( new_amt <= 0 ) {
            deleteCard( card );
            return;
        }

        m_deck.addCard( card.MID, new_amt, toDeck );
        m_db.addCard( card );
        if( row < 0 ) {
            dtm.addCard( card, new_amt );
            m_priceCTM.addPrice( m_priceDB.getPrice( card ), card, amount, toDeck );
        } else {
            dtm.updateCardAmount( card, new_amt );
            m_priceCTM.updateCardAmount( card, new_amt, toDeck );
        }

        if( !m_isLoading ) {
            m_isSaved = false;
        }
        fireActionEvent( LeftPanel.class, Action.ACTION_DECK_CHANGED, Action.COMMAND_CURRENT_DECK_CHANGED );
        updateDeckInfo();

        dtm = null;
        row = null;
        cur_amt = null;
        new_amt = null;
        //System.gc();
    }


    public void deleteCard( c_Card card ) {
        boolean fromDeck = (m_DeckTabPane.getSelectedIndex() != DeckTab.SIDEBOARD.ordinal());
        DeckTableModel dtm = fromDeck ? m_deckCTM : m_sbCTM;
        Integer row = dtm.getMIDRow( card.MID ); //.findValueInColumn( card.MID, DeckTableModel.DeckCols.MID.val );
        
        m_deck.deleteCard( card.MID, fromDeck );
        m_priceCTM.deleteCard( card );
        if( row >= 0 ) {
            dtm.deleteRow( row );
        }

        if( !m_isLoading ) {
            m_isSaved = false;
        }
        fireActionEvent( LeftPanel.class, Action.ACTION_DECK_CHANGED, Action.COMMAND_CURRENT_DECK_CHANGED );
        updateDeckInfo();

        dtm = null;
        row = null;
        //System.gc();
    }

    public void actionPerformed(ActionEvent e) {
        if( e.getID() == Action.ACTION_DECK_LOAD_CARD
            || e.getID() == Action.ACTION_DECK_SB_LOAD_LINE ) {
            //Card Name	Amt	Card Type	Sub-Type	Cost	P/T	Set	MID
            String line = e.getActionCommand();
            String ary[] = line.split( "\t" );
            c_Card card = new c_Card();
            Integer amount = Integer.parseInt( ary[ CardLoading.AMOUNT.ordinal() ] );
            card.Name = ary[ CardLoading.NAME.ordinal() ];
            card.Type = ary[ CardLoading.TYPE.ordinal() ];
            card.SubType = ary[ CardLoading.SUBTYPE.ordinal() ];
            card.CastingCost = new c_CastingCost( ary[ CardLoading.COST.ordinal() ] );
            card.PT = ary[ CardLoading.PT.ordinal() ].replaceAll( "'", "" );
            card.Expansion = ary[ CardLoading.EXPANSION.ordinal() ];
            card.MID = Integer.parseInt( ary[ CardLoading.MID.ordinal() ] );

            boolean toDeck = ( e.getID() == Action.ACTION_DECK_LOAD_CARD );
            m_isLoading = true;
            addCard( card, amount, toDeck );
            m_isLoading = false;

            line = null;
            ary = null;
            card = null;
            amount = null;
        }
    }

    public Integer getSelectedCard() {
        Integer mid = 0;
        if( m_DeckTabPane.getSelectedIndex() != DeckTab.MISC.ordinal() ) {
            boolean fromDeck = (m_DeckTabPane.getSelectedIndex() != DeckTab.SIDEBOARD.ordinal());
            DeckTableModel dtm = fromDeck ? m_deckCTM : m_sbCTM;
            JTable table = fromDeck ? m_MainDeck_Table : m_Sideboard_Table;
            Integer row = table.convertRowIndexToModel( table.getSelectedRow() );
            mid = dtm.getMID( row );//(Integer)dtm.getValueAt( row, DeckTableModel.DeckCols.MID.val );

            dtm = null;
            table = null;
            //System.gc();
        } else { // Get selected card from prices table
            int row = m_Misc_PricesTable.convertRowIndexToModel( m_Misc_PricesTable.getSelectedRow() );
            mid = m_priceCTM.getMID( row ); //(Integer)m_Misc_PricesTable.getValueAt( row, PricesTableModel.PriceCols.MID.val );
        }
        
        return mid;
    }

    public void updateDeckInfo() {
        m_DeckInfoText[ DeckInfo.NAME.ordinal()          ].setText( m_deck.getName() );

        if( m_DeckInfoText[ DeckInfo.NAME.ordinal() ].getText().charAt( 0 ) != this.getFormattedDeckName().charAt( 0 ) ) {
            fireActionEvent( LeftPanel.class, Action.ACTION_DECK_CHANGED, Action.COMMAND_DECK_NAME_CHANGED );
        }

        m_DeckInfoText[ DeckInfo.COLORS.ordinal()        ].setText( m_deck.getColorsText( m_db ) );

        if( m_DeckInfoText[ DeckInfo.CREATIONDATE.ordinal() ].getText().equals( "" ) ) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat( "MMM. dd, yyyy h:mm aaa" );
            m_deck.setCreationDate( sdf.format( cal.getTime() ) );
            m_DeckInfoText[ DeckInfo.CREATIONDATE.ordinal() ].setText( m_deck.getCreationDate() );

            cal = null;
            sdf = null;
        }

        m_DeckInfoText[ DeckInfo.MODIFICATIONS.ordinal() ].setText( m_deck.getModifications().toString() );
        m_DeckInfoText[ DeckInfo.SIZE.ordinal()          ].setText( m_deck.getSizeOf( c_Deck.WhichHalf.DECK ).toString() );
        m_DeckInfoText[ DeckInfo.SIDEBOARDSIZE.ordinal() ].setText( m_deck.getSizeOf( c_Deck.WhichHalf.SB ).toString() );
        m_DeckInfoText[ DeckInfo.PLANESWALKERS.ordinal() ].setText( m_deck.getAmountOfType( c_Deck.WhichType.PLANESWALKERS, c_Deck.WhichHalf.BOTH, m_db ).toString() );
        m_DeckInfoText[ DeckInfo.CREATURES.ordinal()     ].setText( m_deck.getAmountOfType( c_Deck.WhichType.CREATURES,     c_Deck.WhichHalf.BOTH, m_db ).toString() );
        m_DeckInfoText[ DeckInfo.ARTIFACTS.ordinal()     ].setText( m_deck.getAmountOfType( c_Deck.WhichType.ARTIFACTS,     c_Deck.WhichHalf.BOTH, m_db ).toString() );
        m_DeckInfoText[ DeckInfo.ENCHANTMENTS.ordinal()  ].setText( m_deck.getAmountOfType( c_Deck.WhichType.ENCHANTMENTS,  c_Deck.WhichHalf.BOTH, m_db ).toString() );
        m_DeckInfoText[ DeckInfo.SORCERIES.ordinal()     ].setText( m_deck.getAmountOfType( c_Deck.WhichType.SORCERIES,     c_Deck.WhichHalf.BOTH, m_db ).toString() );
        m_DeckInfoText[ DeckInfo.INSTANTS.ordinal()      ].setText( m_deck.getAmountOfType( c_Deck.WhichType.INSTANTS,      c_Deck.WhichHalf.BOTH, m_db ).toString() );
        m_DeckInfoText[ DeckInfo.LANDS.ordinal()         ].setText( m_deck.getAmountOfType( c_Deck.WhichType.LANDS,         c_Deck.WhichHalf.BOTH, m_db ).toString() );

        c_Price price = m_priceDB.getPrice( m_deck, WhichHalf.BOTH );
        m_Price_LowText.setText( price.toString( PriceType.LOW ) );
        m_Price_AverageText.setText( price.toString( PriceType.AVERAGE ) );
        m_Price_HighText.setText( price.toString( PriceType.HIGH ) );

        CardTable.autoResizeColWidth( m_MainDeck_Table );
        CardTable.autoResizeColWidth( m_Sideboard_Table );
        CardTable.autoResizeColWidth( m_Misc_PricesTable );
    }

    public String getFormattedDeckName() {
        String name = m_DeckInfoText[ DeckInfo.NAME.ordinal() ].getText().trim();
        if( name.length() > 16 ) {
            name = name.substring( 0, 16 ) + "...";
        }
        return ( m_isSaved ? "" : "*" ) + name;
    }

    public String getDeckName() {
        return m_DeckInfoText[ DeckInfo.NAME.ordinal() ].getText().trim();
    }
    
    private void keyPressed( boolean fromDeckTable, KeyEvent evt ) {
        if( evt.getID() != KeyEvent.KEY_RELEASED ) {
            if( evt.getKeyCode() == KeyEvent.VK_ENTER ) {
                keyPressedAddCard( fromDeckTable, 1 );
            } else if( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
                keyPressedAddCard( fromDeckTable, -1 );
            }
        } else {
            CardTable table = ( fromDeckTable ? m_MainDeck_Table : m_Sideboard_Table );
            WhichHalf which = ( fromDeckTable ? WhichHalf.DECK : WhichHalf.SB );
            cardSelected( table, m_deck, which );
            table = null;
            which = null;
        }
    }

    private void keyPressedAddCard( boolean fromDeckTable, Integer amount ) {
        JTable table = fromDeckTable ? m_MainDeck_Table : m_Sideboard_Table;
        DeckTableModel dtm = fromDeckTable ? m_deckCTM : m_sbCTM;

        if( table.getSelectedRowCount() != 1 ) {
            table = null;
            dtm = null;
            return;
        }

        Integer row = table.convertRowIndexToModel( table.getSelectedRow() );
        Integer mid = dtm.getMID( row ); //.getValueAt( row, DeckTableModel.DeckCols.MID.val );
        c_Card card = m_db.getCard( mid );
        addCard( card, amount );

        table = null;
        dtm = null;
        card = null;
        //System.gc();
    }

    public void addActionListener( ActionListener listener ) {
        m_listeners.add( ActionListener.class, listener );
    }

    private void fireActionEvent( Class thisClass, Integer action, String command ) {
        Object listeners[] = m_listeners.getListenerList();
        for( int i=listeners.length-1; i>=0; i-- ) {
            if( listeners[i].getClass() == thisClass ) {
                ((ActionListener)listeners[i]).actionPerformed( new ActionEvent( this, action, command ) );

                listeners = null;
                //System.gc();
                return;
            }
        }

        listeners = null;
        //System.gc();
    }

    private void cardSelected( JTable table, c_Deck deck, WhichHalf part ) {
        if( table.getSelectedRows().length == 1 && deck.getSizeOf( part ) > 0 ) {
            fireActionEvent( LeftPanel.class, Action.ACTION_CARD_PREVIEW, Action.COMMAND_CARD_PREVIEW );
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_DeckTabPane = new javax.swing.JTabbedPane();
        m_MainDeck_Panel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        m_MainDeck_Table = new Deck_Builder.CardTable();
        m_Sideboard_Panel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_Sideboard_Table = new Deck_Builder.CardTable();
        m_DeckInfo_Panel = new javax.swing.JPanel();
        m_DeckInfo_ManaCurve = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        m_DeckInfo_Properties = new javax.swing.JPanel();
        m_Properties_DeckNameLabel = new javax.swing.JLabel();
        m_Properties_DeckNameText = new javax.swing.JTextField();
        m_Properties_ColorsLabel = new javax.swing.JLabel();
        m_Properties_ColorsText = new javax.swing.JTextField();
        m_Properties_CreationDateLabel = new javax.swing.JLabel();
        m_Properties_CreationDateText = new javax.swing.JTextField();
        m_Properties_ModificationsLabel = new javax.swing.JLabel();
        m_Properties_ModificationsText = new javax.swing.JTextField();
        m_Properties_SizeLabel = new javax.swing.JLabel();
        m_Properties_SizeText = new javax.swing.JTextField();
        m_Properties_SideboardSizeText = new javax.swing.JTextField();
        m_Properties_SideboardSizeLabel = new javax.swing.JLabel();
        m_Properties_PlaneswalkersText = new javax.swing.JTextField();
        m_Properties_PlaneswalkersLabel = new javax.swing.JLabel();
        m_Properties_CreaturesText = new javax.swing.JTextField();
        m_Properties_CreaturesLabel = new javax.swing.JLabel();
        m_Properties_ArtifactsLabel = new javax.swing.JLabel();
        m_Properties_ArtifactsText = new javax.swing.JTextField();
        m_Properties_EnchantmentsLabel = new javax.swing.JLabel();
        m_Properties_EnchantmentsText = new javax.swing.JTextField();
        m_Properties_SorceriesLabel = new javax.swing.JLabel();
        m_Properties_SorceriesText = new javax.swing.JTextField();
        m_Properties_InstantsLabel = new javax.swing.JLabel();
        m_Properties_InstantsText = new javax.swing.JTextField();
        m_Properties_LandsLabel = new javax.swing.JLabel();
        m_Properties_LandsText = new javax.swing.JTextField();
        m_Misc_Panel = new javax.swing.JPanel();
        m_Misc_Prices = new javax.swing.JPanel();
        m_Price_LowText = new javax.swing.JTextField();
        m_Price_AverageText = new javax.swing.JTextField();
        m_Price_HighText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        m_Misc_PricesTable = new Deck_Builder.CardTable();
        m_Misc_Notes = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        m_Misc_NotesText = new javax.swing.JTextArea();

        m_DeckTabPane.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        m_DeckTabPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                m_DeckTabPaneStateChanged(evt);
            }
        });

        m_MainDeck_Table.setModel(m_deckCTM);
        m_MainDeck_Table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                m_MainDeck_TableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                m_MainDeck_TableMouseReleased(evt);
            }
        });
        m_MainDeck_Table.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                m_MainDeck_TableKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                m_MainDeck_TableKeyReleased(evt);
            }
        });
        jScrollPane5.setViewportView(m_MainDeck_Table);

        javax.swing.GroupLayout m_MainDeck_PanelLayout = new javax.swing.GroupLayout(m_MainDeck_Panel);
        m_MainDeck_Panel.setLayout(m_MainDeck_PanelLayout);
        m_MainDeck_PanelLayout.setHorizontalGroup(
            m_MainDeck_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
        );
        m_MainDeck_PanelLayout.setVerticalGroup(
            m_MainDeck_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
        );

        m_DeckTabPane.addTab("Main Deck", m_MainDeck_Panel);

        m_Sideboard_Table.setModel(m_sbCTM);
        m_Sideboard_Table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                m_Sideboard_TableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                m_Sideboard_TableMouseReleased(evt);
            }
        });
        m_Sideboard_Table.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                m_Sideboard_TableKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                m_Sideboard_TableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(m_Sideboard_Table);

        javax.swing.GroupLayout m_Sideboard_PanelLayout = new javax.swing.GroupLayout(m_Sideboard_Panel);
        m_Sideboard_Panel.setLayout(m_Sideboard_PanelLayout);
        m_Sideboard_PanelLayout.setHorizontalGroup(
            m_Sideboard_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
        );
        m_Sideboard_PanelLayout.setVerticalGroup(
            m_Sideboard_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
        );

        m_DeckTabPane.addTab("Sideboard", m_Sideboard_Panel);

        m_DeckInfo_ManaCurve.setBorder(javax.swing.BorderFactory.createTitledBorder("Mana Curve"));

        jLabel14.setText("Coming Soon!");

        javax.swing.GroupLayout m_DeckInfo_ManaCurveLayout = new javax.swing.GroupLayout(m_DeckInfo_ManaCurve);
        m_DeckInfo_ManaCurve.setLayout(m_DeckInfo_ManaCurveLayout);
        m_DeckInfo_ManaCurveLayout.setHorizontalGroup(
            m_DeckInfo_ManaCurveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_DeckInfo_ManaCurveLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addContainerGap(201, Short.MAX_VALUE))
        );
        m_DeckInfo_ManaCurveLayout.setVerticalGroup(
            m_DeckInfo_ManaCurveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_DeckInfo_ManaCurveLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addContainerGap(281, Short.MAX_VALUE))
        );

        m_DeckInfo_Properties.setBorder(javax.swing.BorderFactory.createTitledBorder("Properties"));

        m_Properties_DeckNameLabel.setText("Deck Name:");
        m_Properties_DeckNameLabel.setPreferredSize(new java.awt.Dimension(80, 10));

        m_Properties_DeckNameText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                m_Properties_DeckNameTextKeyReleased(evt);
            }
        });

        m_Properties_ColorsLabel.setText("Colors:");
        m_Properties_ColorsLabel.setPreferredSize(new java.awt.Dimension(80, 10));

        m_Properties_ColorsText.setEditable(false);

        m_Properties_CreationDateLabel.setText("Creation Date:");
        m_Properties_CreationDateLabel.setPreferredSize(new java.awt.Dimension(80, 10));

        m_Properties_CreationDateText.setEditable(false);

        m_Properties_ModificationsLabel.setText("Modifications:");

        m_Properties_ModificationsText.setEditable(false);
        m_Properties_ModificationsText.setMaximumSize(new java.awt.Dimension(60, 20));
        m_Properties_ModificationsText.setMinimumSize(new java.awt.Dimension(60, 20));
        m_Properties_ModificationsText.setPreferredSize(new java.awt.Dimension(60, 20));

        m_Properties_SizeLabel.setText("Size:");
        m_Properties_SizeLabel.setPreferredSize(new java.awt.Dimension(80, 10));

        m_Properties_SizeText.setEditable(false);
        m_Properties_SizeText.setMaximumSize(new java.awt.Dimension(60, 20));
        m_Properties_SizeText.setMinimumSize(new java.awt.Dimension(60, 20));
        m_Properties_SizeText.setPreferredSize(new java.awt.Dimension(60, 20));

        m_Properties_SideboardSizeText.setEditable(false);
        m_Properties_SideboardSizeText.setMaximumSize(new java.awt.Dimension(60, 20));
        m_Properties_SideboardSizeText.setMinimumSize(new java.awt.Dimension(60, 20));
        m_Properties_SideboardSizeText.setPreferredSize(new java.awt.Dimension(60, 20));

        m_Properties_SideboardSizeLabel.setText("Sideboard:");
        m_Properties_SideboardSizeLabel.setPreferredSize(new java.awt.Dimension(80, 10));

        m_Properties_PlaneswalkersText.setEditable(false);
        m_Properties_PlaneswalkersText.setMaximumSize(new java.awt.Dimension(60, 20));
        m_Properties_PlaneswalkersText.setMinimumSize(new java.awt.Dimension(60, 20));
        m_Properties_PlaneswalkersText.setPreferredSize(new java.awt.Dimension(60, 20));

        m_Properties_PlaneswalkersLabel.setText("Planeswalkers:");
        m_Properties_PlaneswalkersLabel.setPreferredSize(new java.awt.Dimension(80, 10));

        m_Properties_CreaturesText.setEditable(false);
        m_Properties_CreaturesText.setMaximumSize(new java.awt.Dimension(60, 20));
        m_Properties_CreaturesText.setMinimumSize(new java.awt.Dimension(60, 20));
        m_Properties_CreaturesText.setPreferredSize(new java.awt.Dimension(60, 20));

        m_Properties_CreaturesLabel.setText("Creatures:");
        m_Properties_CreaturesLabel.setPreferredSize(new java.awt.Dimension(80, 10));

        m_Properties_ArtifactsLabel.setText("Artifacts:");
        m_Properties_ArtifactsLabel.setPreferredSize(new java.awt.Dimension(80, 10));

        m_Properties_ArtifactsText.setEditable(false);
        m_Properties_ArtifactsText.setMaximumSize(new java.awt.Dimension(60, 20));
        m_Properties_ArtifactsText.setMinimumSize(new java.awt.Dimension(60, 20));
        m_Properties_ArtifactsText.setPreferredSize(new java.awt.Dimension(60, 20));

        m_Properties_EnchantmentsLabel.setText("Enchantments:");
        m_Properties_EnchantmentsLabel.setPreferredSize(new java.awt.Dimension(80, 10));

        m_Properties_EnchantmentsText.setEditable(false);
        m_Properties_EnchantmentsText.setMaximumSize(new java.awt.Dimension(60, 20));
        m_Properties_EnchantmentsText.setMinimumSize(new java.awt.Dimension(60, 20));
        m_Properties_EnchantmentsText.setPreferredSize(new java.awt.Dimension(60, 20));

        m_Properties_SorceriesLabel.setText("Sorceries:");
        m_Properties_SorceriesLabel.setPreferredSize(new java.awt.Dimension(80, 10));

        m_Properties_SorceriesText.setEditable(false);
        m_Properties_SorceriesText.setMaximumSize(new java.awt.Dimension(60, 20));
        m_Properties_SorceriesText.setMinimumSize(new java.awt.Dimension(60, 20));
        m_Properties_SorceriesText.setPreferredSize(new java.awt.Dimension(60, 20));

        m_Properties_InstantsLabel.setText("Instants:");
        m_Properties_InstantsLabel.setPreferredSize(new java.awt.Dimension(80, 10));

        m_Properties_InstantsText.setEditable(false);
        m_Properties_InstantsText.setMaximumSize(new java.awt.Dimension(60, 20));
        m_Properties_InstantsText.setMinimumSize(new java.awt.Dimension(60, 20));
        m_Properties_InstantsText.setPreferredSize(new java.awt.Dimension(60, 20));

        m_Properties_LandsLabel.setText("Lands:");
        m_Properties_LandsLabel.setPreferredSize(new java.awt.Dimension(80, 10));

        m_Properties_LandsText.setEditable(false);
        m_Properties_LandsText.setMaximumSize(new java.awt.Dimension(60, 20));
        m_Properties_LandsText.setMinimumSize(new java.awt.Dimension(60, 20));
        m_Properties_LandsText.setPreferredSize(new java.awt.Dimension(60, 20));

        javax.swing.GroupLayout m_DeckInfo_PropertiesLayout = new javax.swing.GroupLayout(m_DeckInfo_Properties);
        m_DeckInfo_Properties.setLayout(m_DeckInfo_PropertiesLayout);
        m_DeckInfo_PropertiesLayout.setHorizontalGroup(
            m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addComponent(m_Properties_PlaneswalkersLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_PlaneswalkersText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addComponent(m_Properties_InstantsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_InstantsText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addComponent(m_Properties_LandsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_LandsText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addComponent(m_Properties_CreationDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_CreationDateText, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addComponent(m_Properties_ColorsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_ColorsText, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addComponent(m_Properties_DeckNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_DeckNameText, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addComponent(m_Properties_CreaturesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_CreaturesText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addComponent(m_Properties_ArtifactsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_ArtifactsText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addComponent(m_Properties_EnchantmentsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_EnchantmentsText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addComponent(m_Properties_SorceriesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_SorceriesText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_Properties_SizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_Properties_ModificationsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_Properties_ModificationsText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                                .addComponent(m_Properties_SizeText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                                .addComponent(m_Properties_SideboardSizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_Properties_SideboardSizeText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        m_DeckInfo_PropertiesLayout.setVerticalGroup(
            m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_Properties_DeckNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_DeckNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_Properties_ColorsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_ColorsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_Properties_CreationDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_CreationDateText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_ModificationsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_ModificationsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_Properties_SizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_SizeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_SideboardSizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_SideboardSizeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_Properties_PlaneswalkersLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_PlaneswalkersText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_Properties_CreaturesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_CreaturesText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_Properties_ArtifactsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_ArtifactsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_Properties_EnchantmentsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_EnchantmentsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_Properties_SorceriesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_SorceriesText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_Properties_InstantsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_InstantsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_Properties_LandsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_Properties_LandsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout m_DeckInfo_PanelLayout = new javax.swing.GroupLayout(m_DeckInfo_Panel);
        m_DeckInfo_Panel.setLayout(m_DeckInfo_PanelLayout);
        m_DeckInfo_PanelLayout.setHorizontalGroup(
            m_DeckInfo_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_DeckInfo_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_DeckInfo_Properties, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_DeckInfo_ManaCurve, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        m_DeckInfo_PanelLayout.setVerticalGroup(
            m_DeckInfo_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_DeckInfo_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_DeckInfo_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(m_DeckInfo_ManaCurve, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(m_DeckInfo_Properties, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        m_DeckTabPane.addTab("Deck Info", m_DeckInfo_Panel);

        m_Misc_Prices.setBorder(javax.swing.BorderFactory.createTitledBorder("Prices"));

        m_Price_LowText.setEditable(false);
        m_Price_LowText.setText("$0000.00");
        m_Price_LowText.setMaximumSize(new java.awt.Dimension(52, 20));
        m_Price_LowText.setMinimumSize(new java.awt.Dimension(52, 20));

        m_Price_AverageText.setEditable(false);
        m_Price_AverageText.setText("$0000.00");
        m_Price_AverageText.setMaximumSize(new java.awt.Dimension(52, 20));
        m_Price_AverageText.setMinimumSize(new java.awt.Dimension(52, 20));

        m_Price_HighText.setEditable(false);
        m_Price_HighText.setText("$0000.00");
        m_Price_HighText.setMaximumSize(new java.awt.Dimension(52, 20));
        m_Price_HighText.setMinimumSize(new java.awt.Dimension(52, 20));

        jLabel1.setText("Low:");

        jLabel2.setText("Average:");

        jLabel3.setText("High:");

        m_Misc_PricesTable.setModel( m_priceCTM );
        m_Misc_PricesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                m_Misc_PricesTableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                m_Misc_PricesTableMouseReleased(evt);
            }
        });
        m_Misc_PricesTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                m_Misc_PricesTableKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                m_Misc_PricesTableKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(m_Misc_PricesTable);

        javax.swing.GroupLayout m_Misc_PricesLayout = new javax.swing.GroupLayout(m_Misc_Prices);
        m_Misc_Prices.setLayout(m_Misc_PricesLayout);
        m_Misc_PricesLayout.setHorizontalGroup(
            m_Misc_PricesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, m_Misc_PricesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_Price_LowText, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_Price_AverageText, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_Price_HighText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
        );
        m_Misc_PricesLayout.setVerticalGroup(
            m_Misc_PricesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, m_Misc_PricesLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_Misc_PricesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_Price_HighText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(m_Price_AverageText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(m_Price_LowText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)))
        );

        m_Misc_Notes.setBorder(javax.swing.BorderFactory.createTitledBorder("Notes"));

        m_Misc_NotesText.setColumns(20);
        m_Misc_NotesText.setRows(5);
        m_Misc_NotesText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                m_Misc_NotesTextKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(m_Misc_NotesText);

        javax.swing.GroupLayout m_Misc_NotesLayout = new javax.swing.GroupLayout(m_Misc_Notes);
        m_Misc_Notes.setLayout(m_Misc_NotesLayout);
        m_Misc_NotesLayout.setHorizontalGroup(
            m_Misc_NotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
        );
        m_Misc_NotesLayout.setVerticalGroup(
            m_Misc_NotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout m_Misc_PanelLayout = new javax.swing.GroupLayout(m_Misc_Panel);
        m_Misc_Panel.setLayout(m_Misc_PanelLayout);
        m_Misc_PanelLayout.setHorizontalGroup(
            m_Misc_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, m_Misc_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_Misc_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(m_Misc_Notes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(m_Misc_Prices, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        m_Misc_PanelLayout.setVerticalGroup(
            m_Misc_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_Misc_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_Misc_Prices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_Misc_Notes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        m_DeckTabPane.addTab("Misc", m_Misc_Panel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(m_DeckTabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(m_DeckTabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_Properties_DeckNameTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_Properties_DeckNameTextKeyReleased
        if( !m_deck.getName().trim().equals( m_DeckInfoText[ DeckInfo.NAME.ordinal() ].getText().trim() ) ) {
            m_isSaved = false;
            fireActionEvent( LeftPanel.class, Action.ACTION_DECK_CHANGED, Action.COMMAND_DECK_NAME_CHANGED );
        }
    }//GEN-LAST:event_m_Properties_DeckNameTextKeyReleased

    private void m_DeckTabPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_m_DeckTabPaneStateChanged
        int index = m_DeckTabPane.getSelectedIndex();
        WhichHalf part;
        JTable table;
        if( index == DeckTab.MAINDECK.ordinal() ) {
            table = this.m_MainDeck_Table;
            part = WhichHalf.DECK;
        } else if( index == DeckTab.SIDEBOARD.ordinal() ) {
            table = this.m_Sideboard_Table;
            part = WhichHalf.SB;
        } else if( index == DeckTab.MISC.ordinal() ) {
            table = m_Misc_PricesTable;

            if( table.getSelectedRowCount() == 0 ) {
                return;
            }

            int row = table.getSelectedRow();
            String inSB = m_priceCTM.getValueAt( row, PricesTableModel.PriceCols.SB.val ).toString();
            part = ( inSB.equals( "No" ) ? WhichHalf.DECK : WhichHalf.SB );
        } else {
            return;
        }

        cardSelected( table, m_deck, part );
    }//GEN-LAST:event_m_DeckTabPaneStateChanged

    private void m_MainDeck_TableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_MainDeck_TableKeyPressed
        keyPressed( true, evt );
    }//GEN-LAST:event_m_MainDeck_TableKeyPressed

    private void m_MainDeck_TableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_MainDeck_TableMouseClicked
        cardSelected( m_MainDeck_Table, m_deck, WhichHalf.DECK );
    }//GEN-LAST:event_m_MainDeck_TableMouseClicked

    private void m_Sideboard_TableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_Sideboard_TableKeyPressed
        keyPressed( false, evt );
    }//GEN-LAST:event_m_Sideboard_TableKeyPressed

    private void m_Sideboard_TableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_Sideboard_TableMouseClicked
        cardSelected( m_Sideboard_Table, m_deck, WhichHalf.SB );
    }//GEN-LAST:event_m_Sideboard_TableMouseClicked

    private void m_Misc_PricesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_Misc_PricesTableMouseClicked
        if( m_Misc_PricesTable.getSelectedRowCount() != 1 ) {
            return;
        }

        int row = m_Misc_PricesTable.getSelectedRow();
        //String inSB = m_Misc_PricesTable.getValueAt( row, PricesTableModel.PriceCols.SB.val ).toString();
        //WhichHalf part = ( inSB.equals( "No" ) ? WhichHalf.DECK : WhichHalf.SB );
        Boolean inSB = (Boolean)m_Misc_PricesTable.getValueAt( row, PricesTableModel.PriceCols.SB.val );
        WhichHalf part = ( inSB ? WhichHalf.SB : WhichHalf.DECK );
        cardSelected( m_Misc_PricesTable, m_deck, part );
    }//GEN-LAST:event_m_Misc_PricesTableMouseClicked

    private void m_Misc_PricesTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_Misc_PricesTableKeyPressed
        m_Misc_PricesTableMouseClicked( null );
    }//GEN-LAST:event_m_Misc_PricesTableKeyPressed

    private void m_Misc_PricesTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_Misc_PricesTableKeyReleased
        m_Misc_PricesTableMouseClicked( null );
    }//GEN-LAST:event_m_Misc_PricesTableKeyReleased

    private void m_Sideboard_TableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_Sideboard_TableKeyReleased
        keyPressed( false, evt );
    }//GEN-LAST:event_m_Sideboard_TableKeyReleased

    private void m_MainDeck_TableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_MainDeck_TableKeyReleased
        keyPressed( true, evt );
    }//GEN-LAST:event_m_MainDeck_TableKeyReleased

    private void m_MainDeck_TableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_MainDeck_TableMouseReleased
        cardSelected( m_MainDeck_Table, m_deck, WhichHalf.DECK );
    }//GEN-LAST:event_m_MainDeck_TableMouseReleased

    private void m_Sideboard_TableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_Sideboard_TableMouseReleased
        cardSelected( m_Sideboard_Table, m_deck, WhichHalf.SB );
    }//GEN-LAST:event_m_Sideboard_TableMouseReleased

    private void m_Misc_PricesTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_Misc_PricesTableMouseReleased
        m_Misc_PricesTableMouseClicked( null );
    }//GEN-LAST:event_m_Misc_PricesTableMouseReleased

    private void m_Misc_NotesTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_Misc_NotesTextKeyReleased
        if( !m_deck.getNotes().equals( m_Misc_NotesText.getText() ) ) {
            m_isSaved = false;
            fireActionEvent( LeftPanel.class, Action.ACTION_DECK_CHANGED, Action.COMMAND_CURRENT_DECK_CHANGED );
        }
    }//GEN-LAST:event_m_Misc_NotesTextKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPanel m_DeckInfo_ManaCurve;
    private javax.swing.JPanel m_DeckInfo_Panel;
    private javax.swing.JPanel m_DeckInfo_Properties;
    private javax.swing.JTabbedPane m_DeckTabPane;
    private javax.swing.JPanel m_MainDeck_Panel;
    private Deck_Builder.CardTable m_MainDeck_Table;
    private javax.swing.JPanel m_Misc_Notes;
    private javax.swing.JTextArea m_Misc_NotesText;
    private javax.swing.JPanel m_Misc_Panel;
    private javax.swing.JPanel m_Misc_Prices;
    private Deck_Builder.CardTable m_Misc_PricesTable;
    private javax.swing.JTextField m_Price_AverageText;
    private javax.swing.JTextField m_Price_HighText;
    private javax.swing.JTextField m_Price_LowText;
    private javax.swing.JLabel m_Properties_ArtifactsLabel;
    private javax.swing.JTextField m_Properties_ArtifactsText;
    private javax.swing.JLabel m_Properties_ColorsLabel;
    private javax.swing.JTextField m_Properties_ColorsText;
    private javax.swing.JLabel m_Properties_CreationDateLabel;
    private javax.swing.JTextField m_Properties_CreationDateText;
    private javax.swing.JLabel m_Properties_CreaturesLabel;
    private javax.swing.JTextField m_Properties_CreaturesText;
    private javax.swing.JLabel m_Properties_DeckNameLabel;
    private javax.swing.JTextField m_Properties_DeckNameText;
    private javax.swing.JLabel m_Properties_EnchantmentsLabel;
    private javax.swing.JTextField m_Properties_EnchantmentsText;
    private javax.swing.JLabel m_Properties_InstantsLabel;
    private javax.swing.JTextField m_Properties_InstantsText;
    private javax.swing.JLabel m_Properties_LandsLabel;
    private javax.swing.JTextField m_Properties_LandsText;
    private javax.swing.JLabel m_Properties_ModificationsLabel;
    private javax.swing.JTextField m_Properties_ModificationsText;
    private javax.swing.JLabel m_Properties_PlaneswalkersLabel;
    private javax.swing.JTextField m_Properties_PlaneswalkersText;
    private javax.swing.JLabel m_Properties_SideboardSizeLabel;
    private javax.swing.JTextField m_Properties_SideboardSizeText;
    private javax.swing.JLabel m_Properties_SizeLabel;
    private javax.swing.JTextField m_Properties_SizeText;
    private javax.swing.JLabel m_Properties_SorceriesLabel;
    private javax.swing.JTextField m_Properties_SorceriesText;
    private javax.swing.JPanel m_Sideboard_Panel;
    private Deck_Builder.CardTable m_Sideboard_Table;
    // End of variables declaration//GEN-END:variables

}
