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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Phillip
 */
public class c_File {
    private EventListenerList m_listeners = new EventListenerList();
    private static final String[] m_invalidChars = { "\\", "/", ":", "*", "?", "\"", "<", ">", "|" };

    public c_File() {
    }

    public static String getFilename( String filepath ) {
        String separator = "/";
        if( filepath.contains( "\\" ) ) {
            separator = "\\\\";
        }
        String[] path = filepath.split( separator );
        return path[ path.length - 1 ];
    }

    public static String getExtension( String filepath ) {
        String ext = "";
        int i = filepath.lastIndexOf( '.' );

        if( i > 0 && i < filepath.length() - 1 ) {
            ext = filepath.substring(i+1).toLowerCase();
        }
        return ext;
    }

    public static String setExtension( String filepath, String extension ) {
        String ext = getExtension( filepath.toLowerCase() );
        String fpath = filepath;
        if( ext.length() > 0 ) {
            fpath = filepath.replace( "." + ext, "." + extension );
        }
        return fpath;
    }

    public static String removeInvalidFilenameChars( String filename ) {
        String fname = getFilename( filename );
        boolean hasInvChars = false;
        for( String invChar : m_invalidChars ) {
            if( fname.contains( invChar ) ) {
                hasInvChars = true;
                fname = fname.replaceAll( invChar, "" );
            }
        }
        if( hasInvChars ) {
            return filename.replace( getFilename( filename ), fname );
        }
        return filename;
    }

    public void write( String filepath, String data ) throws FileNotFoundException, IOException {
        File f = new File( removeInvalidFilenameChars( filepath ) );
        FileOutputStream fop = new FileOutputStream( f );
        PrintStream ps = new PrintStream( fop );

        if( f.exists() ) {
            ps.print( data );
            ps.close();
            fop.close();
        }

        f = null;
        fop = null;
    }

    public void read( Class _class, ActionListener listener, Integer action, String filepath, boolean asResource ) throws IOException {
        addActionListener( listener );

        if( asResource ) {
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
        } else {
            Scanner scanner = null;
            FileReader fr = null;
            fr = new FileReader( filepath );
            scanner = new Scanner( fr );
            scanner.useDelimiter( "\r\n" );
            ArrayList<String> lines = new ArrayList<String>();

            while( scanner.hasNext() ) {
                lines.add( scanner.next() );
            }
            fireActionEvent( _class, Action.ACTION_FILE_TOTAL_LINES, Integer.toString( lines.size() ) );

            for( int i=0; i<lines.size(); i++ ) {
                fireActionEvent( _class, action, lines.get( i ) );
            }

            if( scanner != null ) {
                scanner.close();

                fr = null;
                scanner = null;
            }

            fireActionEvent( _class, Action.ACTION_FILE_LOAD_DONE, Action.COMMAND_FILE_LOAD_DONE );
            lines = null;
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
                return;
            }
        }

        listeners = null;
    }
}
