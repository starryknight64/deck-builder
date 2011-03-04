/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Deck_Builder;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;

/**
 *
 * @author Phillip
 */
public class c_CastingCost extends c_Colors {

    private final static String GLYPH_DIR = "/resources/Images/%s.png";
    private final static Integer GLYPH_WIDTH_IN = 25;
    private final static Integer GLYPH_HEIGHT_IN = GLYPH_WIDTH_IN;
    private final static Integer GLYPH_WIDTH_OUT = 16;
    private final static Integer GLYPH_HEIGHT_OUT = GLYPH_WIDTH_OUT;
    //private final static String[] GLYPHS = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "X", "B", "U", "G", "R", "W", ")" };

    private ArrayList<String> m_glyphs;

    public c_CastingCost() {
        m_glyphs = new ArrayList<String>();
    }

    public c_CastingCost( String glyphs ) {
        // Normalize glyph string
        if( glyphs.length() <= 0 ) {
            m_glyphs = new ArrayList<String>();
            return;
        }

        String ary[] = glyphs.split( "," );
        m_glyphs = new ArrayList<String>();
        m_glyphs.addAll(Arrays.asList(ary));
        setColors( glyphs );

        ary = null;
    }

    public static String tokenize( String glyphs ) {
        String temp = "";
        boolean isDigit = false;
        boolean isParen = false;
        for( int i=0; i<glyphs.length(); i++ ) {
            char ch = glyphs.charAt( i );
            if( Character.isDigit( ch ) && isParen == false ) {
                isDigit = true;
                temp += ch;
            } else if( ch == '(' ) {
                isParen = true;
                if( isDigit ) {
                    isDigit = false;
                    temp += ",";
                }
            } else if( isParen && ch != ')' ) {
                if( Character.isLetter( ch ) || Character.isDigit( ch ) ) {
                    temp += ch;
                }
            } else if( ch == ')' ) {
                isParen = false;
                temp += ",";
            } else {
                if( isDigit ) {
                    isDigit = false;
                    temp += ",";
                }
                temp += ch + ",";
            }
        }
        if( temp.endsWith( "," ) ) {
            return temp.substring( 0, temp.length() - 1 );
        }
        return temp;
    }

    @Override
    public String toString() {
        String glyphs = "";
        for (int i = 0; i < m_glyphs.size(); i++) {
            glyphs += m_glyphs.get(i);
            if (i + 1 < m_glyphs.size()) {
                glyphs += ",";
            }
        }
        return glyphs;
    }

    public static Integer getCMC( String glyphs ) {
        c_CastingCost cost = new c_CastingCost( glyphs );
        return cost.getCMC();
    }

    public Integer getCMC() {
        Integer cmc = 0;

        for (int i = 0; i < m_glyphs.size(); i++) {
            cmc += getGlyphCMC( m_glyphs.get( i ) );
        }
        return cmc;
    }

    public static Integer getGlyphCMC( String glyph ) {
        if( glyph.length() == 0 || glyph.equals( "X" ) ) {
            return 0;
        }

        int j = 0;
        boolean hasDigits = false;
        while( j < glyph.length() && Character.isDigit( glyph.charAt( j ) ) ) {
            hasDigits = true;
            j++;
        }

        if( hasDigits ) {
            return Integer.parseInt( glyph.substring( 0, j ) );
        } else {
            return 1;
        }
    }

    private boolean doesImageExist( String path ) {
        return( getClass().getResource( path ) != null );
    }

    private ImageIcon getGlyphImage( String glyph ) {
        String path = String.format( GLYPH_DIR, glyph );
        if( doesImageExist( path ) ) {
            return new ImageIcon( getClass().getResource( path ) );
        }
        return new ImageIcon( getGlyphImage( "-1" ).getImage() );
    }

    public ImageIcon getImage() {
        if( m_glyphs.size() <= 0 ) {
            return new ImageIcon( getGlyphImage( "-1" ).getImage() );
        }

        ImageIcon glyph;
        int width_in = getWidthIn( m_glyphs.get( 0 ) );
        BufferedImage img = new BufferedImage( width_in * m_glyphs.size(), GLYPH_HEIGHT_IN, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = img.createGraphics();
        String desc = "";
        for( int i=0; i<m_glyphs.size(); i++ ) {
            desc += m_glyphs.get( i ) + ",";
            glyph = getGlyphImage( m_glyphs.get( i ) );
            g.drawImage( glyph.getImage(), i * width_in, 0, null);
        }

        g = null;
        glyph = null;

        int width_out = getWidthOut( m_glyphs.get( 0 ) );
        ImageIcon icon = new ImageIcon( img.getScaledInstance( width_out * m_glyphs.size(), GLYPH_HEIGHT_OUT, Image.SCALE_SMOOTH ), "" );
        icon.setDescription( desc.substring( 0, desc.length()-1 ) );
        return icon;
    }

    private int getWidthIn( String glyph ) {
        int width_in = GLYPH_WIDTH_IN;
        if( glyph.equals( "1000000" ) ) {
            width_in = 61; // Gleemax
        } else if( glyph.equals( "500" ) ) {
            width_in = 13; // Little Girl
        }

        return width_in;
    }
    private int getWidthOut( String glyph ) {
        int width_out = GLYPH_WIDTH_OUT;
        if( glyph.equals( "1000000" ) ) {
            width_out = 39; // Gleemax
        } else if( glyph.equals( "500" ) ) {
            width_out = 8; // Little Girl
        }

        return width_out;
    }
}
