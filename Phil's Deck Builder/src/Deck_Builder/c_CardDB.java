/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 *
 * @author Phillip
 */
public class c_CardDB implements ActionListener {
    private static final String EXPANSIONS_FILE = "Expansions.txt";
    private HashMap<Integer, c_Card> m_cards = new HashMap<Integer, c_Card>();
    private c_ExpansionDB m_expansionDB;
    
                  //CardName         Expansion    MID
    private HashMap<Integer, HashMap<c_Expansion, Integer>> m_cardNamesToMIDs = new HashMap<Integer, HashMap<c_Expansion, Integer>>();

    public c_CardDB() {
        m_expansionDB = new c_ExpansionDB( EXPANSIONS_FILE );
    }
    public c_CardDB( String filename ) {
        c_File file = new c_File();
        m_expansionDB = new c_ExpansionDB( EXPANSIONS_FILE );
        try {
            file.read( this.getClass(), this, Action.ACTION_CARDS_DB_LOAD_LINE, filename, false );
        } catch( Exception ex ) {
            int i =0;
        }
        file = null;
    }

    public void actionPerformed( ActionEvent e ) {
        //ID	Language	Name	Alt	Cost	Type	Set	Rarity	P	T	Oracle Rules	Printed Name	Printed Type	Printed Rules	Flavor Text	Card #	Artist	Printings
        if( !e.getActionCommand().startsWith( "ID" ) && !e.getActionCommand().equals( Action.COMMAND_FILE_LOAD_DONE ) ) {
            String ary[] = e.getActionCommand().split( "\t" );
            c_Card card = new c_Card( ary );
            if( card.Name != null ) {
                addCard( card );
                card = null;
            }
        }
    }

    public enum DBCol {
        MID,
        Language,
        Name,
        Alt,
        Cost,
        Type,
        Expansion,
        Rarity,
        Power,
        Toughness,
        Oracle_Rules,
        Printed_Name,
        Printed_Type,
        Printed_Rules,
        Flavor,
        Card_Num,
        Artist,
        Expansion_List;
        public int val = this.ordinal();
    }

    public c_ExpansionDB getExpansionDB() {
        return m_expansionDB;
    }

    public boolean contains( Integer mid ) {
        return m_cards.containsKey( mid );
    }
    public boolean contains( String name ) {
        return m_cardNamesToMIDs.containsKey( name.hashCode() );
    }

    public boolean addCard( c_Card card, HashMap<c_Expansion, Integer> list ) {
        if( !contains( card.MID ) ) {
            m_cards.put( card.MID, card );
            if( contains( card.Name ) ) {
                m_cardNamesToMIDs.get( card.Name.hashCode() ).putAll( list );
            } else {
                m_cardNamesToMIDs.put( card.Name.hashCode(), list );
            }
            return true;
        }
        return false;
    }
    public boolean addCard( c_Card card ) {
        if( !contains( card.MID ) ) {
            m_cards.put( card.MID, card );
            if( contains( card.Name ) ) {
                m_cardNamesToMIDs.get( card.Name.hashCode() ).put( m_expansionDB.getExpansion( card.Expansion ), card.MID );
            } else {
                HashMap<c_Expansion, Integer> list = new HashMap<c_Expansion, Integer>();
                list.put( m_expansionDB.getExpansion( card.Expansion ), card.MID );
                m_cardNamesToMIDs.put( card.Name.hashCode(), list );
                list = null;
            }
            return true;
        }
        return false;
    }

    public c_Card getCard( Integer mid ) {
        if( contains( mid ) ) {
            return m_cards.get( mid );
        } else {
            final WebBrowserPanel webBrowser = new WebBrowserPanel();
            final c_Card card = new c_Card();

            webBrowser.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    card.setCard( webBrowser.getCard() );
                }
            } );

            webBrowser.loadCard( mid );
            while( webBrowser.isLoading() ) {}
            return card;
        }
    }
}
