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
