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

package GUI;

import Data.c_CardDB;
import Data.c_Deck;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;

/**
 *
 * @author Phillip
 */
public class ProxyPrinterDialog extends JDialog {

    private ProxyPrinter m_printer;

    public ProxyPrinterDialog( Frame owner, c_Deck deck, c_CardDB db ) {
        super( owner, "Proxy Printer", true );
        m_printer = new ProxyPrinter( deck, db );
        m_printer.setDialog( this );
        m_printer.addActionListener( (ActionListener)owner );
        add( m_printer );
        Dimension dim = m_printer.getPreferredSize();
        setBounds( 0, 0, dim.width, dim.height );
        dim = null;
    }

    public void closeDialog() {
        processWindowEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );
    }
}
