/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import java.util.Scanner;
import javax.swing.UIManager;

/**
 *
 * @author Phillip
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    String s = "hello|\nworld*foo*bar\r\nEUREKA!";
    System.out.println(s);
    // prints:
    // hello|world*foo*bar
    // EUREKA!

    Scanner sc;

    sc = new Scanner(s);
    sc.useDelimiter("\r\n");
    while (sc.hasNext()) {
        System.out.print("[" + sc.next() + "]");
    }
    // prints:
    // [hello][world][foo][bar
    // EUREKA!]

    System.out.println();

    sc = new Scanner(s).useDelimiter("\r?\n|\r|\\|");
    while (sc.hasNext()) {
        System.out.print("[" + sc.next() + "]");
    }

        try {
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            if( lookAndFeel != null ) {
                UIManager.setLookAndFeel( lookAndFeel );
            }
        } catch( Exception ex ) {
            System.err.println( "Couldn't use system look and feel." );
        }

        MainWindow w = new MainWindow();
        w.setVisible( true );
        w = null;
        //System.gc();
//        MainFrame frame = new MainFrame();
//        frame.setVisible(true);
    }

}
