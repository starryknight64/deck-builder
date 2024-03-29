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
 * MainWindow.java
 *
 * Created on Nov 2, 2010, 3:58:04 PM
 */

package GUI;

import Deck_Builder.Dialog;
import Deck_Builder.Action;
import Data.c_CardDB;
import Data.c_File;
import Data.c_Card;
import Parsers.DeckFormat;
import Parsers.DeckFormats;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Phillip
 */
public class MainWindow extends JFrame implements ActionListener {
    private c_Card m_curCard;
    private c_CardDB m_db;
    private CustomSplashScreen screen;
    private String[] m_loadingText = new String[] { "Loading Expansions...",
                                                    "Loading Block Legality...",
                                                    "Loading Standard Legality...",
                                                    "Loading Extended Legality...",
                                                    "Loading Legacy Legality...",
                                                    "Loading Vintage Legality...",
                                                    "Loading Cards...",
                                                    "Downloading Prices...",
                                                    "Loading Prices...",
                                                    "Done!" };
    private int m_loadingIndex = 0;
    private int m_totalLines = 100;

    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents();

        setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                int success = closeDecks( m_leftPanel.getAllDecks() );
                if( success == JOptionPane.CANCEL_OPTION ) {
                    /* if, during the close, the user decided to cancel, then don't exit the program */
                    return;
                }
                m_db.saveNewCards();
                dispose();
                System.exit(0);
            }
        } );

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        Double scale = 0.9;

        setSize( (int)(dim.width * scale), (int)(dim.height * scale) );
        jSplitPane1.setDividerLocation( (int)(getWidth() * 0.5) - jSplitPane1.getDividerSize() );
        m_webBrowserPanel.giveFrame( this );
        m_webBrowserPanel.addActionListener( this );
        m_webBrowserPanel.load( "http://gatherer.wizards.com" );
        m_curCard = new c_Card();
        m_leftPanel.addActionListener( this );

        screen = new CustomSplashScreen( new ImageIcon( getClass().getResource( "/resources/Images/logo.png" ) ) );
        screen.setLocationRelativeTo( null );
        screen.setScreenVisible( true );
        boolean success = m_leftPanel.loadDBs( this );
        screen.setScreenVisible( false );
        screen = null;
        
        m_db = m_leftPanel.getCardDB();

        setVisible( true );
        if( success == false ) {
            Dialog.MsgBox( this, "There was an error initializing the deck builder. It is recommended you restart to avoid further errors." );
        }

        dim = null;
        }

    public void actionPerformed( ActionEvent e ) {
        if( e.getID() == Action.ACTION_BROWSER_LOADING_DONE ) {
            if( Action.COMMAND_CARD_PREVIEW.equals( e.getActionCommand() ) ) {
                m_curCard = m_webBrowserPanel.getCard();
                m_leftPanel.setPreviewCard( m_curCard );
            }
        } else if( e.getID() == Action.ACTION_DECK_CHANGED ) {
            boolean isSaved = m_leftPanel.getCurrentDeckTab().isSaved();
            m_File_SaveDeck.setEnabled( !isSaved );
            m_File_CloseDeck.setEnabled( m_leftPanel.getTotalDecks() > 1 || !isSaved );
        } else if( e.getID() == Action.ACTION_PROXY_CARD_SELECTED ) {
            int mid = Integer.parseInt( e.getActionCommand() );
            m_curCard = m_db.getCard( mid );
            m_leftPanel.setPreviewCard( m_curCard );
        } else if( e.getID() == Action.ACTION_FILE_TOTAL_LINES ) {
            if( screen != null ) {
                m_totalLines = Integer.parseInt( e.getActionCommand() );
                screen.setProgressMax( m_totalLines );
            }
        } else if( e.getID() == Action.ACTION_FILE_LOAD_DONE ) {
            if( screen != null ) {
                screen.setProgress( m_loadingText[ m_loadingIndex++ ], m_totalLines );
                screen.setProgress( m_loadingText[ m_loadingIndex ], 0 );
            }
        } else if( screen != null ) {
            screen.setProgress( m_loadingText[ m_loadingIndex ], screen.getProgress() + 1 );
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_webBrowserPanel = new GUI.WebBrowserPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        m_leftPanel = new GUI.LeftPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        m_File_NewDeck = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        m_File_OpenDeck = new javax.swing.JMenuItem();
        m_File_SaveDeck = new javax.swing.JMenuItem();
        m_File_SaveDeckAs = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        m_File_CloseDeck = new javax.swing.JMenuItem();
        m_File_CloseAllDecks = new javax.swing.JMenuItem();
        m_File_PrintProxies = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        m_File_Exit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jScrollPane1.setViewportView(m_webBrowserPanel);

        jSplitPane1.setRightComponent(jScrollPane1);

        jScrollPane2.setViewportView(m_leftPanel);

        jSplitPane1.setLeftComponent(jScrollPane2);

        jMenu3.setMnemonic('F');
        jMenu3.setText("File");

        m_File_NewDeck.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        m_File_NewDeck.setMnemonic('N');
        m_File_NewDeck.setText("New Deck");
        m_File_NewDeck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_File_NewDeckActionPerformed(evt);
            }
        });
        jMenu3.add(m_File_NewDeck);
        jMenu3.add(jSeparator1);

        m_File_OpenDeck.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        m_File_OpenDeck.setMnemonic('O');
        m_File_OpenDeck.setText("Open Deck...");
        m_File_OpenDeck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_File_OpenDeckActionPerformed(evt);
            }
        });
        jMenu3.add(m_File_OpenDeck);

        m_File_SaveDeck.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        m_File_SaveDeck.setMnemonic('S');
        m_File_SaveDeck.setText("Save Deck");
        m_File_SaveDeck.setEnabled(false);
        m_File_SaveDeck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_File_SaveDeckActionPerformed(evt);
            }
        });
        jMenu3.add(m_File_SaveDeck);

        m_File_SaveDeckAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        m_File_SaveDeckAs.setMnemonic('A');
        m_File_SaveDeckAs.setText("Save Deck As...");
        m_File_SaveDeckAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_File_SaveDeckAsActionPerformed(evt);
            }
        });
        jMenu3.add(m_File_SaveDeckAs);
        jMenu3.add(jSeparator2);

        m_File_CloseDeck.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        m_File_CloseDeck.setMnemonic('C');
        m_File_CloseDeck.setText("Close Deck");
        m_File_CloseDeck.setEnabled(false);
        m_File_CloseDeck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_File_CloseDeckActionPerformed(evt);
            }
        });
        jMenu3.add(m_File_CloseDeck);

        m_File_CloseAllDecks.setMnemonic('D');
        m_File_CloseAllDecks.setText("Close All Decks...");
        m_File_CloseAllDecks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_File_CloseAllDecksActionPerformed(evt);
            }
        });
        jMenu3.add(m_File_CloseAllDecks);

        m_File_PrintProxies.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        m_File_PrintProxies.setMnemonic('P');
        m_File_PrintProxies.setText("Print Proxies...");
        m_File_PrintProxies.setToolTipText("Coming Soon!");
        m_File_PrintProxies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_File_PrintProxiesActionPerformed(evt);
            }
        });
        jMenu3.add(m_File_PrintProxies);
        jMenu3.add(jSeparator3);

        m_File_Exit.setMnemonic('x');
        m_File_Exit.setText("Exit");
        m_File_Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_File_ExitActionPerformed(evt);
            }
        });
        jMenu3.add(m_File_Exit);

        jMenuBar1.add(jMenu3);

        jMenu2.setMnemonic('E');
        jMenu2.setText("Edit");

        jMenuItem7.setText("Preferences...");
        jMenuItem7.setToolTipText("Coming Soon!");
        jMenuItem7.setEnabled(false);
        jMenu2.add(jMenuItem7);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void m_File_OpenDeckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_File_OpenDeckActionPerformed
        if( m_leftPanel.getTotalDecks() < LeftPanel.MAX_DECKS ) {
            JFileChooser dlg = new JFileChooser();
            dlg.setDialogTitle( "Open Deck..." );
            dlg.addChoosableFileFilter( DeckFormats.Apprentice.getDeckFormat() );
            dlg.addChoosableFileFilter( DeckFormats.PhilsDeckBuilder.getDeckFormat() );
            
            int cmd = dlg.showOpenDialog( this );

            if( cmd == JFileChooser.APPROVE_OPTION ) {
                try {
                    m_leftPanel.loadDeck( (DeckFormat)dlg.getFileFilter(), dlg.getSelectedFile().getCanonicalPath() );
                } catch( Exception ex ) {
                    Dialog.ErrorBox( this, ex.getStackTrace() );
                }
            }

            dlg = null;
        } else {
            Dialog.MsgBox( this, "You have too many decks open. The maximum allowed is " + LeftPanel.MAX_DECKS.toString() + ". Close a deck first and then try to open a deck." );
        }
    }//GEN-LAST:event_m_File_OpenDeckActionPerformed

    private void m_File_SaveDeckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_File_SaveDeckActionPerformed
        DeckTabPanel decktab = m_leftPanel.getCurrentDeckTab();
        saveDeck( decktab, decktab.getFilename().isEmpty() );
        decktab = null;
    }//GEN-LAST:event_m_File_SaveDeckActionPerformed

    private boolean saveDeck( DeckTabPanel deck, boolean isSaveAs ) {
        boolean saveSuccess = true;
        int cmd = JFileChooser.CANCEL_OPTION;
        String filepath = deck.getFilename();
        FileFilter filter = DeckFormats.PhilsDeckBuilder.getDeckFormat();
        if( isSaveAs ) {
            JFileChooser dlg = new JFileChooser();
            try {
                dlg.setDialogTitle( "Save Deck" + ( isSaveAs ? " As..." : "..." ) );
                dlg.setSelectedFile( new File( ".\\" + c_File.removeInvalidFilenameChars( deck.getDeckName() ) + ".tsv" ) );
                dlg.setAcceptAllFileFilterUsed( false );
                dlg.addChoosableFileFilter( DeckFormats.Apprentice.getDeckFormat() );
                dlg.addChoosableFileFilter( DeckFormats.PhilsDeckBuilder.getDeckFormat() );

                cmd = dlg.showSaveDialog( this );
                if( cmd != JFileChooser.APPROVE_OPTION ) {
                    dlg = null;
                    return false;
                }
                filter = dlg.getFileFilter();
                filepath = dlg.getSelectedFile().getCanonicalPath();
            } catch( Exception ex ) {
                Dialog.ErrorBox( this, ex.getStackTrace() );
                dlg = null;
                return false;
            }

            dlg = null;
        }

        if( cmd == JFileChooser.APPROVE_OPTION || !isSaveAs ) {
            deck.getDeck().setName( deck.getDeckName() );
            saveSuccess = deck.saveDeck( (DeckFormat)filter, filepath );
        }

        if( saveSuccess ) {
            m_File_SaveDeck.setEnabled( false );
            deck.updateDeckInfo();
        }
        
        return saveSuccess;
    }

    private void m_File_ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_File_ExitActionPerformed
        processEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );
    }//GEN-LAST:event_m_File_ExitActionPerformed

    private void m_File_SaveDeckAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_File_SaveDeckAsActionPerformed
        saveDeck( m_leftPanel.getCurrentDeckTab(), true );
    }//GEN-LAST:event_m_File_SaveDeckAsActionPerformed

    private void m_File_CloseDeckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_File_CloseDeckActionPerformed
        closeDeck( m_leftPanel.getCurrentDeckTab() );
    }//GEN-LAST:event_m_File_CloseDeckActionPerformed

    private void m_File_NewDeckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_File_NewDeckActionPerformed
        if( m_leftPanel.getTotalDecks() < LeftPanel.MAX_DECKS ) {
            m_leftPanel.addNewDeck();
        }
    }//GEN-LAST:event_m_File_NewDeckActionPerformed

    private void m_File_CloseAllDecksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_File_CloseAllDecksActionPerformed
        closeDecks( m_leftPanel.getAllDecks() );
    }//GEN-LAST:event_m_File_CloseAllDecksActionPerformed

    private void m_File_PrintProxiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_File_PrintProxiesActionPerformed
        ProxyPrinterDialog dlg = new ProxyPrinterDialog( this, m_leftPanel.getCurrentDeckTab().getDeck(), m_leftPanel.getCardDB() );
        dlg.setVisible( true );
        dlg = null;
    }//GEN-LAST:event_m_File_PrintProxiesActionPerformed

    private int closeDecks( ArrayList<DeckTabPanel> decks ) {
        int success = JOptionPane.YES_OPTION;
        for( DeckTabPanel deck : decks ) {
            success = closeDeck( deck );
            if( success == JOptionPane.CANCEL_OPTION ) {
                break;
            }
        }
        return success;
    }

    private int closeDeck( DeckTabPanel deck ) {
        if( !deck.isSaved() ) {
            int ans = Dialog.MsgBox( this, "Save Deck", "Would you like to save '" + deck.getDeckName() + "' before closing?", JOptionPane.YES_NO_CANCEL_OPTION );
            boolean closeDeck = false;
            if( ans == JOptionPane.YES_OPTION ) {
                boolean saveSuccess = saveDeck( deck, deck.getFilename().isEmpty() );
                if( saveSuccess ) {
                    m_leftPanel.addNewDeck();
                    closeDeck = ( m_leftPanel.getTotalDecks() > 1 );
                } else {
                    /* Either user decided to cancel or had an issue with saving, so cancel the operation */
                    return JOptionPane.CANCEL_OPTION;
                }
            } else if( ans == JOptionPane.NO_OPTION ) {
                m_leftPanel.addNewDeck();
                closeDeck = ( m_leftPanel.getTotalDecks() > 1 );
            } else if( ans == JOptionPane.CANCEL_OPTION || ans == JOptionPane.CLOSED_OPTION ) {
                /* Cancel the operation */
                return JOptionPane.CANCEL_OPTION;
            }

            if( closeDeck ) {
                m_leftPanel.closeDeck( deck );
            }
        } else {
            if( m_leftPanel.getTotalDecks() == 1 ) {
                m_leftPanel.addNewDeck();
            }
            m_leftPanel.closeDeck( deck );
        }
        return JOptionPane.YES_OPTION;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenuItem m_File_CloseAllDecks;
    private javax.swing.JMenuItem m_File_CloseDeck;
    private javax.swing.JMenuItem m_File_Exit;
    private javax.swing.JMenuItem m_File_NewDeck;
    private javax.swing.JMenuItem m_File_OpenDeck;
    private javax.swing.JMenuItem m_File_PrintProxies;
    private javax.swing.JMenuItem m_File_SaveDeck;
    private javax.swing.JMenuItem m_File_SaveDeckAs;
    private GUI.LeftPanel m_leftPanel;
    private GUI.WebBrowserPanel m_webBrowserPanel;
    // End of variables declaration//GEN-END:variables
}
