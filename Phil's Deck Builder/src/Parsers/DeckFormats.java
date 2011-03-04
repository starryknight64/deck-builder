/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Parsers;

/**
 *
 * @author Phillip
 */
public enum DeckFormats {
    PhilsDeckBuilder( new frmtPhilsDeckBuilder() ),
    Apprentice( new frmtApprentice() );

    private DeckFormat m_format;

    DeckFormats( DeckFormat format ) {
        m_format = format;
    }

    public DeckFormat getDeckFormat() {
        return m_format;
    }
}
