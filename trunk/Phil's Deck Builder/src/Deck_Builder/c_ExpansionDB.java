/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import Deck_Builder.c_Expansion.Acronym;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;

/**
 *
 * @author Phillip
 */
public class c_ExpansionDB implements ActionListener {
    private static final String EXPANSIONS_FILE = "Expansions.txt";

    ActionListener m_loadingListener = null;
                 /* Integer is EID (Expansion ID) */
    private HashMap<Integer, c_Expansion> m_expansions = new HashMap<Integer, c_Expansion>();
    
                 /* Keys are hashcodes of Acronyms strings, values are EID's */
    private HashMap<Integer, Integer> m_traderAcronyms = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> m_workstationAcronyms = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> m_gathererAcronyms = new HashMap<Integer, Integer>();

    private Legals m_legalLoading = Legals.Standard;
    private Keyword m_legalTypeLoading = Keyword.Banned;

    private String m_blockLoading = "";
    private String m_currentBlock = "";
    private String m_previousBlock = "";
    private String m_currentCore = "";
    private String m_previousCore = "";
    private ArrayList<String> m_blocks = new ArrayList<String>();

                 /* Block,     CardNameHash, Legality */
    private HashMap<String, HashMap<Integer, Keyword>> m_blockLegals = new HashMap<String, HashMap<Integer, Keyword>>();

                 /* Legals,    CardNameHash, Legality */
    private EnumMap<Legals, HashMap<Integer, Keyword>> m_otherLegals = new EnumMap<Legals, HashMap<Integer, Keyword>>( Legals.class );

                 /* Legals,           EID */
    private EnumMap<Legals, ArrayList<Integer>> m_otherLegalsExpansions = new EnumMap<Legals, ArrayList<Integer>>( Legals.class );

                 /* Key is hashcode of expansion name, value is EID */
    private HashMap<Integer, Integer> m_expansionNames = new HashMap<Integer, Integer>();

    private final ArrayList<HashMap<Integer, Integer>> m_lookups = new ArrayList<HashMap<Integer, Integer>>();

    public c_ExpansionDB() {
        init();
    }

    public boolean loadExpansionDB( ActionListener listener ) {
        boolean success = true;
        m_loadingListener = listener;
        c_File file = new c_File();
        try {
            file.read( this.getClass(), this, Action.ACTION_EXPANSION_LOAD_LINE, EXPANSIONS_FILE, false );
            file.read( this.getClass(), this, Action.ACTION_BLOCK_LEGAL_LOAD_LINE, "legality/block.txt", false );
            file.read( this.getClass(), this, Action.ACTION_STANDARD_LEGAL_LOAD_LINE, "legality/standard.txt", false );
            file.read( this.getClass(), this, Action.ACTION_EXTENDED_LEGAL_LOAD_LINE, "legality/extended.txt", false );
            file.read( this.getClass(), this, Action.ACTION_LEGACY_LEGAL_LOAD_LINE, "legality/legacy.txt", false );
            file.read( this.getClass(), this, Action.ACTION_VINTAGE_LEGAL_LOAD_LINE, "legality/vintage.txt", false );
            file = null;
            updateLegals();
        } catch ( Exception ex ) {
            success = false;
        }
        file = null;
        m_loadingListener = null;
        return success;
    }
    
    private void init() {
        m_otherLegalsExpansions.put( Legals.Standard, new ArrayList<Integer>() );
        m_otherLegalsExpansions.put( Legals.Extended, new ArrayList<Integer>() );
        m_otherLegalsExpansions.put( Legals.Legacy,   new ArrayList<Integer>() );
        m_otherLegalsExpansions.put( Legals.Vintage,  new ArrayList<Integer>() );
        m_lookups.add( m_traderAcronyms );
        m_lookups.add( m_workstationAcronyms );
        m_lookups.add( m_gathererAcronyms );
    }

    public ArrayList<Integer> getLegalExpansions( Legals leg ) {
        return m_otherLegalsExpansions.get( leg );
    }

