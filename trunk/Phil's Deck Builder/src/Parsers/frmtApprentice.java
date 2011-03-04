/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Parsers;

import Deck_Builder.c_Card;
import Deck_Builder.c_CardDB;
import Deck_Builder.c_Deck;
import Deck_Builder.c_Deck.WhichHalf;

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

    public boolean saveDeck( String filename, c_Deck deck, c_CardDB db ) {
        boolean success = true;
        try {
            String lines = "";
            lines += addLine( new Keyword[]{ Keyword.NAME }, new String[]{ deck.getName() } );

            for( int mid : deck.getCards().keySet() ) {
                c_Card card = db.getCard( mid );
                Integer amt = deck.getAmountOfCard( mid, WhichHalf.DECK );
                lines += addLine( new Keyword[]{ Keyword.CARD_AMT, Keyword.CARD_NAME }, new String[]{ amt.toString(), card.Name } );
            }

            for( int mid : deck.getSBCards().keySet() ) {
                c_Card card = db.getCard( mid );
                Integer amt = deck.getAmountOfCard( mid, WhichHalf.SB );
                lines += addLine( Keyword.SB_PREFIX );
                lines += addLine( new Keyword[]{ Keyword.CARD_AMT, Keyword.CARD_NAME }, new String[]{ amt.toString(), card.Name } );
            }

            writeDeck( filename, lines );
        } catch( Exception ex ) {
            success = false;
        }
        return success;
    }
}
