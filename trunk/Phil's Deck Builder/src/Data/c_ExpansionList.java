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

import java.util.ArrayList;

/**
 *
 * @author Phillip
 */
public class c_ExpansionList {
    private ArrayList<Integer> m_list = new ArrayList<Integer>();
    private c_ExpansionDB m_expansionDB;

    public c_ExpansionList( c_ExpansionDB edb ) {
        m_expansionDB = edb;
    }

    public void addExpansion( c_Expansion exp ) {
        addExpansion( m_expansionDB.getEID( exp.getName() ) );
    }
    public void addExpansion( Integer eid ) {
        m_list.add( eid );
    }

    public c_Expansion[] getList() {
        return m_list.toArray( new c_Expansion[] {} );
    }

    public boolean contains( c_Expansion exp ) {
        return m_list.contains( m_expansionDB.getEID( exp.getName() ) );
    }

    @Override
    public String toString() {
        String exps = "";
        for( Integer eid : m_list ) {
            exps += m_expansionDB.getExpansion( eid ).toString() + ",";
        }
        if( exps.length() > 0 ) {
            return exps.substring( 0, exps.length() - 1 );
        }
        return exps;
    }
}
