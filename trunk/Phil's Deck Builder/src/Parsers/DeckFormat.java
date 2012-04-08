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

package Parsers;

import Data.c_CardDB;
import Data.c_Deck;
import Data.c_File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.EnumMap;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Phillip
 */
public abstract class DeckFormat extends FileFilter {
    
    public enum Keyword {
        NAME,
        NUM_CREATURES,
        NUM_SPELLS,
        NUM_LANDS,
        CREATURES_HDR,
        SPELLS_HDR,
        LANDS_HDR,
        CARD_AMT,
        CARD_NAME,
        CARD_EXP,
        CARD_NUM,
        SB_CARD_AMT,
        SB_CARD_NAME,
        SB_CARD_EXP,
        SB_CARD_NUM,
        SB_HDR,
        SB_PREFIX,
        TOTAL_CARDS,
        MISC
    }
    
    protected c_File m_file = new c_File();
    private EventListenerList m_listeners = new EventListenerList();
    
    public static final String[][] Arcane = {
        { Keyword.CARD_AMT.toString(),  "%s,"      },
        { Keyword.CARD_NAME.toString(), "%s,"      },
        { Keyword.CARD_EXP.toString(), "%s\n"      }
    };
    
    public static final String[][] Forge = {
        { Keyword.NAME.toString(),      "%s\n"          },
        { Keyword.MISC.toString(),      "[general]\n"   },
        { Keyword.MISC.toString(),      "[main]\n"      },
        { Keyword.CARD_AMT.toString(),  "%s "           },
        { Keyword.CARD_NAME.toString(), "%s\n"          },
        { Keyword.SB_HDR.toString(),    "[sideboard]\n" },
        { Keyword.CARD_AMT.toString(),  "%s "           },
        { Keyword.CARD_NAME.toString(), "%s\n"          }
    };
    
    public static final String[][] Incantus = {
        { Keyword.CARD_AMT.toString(),  "%s  " },
        { Keyword.CARD_NAME.toString(), "%s\n" }
    };
    
    public static final String[][] Magarena = {
        { Keyword.NUM_CREATURES.toString(), "# %s creatures\n" },
        { Keyword.CARD_AMT.toString(),      "%s "              },
        { Keyword.CARD_NAME.toString(),     "%s\n"             },
        { Keyword.MISC.toString(),          "\n"               },
        { Keyword.NUM_SPELLS.toString(),    "# %s spells\n"    },
        { Keyword.CARD_AMT.toString(),      "%s "              },
        { Keyword.CARD_NAME.toString(),     "%s\n"             },
        { Keyword.MISC.toString(),          "\n"               },
        { Keyword.NUM_LANDS.toString(),     "# %s lands\n"     },
        { Keyword.CARD_AMT.toString(),      "%s "              },
        { Keyword.CARD_NAME.toString(),     "%s\n"             }
    };
    
    public static final String[][] MAGE = {
        { Keyword.NAME.toString(),          "NAME:%s\n"        },
        { Keyword.MISC.toString(),          "AUTHOR:UNKNOWN\n" },
        { Keyword.CREATURES_HDR.toString(), "# Creatures\n"    },
        { Keyword.CARD_AMT.toString(),      "%s "              },
        { Keyword.CARD_EXP.toString(),      "[%s:"             },
        { Keyword.CARD_NUM.toString(),      "%s] "             },
        { Keyword.CARD_NAME.toString(),     "%s\n"             },
        { Keyword.SPELLS_HDR.toString(),    "# Spells\n"       },
        { Keyword.CARD_AMT.toString(),      "%s "              },
        { Keyword.CARD_EXP.toString(),      "[%s:"             },
        { Keyword.CARD_NUM.toString(),      "%s] "             },
        { Keyword.CARD_NAME.toString(),     "%s\n"             },
        { Keyword.LANDS_HDR.toString(),     "# Lands\n"        },
        { Keyword.CARD_AMT.toString(),      "%s "              },
        { Keyword.CARD_EXP.toString(),      "[%s:"             },
        { Keyword.CARD_NUM.toString(),      "%s] "             },
        { Keyword.CARD_NAME.toString(),     "%s\n"             }
    };
    
    public static final String[][] Workstation = {
        { Keyword.MISC.toString(),          "// Deck file for Magic Workstation (http://www.magicworkstation.com)\n"               },
        { Keyword.MISC.toString(),          "\n"               },
        { Keyword.LANDS_HDR.toString(),     "// Lands\n"       },
        { Keyword.CARD_AMT.toString(),      "    %s "          },
        { Keyword.CARD_EXP.toString(),      "[%s] "            },
        { Keyword.CARD_NAME.toString(),     "%s\n"             },
        { Keyword.MISC.toString(),          "\n"               },
        { Keyword.CREATURES_HDR.toString(), "// Creatures\n"   },
        { Keyword.CARD_AMT.toString(),      "    %s "          },
        { Keyword.CARD_EXP.toString(),      "[%s] "            },
        { Keyword.CARD_NAME.toString(),     "%s\n"             },
        { Keyword.MISC.toString(),          "\n"               },
        { Keyword.SPELLS_HDR.toString(),    "// Spells\n"      },
        { Keyword.CARD_AMT.toString(),      "    %s "          },
        { Keyword.CARD_EXP.toString(),      "[%s] "            },
        { Keyword.CARD_NAME.toString(),     "%s\n"             }
    };
    
