/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import GUI.MainWindow;
import javax.swing.UIManager;

/**
 *
 * @author Phillip
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main( String[] args ) {
        try {
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            if( lookAndFeel != null ) {
                UIManager.setLookAndFeel( lookAndFeel );
            }
        } catch( Exception ex ) {
            System.err.println( "Couldn't use system look and feel." );
        }

        MainWindow w = new MainWindow();
        w = null;
    }

}
