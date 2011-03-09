/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Phillip
 */
public class Dialog {

    public static void MsgBox( JFrame frame, String message ) {
        JOptionPane.showMessageDialog( frame, message );
    }
    public static void MsgBox( JFrame frame, String title, String message ) {
        JOptionPane.showMessageDialog( frame, message, title, JOptionPane.INFORMATION_MESSAGE );
    }
    public static int MsgBox( JFrame frame, String title, String message, int options ) {
        return JOptionPane.showConfirmDialog( frame, message, title, options );
    }
    public static int MsgBox( JFrame frame, String title, String message, int options, int type ) {
        return JOptionPane.showConfirmDialog( frame, message, title, options, type );
    }

    public static String InputBox( JFrame frame, String message, String default_str ) {
        return JOptionPane.showInputDialog( frame, message, default_str );
    }

    public static void ErrorBox( JFrame frame, StackTraceElement[] stack_trace ) {
        String message = "";
        for( int i=0; i<stack_trace.length && i<10; i++ ) {
            message += stack_trace[ i ] + "\n";
        }
        JOptionPane.showMessageDialog( frame, message );

        message = null;
    }

}
