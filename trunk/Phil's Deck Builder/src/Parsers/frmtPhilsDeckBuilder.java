/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Parsers;

import Deck_Builder.c_CardDB;
import Deck_Builder.c_Deck;
import Deck_Builder.c_File;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Phillip
 */
public class frmtPhilsDeckBuilder extends DeckFormat {
    
    public static final String Extension = "tsv";

    public frmtPhilsDeckBuilder() {
        super( "Phil's Deck Files", Extension );
    }

    @Override
    public boolean saveDeck( String filename, c_Deck deck, c_CardDB db ) {
        String ext = c_File.getExtension( filename.toLowerCase() );
        String filepath = filename;
        if( !ext.equals( Extension ) ) {
            filepath = c_File.setExtension( filename, Extension );
        }
        return deck.saveDeck( filepath, db );
    }
}
