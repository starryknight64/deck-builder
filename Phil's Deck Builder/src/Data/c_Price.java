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

import java.text.DecimalFormat;

/**
 *
 * @author Phillip
 */
public class c_Price {

    public enum PriceType {
        LOW,
        AVERAGE,
        HIGH
    }

    public static final PriceType Default_Price = PriceType.AVERAGE;
    
    private Double m_low;
    private Double m_average;
    private Double m_high;
    private PriceType m_which;
    
    public c_Price() {
        m_low = 0.0;
        m_average = 0.0;
        m_high = 0.0;
        m_which = Default_Price;
    }
    
    public c_Price( Double low, Double average, Double high, PriceType which ) {
        m_low = low;
        m_average = average;
        m_high = high;
        m_which = which;
    }

    @Override
    public void finalize() throws Throwable {
        m_low = null;
        m_average = null;
        m_high = null;
        m_which = null;
        super.finalize();
    }
    
    public Double Low() {
        return parsePrice( m_low );
    }
    public Double Average() {
        return parsePrice( m_average );
    }
    public Double High() {
        return parsePrice( m_high );
    }
    public Double LowRaw() {
        return m_low;
    }
    public Double AverageRaw() {
        return m_average;
    }
    public Double HighRaw() {
        return m_high;
    }

    public Double getDefaultPrice() {
        return getPrice( m_which );
    }
    
    public Double getPrice( PriceType which ) {
        Double price = 0.0;
        if( which == PriceType.LOW ) {
            price = Low();
        } else if( which == PriceType.AVERAGE ) {
            price = Average();
        } else if( which == PriceType.HIGH ) {
            price = High();
        }
        return price;
    }

    public c_Price getPriceTimesAmount( Integer amount ) {
        return new c_Price( m_low * amount, m_average * amount, m_high * amount, PriceType.AVERAGE );
    }

    private Double parsePrice( Double price ) {
        return Double.valueOf( formatPrice( price ) );
    }

    public static String formatPrice( Double price ) {
        return new DecimalFormat( "#0.00" ).format( price );
    }
    
    private String getPriceString( PriceType which ) {
        return "$" + formatPrice( getPrice( which ) );
    }

    public void addPrice( c_Price price ) {
        addPriceTimesAmount( price, 1 );
    }
    public void addPriceTimesAmount( c_Price price, Integer amount ) {
        m_low += (amount * price.LowRaw());
        m_average += (amount * price.AverageRaw());
        m_high += (amount * price.HighRaw());
    }
    
    @Override
    public String toString() {
        return getPriceString( m_which );
    }
    public String toString( PriceType which ) {
        return getPriceString( which );
    }
}
