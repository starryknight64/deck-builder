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

    private static final int MIN_COLS_REQUIRED_FOR_CREATURES_PLANESWALKERS = DBCol.Toughness.val + 1;
    private static final int MIN_COLS_REQUIRED_FOR_NON_CREATURES           = DBCol.Expansion.val + 1;

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
            // We are attempting to parse a row that was added to the DB by the deck builder (or by a human I suppose)
            if( dbRow.length >= MIN_COLS_REQUIRED_FOR_NON_CREATURES && dbRow.length < MIN_COLS_REQUIRED_FOR_CREATURES_PLANESWALKERS ) {
                // Let's hope it's a non-creature type
                // If it is a creature type, then we don't have enough columns!
                // Note that String.split ignores trailing whitespace, which includes tabs
                String type = dbRow[ DBCol.Type.val ].toLowerCase();
                if( type.contains( "creature" ) || type.contains( "planeswalker" ) ) {
                    int i =0;
                    return;
                }
            }
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

        if( dbRow.length >= MIN_COLS_REQUIRED_FOR_CREATURES_PLANESWALKERS ) {
            String p = dbRow[ DBCol.Power.val ];
            String t = dbRow[ DBCol.Toughness.val ];
            if( p.length() > 0 && t.length() > 0 ) {
                PT = p + "/" + t;
            } else {
                PT = "";
            }
        } else {
            PT = "";
        }

        CastingCost = new c_CastingCost( c_CastingCost.tokenize( dbRow[ DBCol.Cost.val ] ) );
        Expansion = dbRow[ DBCol.Expansion.val ];
        MID = Integer.parseInt( dbRow[ DBCol.MID.val ] );
    }

    public String getDBRow() {
        String row = "";

        //ID	Language	Name	Alt	Cost	Type	Set	Rarity	P	T	Oracle Rules	Printed Name	Printed Type	Printed Rules	Flavor Text	Card #	Artist	Printings
        row += this.MID.toString() + "\t";              //MID
        row += "English" + "\t";                        //Language
        row += this.Name + "\t";                        //Name
        row += "" + "\t";                               //Alt
        row += this.CastingCost.getDBString() + "\t";   //Cost
        row += this.getFullType() + "\t";               //Type
        row += this.Expansion + "\t";                   //Set
        row += "" + "\t";                               //Rarity
        row += this.getPower() + "\t";                  //Power
        row += this.getToughness() + "\t";              //Toughness

        //Oracle Rules	Printed Name	Printed Type	Printed Rules	Flavor Text	Card #	Artist	Printings
        for( int i=0; i<7; i++ ) {
            row += "\t";
        }

        return row;
    }

    public String getFullType() {
        return Type + ( SubType.equals( "" ) ? "" : " - " + SubType );
    }

    public String getPower() {
        String power = "";
        if( !PT.equals( "" ) ) {
            power = PT.split( "/" )[ 0 ];
        }
        return power;
    }

    public String getToughness() {
        String toughness = "";
        if( !PT.equals( "" ) ) {
            toughness = PT.split( "/" )[ 1 ];
        }
        return toughness;
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
