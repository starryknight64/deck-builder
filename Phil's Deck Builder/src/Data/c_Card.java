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

import GUI.WebBrowserPanel;
import Data.c_CardDB.DBCol;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Phillip
 */
public class c_Card {
    public static final Integer WIDTH = 223;
    public static final Integer HEIGHT = 310;

    public String Name;
    public String Type;
    public String SubType;
    public String PT;
    public c_CastingCost CastingCost;
    public String Expansion;
    public Integer MID;

    public c_Card() {
        Name = "";
        Type = "";
        SubType = "";
        PT = "";
        CastingCost = new c_CastingCost();
        Expansion = "";
        MID = 0;
    }

    public c_Card( String[] dbRow ) {
        if( dbRow.length != DBCol.values().length ) {
            int i =0;
            return;
        }

        Name = dbRow[ DBCol.Name.val ];
        if( Name.contains( "," ) ) {
            Name = Name.substring( 1, Name.length() - 1 );
        }

        String type = dbRow[ DBCol.Type.val ];
        if( type.contains( " - " ) ) {
            String ary[] = type.split( " - " );
            Type = ary[ 0 ];
            SubType = ary[ 1 ];
        } else {
            Type = type;
            SubType = "";
        }

        String p = dbRow[ DBCol.Power.val ];
        String t = dbRow[ DBCol.Toughness.val ];
        if( p.length() > 0 && t.length() > 0 ) {
            PT = p + "/" + t;
        } else {
            PT = "";
        }

        CastingCost = new c_CastingCost( c_CastingCost.tokenize( dbRow[ DBCol.Cost.val ] ) );
        Expansion = dbRow[ DBCol.Expansion.val ];
        MID = Integer.parseInt( dbRow[ DBCol.MID.val ] );
    }

    public void setCard( c_Card card ) {
        Name = card.Name;
        Type = card.Type;
        SubType = card.SubType;
        PT = card.PT;
        CastingCost = card.CastingCost;
        Expansion = card.Expansion;
        MID = card.MID;
    }

    public ImageIcon getImage() {
        try {
            ImageIcon icon = new ImageIcon( ImageIO.read( new File( getImagePath() ) ).getScaledInstance( WIDTH, HEIGHT, Image.SCALE_SMOOTH ) );
            return icon;
        } catch( Exception ex ) {
            try {
                FileUtils.copyURLToFile( new URL( WebBrowserPanel.getCardImageURL( MID ) ), new File( getImagePath() ) );
                return getImage();
            } catch( Exception ex2 ) {
                int i =0;
            }
        }
        return null;
    }

    public String getImagePath() {
        return getImagePath( MID );
    }
    public String getImagePath( Integer mid ) {
        return String.format( "Cards/Images/%s.png", mid );
    }

    @Override
    public String toString() {
        return toString( 1 );
    }
    public String toString( Integer amount ) {
        /* Card Name	Amt	Card Type	Sub-Type	Cost	P/T	Set	MID */
        return String.format( "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                              Name,
                              amount.toString(),
                              Type,
                              SubType,
                              CastingCost.toString(),
                              PT,
                              Expansion,
                              MID.toString()
                              );
    }
}
