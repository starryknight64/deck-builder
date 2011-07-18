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
 * LeftPanel.java
 *
 * Created on Nov 2, 2010, 7:25:48 PM
 */

package GUI;

import Deck_Builder.Dialog;
import Deck_Builder.Action;
import Data.c_PriceDB;
import Data.c_ExpansionDB;
import Data.c_CardDB;
import Data.c_Deck;
import Data.c_Card;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import GUI.CardTable.RecentlyViewedTableModel;
import Parsers.DeckFormat;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Phillip
 */
public class LeftPanel extends JPanel implements ActionListener {
    private RecentlyViewedTableModel m_recentlyViewedCTM = new RecentlyViewedTableModel();

    private c_Deck m_recentlyViewedCards = new c_Deck();
    private c_ExpansionDB m_expansionDB;
    private c_CardDB m_cardDB;
    private c_PriceDB m_priceDB;
    private c_Card m_previewCard = new c_Card();
    public static final Integer MAX_DECKS = 6;
    
    private boolean isAddingNewDeck = false;
    private boolean isClosingDeck = false;
    private EventListenerList m_listeners = new EventListenerList();

    /** Creates new form LeftPanel */
    public LeftPanel() {
        super();
        initComponents();
        setAddCardPanelVisibility( false );
    }

    public boolean loadDBs( ActionListener listener ) {
        boolean success = true;
        m_expansionDB = new c_ExpansionDB();
        success &= m_expansionDB.loadExpansionDB( listener );
        m_cardDB = new c_CardDB( m_expansionDB );
        success &= m_cardDB.loadCardDB( listener );
        m_priceDB = new c_PriceDB( m_cardDB );
        success &= m_priceDB.loadPricesDB( listener );
        addDeckTabPanel( new DeckTabPanel( m_cardDB, m_priceDB ) );

        return success;
    }

