/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import Deck_Builder.c_Deck.WhichHalf;
import Deck_Builder.c_Expansion.Acronym;
import Deck_Builder.c_Price.PriceType;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Phillip
 */
public class c_PriceDB implements ActionListener {
    //Card|Price|StdDev|Average|High|Low|Change|Raw N
    private enum PricesParsing {
        NAME,
        PRICE,
        STD_DEV,
        AVERAGE,
        HIGH,
        LOW,
        CHANGE,
        RAW_NUMBER
    }

    private static final String PRICES_FILE = "Prices.txt";
    private static final String ACRONYMS_FILE = "/resources/Acronyms.txt";
                  //Card Name is String
    private HashMap<String, c_PriceEntry> m_prices = new HashMap<String, c_PriceEntry>();
    private c_ExpansionDB m_expansionDB;
    private boolean parsedFirstLineAcronyms = false;
    private boolean parsedFirstListPrices = false;

    private c_CardDB m_db;

    public c_PriceDB() {
        m_db = new c_CardDB();
        m_expansionDB = new c_ExpansionDB();
    }

    public c_PriceDB( c_CardDB db ) {
        m_db = db;
        m_expansionDB = db.getExpansionDB();
        updatePrices();
    }

    public void actionPerformed( ActionEvent e ) {
        String line = e.getActionCommand();
        if( e.getID() == Action.ACTION_PRICES_LOAD_LINE ) {
            if( parsedFirstListPrices == false ) {
                parsedFirstListPrices = true;
                return;
            }

            String ary[] = line.split( "\\|" );
            c_PriceEntry entry;
            c_Price price;
            Double low, avg, high;
            String name, expansion, temp;

            //Card|Price|StdDev|Average|High|Low|Change|Raw N
            //AEther Figment|0.25|0.00|0.25|0.25|0.25|0.00|4
            //AEther Flash (6th)|0.36|0.00|0.36|0.36|0.36|0.00|1
            temp = ary[ PricesParsing.NAME.ordinal() ];
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

            avg = Double.parseDouble( ary[ PricesParsing.AVERAGE.ordinal() ] );
            high = Double.parseDouble( ary[ PricesParsing.HIGH.ordinal() ] );
            low = Double.parseDouble( ary[ PricesParsing.LOW.ordinal() ] );
            price = new c_Price( low, avg, high, PriceType.AVERAGE );

            if( !contains( name ) ) {
                entry = new c_PriceEntry();
                entry.addPriceEntry( price, expansion );
                m_prices.put( name, entry );
            } else {
                entry = m_prices.get( name );
                entry.addPriceEntry( price, expansion );
            }

            ary = null;
            entry = null;
            price = null;
            name = null;
            expansion = null;
            temp = null;
        }

        line = null;
    }

    public void updatePrices() {
        try {
            FileUtils.copyURLToFile( new URL( "http://www.magictraders.com/pricelists/current-magic-excel.txt" ), new File( PRICES_FILE ) );
            updatePrices( PRICES_FILE );
        } catch( Exception ex ) {
            Logger.getLogger(c_PriceDB.class.getName()).log(Level.SEVERE, "Prices could not be downloaded from magictraders.com!", ex);
        }
    }
    
    public void updatePrices( String filepath ) {
        c_File file = new c_File();
        try {
            file.read( this.getClass(), this, Action.ACTION_PRICES_LOAD_LINE, filepath, false );
        } catch (IOException ex) {
            Logger.getLogger(c_PriceDB.class.getName()).log(Level.SEVERE, "Prices could not be loaded from disk!", ex);
        }
        file = null;
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
