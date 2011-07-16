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
 * ProxyPrinter.java
 *
 * Created on Feb 19, 2011, 8:26:01 AM
 */

package GUI;

import Deck_Builder.Action;
import Data.c_CardDB;
import Data.c_Card;
import Data.c_Deck;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.HashMap;
import GUI.CardTable.ProxiesTableModel;
import GUI.CardTable.ProxiesTableModel.ProxyCols;
import Data.c_Deck.WhichHalf;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Phillip
 */
public class ProxyPrinter extends JPanel implements Pageable {

    private static final int DPI = 72;
    private static final float CARD_WIDTH_IN = 2.5f;
    private static final float CARD_HEIGHT_IN = 3.5f;
    private static final int CARD_WIDTH_PX = (int)(DPI * CARD_WIDTH_IN);
    private static final int CARD_HEIGHT_PX = (int)(DPI * CARD_HEIGHT_IN);

    private static final float PAGE_WIDTH_IN = 8.5f;
    private static final float PAGE_HEIGHT_IN = 11.0f;
    private static final int PAGE_MARGIN = 0;
    private static final int PAGE_WIDTH_PX =  (int)(DPI * PAGE_WIDTH_IN)  - (2*PAGE_MARGIN);
    private static final int PAGE_HEIGHT_PX = (int)(DPI * PAGE_HEIGHT_IN) - (2*PAGE_MARGIN);

    private static final int CARDS_PER_PAGE_HORZ = PAGE_WIDTH_PX  / CARD_WIDTH_PX;
    private static final int CARDS_PER_PAGE_VERT = PAGE_HEIGHT_PX / CARD_HEIGHT_PX;
    private static final int CARDS_PER_PAGE = CARDS_PER_PAGE_HORZ * CARDS_PER_PAGE_VERT;

    private static final int CARD_SPACER_HORZ = (PAGE_WIDTH_PX  - (CARDS_PER_PAGE_HORZ * CARD_WIDTH_PX))  / (CARDS_PER_PAGE_HORZ+1);
    private static final int CARD_SPACER_VERT = (PAGE_HEIGHT_PX - (CARDS_PER_PAGE_VERT * CARD_HEIGHT_PX)) / (CARDS_PER_PAGE_VERT+1);

    private ArrayList<ProxyPage> m_pages = new ArrayList<ProxyPage>();
    private int m_cardsTotal = 0;

    private c_Deck m_deck;
    private HashMap<Integer, Integer> m_cards;
    private c_CardDB m_db;
    private ProxiesTableModel m_proxyTableModel = new ProxiesTableModel();
    private ProxyPrinterDialog m_dialog;

    private EventListenerList m_listeners = new EventListenerList();

    /** Creates new form ProxyPrinter */
    public ProxyPrinter() {
        initComponents();
        m_deck = new c_Deck();
        m_cards = new HashMap<Integer, Integer>();
        m_db = new c_CardDB();
    }

    public ProxyPrinter( c_Deck deck, c_CardDB db ) {
        initComponents();
        m_deck = deck;
        m_cards = m_deck.getAllCards();
        m_db = db;
        addDeckHalf( m_deck.getCards(), WhichHalf.DECK );
        addDeckHalf( m_deck.getSBCards(), WhichHalf.SB );
        CardTable.autoResizeColWidth( m_proxyTable );
    }

