/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Parsers;

import Data.c_CardDB;
import Data.c_Deck;
import Data.c_File;
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

    public boolean loadDeck( String filename, c_Deck deck, c_CardDB db ) {
        return true;
    }

    public boolean saveDeck( String filename, c_Deck deck, c_CardDB db ) {
        String ext = c_File.getExtension( filename.toLowerCase() );
        String filepath = filename;
        if( !ext.equals( Extension ) ) {
            filepath = c_File.setExtension( filename, Extension );
        }
        return deck.saveDeck( filepath, db );
    }
}
