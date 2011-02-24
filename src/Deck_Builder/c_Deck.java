/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Phillip
 */
public class c_Deck implements ActionListener {

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
    }
    
    private HashMap<Integer, Integer> m_cards = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> m_SBcards = new HashMap<Integer, Integer>();
    private Integer m_modifications = 0;
    private String m_name = "Untitled Deck";
    private String m_notes = "";
    private String m_creationDate = "";
    
    private boolean m_isLoadingDeckFile = false;
    private DeckLoading m_currentPart;

    private EventListenerList m_listeners = new EventListenerList();
    
    public c_Deck() {
    }

    public void loadDeckFromFile( String filepath ) {
        m_isLoadingDeckFile = true;
        m_currentPart = DeckLoading.NAME;
        c_File file = new c_File();
        try {
            file.read(c_Deck.class, this, Action.ACTION_DECK_LOAD_CARD, filepath, false);
        } catch (IOException ex) {
            Logger.getLogger(c_Deck.class.getName()).log(Level.SEVERE, String.format( "Deck file '%s' could not be loaded!", filepath ), ex);
        }
        m_isLoadingDeckFile = false;
        file = null;
        //System.gc();
    }
    
    public boolean saveDeck( String filepath, c_CardDB db ) {
        boolean saveSuccess = true;
        String lines = "";
        
        lines += getLine( DeckLoading.NAME, m_name );
        lines += getLine( DeckLoading.SIZE, getSizeOf( WhichHalf.BOTH ).toString() );
        lines += getLine( DeckLoading.COLORS, getColorsText( db ) );
        lines += getLine( DeckLoading.CREATION_DATE, m_creationDate );
        lines += getLine( DeckLoading.MODIFICATIONS, m_modifications.toString() );
        lines += getLine( DeckLoading.NOTES, m_notes );
        lines += getLine( DeckLoading.DECK, "" );

        c_Card card;
        for( int mid : m_cards.keySet() ) {
            card = db.getCard( mid );
            lines += card.toString( m_cards.get( mid ) ) + "\r\n";
        }

        if( !m_SBcards.isEmpty() ) {
            lines += getLine( DeckLoading.SIDEBOARD, "" );
            for( int mid : m_SBcards.keySet() ) {
                card = db.getCard( mid );
                lines += card.toString( m_SBcards.get( mid ) ) + "\r\n";
            }
        }

        c_File file = new c_File();
        try {
            file.write( filepath, lines );
        } catch (FileNotFoundException ex) {
            saveSuccess = false;
            Logger.getLogger(c_Deck.class.getName()).log(Level.SEVERE, String.format( "File '%s' not found!", filepath ), ex);
        } catch (IOException ex) {
            saveSuccess = false;
            Logger.getLogger(c_Deck.class.getName()).log(Level.SEVERE, String.format( "Error writing to '%s'", filepath ), ex);
        }

        file = null;
        card = null;
        lines = null;
        return saveSuccess;
    }
    
    private String getLine( DeckLoading which, String value ) {
        return which.toString() + value + "\r\n";
    }
    
    public void actionPerformed( ActionEvent e ) {
        if( e.getID() == Action.ACTION_DECK_LOAD_CARD ) {
            String line = e.getActionCommand();
            
            if( ( line.startsWith( m_currentPart.toString() ) || line.startsWith( "Cards:" ) )
                && m_currentPart != DeckLoading.DECK
                && m_currentPart != DeckLoading.SIDEBOARD ) {
                if( m_currentPart == DeckLoading.NAME ) {
                    m_name = str.rightOf( line, DeckLoading.NAME.toString() );
                    m_currentPart = DeckLoading.SIZE;
                } else if( m_currentPart == DeckLoading.SIZE ) {
                    // This is calculated, move along
                    m_currentPart = DeckLoading.COLORS;
                } else if( m_currentPart == DeckLoading.COLORS ) {
                    // This is also calculated, keep moving
                    m_currentPart = DeckLoading.CREATION_DATE;
                } else if( m_currentPart == DeckLoading.CREATION_DATE ) {
                    m_creationDate = str.rightOf( line, DeckLoading.CREATION_DATE.toString() );
                    m_currentPart = DeckLoading.MODIFICATIONS;
                } else if( m_currentPart == DeckLoading.MODIFICATIONS ) {
                    m_modifications = Integer.parseInt( str.rightOf( line, DeckLoading.MODIFICATIONS.toString() ) );
                    m_currentPart = DeckLoading.NOTES;
                } else if( m_currentPart == DeckLoading.NOTES ) {
                    if( !line.equals( DeckLoading.NOTES.toString() ) ) {
                        m_notes += str.rightOf( line, DeckLoading.NOTES.toString() ) + "\r\n";
                    } else {
                        m_currentPart = DeckLoading.DECK;
                    }
                }
            } else if( m_currentPart == DeckLoading.NOTES ) {
                if( line.startsWith( DeckLoading.DECK.toString() )
                    || line.startsWith( DeckLoading.DECK_OLD.toString() ) ) {
                    m_currentPart = DeckLoading.DECK;
                    return;
                }
                m_notes += line + "\r\n";
            } else if( m_currentPart == DeckLoading.DECK ) {
                if( line.startsWith( DeckLoading.SIDEBOARD.toString() ) ) {
                    m_currentPart = DeckLoading.SIDEBOARD;
                    return;
                } else if( line.startsWith( DeckLoading.DECK.toString() )
                        || line.startsWith( DeckLoading.DECK_OLD.toString() ) ) {
                    return;
                }

                if( !line.equals( "" ) ) {
                    fireActionEvent( DeckTabPanel.class, Action.ACTION_DECK_LOAD_CARD, line );
                }
            } else if( m_currentPart == DeckLoading.SIDEBOARD ) {
                if( !line.equals( "" ) ) {
                    fireActionEvent( DeckTabPanel.class, Action.ACTION_DECK_SB_LOAD_LINE, line );
                }
            }
        }
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
        //System.gc();
    }

    public void deleteCard( Integer MID, boolean fromDeck ) {
        HashMap<Integer, Integer> cards = fromDeck ? m_cards : m_SBcards;

        if( hasCard( MID, fromDeck ) ) {
            cards.remove( MID );
        }

        cards = null;
        //System.gc();
    }

    public HashMap<Integer, Integer> getCards() {
        return (HashMap<Integer, Integer>)m_cards.clone();
    }
    public HashMap<Integer, Integer> getSBCards() {
        return (HashMap<Integer, Integer>)m_SBcards.clone();
    }

    public void addActionListener( ActionListener listener ) {
        m_listeners.add( ActionListener.class, listener );
    }

    private void fireActionEvent( Class thisClass, Integer action, String command ) {
        Object listeners[] = m_listeners.getListenerList();
        for( int i=listeners.length-1; i>=0; i-- ) {
            if( listeners[i].getClass() == thisClass ) {
                ((ActionListener)listeners[i]).actionPerformed( new ActionEvent( this, action, command ) );

                listeners = null;
                //System.gc();
                return;
            }
        }

        listeners = null;
        //System.gc();
    }
}
