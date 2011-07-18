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

package Data;

import java.util.HashMap;

/**
 *
 * @author Phillip
 */
public class c_Deck {

    public enum WhichHalf {
        DECK,
        SB,
        BOTH
    }

    public enum WhichType {
        PLANESWALKERS( "Planeswalker" ),
        CREATURES( "Creature" ),
        ARTIFACTS( "Artifact" ),
        ENCHANTMENTS( "Enchantment" ),
        SORCERIES( "Sorcery" ),
        INSTANTS( "Instant" ),
        LANDS( "Land" );

        private String m_name;

        WhichType( String name ) {
            m_name = name;
        }

        @Override
        public String toString() {
            return m_name;
        }
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

    public enum CardLoading {
        NAME,
        AMOUNT,
        TYPE,
        SUBTYPE,
        COST,
        PT,
        EXPANSION,
        MID;
        public int val = this.ordinal();
    }
    
    private HashMap<Integer, Integer> m_cards = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> m_SBcards = new HashMap<Integer, Integer>();
    private Integer m_modifications = 0;
    private String m_name = "Untitled Deck";
    private String m_notes = "";
    private String m_creationDate = "";

    public c_Deck() {
    }

    public void setDeckInfo( c_Deck deck ) {
        m_name = deck.getName();
        m_modifications = deck.getModifications();
        m_creationDate = deck.getCreationDate();
        m_notes = deck.getNotes();
    }

    public String getCreationDate() {
        return m_creationDate;
    }
    public void setCreationDate( String date ) {
        m_creationDate = date;
    }

    public String getNotes() {
        return m_notes;
    }
    public void setNotes( String notes ) {
        m_notes = notes;
    }

    public String getName() {
        return m_name;
    }
    public void setName( String name ) {
        m_name = name;
    }

    public Integer getModifications() {
        return m_modifications;
    }
    public void setModifications( Integer mods ) {
        if( mods >= 0 ) {
            m_modifications = mods;
        }
    }

    public Integer getSizeOf( WhichHalf part ) {
        Integer count = 0;
        if( part == WhichHalf.DECK || part == WhichHalf.BOTH ) {
            for( int i : m_cards.values() ) {
                count += i;
            }
        }
        if( part == WhichHalf.SB || part == WhichHalf.BOTH ) {
            for( int i : m_SBcards.values() ) {
                count += i;
            }
        }
        return count;
    }

    public Integer getAmountOfCard( Integer mid, WhichHalf part ) {
         Integer count = 0;
         if( part == WhichHalf.DECK || part == WhichHalf.BOTH ) {
             if( hasCard( mid, true ) ) {
                 count += m_cards.get( mid );
             }
         }
         if( part == WhichHalf.SB || part == WhichHalf.BOTH ) {
             if( hasCard( mid, false ) ) {
                 count += m_SBcards.get( mid );
             }
         }
         return count;
    }

    public Integer getAmountOfType( WhichType type, WhichHalf part, c_CardDB db ) {
        Integer count = 0;
        if( part == WhichHalf.DECK || part == WhichHalf.BOTH ) {
            for( int mid : m_cards.keySet() ) {
                if( db.getCard( mid ).Type.contains( type.toString() ) ) {
                    count += m_cards.get( mid );
                }
            }
        }
        if( part == WhichHalf.SB || part == WhichHalf.BOTH ) {
            for( int mid : m_SBcards.keySet() ) {
                if( db.getCard( mid ).Type.contains( type.toString() ) ) {
                    count += m_SBcards.get( mid );
                }
            }
        }
        return count;
    }

    public String getColorsText( c_CardDB db ) {
        c_Colors colors = new c_Colors();
        for( Integer mid : m_cards.keySet() ) {
            colors.setColors( db.getCard( mid ) );
        }
        for( Integer mid : m_SBcards.keySet() ) {
            colors.setColors( db.getCard( mid ) );
        }
        return colors.getColorsText();
    }

    public boolean hasCard( Integer MID, boolean isDeck ) {
        if( isDeck ) {
            return m_cards.containsKey( MID );
        }
        return m_SBcards.containsKey( MID );
    }

    public void addCard( Integer MID, Integer newamount, boolean toDeck ) {
        HashMap<Integer, Integer> cards = toDeck ? m_cards : m_SBcards;

        if( hasCard( MID, toDeck ) ) {
            cards.remove( MID );
        }
        
        cards.put( MID, newamount );

        cards = null;
    }

    public void deleteCard( Integer MID, boolean fromDeck ) {
        HashMap<Integer, Integer> cards = fromDeck ? m_cards : m_SBcards;

        if( hasCard( MID, fromDeck ) ) {
            cards.remove( MID );
        }

        cards = null;
    }

    public HashMap<Integer, Integer> getCards() {
        return (HashMap<Integer, Integer>)m_cards.clone();
    }
    public HashMap<Integer, Integer> getSBCards() {
        return (HashMap<Integer, Integer>)m_SBcards.clone();
    }
    public HashMap<Integer, Integer> getAllCards() {
        HashMap<Integer, Integer> cards = getCards();
        cards.putAll( getSBCards() );
        return cards;
    }
}
