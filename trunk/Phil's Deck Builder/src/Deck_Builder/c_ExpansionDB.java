/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import Deck_Builder.c_Expansion.Acronym;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Phillip
 */
public class c_ExpansionDB implements ActionListener {
                  //Integer is EID (Expansion ID)
    private HashMap<Integer, c_Expansion> m_expansions = new HashMap<Integer, c_Expansion>();
    
                  //Keys are hashcodes of Acronyms strings, values are EID's
    private HashMap<Integer, Integer> m_traderAcronyms = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> m_workstationAcronyms = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> m_gathererAcronyms = new HashMap<Integer, Integer>();

                  //Key is hashcode of expansion name, value is EID
    private HashMap<Integer, Integer> m_expansionNames = new HashMap<Integer, Integer>();

    private final ArrayList<HashMap<Integer, Integer>> m_lookups = new ArrayList<HashMap<Integer, Integer>>();
    {
        m_lookups.add( m_traderAcronyms );
        m_lookups.add( m_workstationAcronyms );
        m_lookups.add( m_gathererAcronyms );
    }

    public c_ExpansionDB() {
    }
    public c_ExpansionDB( String filename ) {
        c_File file = new c_File();
        try {
            file.read( this.getClass(), this, Action.ACTION_EXPANSION_LOAD_LINE, filename, false );
            file = null;
        } catch ( Exception ex ) {
            int i=0;
        }
    }

    public Integer getEID( Acronym which, String acronym ) {
        return m_lookups.get( which.val ).get( acronym.hashCode() );
    }
    public Integer getEID( String expName ) {
        return m_expansionNames.get( expName.hashCode() );
    }
    public Integer getEID( c_Expansion exp ) {
        return getEID( exp.getName() );
    }

    public c_Expansion getExpansion( Acronym which, String acronym ) {
        return getExpansion( getEID( which, acronym ) );
    }
    public c_Expansion getExpansion( String expName ) {
        return getExpansion( getEID( expName ) );
    }
    public c_Expansion getExpansion( Integer eid ) {
        return m_expansions.get( eid );
    }

    public boolean contains( Acronym which, String acronym ) {
        return m_lookups.get( which.val ).containsKey( acronym.hashCode() );
    }
    public boolean contains( String expName ) {
        return m_expansionNames.containsKey( expName.hashCode() );
    }

    public void actionPerformed( ActionEvent e ) {
        if( !e.getActionCommand().startsWith( "Trader\tMWS\tGatherer\tExpansion\tCore\tDate" ) ) {
            c_Expansion exp = new c_Expansion( e.getActionCommand().split( "\t" ) );
            Integer eid = m_expansions.size();
            m_expansions.put( eid, exp );

            String[] acronyms = exp.getAllAcronyms();
            for( int i=0; i<acronyms.length; i++ ) {
                if( !acronyms[ i ].equals( "?" ) ) {
                    m_lookups.get( i ).put( acronyms[ i ].hashCode(), eid );
                }
            }
            m_expansionNames.put( exp.getName().hashCode(), eid );

            exp = null;
        }
    }

    public enum Expansion {
        Name( Acronym.values().length ),
        isCore( Acronym.values().length + 1 ),
        Date( Acronym.values().length + 2 );

        public int val;

        Expansion( int v ) {
            val = v;
        }
    }


}
