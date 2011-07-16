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
