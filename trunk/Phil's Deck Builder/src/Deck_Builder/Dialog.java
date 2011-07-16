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
