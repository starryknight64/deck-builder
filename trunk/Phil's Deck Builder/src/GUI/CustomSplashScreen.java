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

/**
*
* @author Phillip
*/

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class CustomSplashScreen extends JWindow {
    private JLabel m_imageLabel = new JLabel();
    private JProgressBar m_progressBar = new JProgressBar();
    private ImageIcon m_image;

    public CustomSplashScreen( ImageIcon imgIcon ) {
        m_image = imgIcon;
        try {
            jbInit();
        } catch( Exception ex ) {
            ex.printStackTrace();
        }
    }

    void jbInit() throws Exception {
        m_imageLabel.setIcon( m_image );
        getContentPane().add( m_imageLabel, BorderLayout.CENTER );
        m_progressBar.setIndeterminate( true );
        m_progressBar.setUI( new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() {
                return Color.BLACK;
            }

            @Override
            protected Color getSelectionForeground() {
                return Color.BLACK;
            }
        } );
        getContentPane().add( m_progressBar, BorderLayout.SOUTH );
        pack();
    }

    public void setProgressMax( int maxProgress ) {
        m_progressBar.setMaximum( maxProgress );
    }

    public int getProgress() {
        return m_progressBar.getValue();
    }

    public void setProgress( int progress ) {
        final int theProgress = progress;
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                m_progressBar.setValue( theProgress );
            }
        } );
    }

    public void setProgress( String message, int progress ) {
        final int theProgress = progress;
        final String theMessage = message;
        setProgress( progress );
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                m_progressBar.setValue( theProgress );
                setMessage( theMessage );
            }
        } );
    }

    public void setScreenVisible( boolean v ) {
        final boolean visible = v;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible( visible );
            }
        } );
    }

    private void setMessage( String message ) {
        if( message == null ) {
            message = "";
            m_progressBar.setStringPainted( false );
        } else {
            m_progressBar.setStringPainted( true );
        }

        m_progressBar.setString( message );
    }
}