    public ArrayList<Integer> getBlockExpansions( String block ) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for( int eid : m_expansions.keySet() ) {
            if( m_expansions.get( eid ).getBlock().equals( block ) ) {
                list.add( eid );
            }
        }
        return list;
    }

    public boolean doesBlockContainLegals( String block ) {
        return m_blockLegals.containsKey( block );
    }

    public HashMap<Integer, Keyword> getBlockLegals( String block ) {
        return m_blockLegals.get( block );
    }

    public boolean doesLegalContainLegals( Legals leg ) {
        return m_otherLegals.containsKey( leg );
    }

    public HashMap<Integer, Keyword> getOtherLegals( Legals leg ) {
        return m_otherLegals.get( leg );
    }

    public ArrayList<String> getBlocks() {
        return m_blocks;
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
    
    private void loadingListenerAction( ActionEvent e ) {
        if( m_loadingListener != null ) {
            m_loadingListener.actionPerformed( e );
        }
    }

    public void actionPerformed( ActionEvent e ) {
        if( e.getID() == Action.ACTION_EXPANSION_LOAD_LINE && !e.getActionCommand().startsWith( "Trader\t" )  ) {
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

            String block = exp.getBlock();
            if( !m_blocks.contains( block ) && !block.equals( "" ) ) {
                m_blocks.add( block );
                m_previousBlock = m_currentBlock;
                m_currentBlock = block;
            }

            if( exp.isCore() ) {
                m_previousCore = m_currentCore;
                m_currentCore = exp.getName();
            }
            
            loadingListenerAction( e );

            exp = null;
        } else if( e.getID() == Action.ACTION_BLOCK_LEGAL_LOAD_LINE ) {
            String cmd = e.getActionCommand();
            if( cmd.startsWith( "--" ) ) {
                for( String block : m_blocks ) {
                    if( cmd.equals( "--" + block ) ) {
                        m_blockLoading = block;
                        return;
                    }
                }
                for( Keyword word : Keyword.values() ) {
                    if( cmd.equals( "--" + word.name() ) ) {
                        m_legalTypeLoading = word;
                        return;
                    }
                }
                
                loadingListenerAction( e );
                /* If we made it this far, then it means it's likely a URL and we can skip it */
            } else if( m_blockLegals.containsKey( m_blockLoading ) ) {
                /* cmd is a card's name */
                m_blockLegals.get( m_blockLoading ).put( cmd.hashCode(), m_legalTypeLoading );
                loadingListenerAction( e );
            } else {
                HashMap<Integer, Keyword> list = new HashMap<Integer, Keyword>();
                list.put( cmd.hashCode(), m_legalTypeLoading );
                m_blockLegals.put( m_blockLoading, list );
                loadingListenerAction( e );
                list = null;
            }
        } else if( e.getID() == Action.ACTION_LEGACY_LEGAL_LOAD_LINE
                || e.getID() == Action.ACTION_EXTENDED_LEGAL_LOAD_LINE
                || e.getID() == Action.ACTION_STANDARD_LEGAL_LOAD_LINE
                || e.getID() == Action.ACTION_VINTAGE_LEGAL_LOAD_LINE ) {
            String cmd = e.getActionCommand();
            m_legalLoading = Legals.getLegalFromAction( e.getID() );
            if( cmd.startsWith( "--" ) ) {
                for( Keyword word : Keyword.values() ) {
                    if( cmd.equals( "--" + word.name() ) ) {
                        m_legalTypeLoading = word;
                        loadingListenerAction( e );
                        return;
                    }
                }
            } else if( m_otherLegals.containsKey( m_legalLoading ) ) {
                /* cmd == card name! */
                m_otherLegals.get( m_legalLoading ).put( cmd.hashCode(), m_legalTypeLoading );
                loadingListenerAction( e );
            } else {
                HashMap<Integer, Keyword> list = new HashMap<Integer, Keyword>();
                list.put( cmd.hashCode(), m_legalTypeLoading );
                m_otherLegals.put( m_legalLoading, list );
                loadingListenerAction( e );
            }
        } else {
            loadingListenerAction( e );
        }
    }

    private void updateLegals() {
        Calendar cal = Calendar.getInstance();
        ArrayList<Integer> expansions = m_otherLegalsExpansions.get( Legals.Standard );

        /* Standard */
        int eid1 = m_expansionNames.get( m_currentCore.hashCode() );
        int thisMonth = cal.get( Calendar.MONTH );
        if( thisMonth >= Calendar.JULY && thisMonth <= Calendar.OCTOBER && cal.after( m_expansions.get( eid1 ).getDate() ) ) {
            /* Then we can have the previous core set be a part of Standard */
            expansions.add( m_expansionNames.get( m_previousCore.hashCode() ) );
        }
        expansions.add( eid1 ); /* Add current core set regardless */
        expansions.addAll( getBlockExpansions( m_previousBlock ) );
        expansions.addAll( getBlockExpansions( m_currentBlock ) );

        /* Extended */
        cal = Calendar.getInstance();
        cal.set( cal.get( Calendar.YEAR ) - 4, cal.get( Calendar.OCTOBER ), 1 );
        expansions = m_otherLegalsExpansions.get( Legals.Extended );
        for( int eid2 : m_expansions.keySet() ) {
            if( cal.before( m_expansions.get( eid2 ).getDate() ) ) {
                expansions.add( eid2 );
            }
        }

        /* Legacy */
        expansions = m_otherLegalsExpansions.get( Legals.Legacy );
        expansions.addAll( m_expansions.keySet() );
        eid1 = getEID( "Unglued" );
        expansions.remove( eid1 );
        eid1 = getEID( "Unhinged" );
        expansions.remove( eid1 );

        /* Vintage (same as Legacy--just different banned/restricted lists) */
        m_otherLegalsExpansions.get( Legals.Vintage ).addAll( expansions );

        expansions = null;
        cal = null;
    }
    
    public enum Legals {
        Extended,
        Vintage,
        Legacy,
        Standard;
        public int val = this.ordinal();

        public static Legals getLegalFromAction( int action ) {
            if( action == Action.ACTION_STANDARD_LEGAL_LOAD_LINE ) {
                return Standard;
            } else if( action == Action.ACTION_EXTENDED_LEGAL_LOAD_LINE ) {
                return Extended;
            } else if( action == Action.ACTION_LEGACY_LEGAL_LOAD_LINE) {
                return Legacy;
            } else if( action == Action.ACTION_VINTAGE_LEGAL_LOAD_LINE ) {
                return Vintage;
            }
            return null;
        }
    }

    public enum Keyword {
        Banned,
        Restricted;
        public int val = this.ordinal();
    }

    public enum Expansion {
        Name,
        isCore,
        Block,
        Date;
        public int val = Acronym.values().length + this.ordinal();
    }
}
