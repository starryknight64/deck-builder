/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Parsers;

import Data.c_CardDB;
import Data.c_Deck;
import Data.c_File;
import java.io.File;
import java.util.EnumMap;
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

    public abstract boolean saveDeck( String filename, c_Deck deck, c_CardDB db );
}
