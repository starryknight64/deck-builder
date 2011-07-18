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
import Data.c_CastingCost;
import Data.c_Deck;
import Data.c_Deck.CardLoading;
import Data.c_Deck.WhichHalf;
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
public class frmtPhilsDeckBuilder extends DeckFormat {
    
    public static final String Extension = "tsv";

    public frmtPhilsDeckBuilder() {
        super( "Phil's Deck Files", Extension );
    }

    private enum DeckLoading {
        NAME( "Name: " ),
        SIZE( "Size: " ),
        COLORS( "Colors: " ),
        CREATION_DATE( "Creation Date: " ),
        MODIFICATIONS( "Modifications: " ),
        NOTES( "Notes: " ),
        DECK( "Card Name\tAmt\tCard Type\tSub-Type\tCost\tP/T\tSet\tMID" ),
        DECK_OLD( "Card Name\tAmt\tKind\tType\tCost\tP/T\tSet\tMID" ),
        SIDEBOARD( "Sideboard:" );

        private String value;

        DeckLoading( String text ) {
            value = text;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private String getLine( DeckLoading which, String value ) {
        return which.toString() + value + "\r\n";
    }

    public boolean loadDeck( String filename, c_Deck deck, c_CardDB db ) {
        boolean success = true;

        c_File file = new c_File();
        try {
            ArrayList<String> lines = file.read( filename, false );
            for( int i=0; i<lines.size(); i++ ) {
                String line = lines.get( i );

                if( line == null ) {
                    break;
                }

                if( line.startsWith( DeckLoading.NAME.value ) ) {
                    deck.setName( line.replaceFirst( DeckLoading.NAME.value, "" ) );
                } else if( line.startsWith( DeckLoading.SIZE.value ) ) {
                    continue;
                } else if( line.startsWith( DeckLoading.COLORS.value ) ) {
                    continue;
                } else if( line.startsWith( DeckLoading.CREATION_DATE.value ) ) {
                    deck.setCreationDate( line.replaceFirst( DeckLoading.CREATION_DATE.value, "" ) );
                } else if( line.startsWith( DeckLoading.MODIFICATIONS.value ) ) {
                    deck.setModifications( Integer.parseInt( line.replaceFirst( DeckLoading.MODIFICATIONS.value, "" ).trim() ) );
                } else if( line.startsWith( DeckLoading.NOTES.value ) ) {
                    String notes = line.replaceFirst( DeckLoading.NOTES.value, "" );
                    while( !lines.get( i + 1 ).startsWith( DeckLoading.DECK.value )
                        && !lines.get( i + 1 ).startsWith( DeckLoading.DECK_OLD.value ) ) {
                        i++;
                        notes += "\r\n" + lines.get( i );
                    }
                    deck.setNotes( notes );
                    notes = null;
                } else if( line.startsWith( DeckLoading.DECK.value )
                        || line.startsWith( DeckLoading.DECK_OLD.value ) ) {
                    c_Card card;
                    line = lines.get( i + 1 );
                    while( !line.startsWith( DeckLoading.SIDEBOARD.value )
                        && !line.equals( "" ) ) {
                        card = new c_Card();
                        int amt = parseCardLine( line, card );
                        deck.addCard( card.MID, amt, true );

                        i++;
                        if( i+1 >= lines.size() ) {
                            break;
                        }
                        line = lines.get( i + 1 );
                    }
                    card = null;
                } else if( line.startsWith( DeckLoading.SIDEBOARD.value ) ) {
                    c_Card card;
                    line = lines.get( i + 1 );
                    while( !line.equals( "" ) ) {
                        card = new c_Card();
                        int amt = parseCardLine( line, card );
                        deck.addCard( card.MID, amt, false );
                        
                        i++;
                        if( i+1 >= lines.size() ) {
                            break;
                        }
                        line = lines.get( i + 1 );
                    }
                    card = null;
                }
            }
            lines = null;
        } catch (IOException ex) {
            Logger.getLogger(c_Deck.class.getName()).log(Level.SEVERE, String.format( "Deck file '%s' could not be loaded!", filename ), ex);
            success = false;
        }

        return success;
    }

    private int parseCardLine( String line, c_Card card ) {
        String ary[] = line.split( "\t" );
        int amount = Integer.parseInt( ary[ CardLoading.AMOUNT.val ] );

        card.Name = ary[ CardLoading.NAME.val ];
        card.Type = ary[ CardLoading.TYPE.val ];
        card.SubType = ary[ CardLoading.SUBTYPE.val ];
        card.CastingCost = new c_CastingCost( ary[ CardLoading.COST.val ] );
        card.PT = ary[ CardLoading.PT.val ].replaceAll( "'", "" );
        card.Expansion = ary[ CardLoading.EXPANSION.val ];
        card.MID = Integer.parseInt( ary[ CardLoading.MID.val ] );

        line = null;
        ary = null;
        card = null;
        return amount;
    }

    public boolean saveDeck( String filename, c_Deck deck, c_CardDB db ) {
        boolean saveSuccess = true;
        String lines = "";

        lines += getLine( DeckLoading.NAME, deck.getName() );
        lines += getLine( DeckLoading.SIZE, deck.getSizeOf( WhichHalf.BOTH ).toString() );
        lines += getLine( DeckLoading.COLORS, deck.getColorsText( db ) );
        lines += getLine( DeckLoading.CREATION_DATE, deck.getCreationDate() );
        lines += getLine( DeckLoading.MODIFICATIONS, deck.getModifications().toString() );
        lines += getLine( DeckLoading.NOTES, deck.getNotes() );
        lines += getLine( DeckLoading.DECK, "" );

        c_Card card;
        HashMap<Integer, Integer> cards = deck.getCards();
        for( int mid : cards.keySet() ) {
            card = db.getCard( mid );
            lines += card.toString( cards.get( mid ) ) + "\r\n";
        }

        cards = deck.getSBCards();
        if( !cards.isEmpty() ) {
            lines += getLine( DeckLoading.SIDEBOARD, "" );
            for( int mid : cards.keySet() ) {
                card = db.getCard( mid );
                lines += card.toString( cards.get( mid ) ) + "\r\n";
            }
        }

        saveSuccess = writeDeck( filename, lines );

        card = null;
        cards = null;
        lines = null;
        return saveSuccess;
    }
}
