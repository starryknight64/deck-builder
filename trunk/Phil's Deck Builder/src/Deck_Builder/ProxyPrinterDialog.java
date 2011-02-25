/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

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