    public c_CardDB getCardDB() {
        return m_cardDB;
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

    public void loadDeck( DeckFormat format, String filepath ) {
        DeckTabPanel dtp = new DeckTabPanel( format, filepath, m_cardDB, m_priceDB );
        addDeckTabPanel( dtp );
        dtp = null;
    }

    private void addDeckTabPanel( DeckTabPanel dtp ) {
        isAddingNewDeck = true;
        dtp.addActionListener( this );
        m_DeckTabs.insertTab( dtp.getDeckName(), null, dtp, null, m_DeckTabs.getTabCount() - 1 );
        m_DeckTabs.setSelectedIndex( m_DeckTabs.getTabCount() - 2 );
        isAddingNewDeck = false;
    }

    public boolean saveCurrentDeck( DeckFormat format, String filepath ) {
        DeckTabPanel curDeckTab = (DeckTabPanel)m_DeckTabs.getSelectedComponent();
        boolean success = curDeckTab.saveDeck( format, filepath );
        curDeckTab = null;
        return success;
    }

    public DeckTabPanel getCurrentDeckTab() {
        return (DeckTabPanel)m_DeckTabs.getSelectedComponent();
    }

    public int getTotalDecks() {
        return m_DeckTabs.getTabCount() - 1;
    }

    public ArrayList<DeckTabPanel> getAllDecks() {
        ArrayList<DeckTabPanel> decks = new ArrayList<DeckTabPanel>();
        DeckTabPanel deck;
        for( int i=0; i<m_DeckTabs.getTabCount()-1; i++ ) {
            deck = (DeckTabPanel)m_DeckTabs.getComponentAt( i );
            decks.add( deck );
        }

        deck = null;
        return decks;
    }

    public void setPreviewCard( c_Card card ) {
        Integer row = m_recentlyViewedCTM.getMIDRow( card.MID );

        if( row < 0 ) {
            m_recentlyViewedCards.addCard( card.MID, 1, true );
            m_cardDB.addCard( card );
            m_recentlyViewedCTM.addCard( card );
            CardTable.autoResizeColWidth( m_RecentlyViewed_Table );
        }

        m_previewCard = card;
        m_CardPreview_Image.setIcon( m_previewCard.getImage() );

        row = null;
    }

    public void actionPerformed( ActionEvent e ) {
        String command = e.getActionCommand();
        if( command.equals( Action.COMMAND_CARD_PREVIEW ) ) {
            DeckTabPanel curDeckTab = (DeckTabPanel)m_DeckTabs.getSelectedComponent();
            Integer mid = curDeckTab.getSelectedCard();
            c_Card card = m_cardDB.getCard( mid );
            setPreviewCard( card );

            curDeckTab = null;
            mid = null;
            card = null;
        } else if( command.equals( Action.COMMAND_DECK_NAME_CHANGED ) || command.equals( Action.COMMAND_CURRENT_DECK_CHANGED ) ) {
            DeckTabPanel curDeckTab = (DeckTabPanel)m_DeckTabs.getSelectedComponent();
            m_DeckTabs.setTitleAt( m_DeckTabs.getSelectedIndex(), curDeckTab.getFormattedDeckName() );
            fireActionEvent( MainWindow.class, Action.ACTION_DECK_CHANGED, Action.COMMAND_CURRENT_DECK_CHANGED );
            curDeckTab = null;
        }

        command = null;
    }
    
    public void addNewDeck() {
        if( !isAddingNewDeck ) {
            isAddingNewDeck = true;
            DeckTabPanel dtp = new DeckTabPanel( m_cardDB, m_priceDB );
            dtp.addActionListener( this );
            m_DeckTabs.insertTab( "Untitled Deck", null, dtp, null, m_DeckTabs.getTabCount()-1 );
            m_DeckTabs.setSelectedIndex( m_DeckTabs.getTabCount() - 2 );
            fireActionEvent( MainWindow.class, Action.ACTION_DECK_CHANGED, Action.COMMAND_CURRENT_DECK_CHANGED );
            isAddingNewDeck = false;
            
            dtp = null;
        }
    }

    public void closeDeck( DeckTabPanel deck ) {
        isClosingDeck = true;
        m_DeckTabs.remove( deck );
        isClosingDeck = false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_RecentlyViewed_Panel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_RecentlyViewed_Table = new GUI.CardTable();
        m_CardPreview_Panel = new javax.swing.JPanel();
        m_CardPreview_AddCard = new javax.swing.JPanel();
        m_AddCard_Amount = new javax.swing.JComboBox();
        m_AddCard_Button = new javax.swing.JButton();
        m_AddCard_Label = new javax.swing.JLabel();
        m_CardPreview_Image = new javax.swing.JLabel();
        m_DeckTabs = new javax.swing.JTabbedPane();
        m_DeckTabs_NewDeck = new javax.swing.JPanel();

        m_RecentlyViewed_Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Recently Viewed"));

        m_RecentlyViewed_Table.setModel( m_recentlyViewedCTM );
        m_RecentlyViewed_Table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                m_RecentlyViewed_TableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                m_RecentlyViewed_TableMouseReleased(evt);
            }
        });
        m_RecentlyViewed_Table.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                m_RecentlyViewed_TableKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                m_RecentlyViewed_TableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(m_RecentlyViewed_Table);

        javax.swing.GroupLayout m_RecentlyViewed_PanelLayout = new javax.swing.GroupLayout(m_RecentlyViewed_Panel);
        m_RecentlyViewed_Panel.setLayout(m_RecentlyViewed_PanelLayout);
        m_RecentlyViewed_PanelLayout.setHorizontalGroup(
            m_RecentlyViewed_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
        );
        m_RecentlyViewed_PanelLayout.setVerticalGroup(
            m_RecentlyViewed_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );

        m_CardPreview_Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Card Preview"));
        m_CardPreview_Panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        m_CardPreview_AddCard.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        m_AddCard_Amount.setMaximumRowCount(20);
        m_AddCard_Amount.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "Other..." }));
        m_AddCard_Amount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_AddCard_AmountActionPerformed(evt);
            }
        });

        m_AddCard_Button.setText("Add");
        m_AddCard_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_AddCard_ButtonActionPerformed(evt);
            }
        });

        m_AddCard_Label.setText("Add How Many?");
        m_AddCard_Label.setOpaque(true);

        javax.swing.GroupLayout m_CardPreview_AddCardLayout = new javax.swing.GroupLayout(m_CardPreview_AddCard);
        m_CardPreview_AddCard.setLayout(m_CardPreview_AddCardLayout);
        m_CardPreview_AddCardLayout.setHorizontalGroup(
            m_CardPreview_AddCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_CardPreview_AddCardLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(m_AddCard_Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_AddCard_Amount, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_AddCard_Button, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
        );
        m_CardPreview_AddCardLayout.setVerticalGroup(
            m_CardPreview_AddCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_CardPreview_AddCardLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(m_CardPreview_AddCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_AddCard_Label)
                    .addComponent(m_AddCard_Amount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_AddCard_Button)))
        );

        m_CardPreview_Panel.add(m_CardPreview_AddCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 224, 30));

        m_CardPreview_Image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Cards/Images/0.png"))); // NOI18N
        m_CardPreview_Image.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                m_CardPreview_ImageMouseClicked(evt);
            }
        });
        m_CardPreview_Panel.add(m_CardPreview_Image, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 223, 310));

        m_DeckTabs.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        m_DeckTabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                m_DeckTabsStateChanged(evt);
            }
        });

        javax.swing.GroupLayout m_DeckTabs_NewDeckLayout = new javax.swing.GroupLayout(m_DeckTabs_NewDeck);
        m_DeckTabs_NewDeck.setLayout(m_DeckTabs_NewDeckLayout);
        m_DeckTabs_NewDeckLayout.setHorizontalGroup(
            m_DeckTabs_NewDeckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 486, Short.MAX_VALUE)
        );
        m_DeckTabs_NewDeckLayout.setVerticalGroup(
            m_DeckTabs_NewDeckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 423, Short.MAX_VALUE)
        );

        m_DeckTabs.addTab("New Deck", m_DeckTabs_NewDeck);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(m_DeckTabs, 0, 0, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(m_RecentlyViewed_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_CardPreview_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_CardPreview_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_RecentlyViewed_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_DeckTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_DeckTabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_m_DeckTabsStateChanged
        if( m_DeckTabs.getSelectedIndex() == m_DeckTabs.getTabCount() - 1
         && m_DeckTabs.getSelectedIndex() > 0
         && m_DeckTabs.getTabCount() < MAX_DECKS + 1 ) {
            addNewDeck();
        } else if( isClosingDeck ) {
            fireActionEvent( MainWindow.class, Action.ACTION_DECK_CHANGED, Action.COMMAND_CURRENT_DECK_CHANGED );
        } else if( m_DeckTabs.getSelectedIndex() >= MAX_DECKS ) {
            m_DeckTabs.setSelectedIndex( m_DeckTabs.getTabCount() - 2 );
        }
    }//GEN-LAST:event_m_DeckTabsStateChanged

    private void m_AddCard_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_AddCard_ButtonActionPerformed
        int amount = Integer.parseInt( m_AddCard_Amount.getSelectedItem().toString() );
        addPreviewCardToCurrentDeck( amount );
    }//GEN-LAST:event_m_AddCard_ButtonActionPerformed

    private void addPreviewCardToCurrentDeck( int amt ) {                                                 
        DeckTabPanel curDeckTab = (DeckTabPanel)m_DeckTabs.getSelectedComponent();
        curDeckTab.addCard( m_previewCard, amt );
        curDeckTab = null;
    }

    private void setAddCardPanelVisibility( boolean val ) {
        m_CardPreview_AddCard.setVisible( val );
        m_AddCard_Label.setVisible( val );
        m_AddCard_Amount.setVisible( val );
        m_AddCard_Button.setVisible( val );
    }

    private void m_CardPreview_ImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_CardPreview_ImageMouseClicked
        if( evt.getClickCount() > 1 && m_previewCard.MID > 0 ) {
            setAddCardPanelVisibility( !m_CardPreview_AddCard.isVisible() );
        }
    }//GEN-LAST:event_m_CardPreview_ImageMouseClicked

    private void m_AddCard_AmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_AddCard_AmountActionPerformed
        if( m_AddCard_Amount.getSelectedIndex() == m_AddCard_Amount.getItemCount() - 1 ) {
            Integer max = Integer.parseInt( m_AddCard_Amount.getItemAt( 0 ).toString() );
            for( int i=1; i<m_AddCard_Amount.getItemCount()-1; i++ ) {
                max = Math.max( Integer.parseInt( m_AddCard_Amount.getItemAt(i).toString() ), max );
            }
            max++;

            String amt_text = Dialog.InputBox( null, "How many card(s) would you like to add?", max.toString() );

            if( amt_text == null ) {
                m_AddCard_Amount.setSelectedIndex( 0 );
                return;
            }

            try {
                Integer amt = Integer.parseInt( amt_text );

                if( amt <= 0 ) {
                    throw new Exception();
                }

                boolean found = false;
                for( int i=0; i<m_AddCard_Amount.getItemCount(); i++ ) {
                    if( m_AddCard_Amount.getItemAt(i).toString().equals( amt.toString() ) ) {
                        m_AddCard_Amount.setSelectedIndex( i );
                        found = true;
                        break;
                    }
                }

                if( !found ) {
                    m_AddCard_Amount.insertItemAt( amt, m_AddCard_Amount.getItemCount() - 1 );
                    m_AddCard_Amount.setSelectedIndex( m_AddCard_Amount.getItemCount() - 2 );
                }

                m_AddCard_ButtonActionPerformed( null );
                setAddCardPanelVisibility( false );
            } catch( Exception ex ) {
                m_AddCard_Amount.setSelectedIndex( 0 );
                Dialog.MsgBox( null, "Please enter a valid, non-zero, non-negative integer!" );
            }
        }
    }//GEN-LAST:event_m_AddCard_AmountActionPerformed

    private void m_RecentlyViewed_TableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_RecentlyViewed_TableMouseClicked
        previewCardSelected( evt.getClickCount() == 2 );
    }//GEN-LAST:event_m_RecentlyViewed_TableMouseClicked

    private void previewCardSelected( boolean addCardToDeck ) {
        if( m_RecentlyViewed_Table.getRowCount() == 0
         || m_RecentlyViewed_Table.getSelectedRowCount() != 1 ) {
            return;
        }
        
        int row = m_RecentlyViewed_Table.convertRowIndexToModel( m_RecentlyViewed_Table.getSelectedRow() );
        Integer mid = m_recentlyViewedCTM.getMID( row );
        if( mid != null
         && !mid.toString().equals( "" ) ) {
            c_Card card = m_cardDB.getCard( mid );
            setPreviewCard( card );

            card = null;
        }

        if( addCardToDeck ) {
            addPreviewCardToCurrentDeck( 1 );
        }
    }
    
    private void m_RecentlyViewed_TableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_RecentlyViewed_TableKeyPressed
        previewCardSelected( evt.getKeyCode() == KeyEvent.VK_ENTER );
    }//GEN-LAST:event_m_RecentlyViewed_TableKeyPressed

    private void m_RecentlyViewed_TableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_RecentlyViewed_TableKeyReleased
        previewCardSelected( evt.getKeyCode() == KeyEvent.VK_ENTER );
    }//GEN-LAST:event_m_RecentlyViewed_TableKeyReleased

    private void m_RecentlyViewed_TableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_RecentlyViewed_TableMouseReleased
        previewCardSelected( false );
    }//GEN-LAST:event_m_RecentlyViewed_TableMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox m_AddCard_Amount;
    private javax.swing.JButton m_AddCard_Button;
    private javax.swing.JLabel m_AddCard_Label;
    private javax.swing.JPanel m_CardPreview_AddCard;
    private javax.swing.JLabel m_CardPreview_Image;
    private javax.swing.JPanel m_CardPreview_Panel;
    private javax.swing.JTabbedPane m_DeckTabs;
    private javax.swing.JPanel m_DeckTabs_NewDeck;
    private javax.swing.JPanel m_RecentlyViewed_Panel;
    private GUI.CardTable m_RecentlyViewed_Table;
    // End of variables declaration//GEN-END:variables

}