    public void setDialog( ProxyPrinterDialog dlg ) {
        m_dialog = dlg;
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

    private void addDeckHalf( HashMap<Integer, Integer> cards, WhichHalf which ) {
        Boolean inSB = (which == WhichHalf.SB);
        for( int mid : cards.keySet() ) {
            c_Card card = m_db.getCard( mid );
            int amt = m_deck.getAmountOfCard( mid, which );
            m_proxyTableModel.addMID( mid );
            m_proxyTableModel.insertRow( m_proxyTable.getRowCount(), new Object[] { inSB, card.Name, card.Type, card.Expansion, amt, amt } );
        }
    }

    private void updatePreviewCard() {
        if( m_proxyTable.getSelectedRowCount() == 1 ) {
            int row = m_proxyTable.convertRowIndexToModel( m_proxyTable.getSelectedRow() );
            Integer mid = m_proxyTableModel.getMID( row );
            fireActionEvent( MainWindow.class, Action.ACTION_PROXY_CARD_SELECTED, mid.toString() );
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

        m_cancelButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        m_selectAllButton = new javax.swing.JButton();
        m_selectInvertButton = new javax.swing.JButton();
        m_selectNoneButton = new javax.swing.JButton();
        m_selectSideboardButton = new javax.swing.JButton();
        m_selectLandsButton = new javax.swing.JButton();
        m_selectEachButton = new javax.swing.JButton();
        m_selectEachText = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_proxyTable = new GUI.CardTable();
        m_printButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(303, 316));

        m_cancelButton.setText("Cancel");
        m_cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_cancelButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Select"));

        m_selectAllButton.setText("All");
        m_selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_selectAllButtonActionPerformed(evt);
            }
        });

        m_selectInvertButton.setText("Invert");
        m_selectInvertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_selectInvertButtonActionPerformed(evt);
            }
        });

        m_selectNoneButton.setText("None");
        m_selectNoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_selectNoneButtonActionPerformed(evt);
            }
        });

        m_selectSideboardButton.setText("Sideboard");
        m_selectSideboardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_selectSideboardButtonActionPerformed(evt);
            }
        });

        m_selectLandsButton.setText("Lands");
        m_selectLandsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_selectLandsButtonActionPerformed(evt);
            }
        });

        m_selectEachButton.setText("Each");
        m_selectEachButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_selectEachButtonActionPerformed(evt);
            }
        });

        m_selectEachText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_selectEachText.setText("4");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(m_selectAllButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(m_selectNoneButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(m_selectInvertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(m_selectLandsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(m_selectEachText, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_selectEachButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(m_selectSideboardButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_selectAllButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_selectNoneButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_selectSideboardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_selectInvertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_selectLandsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_selectEachText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_selectEachButton)))
        );

        m_proxyTable.setModel(m_proxyTableModel);
        m_proxyTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                m_proxyTableMouseClicked(evt);
            }
        });
        m_proxyTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                m_proxyTableKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                m_proxyTableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(m_proxyTable);

        m_printButton.setText("Print");
        m_printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_printButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(141, Short.MAX_VALUE)
                .addComponent(m_printButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane1, 0, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_printButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_proxyTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_proxyTableKeyPressed
        updatePreviewCard();
    }//GEN-LAST:event_m_proxyTableKeyPressed

    private void m_proxyTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_proxyTableKeyReleased
        updatePreviewCard();
    }//GEN-LAST:event_m_proxyTableKeyReleased

    private void m_proxyTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_proxyTableMouseClicked
        updatePreviewCard();
    }//GEN-LAST:event_m_proxyTableMouseClicked

    private void m_printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_printButtonActionPerformed
        PrinterJob pj = PrinterJob.getPrinterJob();

        int ind = 0;
        ProxyPage page = new ProxyPage( ind++, m_db );
        HashMap<Integer, Integer> cards = m_deck.getCards();
        for( int mid : cards.keySet() ) {
            int notAdded = page.addCard( mid, cards.get( mid ) );
            while( notAdded > 0 ) {
                m_pages.add( page );
                page = new ProxyPage( ind++, m_db );
                notAdded = page.addCard( mid, notAdded );
            }
        }
        if( page.hasCards() ) {
            m_pages.add( page );
        }

        pj.setPageable( this );
        if( pj.printDialog() ) {
            try {
                pj.print();
            } catch (PrinterException e) {
                System.out.println(e);
            }
        }
    }//GEN-LAST:event_m_printButtonActionPerformed

    private void m_selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_selectAllButtonActionPerformed
        for( int i=0; i<m_proxyTableModel.getRowCount(); i++ ) {
            int deck_amt = (Integer)m_proxyTableModel.getValueAt( i, ProxyCols.Deck_Amount.val );
            m_proxyTableModel.setValueAt( deck_amt, i, ProxyCols.Print_Amount.val );
        }
    }//GEN-LAST:event_m_selectAllButtonActionPerformed

    private void m_cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_cancelButtonActionPerformed
        m_dialog.closeDialog();
    }//GEN-LAST:event_m_cancelButtonActionPerformed

    private void m_selectNoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_selectNoneButtonActionPerformed
        for( int i=0; i<m_proxyTableModel.getRowCount(); i++ ) {
            m_proxyTableModel.setValueAt( 0, i, ProxiesTableModel.ProxyCols.Print_Amount.val );
        }
    }//GEN-LAST:event_m_selectNoneButtonActionPerformed

    private void m_selectSideboardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_selectSideboardButtonActionPerformed
        for( int i=0; i<m_proxyTableModel.getRowCount(); i++ ) {
            Boolean inSB = (Boolean)m_proxyTableModel.getValueAt( i, ProxyCols.SB.val );
            int deck_amt = (Integer)m_proxyTableModel.getValueAt( i, ProxyCols.Deck_Amount.val );
            if( !inSB ) {
                m_proxyTableModel.setValueAt( 0, i, ProxyCols.Print_Amount.val );
            } else {
                m_proxyTableModel.setValueAt( deck_amt, i, ProxyCols.Print_Amount.val );
            }
        }
    }//GEN-LAST:event_m_selectSideboardButtonActionPerformed

    private void m_selectLandsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_selectLandsButtonActionPerformed
        boolean isLand;
        for( int i=0; i<m_proxyTableModel.getRowCount(); i++ ) {
            String types[] = ((String)m_proxyTableModel.getValueAt( i, ProxyCols.Type.val )).split( " " );
            int deck_amt = (Integer)m_proxyTableModel.getValueAt( i, ProxyCols.Deck_Amount.val );
            isLand = false;
            for( String type : types ) {
                if( type.equalsIgnoreCase( "Land" ) ) {
                    isLand = true;
                    m_proxyTableModel.setValueAt( deck_amt, i, ProxiesTableModel.ProxyCols.Print_Amount.val );
                }
            }

            if( !isLand ) {
                m_proxyTableModel.setValueAt( 0, i, ProxiesTableModel.ProxyCols.Print_Amount.val );
            }
        }
    }//GEN-LAST:event_m_selectLandsButtonActionPerformed

    private void m_selectInvertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_selectInvertButtonActionPerformed
        for( int i=0; i<m_proxyTableModel.getRowCount(); i++ ) {
            int deck_amt = (Integer)m_proxyTableModel.getValueAt( i, ProxyCols.Deck_Amount.val );
            int print_amt = (Integer)m_proxyTableModel.getValueAt( i, ProxyCols.Print_Amount.val );
            int inverted_amt = (deck_amt - print_amt <= 0 ? 0 : deck_amt - print_amt );
            m_proxyTableModel.setValueAt( inverted_amt, i, ProxiesTableModel.ProxyCols.Print_Amount.val );
        }
    }//GEN-LAST:event_m_selectInvertButtonActionPerformed

    private void m_selectEachButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_selectEachButtonActionPerformed
        int each_amt = Integer.parseInt( m_selectEachText.getText() );
        for( int i=0; i<m_proxyTableModel.getRowCount(); i++ ) {
            m_proxyTableModel.setValueAt( each_amt, i, ProxiesTableModel.ProxyCols.Print_Amount.val );
        }
    }//GEN-LAST:event_m_selectEachButtonActionPerformed

    private int getTotalCardsToPrint() {
        return getTotalCardsToPrint( false );
    }
    private int getTotalCardsToPrint( boolean forceUpdate ) {
        if( m_cardsTotal == 0 || forceUpdate ) {
            HashMap<Integer, Integer> cards = m_deck.getCards();
            for( int mid : cards.keySet() ) {
                m_cardsTotal += cards.get( mid );
            }
        }
        return m_cardsTotal;
    }

    public int getNumberOfPages() {
        if( m_pages.isEmpty() ) {
            int totalCards = getTotalCardsToPrint( true );
            return (int)(totalCards / CARDS_PER_PAGE) + ((totalCards % CARDS_PER_PAGE) > 0 ? 1 : 0);
        }
        return m_pages.size();
    }

    public PageFormat getPageFormat( int pageIndex ) throws IndexOutOfBoundsException {
        PageFormat pf = new PageFormat();
        Paper paper = new Paper();
        paper.setImageableArea( PAGE_MARGIN, PAGE_MARGIN, PAGE_WIDTH_PX, PAGE_HEIGHT_PX );
        pf.setPaper( paper );
        return pf;
    }

    public Printable getPrintable( int pageIndex ) throws IndexOutOfBoundsException {
        if( pageIndex < getNumberOfPages() ) {
            return m_pages.get( pageIndex );
        }
        return null;
    }

    private class ProxyPage implements Printable {

        private int m_pageIndex;
        private c_CardDB m_db;
        private ArrayList<Integer> m_cards = new ArrayList<Integer>();

        public ProxyPage( int pageIndex, c_CardDB db ) {
            m_pageIndex = pageIndex;
            m_db = db;
        }

        public boolean hasCards() {
            return !m_cards.isEmpty();
        }

        public int addCard( int mid, int amt ) {
            int amt_left = amt;
            for( int i=m_cards.size(); i<CARDS_PER_PAGE && amt_left>0; i++, amt_left-- ) {
                m_cards.add( mid );
            }
            return amt_left;
        }

        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            if( pageIndex != m_pageIndex ) {
                return NO_SUCH_PAGE;
            }
            try {
                int cnt = 0;
                for( int i=0; i<CARDS_PER_PAGE_VERT; i++ ) {
                    for( int j=0; j<CARDS_PER_PAGE_HORZ; j++ ) {
                        int x = (j * (CARD_WIDTH_PX  + CARD_SPACER_HORZ)) + CARD_SPACER_HORZ;
                        int y = (i * (CARD_HEIGHT_PX + CARD_SPACER_VERT)) + CARD_SPACER_VERT;
                        int mid = m_cards.get( cnt );
                        ImageIcon img = m_db.getCard( mid ).getImage();
                        g.drawImage( img.getImage().getScaledInstance( CARD_WIDTH_PX, CARD_HEIGHT_PX, Image.SCALE_SMOOTH ), x, y, null );
                        cnt++;
                        if( cnt >= CARDS_PER_PAGE || cnt >= m_cards.size() ) {
                            break;
                        }
                    }
                }
            } catch( Exception ex ) {
                int i=0;
                return NO_SUCH_PAGE;
            }
            return PAGE_EXISTS;
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton m_cancelButton;
    private javax.swing.JButton m_printButton;
    private GUI.CardTable m_proxyTable;
    private javax.swing.JButton m_selectAllButton;
    private javax.swing.JButton m_selectEachButton;
    private javax.swing.JTextField m_selectEachText;
    private javax.swing.JButton m_selectInvertButton;
    private javax.swing.JButton m_selectLandsButton;
    private javax.swing.JButton m_selectNoneButton;
    private javax.swing.JButton m_selectSideboardButton;
    // End of variables declaration//GEN-END:variables

}
