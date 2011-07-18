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
import Data.c_Expansion;
import Data.c_File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Phillip
 */
public class frmtApprentice extends DeckFormat {

    public static final String Extension = "dec";

    private enum DeckLoading {
        NAME( "//NAME: " ),
        SIDEBOARD( " SB: " );

        private String value;

        DeckLoading( String text ) {
            value = text;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    {
        Format.put( Keyword.NAME,      "//NAME: %s\n" );
        Format.put( Keyword.CARD_AMT,  "     %s " );
        Format.put( Keyword.CARD_NAME, " %s\n" );
        Format.put( Keyword.SB_PREFIX, " SB: " );
        Format.put( Keyword.SB_CARD_AMT,  "%s " );
        Format.put( Keyword.SB_CARD_NAME, "%s\n" );
    }

    public frmtApprentice() {
        super( "Magic Apprentice Deck Files", Extension );
    }

    public boolean loadDeck( String filename, c_Deck deck, c_CardDB db ) {
        boolean success = true;

        c_File file = new c_File();
        try {
            ArrayList<String> contents = file.read( filename, false );
            String line;
            for( int i=0; i<contents.size(); i++ ) {
                line = contents.get( i );

                if( line == null
                 || line.equals( "" ) ) {
                    break;
                }

                if( line.startsWith( DeckLoading.NAME.value ) ) {
                    deck.setName( line.replaceFirst( DeckLoading.NAME.value, "" ) );
                } else if( line.startsWith( DeckLoading.SIDEBOARD.value ) ) {
                    loadCard( false, line.replaceFirst( DeckLoading.SIDEBOARD.value, "" ).trim(), deck, db );
                } else {
                    loadCard( true, line.trim(), deck, db );
                }
            }
            contents = null;
            line = null;
        } catch (IOException ex) {
            Logger.getLogger(frmtApprentice.class.getName()).log(Level.SEVERE, null, ex);
            success = false;
        }

        return success;
    }

    private void loadCard( boolean toDeck, String line, c_Deck deck, c_CardDB db ) {
        String ary[] = line.split( " " );
        int amount = Integer.parseInt( ary[ 0 ] );
        String cardname = line.replaceFirst( ary[ 0 ], "" ).trim();

        if( db.contains( cardname ) ) {
            HashMap<c_Expansion, Integer> expansions = db.getExpansionList( cardname );
            int mid = expansions.values().toArray( new Integer[] {} )[ 0 ];
            deck.addCard( mid, amount, toDeck );

            expansions = null;
        } else {
            int i =0;
            // TODO: create some way for the DB to load a card by name
        }

        ary = null;
        cardname = null;
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
                lines += addLine( new Keyword[]{ Keyword.SB_CARD_AMT, Keyword.CARD_NAME }, new String[]{ amt.toString(), card.Name } );
            }

            success = writeDeck( filename, lines );
        } catch( Exception ex ) {
            success = false;
        }
        
        card = null;
        return success;
    }
}
