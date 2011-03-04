/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import java.awt.event.ActionEvent;

/**
 *
 * @author Phillip
 */
public class Action {
    public static final Integer ACTION_INVALID              = ActionEvent.ACTION_FIRST + 1;
    public static final Integer ACTION_BROWSER_LOADING_DONE = ActionEvent.ACTION_FIRST + 2;
    public static final Integer ACTION_DECK_CHANGED         = ActionEvent.ACTION_FIRST + 3;
    public static final Integer ACTION_ACRONYMS_LOAD_LINE   = ActionEvent.ACTION_FIRST + 4;
    public static final Integer ACTION_PRICES_LOAD_LINE     = ActionEvent.ACTION_FIRST + 5;
    public static final Integer ACTION_DECK_LOAD_CARD       = ActionEvent.ACTION_FIRST + 6;
    public static final Integer ACTION_DECK_SB_LOAD_LINE    = ActionEvent.ACTION_FIRST + 7;
    public static final Integer ACTION_FILE_LOAD_DONE       = ActionEvent.ACTION_FIRST + 8;
    public static final Integer ACTION_CARD_PREVIEW         = ActionEvent.ACTION_FIRST + 9;
    public static final Integer ACTION_PROXY_CARD_SELECTED  = ActionEvent.ACTION_FIRST + 10;
    public static final Integer ACTION_EXPANSION_LOAD_LINE  = ActionEvent.ACTION_FIRST + 11;
    public static final Integer ACTION_CARDS_DB_LOAD_LINE   = ActionEvent.ACTION_FIRST + 12;

    //public static final String COMMAND_CARD_PREVIEW    = "LoadCard";
    public static final String COMMAND_DECK_NAME_CHANGED    = "DeckNameChanged";
    public static final String COMMAND_CURRENT_DECK_CHANGED = "CurrentDeckChanged";
    public static final String COMMAND_FILE_LOAD_DONE       = "FileLoadDone";
    public static final String COMMAND_CARD_PREVIEW         = "PreviewCard";

    private Integer m_action;
    private String m_command;

    Action() {
        m_action = ACTION_INVALID;
        m_command = "";
    }

    Action( Integer action, String command ) {
        m_action = action;
        m_command = command;
    }

    @Override
    public void finalize() throws Throwable {
        m_action = null;
        m_command = null;
        super.finalize();
    }

    public Integer value() {
        return m_action;
    }

    @Override
    public String toString() {
        return m_command;
    }
}