    public static final String[][] MagicWars = {
        { Keyword.TOTAL_CARDS.toString(),   "%s Total Cards\n" },
        { Keyword.MISC.toString(),          "\n"               },
        { Keyword.NUM_CREATURES.toString(), "%s Creatures\n"   },
        { Keyword.MISC.toString(),          "-------------\n"  },
        { Keyword.CARD_AMT.toString(),      "%sx "             },
        { Keyword.CARD_EXP.toString(),      "[%s] "            },
        { Keyword.CARD_NAME.toString(),     "%s\n"             },
        { Keyword.MISC.toString(),          "\n"               },
        { Keyword.NUM_CREATURES.toString(), "%s Spells\n"   },
        { Keyword.MISC.toString(),          "----------\n"  },
        { Keyword.CARD_AMT.toString(),      "%sx "             },
        { Keyword.CARD_EXP.toString(),      "[%s] "            },
        { Keyword.CARD_NAME.toString(),     "%s\n"             },
        { Keyword.MISC.toString(),          "\n"               },
        { Keyword.NUM_CREATURES.toString(), "%s Land\n"   },
        { Keyword.MISC.toString(),          "--------\n"  },
        { Keyword.CARD_AMT.toString(),      "%sx "             },
        { Keyword.CARD_EXP.toString(),      "[%s] "            },
        { Keyword.CARD_NAME.toString(),     "%s\n"             },
        { Keyword.MISC.toString(),          "\n"               },
        { Keyword.SB_HDR.toString(),        "Sideboard\n"      },
        { Keyword.MISC.toString(),          "--------\n"  },
        { Keyword.CARD_AMT.toString(),      "%sx "             },
        { Keyword.CARD_EXP.toString(),      "[%s] "            },
        { Keyword.CARD_NAME.toString(),     "%s\n"             }
    };
    
    public static final String[][] mtgDeckBuilder = {
        { Keyword.CARD_AMT.toString(),  "%s,"      },
        { Keyword.CARD_NAME.toString(), "%s,"      }, // Don't forget to put "(1)" after every basic land!
        { Keyword.CARD_EXP.toString(),  "%s\n"     }
    };

    public static final String[][] OnlinePlayTable = {
        { Keyword.NAME.toString(),      "// NAME: %s\n" },
        { Keyword.MISC.toString(),      "// \n"         },
        { Keyword.MISC.toString(),      "// This deck file was generated by MTG Deckbuilder.\n"      },
        { Keyword.MISC.toString(),      "// MTG Deckbuilder is part of the Online PlayTable software package.\n"      },
        { Keyword.MISC.toString(),      "// For more info and other great free magic the gathering software\n"      },
        { Keyword.MISC.toString(),      "// visit http://www.onlineplaytable.com - Home of Online PlayTable, a free online magic client\n"      },
        { Keyword.MISC.toString(),      "// \n"         },
        { Keyword.CARD_AMT.toString(),  "    %s "       },
        { Keyword.CARD_EXP.toString(),  "[%s] "         },
        { Keyword.CARD_NAME.toString(), "%s\n"          },
        { Keyword.SB_PREFIX.toString(), "SB: "          },
        { Keyword.CARD_AMT.toString(),  "%s "           },
        { Keyword.CARD_EXP.toString(),  "[%s] "         },
        { Keyword.CARD_NAME.toString(), "%s\n"          }
    };

    public static final String[][] Wagic = {
        { Keyword.NAME.toString(),      "#NAME:%s\n" },
        { Keyword.MISC.toString(),      "#DESC:\n"   },
        { Keyword.CARD_NAME.toString(), "%s"         },
        { Keyword.CARD_EXP.toString(),  "(%s) "      },
        { Keyword.CARD_AMT.toString(),  "*%s\n"      }
    };

    protected static final EnumMap<Keyword, String> Format = new EnumMap<Keyword, String>( Keyword.class );
    protected FileFilter m_fileFilter = new FileFilter() {
        public boolean accept( File f ) {
            if( f.isDirectory() ) {
                return true;
            }
            return c_File.getExtension( f.getName().toLowerCase() ).equals( m_ext );
        }

        @Override
        public String getDescription() {
            return String.format( "%s (*.%s)", m_desc, m_ext );
        }
    };
    private String m_desc;
    private String m_ext;

    public DeckFormat( String desc, String ext ) {
        m_desc = desc;
        m_ext = ext;
    }

    public boolean accept( File f ) {
        if( f.isDirectory() ) {
            return true;
        }
        return c_File.getExtension( f.getName().toLowerCase() ).equals( m_ext );
    }

    public String getDescription() {
        return String.format( "%s (*.%s)", m_desc, m_ext );
    }

    protected boolean writeDeck( String filename, String lines ) {
        String filepath = filename;
        if( !filename.toLowerCase().endsWith( m_ext.toLowerCase() ) ) {
            filepath = c_File.setExtension( filepath, m_ext );
        }
        boolean success = true;
        try {
            m_file.write( filepath, lines );
        } catch( Exception ex ) {
            success = false;
        }
        return success;
    }

    protected String addLine( Keyword words[], String text[] ) throws Exception {
        if( words.length != text.length ) {
            throw new Exception();
        }
        String format = "";
        for( int i=0; i<words.length; i++ ) {
            format += String.format( Format.get( words[ i ] ), text[ i ] );
        }
        return format;
    }

    protected String addLine( Keyword word ) {
        return Format.get( word );
    }

    public void addActionListener( ActionListener listener ) {
        m_listeners.add( ActionListener.class, listener );
    }

    public void fireActionEvent( Class thisClass, Integer action, String command ) {
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

    public abstract boolean saveDeck( String filename, c_Deck deck, c_CardDB db );
    public abstract boolean loadDeck( String filename, c_Deck deck, c_CardDB db );
}