/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import Deck_Builder.c_ExpansionDB.Expansion;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
Usage idea:
Using my Database (not mwBase's)
Split up DB into expansions
Have list of Acronyms/Gatherer strings listed in reverse chronological order
Parse expansions into deck builder in same order
When searching DB by name, search in same order as was added to DB
When searching DB by MID, search each expansion in same order as was added to DB
When searching DB by name/expansion, search the specified expansion for the specified name
*/

/**
 *
 * @author Phillip
 */
public class c_Expansion {
    private String m_traderAcronym;
    private String m_workstationAcronym;
    private String m_gathererAcronym;
    private String m_expansion;
    private Boolean m_isCore;
    private String m_block;
    private Date m_date;
    
    public c_Expansion( String[] expLine ) {
        m_traderAcronym      = expLine[ Acronym.Trader.val ];
        m_workstationAcronym = expLine[ Acronym.Workstation.val ];
        m_gathererAcronym    = expLine[ Acronym.Gatherer.val ];
        m_expansion          = expLine[ Expansion.Name.val ];
        m_isCore             = expLine[ Expansion.isCore.val ].equalsIgnoreCase( "Yes" );

        if( !expLine[ Expansion.Date.val ].equals( "?" ) ) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy" );
                m_date = sdf.parse( expLine[ Expansion.Date.val ] );
                sdf = null;
            } catch( Exception ex ) {
                int i=0;
            }
        }

        String block = expLine[ Expansion.Block.val ];
        if( !block.equals( "?" ) ) {
            m_block = block;
        } else {
            m_block = "";
        }
    }

    public String getName() {
        return m_expansion;
    }

    public Date getDate() {
        return m_date;
    }

    public Boolean isCore() {
        return m_isCore;
    }

    public String getBlock() {
        return m_block;
    }
    
    public String[] getAllAcronyms() {
        return new String[] { m_traderAcronym, m_workstationAcronym, m_gathererAcronym };
    }
    public String getAcronym( Acronym which ) {
        if( which.equals( Acronym.Workstation ) ) {
            return m_workstationAcronym;
        } else if( which.equals( Acronym.Trader ) ) {
            return m_traderAcronym;
        } else if( which.equals( Acronym.Gatherer ) ) {
            return m_gathererAcronym;
        }
        return "";
    }
    
    @Override
    public String toString() {
        return m_expansion;
    }
    
    public enum Acronym {
        Trader,
        Workstation,
        Gatherer;
        public int val = this.ordinal();
    }

}
