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

import Deck_Builder.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import Data.c_Deck.WhichHalf;
import Data.c_Expansion.Acronym;
import Data.c_Price.PriceType;
import Deck_Builder.str;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Phillip
 */
public class c_PriceDB implements ActionListener {
    /* Card|Price|StdDev|Average|High|Low|Change|Raw N */
    private enum PricesParsing {
        NAME,
        PRICE,
        STD_DEV,
        AVERAGE,
        HIGH,
        LOW,
        CHANGE,
        RAW_NUMBER;
        public int val = this.ordinal();
    }

    private static final String PRICES_FILE = "Prices.txt";
    private static final String ACRONYMS_FILE = "/resources/Acronyms.txt";
                 /* Card Name is String */
    private HashMap<String, c_PriceEntry> m_prices = new HashMap<String, c_PriceEntry>();
    private c_ExpansionDB m_expansionDB;
    private boolean parsedFirstLineAcronyms = false;
    private boolean parsedFirstListPrices = false;

    ActionListener m_loadingListener = null;
    private c_CardDB m_db;

    public c_PriceDB() {
    }
    public c_PriceDB( c_CardDB db ) {
        m_db = db;
        m_expansionDB = db.getExpansionDB();
    }

    public void actionPerformed( ActionEvent e ) {
        String line = e.getActionCommand();
        if( e.getID() == Action.ACTION_PRICES_LOAD_LINE ) {
            if( parsedFirstListPrices == false ) {
                parsedFirstListPrices = true;
                m_loadingListener.actionPerformed( e );
                return;
            }

            String ary[] = line.split( "\\|" );
            c_PriceEntry entry;
            c_Price price;
            Double low, avg, high;
            String name, expansion, temp;

            /* Card|Price|StdDev|Average|High|Low|Change|Raw N
               AEther Figment|0.25|0.00|0.25|0.25|0.25|0.00|4
               AEther Flash (6th)|0.36|0.00|0.36|0.36|0.36|0.00|1 */
            temp = ary[ PricesParsing.NAME.val ];
            if( temp.endsWith( ")" ) ) {
                expansion = str.middleOf( "(", temp, ")" );
                name = str.leftOf( temp, " (" );

                if( m_expansionDB.contains( Acronym.Trader, expansion ) ) {
                    expansion = m_expansionDB.getExpansion( Acronym.Trader, expansion ).getName();
                } else {
                    expansion = "";
                }
            } else {
                expansion = "";
                name = temp;
            }

            avg = Double.parseDouble( ary[ PricesParsing.AVERAGE.val ] );
            high = Double.parseDouble( ary[ PricesParsing.HIGH.val ] );
            low = Double.parseDouble( ary[ PricesParsing.LOW.val ] );
            price = new c_Price( low, avg, high, PriceType.AVERAGE );

            if( !contains( name ) ) {
                entry = new c_PriceEntry();
                entry.addPriceEntry( price, expansion );
                m_prices.put( name, entry );
            } else {
                entry = m_prices.get( name );
                entry.addPriceEntry( price, expansion );
            }

            m_loadingListener.actionPerformed( e );
            ary = null;
            entry = null;
            price = null;
            name = null;
            expansion = null;
            temp = null;
        } else {
            m_loadingListener.actionPerformed( e );
        }

        line = null;
    }

    public boolean loadPricesDB( ActionListener listener ) {
        boolean success = true;
        try {
            FileUtils.copyURLToFile( new URL( "http://www.magictraders.com/pricelists/current-magic-excel.txt" ), new File( PRICES_FILE ) );
            listener.actionPerformed( new ActionEvent( this, Action.ACTION_FILE_LOAD_DONE, "" ) );
            success = updatePrices( listener, PRICES_FILE );
        } catch( Exception ex ) {
            for( StackTraceElement elem : ex.getStackTrace() ) {
                System.err.print( elem.toString() + "\n" );
            }
            success = false;
        }
        return success;
    }
    
    public boolean updatePrices( ActionListener listener, String filepath ) {
        boolean success = true;
        m_loadingListener = listener;
        c_File file = new c_File();
        try {
            file.read( this.getClass(), this, Action.ACTION_PRICES_LOAD_LINE, filepath, false );
        } catch( IOException ex ) {
            success = false;
        }
        file = null;
        m_loadingListener = null;
        return success;
    }

    public boolean contains( String cardname ) {
        return m_prices.containsKey( cardname );
    }

    public c_Price getPrice( c_Card card ) {
        return getPrice( card.Name, card.Expansion );
    }
    public c_Price getPrice( String cardname, String expansion ) {
        if( contains( cardname ) ) {
            return m_prices.get( cardname ).getPrice( expansion );
        }
        return new c_Price();
    }
    public c_Price getPrice( c_Deck deck, WhichHalf part ) {
        c_Price price = new c_Price();
        if( part == WhichHalf.DECK || part == WhichHalf.BOTH ) {
            HashMap<Integer, Integer> cards = deck.getCards();
            for( int mid : cards.keySet() ) {
                Integer amount = cards.get( mid );
                c_Card card = m_db.getCard( mid );
                c_Price cardprice = getPrice( card.Name, card.Expansion );
                price.addPriceTimesAmount( cardprice, amount );

                amount = null;
                card = null;
                cardprice = null;
            }
        }
        if( part == WhichHalf.SB || part == WhichHalf.BOTH ) {
            HashMap<Integer, Integer> cards = deck.getSBCards();
            for( int mid : cards.keySet() ) {
                Integer amount = cards.get( mid );
                c_Card card = m_db.getCard( mid );
                c_Price cardprice = getPrice( card.Name, card.Expansion );
                price.addPriceTimesAmount( cardprice, amount );

                amount = null;
                card = null;
                cardprice = null;
            }
        }
        return price;
    }
}
