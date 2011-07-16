/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Parsers;

import Data.c_Card;
import Data.c_CardDB;
import Data.c_Deck;
import Data.c_Deck.WhichHalf;

/**
 *
 * @author Phillip
 */
public class frmtApprentice extends DeckFormat {

    public static final String Extension = "dec";

    {
        Format.put( Keyword.NAME,      "//NAME: %s\n" );
        Format.put( Keyword.CARD_AMT,  "     %s " );
        Format.put( Keyword.CARD_NAME, " %s\n" );
        Format.put( Keyword.SB_PREFIX, "SB: " );
        Format.put( Keyword.SB_CARD_AMT,  " %s " );
        Format.put( Keyword.SB_CARD_NAME, "%s\n" );
    }

    public frmtApprentice() {
        super( "Magic Apprentice Deck Files", Extension );
    }

    public boolean loadDeck( String filename, c_Deck deck, c_CardDB db ) {
        return true;
    }

    public boolean saveDeck( String filename, c_Deck deck, c_CardDB db ) {
        boolean success = true;
        c_Card card;
        
        try {
            String lines = "";
            lines += addLine( new Keyword[]{ Keyword.NAME }, new String[]{ deck.getName() } );

            for( int mid : deck.getCards().keySet() ) {
                card = db.getCard( mid );
                Integer amt = deck.getAmountOfCard( mid, WhichHalf.DECK );
                lines += addLine( new Keyword[]{ Keyword.CARD_AMT, Keyword.CARD_NAME }, new String[]{ amt.toString(), card.Name } );
            }

            for( int mid : deck.getSBCards().keySet() ) {
                card = db.getCard( mid );
                Integer amt = deck.getAmountOfCard( mid, WhichHalf.SB );
                lines += addLine( Keyword.SB_PREFIX );
                lines += addLine( new Keyword[]{ Keyword.CARD_AMT, Keyword.CARD_NAME }, new String[]{ amt.toString(), card.Name } );
            }

            writeDeck( filename, lines );
        } catch( Exception ex ) {
            success = false;
        }
        
        card = null;
        return success;
    }
}
