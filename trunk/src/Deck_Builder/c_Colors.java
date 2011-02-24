/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

/**
 *
 * @author Phillip
 */
public class c_Colors {
    private boolean m_blue;
    private boolean m_green;
    private boolean m_white;
    private boolean m_black;
    private boolean m_red;
    
    private enum GlyphColor {
        BLUE( "U" ),
        GREEN( "G" ),
        WHITE( "W" ),
        BLACK( "B" ),
        RED( "R" );
        
        private String m_name;
        
        GlyphColor( String chr ) {
            m_name = chr;
        }
        
        @Override
        public String toString() {
            return m_name;
        }
    }
    
    c_Colors() {
        resetColors();
    }

    public final void resetColors() {
        m_blue = false;
        m_green = false;
        m_white = false;
        m_black = false;
        m_red = false;
    }

    public void setColors( c_Card card ) {
        setColors( card.CastingCost.getColors() );
    }
    public void setColors( c_Colors colors ) {
        m_blue |= colors.isBlue();
        m_green |= colors.isGreen();
        m_white |= colors.isWhite();
        m_black |= colors.isBlack();
        m_red |= colors.isRed();
    }
    public void setColors( String glyphs ) {
        for( int i=0; i<GlyphColor.values().length; i++ ) {
            if( glyphs.contains( GlyphColor.values()[ i ].toString() ) ) {
                m_blue |= (GlyphColor.values()[ i ] == GlyphColor.BLUE );
                m_green |= (GlyphColor.values()[ i ] == GlyphColor.GREEN );
                m_white |= (GlyphColor.values()[ i ] == GlyphColor.WHITE );
                m_black |= (GlyphColor.values()[ i ] == GlyphColor.BLACK );
                m_red |= (GlyphColor.values()[ i ] == GlyphColor.RED );
            }
        }
    }

    public c_Colors getColors() {
        return this;
    }
    
    public String getColorsText() {
        String colors = "";
        if( m_blue && m_green && m_white && m_black && m_red ) {
            return "All Colors";
        } else {
            if( m_blue ) {
                colors += "Blue, ";
            }
            if( m_green ) {
                colors += "Green, ";
            }
            if( m_white ) {
                colors += "White, ";
            }
            if( m_black ) {
                colors += "Black, ";
            }
            if( m_red ) {
                colors += "Red";
            }
            if( colors.equals( "" ) ) {
                return "Colorless";
            }
            if( colors.endsWith( ", " ) ) {
                return colors.substring( 0, colors.length() - 2 );
            }
        }
        return colors;
    }

    public boolean isBlue() {
        return m_blue;
    }
    public boolean isGreen() {
        return m_green;
    }
    public boolean isWhite() {
        return m_white;
    }
    public boolean isBlack() {
        return m_black;
    }
    public boolean isRed() {
        return m_red;
    }
    public boolean isColorless() {
        return !(m_blue && m_green && m_white && m_black && m_red);
    }
}
