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

package Data;

import Data.c_ExpansionDB.Expansion;
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
    private String m_magicCardsInfoAcronym;
    private String m_traderAcronym;
    private String m_workstationAcronym;
    private String m_gathererAcronym;
    private String m_expansion;
    private Boolean m_isCore;
    private String m_block;
    private Date m_date;
    
    public c_Expansion( String[] expLine ) {
        m_magicCardsInfoAcronym = expLine[ Acronym.MagicCardsInfo.val ];
        m_traderAcronym         = expLine[ Acronym.Trader.val ];
        m_workstationAcronym    = expLine[ Acronym.Workstation.val ];
        m_gathererAcronym       = expLine[ Acronym.Gatherer.val ];
        m_expansion             = expLine[ Expansion.Name.val ];
        m_isCore                = expLine[ Expansion.isCore.val ].equalsIgnoreCase( "Yes" );

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
        return new String[] { m_magicCardsInfoAcronym, m_traderAcronym, m_workstationAcronym, m_gathererAcronym };
    }
    public String getAcronym( Acronym which ) {
        if( which.equals( Acronym.MagicCardsInfo ) ) {
            return m_magicCardsInfoAcronym;
        } else if( which.equals( Acronym.Workstation ) ) {
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
        MagicCardsInfo,
        Trader,
        Workstation,
        Gatherer;
        public int val = this.ordinal();
    }

}
