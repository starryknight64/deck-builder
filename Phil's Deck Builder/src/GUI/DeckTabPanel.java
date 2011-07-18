/*Copyright (c) 2011 Phillip Ponzer

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. */

/*
 * DeckTabPanel.java
 *
 * Created on Nov 2, 2010, 8:35:05 PM
 */

package GUI;

import Deck_Builder.Action;
import Data.c_PriceDB;
import Data.c_CardDB;
import Data.c_Card;
import Data.c_Deck;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import GUI.CardTable.DeckTableModel;
import GUI.CardTable.PricesTableModel;
import Data.c_Deck.WhichHalf;
import Data.c_Deck.WhichType;
import Data.c_ExpansionDB.Legals;
import Data.c_Price;
import Data.c_Price.PriceType;
import Parsers.DeckFormat;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Phillip
 */
public class DeckTabPanel extends JPanel {

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
        public int val = this.ordinal();
    }
    private JTextField[] m_DeckInfoText;

    private enum DeckTab {
        MAINDECK,
        SIDEBOARD,
        DECKINFO,
        MISC;
        public int val = this.ordinal();
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

    /** Creates new form DeckTabPanel */
    public DeckTabPanel() {
        m_db = new c_CardDB();
        m_priceDB = new c_PriceDB();
        initComponents();
        init();
    }
    public DeckTabPanel( c_CardDB db, c_PriceDB pdb ) {
        m_db = db;
        m_priceDB = pdb;
        initComponents();
        init();
    }
    public DeckTabPanel( DeckFormat format, String deckfilepath, c_CardDB db, c_PriceDB pdb ) {
        super();
        m_db = db;
        m_priceDB = pdb;
        initComponents();
        init();
        loadDeckFromFile( format, deckfilepath, db );
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

        updateDeckInfo();
    }

    private void loadDeckFromFile( DeckFormat format, String deckfilepath, c_CardDB db ) {
        m_isLoading = true;

        c_Deck deck = new c_Deck();
        format.loadDeck( deckfilepath, deck, db );
        m_deck.setDeckInfo( deck );

        HashMap<Integer, Integer> cards = deck.getCards();
        c_Card card;

        for( int mid : cards.keySet() ) {
            card = m_db.getCard( mid );
            addCard( card, cards.get( mid ), true );
        }

        cards = deck.getSBCards();
        for( int mid : cards.keySet() ) {
            card = m_db.getCard( mid );
            addCard( card, cards.get( mid ), false );
        }

        m_isLoading = false;
        m_filename = deckfilepath;

        deck = null;
        cards = null;
        card = null;
    }

    public c_Deck getDeck() {
        return m_deck;
    }

    public boolean saveDeck( DeckFormat filter, String filepath ) {
        m_isSaved = ((DeckFormat)filter).saveDeck( filepath, m_deck, m_db );
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
        boolean toDeck = (m_DeckTabPane.getSelectedIndex() != DeckTab.SIDEBOARD.val);
        addCard( card, amount, toDeck );
    }
    public void addCard( c_Card card, Integer amount, boolean toDeck ) {
        DeckTableModel dtm = toDeck ? m_deckCTM : m_sbCTM;
        CardTable table = toDeck ? m_MainDeck_Table : m_Sideboard_Table;
        Integer row = dtm.getMIDRow( card.MID );
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
        row = table.convertRowIndexToView( dtm.getMIDRow( card.MID ) );
        table.changeSelection( row, 0, false, false );

        if( !m_isLoading ) {
            m_isSaved = false;
        }
        fireActionEvent( LeftPanel.class, Action.ACTION_DECK_CHANGED, Action.COMMAND_CURRENT_DECK_CHANGED );
        updateDeckInfo();

        dtm = null;
        table = null;
        row = null;
        cur_amt = null;
        new_amt = null;
    }


    public void deleteCard( c_Card card ) {
        boolean fromDeck = (m_DeckTabPane.getSelectedIndex() != DeckTab.SIDEBOARD.val);
        DeckTableModel dtm = fromDeck ? m_deckCTM : m_sbCTM;
        Integer row = dtm.getMIDRow( card.MID );
        
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
    }

    public Integer getSelectedCard() {
        Integer mid = 0;
        if( m_DeckTabPane.getSelectedIndex() != DeckTab.MISC.val ) {
            boolean fromDeck = (m_DeckTabPane.getSelectedIndex() != DeckTab.SIDEBOARD.val);
            DeckTableModel dtm = fromDeck ? m_deckCTM : m_sbCTM;
            JTable table = fromDeck ? m_MainDeck_Table : m_Sideboard_Table;
            Integer row = table.convertRowIndexToModel( table.getSelectedRow() );
            mid = dtm.getMID( row );

            dtm = null;
            table = null;
        } else { /* Get selected card from prices table */
            int row = m_Misc_PricesTable.convertRowIndexToModel( m_Misc_PricesTable.getSelectedRow() );
            mid = m_priceCTM.getMID( row );
        }
        
        return mid;
    }

    public void updateDeckInfo() {
        m_DeckInfoText[ DeckInfo.NAME.val          ].setText( m_deck.getName() );

        if( m_DeckInfoText[ DeckInfo.NAME.val ].getText().charAt( 0 ) != this.getFormattedDeckName().charAt( 0 )
         || m_isSaved ) {
            fireActionEvent( LeftPanel.class, Action.ACTION_DECK_CHANGED, Action.COMMAND_DECK_NAME_CHANGED );
        }

        m_DeckInfoText[ DeckInfo.COLORS.val        ].setText( m_deck.getColorsText( m_db ) );

        if( m_DeckInfoText[ DeckInfo.CREATIONDATE.val ].getText().equals( "" ) ) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat( "MMM. dd, yyyy h:mm aaa" );
            m_deck.setCreationDate( sdf.format( cal.getTime() ) );
            m_DeckInfoText[ DeckInfo.CREATIONDATE.val ].setText( m_deck.getCreationDate() );

            cal = null;
            sdf = null;
        }

        m_DeckInfoText[ DeckInfo.MODIFICATIONS.val ].setText( m_deck.getModifications().toString() );

        m_DeckInfoText[ DeckInfo.SIZE.val          ].setText( m_deck.getSizeOf( WhichHalf.DECK ).toString() );
        m_DeckInfoText[ DeckInfo.SIDEBOARDSIZE.val ].setText( m_deck.getSizeOf( WhichHalf.SB ).toString() );
        m_DeckTabPane.setTitleAt( DeckTab.MAINDECK.val, "Main Deck (" + m_DeckInfoText[ DeckInfo.SIZE.val ].getText() + ")" );
        m_DeckTabPane.setTitleAt( DeckTab.SIDEBOARD.val, "Sideboard (" + m_DeckInfoText[ DeckInfo.SIDEBOARDSIZE.val ].getText() + ")" );
        
        m_DeckInfoText[ DeckInfo.PLANESWALKERS.val ].setText( m_deck.getAmountOfType( WhichType.PLANESWALKERS, WhichHalf.BOTH, m_db ).toString() );
        m_DeckInfoText[ DeckInfo.CREATURES.val     ].setText( m_deck.getAmountOfType( WhichType.CREATURES,     WhichHalf.BOTH, m_db ).toString() );
        m_DeckInfoText[ DeckInfo.ARTIFACTS.val     ].setText( m_deck.getAmountOfType( WhichType.ARTIFACTS,     WhichHalf.BOTH, m_db ).toString() );
        m_DeckInfoText[ DeckInfo.ENCHANTMENTS.val  ].setText( m_deck.getAmountOfType( WhichType.ENCHANTMENTS,  WhichHalf.BOTH, m_db ).toString() );
        m_DeckInfoText[ DeckInfo.SORCERIES.val     ].setText( m_deck.getAmountOfType( WhichType.SORCERIES,     WhichHalf.BOTH, m_db ).toString() );
        m_DeckInfoText[ DeckInfo.INSTANTS.val      ].setText( m_deck.getAmountOfType( WhichType.INSTANTS,      WhichHalf.BOTH, m_db ).toString() );
        m_DeckInfoText[ DeckInfo.LANDS.val         ].setText( m_deck.getAmountOfType( WhichType.LANDS,         WhichHalf.BOTH, m_db ).toString() );

        c_Price price = m_priceDB.getPrice( m_deck, WhichHalf.BOTH );
        m_Price_LowText.setText( price.toString( PriceType.LOW ) );
        m_Price_AverageText.setText( price.toString( PriceType.AVERAGE ) );
        m_Price_HighText.setText( price.toString( PriceType.HIGH ) );
        price = null;

        if( m_db.getExpansionDB() != null ) {
            ArrayList<String> blocks = m_db.getExpansionDB().getBlocks();
            ArrayList<String> legals = new ArrayList<String>();
            for( String block : blocks ) {
                if( m_db.isInBlock( m_deck, block ) ) {
                    legals.add( block + " Block" );
                }
            }
            for( Legals leg : Legals.values() ) {
                if( m_db.isInLegal( m_deck, leg ) ) {
                    legals.add( leg.name() + " Legal" );
                }
            }
            m_DeckInfo_LegalList.setListData( legals.toArray() );
            legals = null;
            blocks = null;
        }

        CardTable.autoResizeColWidth( m_MainDeck_Table );
        CardTable.autoResizeColWidth( m_Sideboard_Table );
        CardTable.autoResizeColWidth( m_Misc_PricesTable );
    }

    public String getFormattedDeckName() {
        String name = m_DeckInfoText[ DeckInfo.NAME.val ].getText().trim();
        if( name.length() > 16 ) {
            name = name.substring( 0, 16 ) + "...";
        }
        return ( m_isSaved ? "" : "*" ) + name;
    }

    public String getDeckName() {
        return m_DeckInfoText[ DeckInfo.NAME.val ].getText().trim();
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
        Integer mid = dtm.getMID( row );
        c_Card card = m_db.getCard( mid );
        addCard( card, amount );

        table = null;
        dtm = null;
        card = null;
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
                return;
            }
        }

        listeners = null;
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
        m_MainDeck_Table = new GUI.CardTable();
        m_Sideboard_Panel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_Sideboard_Table = new GUI.CardTable();
        m_DeckInfo_Panel = new javax.swing.JPanel();
        m_DeckInfo_Legality = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        m_DeckInfo_LegalList = new javax.swing.JList();
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
        m_Misc_PricesTable = new GUI.CardTable();
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

        m_DeckInfo_Legality.setBorder(javax.swing.BorderFactory.createTitledBorder("Tournament Legality"));

        jScrollPane3.setViewportView(m_DeckInfo_LegalList);

        javax.swing.GroupLayout m_DeckInfo_LegalityLayout = new javax.swing.GroupLayout(m_DeckInfo_Legality);
        m_DeckInfo_Legality.setLayout(m_DeckInfo_LegalityLayout);
        m_DeckInfo_LegalityLayout.setHorizontalGroup(
            m_DeckInfo_LegalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_DeckInfo_LegalityLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addContainerGap())
        );
        m_DeckInfo_LegalityLayout.setVerticalGroup(
            m_DeckInfo_LegalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
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
                        .addComponent(m_Properties_CreationDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_CreationDateText, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addComponent(m_Properties_ColorsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_ColorsText, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addComponent(m_Properties_DeckNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_Properties_DeckNameText, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_Properties_SizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_Properties_ModificationsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_Properties_ModificationsText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                                .addComponent(m_Properties_SizeText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                                .addComponent(m_Properties_SideboardSizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_Properties_SideboardSizeText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                        .addGroup(m_DeckInfo_PropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                                .addComponent(m_Properties_InstantsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_Properties_InstantsText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                                .addComponent(m_Properties_SorceriesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_Properties_SorceriesText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                                .addComponent(m_Properties_ArtifactsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_Properties_ArtifactsText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                                .addComponent(m_Properties_EnchantmentsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_Properties_EnchantmentsText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                                .addComponent(m_Properties_PlaneswalkersLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_Properties_PlaneswalkersText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                                .addComponent(m_Properties_CreaturesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_Properties_CreaturesText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(m_DeckInfo_PropertiesLayout.createSequentialGroup()
                                .addComponent(m_Properties_LandsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_Properties_LandsText, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(130, 130, 130)))
                .addContainerGap())
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
                .addComponent(m_DeckInfo_Legality, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        m_DeckInfo_PanelLayout.setVerticalGroup(
            m_DeckInfo_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_DeckInfo_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_DeckInfo_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(m_DeckInfo_Properties, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(m_DeckInfo_Legality, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
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
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
        );
        m_Misc_NotesLayout.setVerticalGroup(
            m_Misc_NotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
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
        if( !m_deck.getName().trim().equals( m_DeckInfoText[ DeckInfo.NAME.val ].getText().trim() ) ) {
            m_isSaved = false;
            fireActionEvent( LeftPanel.class, Action.ACTION_DECK_CHANGED, Action.COMMAND_DECK_NAME_CHANGED );
        }
    }//GEN-LAST:event_m_Properties_DeckNameTextKeyReleased

    private void m_DeckTabPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_m_DeckTabPaneStateChanged
        int index = m_DeckTabPane.getSelectedIndex();
        WhichHalf part;
        JTable table;
        if( index == DeckTab.MAINDECK.val ) {
            table = this.m_MainDeck_Table;
            part = WhichHalf.DECK;
        } else if( index == DeckTab.SIDEBOARD.val ) {
            table = this.m_Sideboard_Table;
            part = WhichHalf.SB;
        } else if( index == DeckTab.MISC.val ) {
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JList m_DeckInfo_LegalList;
    private javax.swing.JPanel m_DeckInfo_Legality;
    private javax.swing.JPanel m_DeckInfo_Panel;
    private javax.swing.JPanel m_DeckInfo_Properties;
    private javax.swing.JTabbedPane m_DeckTabPane;
    private javax.swing.JPanel m_MainDeck_Panel;
    private GUI.CardTable m_MainDeck_Table;
    private javax.swing.JPanel m_Misc_Notes;
    private javax.swing.JTextArea m_Misc_NotesText;
    private javax.swing.JPanel m_Misc_Panel;
    private javax.swing.JPanel m_Misc_Prices;
    private GUI.CardTable m_Misc_PricesTable;
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
    private GUI.CardTable m_Sideboard_Table;
    // End of variables declaration//GEN-END:variables

}
