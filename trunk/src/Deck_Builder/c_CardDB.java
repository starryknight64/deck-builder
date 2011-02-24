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
public class c_CardDB {
    private HashMap<Integer, c_Card> m_cards;

    public c_CardDB() {
        m_cards = new HashMap<Integer, c_Card>();
    }

    @Override
    public void finalize() throws Throwable {
        m_cards = null;
        super.finalize();
    }

    public boolean contains( Integer MID ) {
        return m_cards.containsKey( MID );
    }

    public boolean addCard( c_Card card ) {
        if( !contains( card.MID ) ) {
            m_cards.put( card.MID, card );
            return true;
        }
        return false;
    }

    public c_Card getCard( Integer MID ) {
        if( contains( MID ) ) {
            return m_cards.get( MID );
        } else {
            final WebBrowserPanel webBrowser = new WebBrowserPanel();
            final c_Card card = new c_Card();

            webBrowser.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    card.setCard( webBrowser.getCard() );
                }
            } );

            webBrowser.loadCard( MID );
            while( webBrowser.isLoading() ) {}
            return card;
        }
    }

}
