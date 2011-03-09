/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Data;

import java.util.HashMap;

/**
 *
 * @author Phillip
 */
public class c_PriceEntry extends c_Price {
                 /* Expansion is String (Gatherer's full version--not the acronym) */
    private HashMap<Integer, c_Price> m_prices = new HashMap<Integer, c_Price>();

    c_PriceEntry() {
        super();
    }

    public void addPriceEntry( c_Price price ) {
        addPriceEntry( price, "" );
    }
    public void addPriceEntry( c_Price price, String expansion ) {
        boolean hasPrice = contains( expansion );

        if( (!hasPrice && !expansion.equals( "" ))
            || m_prices.isEmpty() ) { /* Add price to the average */
            super.addPrice( price );
        }

        if( !expansion.equals( "" ) ) {
            if( !hasPrice ) {
                m_prices.put( expansion.hashCode(), price );
            }
        }
    }
    
    public boolean contains( String expansion ) {
        return m_prices.containsKey( expansion.hashCode() );
    }
    
    public c_Price getPrice( String expansion ) {
        if( contains( expansion ) ) {
            return m_prices.get( expansion.hashCode() );
        }
        return this;
    }

}
