/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Phillip
 */
public class c_File {
    private EventListenerList m_listeners = new EventListenerList();

    public c_File() {
    }

    @Override
    public void finalize() throws Throwable {
        m_listeners = null;
        super.finalize();
    }

    public static String getFilename( String filepath ) {
        String separator = "/";
        if( filepath.contains( "\\" ) ) {
            separator = "\\";
        }
        String[] path = filepath.split( separator );
        return path[ path.length - 1 ];
    }

    public void write( String filepath, String data ) throws FileNotFoundException, IOException {
        File f = new File( filepath );
        FileOutputStream fop = new FileOutputStream( f );

        if( f.exists() ) {
            fop.write( data.getBytes() );
            fop.flush();
            fop.close();
        }

        f = null;
        fop = null;
    }

    public void read( Class _class, ActionListener listener, Integer action, String filepath, boolean asResource ) throws IOException {
        addActionListener( listener );

        if( asResource ) {
            //try {
                //System.console().printf( "filepath='%s'\n", filepath );
                InputStream in = getClass().getResourceAsStream( filepath );
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    fireActionEvent( _class, action, line );
                    line = null;
                }
                
                br = null;
                isr = null;
                in = null;
                line = null;
                //System.gc();
            //} catch (IOException io) {
            //    int i=0;
            //    System.console().printf( "filepath='%s'", filepath );
            //}
        } else {
            Scanner scanner = null;
            FileReader fr = null;
            //try {
                fr = new FileReader( filepath );
                scanner = new Scanner( fr );
                String line;

                //first use a Scanner to get each line
                while ( scanner.hasNextLine() ) {
                    line = scanner.nextLine();
                    fireActionEvent( _class, action, line );
                    line = null;
                }
            //} catch( Exception ex ) {
            //    int i=0;
            //    ex.getStackTrace();
            //} finally {
                if( scanner != null ) {
                    scanner.close();

                    fr = null;
                    scanner = null;
                }

                fireActionEvent( _class, Action.ACTION_FILE_LOAD_DONE, Action.COMMAND_FILE_LOAD_DONE );

                //System.gc();
            //}
        }
    }

    private void addActionListener( ActionListener listener ) {
        m_listeners.add( ActionListener.class, listener );
    }

    private void fireActionEvent( Class thisClass, Integer action, String command ) {
        Object listeners[] = m_listeners.getListenerList();
        for( int i=listeners.length-1; i>=0; i-- ) {
            if( listeners[i].getClass() == thisClass ) {
                ((ActionListener)listeners[i]).actionPerformed( new ActionEvent( this, action, command ) );

                listeners = null;
                ////System.gc();
                return;
            }
        }

        listeners = null;
        ////System.gc();
    }
}
