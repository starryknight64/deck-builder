/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/

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
