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
