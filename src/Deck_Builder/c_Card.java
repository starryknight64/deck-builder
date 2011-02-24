/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

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
//        Price = new c_Price();
    }

    @Override
    public void finalize() throws Throwable {
        Name = null;
        Type = null;
        SubType = null;
        PT = null;
        CastingCost = null;
        Expansion = null;
        MID = null;
        super.finalize();
    }

    public void setCard( c_Card card ) {
        Name = card.Name;
        Type = card.Type;
        SubType = card.SubType;
        PT = card.PT;
        CastingCost = card.CastingCost;
        Expansion = card.Expansion;
        MID = card.MID;
        //Price = card.Price;
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
        //Card Name	Amt	Card Type	Sub-Type	Cost	P/T	Set	MID
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
