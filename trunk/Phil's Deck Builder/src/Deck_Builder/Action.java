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

package Deck_Builder;

import java.awt.event.ActionEvent;

/**
 *
 * @author Phillip
 */
public class Action {
    public static final Integer ACTION_INVALID                  = ActionEvent.ACTION_FIRST + 1;
    public static final Integer ACTION_BROWSER_LOADING_DONE     = ActionEvent.ACTION_FIRST + 2;
    public static final Integer ACTION_DECK_CHANGED             = ActionEvent.ACTION_FIRST + 3;
    public static final Integer ACTION_ACRONYMS_LOAD_LINE       = ActionEvent.ACTION_FIRST + 4;
    public static final Integer ACTION_PRICES_LOAD_LINE         = ActionEvent.ACTION_FIRST + 5;
    public static final Integer ACTION_DECK_LOAD_CARD           = ActionEvent.ACTION_FIRST + 6;
    public static final Integer ACTION_DECK_SB_LOAD_LINE        = ActionEvent.ACTION_FIRST + 7;
    public static final Integer ACTION_FILE_LOAD_DONE           = ActionEvent.ACTION_FIRST + 8;
    public static final Integer ACTION_CARD_PREVIEW             = ActionEvent.ACTION_FIRST + 9;
    public static final Integer ACTION_PROXY_CARD_SELECTED      = ActionEvent.ACTION_FIRST + 10;
    public static final Integer ACTION_EXPANSION_LOAD_LINE      = ActionEvent.ACTION_FIRST + 11;
    public static final Integer ACTION_CARDS_DB_LOAD_LINE       = ActionEvent.ACTION_FIRST + 12;
    public static final Integer ACTION_BLOCK_LEGAL_LOAD_LINE    = ActionEvent.ACTION_FIRST + 13;
    public static final Integer ACTION_EXTENDED_LEGAL_LOAD_LINE = ActionEvent.ACTION_FIRST + 14;
    public static final Integer ACTION_LEGACY_LEGAL_LOAD_LINE   = ActionEvent.ACTION_FIRST + 15;
    public static final Integer ACTION_STANDARD_LEGAL_LOAD_LINE = ActionEvent.ACTION_FIRST + 16;
    public static final Integer ACTION_VINTAGE_LEGAL_LOAD_LINE  = ActionEvent.ACTION_FIRST + 17;
    public static final Integer ACTION_FILE_TOTAL_LINES         = ActionEvent.ACTION_FIRST + 18;
    public static final Integer ACTION_DECK_SAVED               = ActionEvent.ACTION_FIRST + 19;

    public static final String COMMAND_DECK_NAME_CHANGED    = "DeckNameChanged";
    public static final String COMMAND_CURRENT_DECK_CHANGED = "CurrentDeckChanged";
    public static final String COMMAND_FILE_LOAD_DONE       = "FileLoadDone";
    public static final String COMMAND_CARD_PREVIEW         = "PreviewCard";

    private Integer m_action;
    private String m_command;

    public Action() {
        m_action = ACTION_INVALID;
        m_command = "";
    }

    public Action( Integer action, String command ) {
        m_action = action;
        m_command = command;
    }

    public Integer value() {
        return m_action;
    }

    @Override
    public String toString() {
        return m_command;
    }
}
