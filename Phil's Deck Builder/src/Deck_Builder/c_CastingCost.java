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

    private ArrayList<String> m_glyphs;

    public c_CastingCost() {
        m_glyphs = new ArrayList<String>();
    }

    @Override
    public void finalize() throws Throwable {
        m_glyphs = null;
        super.finalize();
    }

    public c_CastingCost( String glyphs ) {
        // Normalize glyph string
        if( glyphs.length() <= 0 ) {
            m_glyphs = new ArrayList<String>();
            return;
        }

        String temp[] = glyphs.split( "," );
        m_glyphs = new ArrayList<String>();
        m_glyphs.addAll(Arrays.asList(temp));
        setColors( glyphs );

        temp = null;
        ////System.gc();
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
//        int j;
//        boolean hasDigits;

        for (int i = 0; i < m_glyphs.size(); i++) {
            cmc += getGlyphCMC( m_glyphs.get( i ) );
//            j = 0;
//            hasDigits = false;
//            while( Character.isDigit( m_glyphs.get(i).charAt(j) ) ) {
//                hasDigits = true;
//                j++;
//            }
//
//            if (hasDigits) {
//                cmc += Integer.parseInt( m_glyphs.get(i).substring(0, j) );
//            } else if( !m_glyphs.get(i).equals( "X" ) ) {
//                cmc++;
//            }
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
        ImageIcon img;
        if( doesImageExist( path ) ) {
            img = new ImageIcon( getClass().getResource( path ) );

            path = null;
            return img;
        }

        path = null;
        ////System.gc();
        img = new ImageIcon( getGlyphImage( "-1" ).getImage() );
        return img;
    }

    public ImageIcon getImage() {
        if( m_glyphs.size() <= 0 ) {
            return new ImageIcon( getGlyphImage( "-1" ).getImage() );
        }

        ImageIcon glyph;
        int width_in = ( m_glyphs.get( 0 ).equals( "1000000" ) ? 61 : GLYPH_WIDTH_IN );
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
        ////System.gc();

        int width_out = ( m_glyphs.get( 0 ).equals( "1000000" ) ? 39 : GLYPH_WIDTH_OUT );
        ImageIcon icon = new ImageIcon( img.getScaledInstance( width_out * m_glyphs.size(), GLYPH_HEIGHT_OUT, Image.SCALE_SMOOTH ), "" );
        icon.setDescription( desc.substring( 0, desc.length()-1 ) );
        return icon;
    }
}
