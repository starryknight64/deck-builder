/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import Deck_Builder.c_ExpansionDB.Keyword;
import Deck_Builder.c_ExpansionDB.Legals;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Phillip
 */
public class c_CardDB implements ActionListener {
    private static final String CARD_DB_FILE = "db.txt";
    private HashMap<Integer, c_Card> m_cards = new HashMap<Integer, c_Card>();
    private c_ExpansionDB m_expansionDB;
    private ActionListener m_loadingListener = null;
    
                 /* CardName         Expansion    MID */
    private HashMap<Integer, HashMap<c_Expansion, Integer>> m_cardNamesToMIDs = new HashMap<Integer, HashMap<c_Expansion, Integer>>();

    public c_CardDB() {
    }
    public c_CardDB( c_ExpansionDB exp ) {
        m_expansionDB = exp;
    }
    
    public boolean loadCardDB( ActionListener listener ) {
        boolean success = true;
        m_loadingListener = listener;
        c_File file = new c_File();
        try {
            file.read( this.getClass(), this, Action.ACTION_CARDS_DB_LOAD_LINE, CARD_DB_FILE, false );
        } catch( Exception ex ) {
            success = false;
        }
        file = null;
        m_loadingListener = null;
        return success;
    }

    public boolean isInLegal( c_Deck deck, Legals leg ) {
        boolean isDeckInLegal = true;
        c_Card card;
        if( m_expansionDB.doesLegalContainLegals( leg ) ) {
            HashMap<Integer, Keyword> list = m_expansionDB.getOtherLegals( leg );
            for( int mid : deck.getAllCards().keySet() ) {
                card = getCard( mid );
                HashMap<c_Expansion, Integer> expList = getExpansionList( card.Name );
                ArrayList<Integer> legalExpansions = m_expansionDB.getLegalExpansions( leg );
                for( c_Expansion exp : expList.keySet() ) {
                    if( !legalExpansions.contains( m_expansionDB.getEID( exp ) ) ) {
                        isDeckInLegal = false;
                        expList = null;
                        legalExpansions = null;
                        break;
                    }
                }

                int nameHash = card.Name.hashCode();
                if( list.containsKey( nameHash ) ) {
                    if( list.get( nameHash ) == Keyword.Banned
                     || deck.getAmountOfCard( mid, c_Deck.WhichHalf.BOTH ) > 1 ) {
                        isDeckInLegal = false;
                        list = null;
                        break;
                    }
                }
            }
        }
        card = null;
        return isDeckInLegal;
    }

    public boolean isInBlock( c_Deck deck, String block ) {
        boolean isDeckInBlock = true;
        c_Card card;
        for( int mid : deck.getAllCards().keySet() ) {
            card = getCard( mid );
            if( contains( card.Name ) ) {
                boolean isCardInBlock = false;
                for( c_Expansion exp : getExpansionList( card.Name ).keySet() ) {
                    if( exp.getBlock().equals( block ) ) {
                        isCardInBlock = true;
                    }
                }
                if( !isCardInBlock ) {
                    isDeckInBlock = false;
                    break;
                } else if( m_expansionDB.doesBlockContainLegals( block ) ) {
                    HashMap<Integer, Keyword> legals = m_expansionDB.getBlockLegals( block );
                    int nameHash = card.Name.hashCode();
                    if( legals.containsKey( nameHash ) ) {
                        if( legals.get( nameHash ).equals( Keyword.Banned )
                         || deck.getAmountOfCard( mid, c_Deck.WhichHalf.BOTH ) > 1 ) {
                            isDeckInBlock = false;
                            legals = null;
                            break;
                        }
                    }
                    legals = null;
                }
            }
        }
        card = null;
        return isDeckInBlock;
    }

    public HashMap<c_Expansion, Integer> getExpansionList( String cardname ) {
        return m_cardNamesToMIDs.get( cardname.hashCode() );
    }

    public void actionPerformed( ActionEvent e ) {
        /* ID	Language	Name	Alt	Cost	Type	Set	Rarity	P	T	Oracle Rules	Printed Name	Printed Type	Printed Rules	Flavor Text	Card #	Artist	Printings */
        if( !e.getActionCommand().startsWith( "ID" ) && e.getID() == Action.ACTION_CARDS_DB_LOAD_LINE ) {
            String ary[] = e.getActionCommand().split( "\t" );
            c_Card card = new c_Card( ary );
            if( card.Name != null ) {
                addCard( card );
                if( m_loadingListener != null ) {
                    m_loadingListener.actionPerformed( e );
                }
                card = null;
            }
        } else {
            if( m_loadingListener != null ) {
                m_loadingListener.actionPerformed( e );
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
