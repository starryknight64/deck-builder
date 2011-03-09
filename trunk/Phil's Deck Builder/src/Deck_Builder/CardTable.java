/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Phillip
 */
public class CardTable extends JTable {

    public CardTable() {
        super();
    }
    
    public CardTable( TableModel model ) {
        super( model );
        init( model );
    }

    @Override
    public void setModel( TableModel model ) {
        // <editor-fold defaultstate="collapsed" desc="super.setModel( model );">
        if (model == null) {
            throw new IllegalArgumentException("Cannot set a null TableModel");
	}
        if (this.dataModel != model) {
	    TableModel old = this.dataModel;
            if (old != null) {
                old.removeTableModelListener(this);
	    }
            this.dataModel = model;
            model.addTableModelListener(this);

            tableChanged(new TableModelEvent(model, TableModelEvent.HEADER_ROW));

	    firePropertyChange("model", old, model);

            if (getAutoCreateRowSorter()) {
                setRowSorter(new TableRowSorter(model));
            }
        }// </editor-fold>
        init( model );
    }

    public static void autoResizeColWidth( JTable table ) {
        int margin = 5;
        DefaultTableColumnModel colModel;
        TableColumn col;
        TableCellRenderer renderer;
        Component comp;

        for( int i=0; i<table.getColumnCount(); i++ ) {
            int vColIndex = i;
            colModel = (DefaultTableColumnModel)( table.getColumnModel() );
            col = colModel.getColumn( vColIndex );
            int width = 0;

            /* Get width of column header */
            renderer = col.getHeaderRenderer();

            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }

            comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);

            width = comp.getPreferredSize().width;

            /* Get maximum width of column data */
            for( int r=0; r<table.getRowCount(); r++ ) {
                renderer = table.getCellRenderer( r, vColIndex );
                comp = renderer.getTableCellRendererComponent( table, table.getValueAt(r, vColIndex), false, false, r, vColIndex );
                width = Math.max( width, comp.getPreferredSize().width );
            }

            /* Add margin */
            width += 2 * margin;

            /* Set the width */
            col.setPreferredWidth(width);
        }

        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment( SwingConstants.LEFT );
        table.getTableHeader().setReorderingAllowed( false );

        colModel = null;
        col = null;
        renderer = null;
        comp = null;
    }

    private void init( TableModel model ) {
        Comparator<Integer> int_comparator = new Comparator<Integer>() {
            public int compare( Integer o1, Integer o2 ) {
                return o1.compareTo( o2 );
            }
        };
        Comparator<String> pt_comparator = new Comparator<String>() {
            public int compare( String o1, String o2 ) {
                if( o1.length() <= 2 || o2.length() <= 2 ) {
                    return o1.compareTo( o2 );
                }

                String pt1[] = o1.split( "/" );
                String pt2[] = o2.split( "/" );
                Integer p1 = Integer.parseInt( pt1[ 0 ] );
                Integer p2 = Integer.parseInt( pt2[ 0 ] );
                Integer t1 = Integer.parseInt( pt1[ 1 ] );
                Integer t2 = Integer.parseInt( pt2[ 1 ] );
                
                int cmp = p1.compareTo( p2 );
                if( cmp == 0 ) {
                    cmp = t1.compareTo( t2 );
                }
                return cmp;
            }
        };
        Comparator<ImageIcon> cmc_comparator = new Comparator<ImageIcon>() {
            public int compare( ImageIcon o1, ImageIcon o2 ) {
                String desc1 = o1.getDescription();
                String desc2 = o2.getDescription();
                if( desc1 == null || desc2 == null ) {
                    if( desc1 == null && desc2 == null ) {
                        return 0;
                    } else if( desc1 == null && desc2 != null ) {
                        return -1;
                    } else {
                        return 1;
                    }
                }

                if( desc1.length() == 0 || desc2.length() == 0 ) {
                    return desc1.compareTo( desc2 );
                }

                String[] cmc1 = desc1.split( "," );
                String[] cmc2 = desc2.split( "," );
                int cmp = c_CastingCost.getCMC( desc1 ).compareTo( c_CastingCost.getCMC( desc2 ) );
                if( cmp != 0 ) {
                    return cmp;
                }

                cmp = ((Integer)cmc1.length).compareTo( cmc2.length );
                if( cmp != 0 ) {
                    return cmp;
                }

                /* At this point we know that both images have the same number of glyphs
                   And we know that the CMC of both images are equal
                   So a String comparison of the two Mana Costs should be sufficient */
                return desc1.compareTo( desc2 );
            }
        };

        CustomCellRenderer ccr_text = new CustomCellRenderer( JLabel.CENTER, new CellLabelText() {
            public String getText( Object value ) {
                return value.toString();
            }
        } );
        CustomCellRenderer ccr_prices = new CustomCellRenderer( JLabel.CENTER, new CellLabelText() {
            public String getText( Object value ) {
                return "$" + c_Price.formatPrice( (Double)value );
            }
        } );

        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>( model );
        TableColumnModel cm = getColumnModel();
        sorter.setSortsOnUpdates( true );

        if( model instanceof DeckTableModel ) {
            /* Set Cell Renderer for Amount and P/T Columns */
            cm.getColumn( DeckTableModel.DeckCols.Amount.val ).setCellRenderer( ccr_text );
            cm.getColumn( DeckTableModel.DeckCols.PT.val ).setCellRenderer( ccr_text );
            
            /* Set Comparators for Mana Cost and Amount Columns */
            sorter.setComparator( DeckTableModel.DeckCols.ManaCost.val, cmc_comparator );
            sorter.setComparator( DeckTableModel.DeckCols.Amount.val, int_comparator );
            sorter.setComparator( DeckTableModel.DeckCols.PT.val, pt_comparator );
            setRowSorter( sorter );

        } else if( model instanceof PricesTableModel ) {
            /* Set Cell Renderer for Low, Average, and High Prices Columns */
            cm.getColumn( PricesTableModel.PriceCols.Amount.val ).setCellRenderer( ccr_text );
            cm.getColumn( PricesTableModel.PriceCols.Low.val ).setCellRenderer( ccr_prices );
            cm.getColumn( PricesTableModel.PriceCols.Average.val ).setCellRenderer( ccr_prices );
            cm.getColumn( PricesTableModel.PriceCols.High.val ).setCellRenderer( ccr_prices );

            /* Set Comparators for Low, Average, and High Prices Columns */
            sorter.setComparator( PricesTableModel.PriceCols.Amount.val, int_comparator );
            setRowSorter( sorter );

        } else if( model instanceof ProxiesTableModel ) {
            /* Set Cell Renderer for Deck Amount and Print Amount Columns */
            cm.getColumn( ProxiesTableModel.ProxyCols.Deck_Amount.val ).setCellRenderer( ccr_text );
            cm.getColumn( ProxiesTableModel.ProxyCols.Print_Amount.val ).setCellRenderer( ccr_text );

            /* Set Comparators for Deck Amount and Print Amount Columns */
            sorter.setComparator( ProxiesTableModel.ProxyCols.Deck_Amount.val, int_comparator );
            sorter.setComparator( ProxiesTableModel.ProxyCols.Print_Amount.val, int_comparator );
            setRowSorter( sorter );

        } else if( model instanceof RecentlyViewedTableModel ) {
            /* Set Comparator for Mana Cost Column */
            sorter.setComparator( RecentlyViewedTableModel.RVCols.ManaCost.val, cmc_comparator );
            setRowSorter( sorter );

        } else {
            setAutoCreateRowSorter( true );
        }
    }

    public class CustomCellRenderer extends JLabel implements TableCellRenderer {
        private Border m_unselectedBorder = null;
        private Border m_selectedBorder = null;
        private int m_horz_align = JLabel.LEFT;
        private CellLabelText m_cellLabel;

        public CustomCellRenderer() {
            setOpaque( true );
            m_cellLabel = new CellLabelText() {
                public String getText( Object value ) {
                    return value.toString();
                }
            };
        }

        public CustomCellRenderer( int horz_align, CellLabelText cellLabel ) {
            setOpaque( true );
            m_horz_align = horz_align;
            m_cellLabel = cellLabel;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText( m_cellLabel.getText( value ) );
            Font font = getFont();
            setFont( new Font( font.getName(), Font.PLAIN, font.getSize() ) );
            setHorizontalAlignment( m_horz_align );

            if( isSelected ) {
                if( m_selectedBorder == null ) {
                    m_selectedBorder = BorderFactory.createMatteBorder( 1, 1, 1, 1, table.getSelectionBackground() );
                }
                setBorder( m_selectedBorder );
                setBackground( table.getSelectionBackground() );
            } else {
                if( m_unselectedBorder == null ) {
                    m_unselectedBorder = BorderFactory.createMatteBorder( 1, 1, 1, 1, table.getBackground() );
                }
                setBorder( m_unselectedBorder );
                setBackground( table.getBackground() );
            }

            return this;
        }
    }
    
    public abstract class CellLabelText {
        public abstract String getText( Object value );
    }

    public static class CardTableModel extends DefaultTableModel {

        private ArrayList<Integer> m_MIDs = new ArrayList<Integer>();

        public CardTableModel() {
            super();
        }

        public CardTableModel( Object[][] data, Object[] columnNames ) {
            super( data, columnNames );
        }

        public Integer findValueInColumn( Object value, Integer column ) {
            if( column >= 0 && column < getColumnCount() ) {
                for( int i=0; i<getRowCount(); i++ ) {
                    if( value.toString().equals( getValueAt( i, column ).toString() ) ) {
                        return i;
                    }
                }
            }
            return -1;
        }

        public void addMID( int mid ) {
            m_MIDs.add( mid );
        }

        public Integer getMID( int row ) {
            return m_MIDs.get( row );
        }

        public Integer getMIDRow( int mid ) {
            return m_MIDs.indexOf( mid );
        }

        public void delMID( int row ) {
            m_MIDs.remove( row );
        }
    }
    
    public static class DeckTableModel extends CardTableModel {

        public enum DeckCols {
            ManaCost ( "Mana Cost", ImageIcon.class, true  ),
            Name     ( "Name",      String.class,    true  ),
            Amount   ( "Amt",       Integer.class,   true  ),
            Type     ( "Type",      String.class,    true  ),
            SubType  ( "Sub-Type",  String.class,    true  ),
            PT       ( "P/T",       String.class,    true  ),
            Expansion( "Expansion", String.class,    true  ),
            MID      ( "MID",       Integer.class,   false );

            public Boolean isVisible;
            public Integer val = this.ordinal();
            public String name;
            private Class m_class;

            private static ArrayList<String> m_names = new ArrayList<String>();
            private static Integer m_visibleCols = 0;
            static {
                for( DeckCols col : EnumSet.allOf( DeckCols.class ) ) {
                    m_names.add( col.name );
                    if( col.isVisible ) {
                        m_visibleCols++;
                    }
                }
            }

            private DeckCols( String nm, Class cls, boolean vis ) {
                isVisible = vis;
                m_class = cls;
                name = nm;
            }

            public Class getColClass() {
                return m_class;
            }

            public static String[] getNames() {
                return m_names.toArray( new String[] {} );
            }

            public static DeckCols valueOf( int value ) {
                return DeckCols.values()[ value ];
            }

            public static int getVisibleCount() {
                return m_visibleCols;
            }
        }
        
        public DeckTableModel() {
            super( new Object[][] {}, DeckCols.getNames() );
        }

        public void addCard( c_Card card, int amt ) {
            addMID( card.MID );
            insertRow( this.getRowCount(), new Object[] { card.CastingCost.getImage(), card.Name, amt, card.Type, card.SubType, card.PT, card.Expansion } );
        }

        public void updateCardAmount( c_Card card, int amt ) {
            int row = getMIDRow( card.MID );
            if( row >= 0 ) {
                setValueAt( amt, row, DeckCols.Amount.val );
            }
        }

        public void deleteRow( int row ) {
            removeRow( row );
            delMID( row );
        }

        @Override
        public int getColumnCount() {
            return DeckCols.getVisibleCount();
        }

        @Override
        public Class getColumnClass( int col ) {
            return DeckCols.valueOf( col ).getColClass();
        }

        @Override
        public boolean isCellEditable( int row, int col ) {
            return( col == DeckCols.Amount.val );
        }
    }

    public static class PricesTableModel extends CardTableModel {

        public enum PriceCols {
            Amount   ( "Amt",       Integer.class, true  ),
            Name     ( "Name",      String.class,  true  ),
            Expansion( "Expansion", String.class,  true  ),
            Low      ( "Low",       Double.class,  true  ),
            Average  ( "Avg",       Double.class,  true  ),
            High     ( "High",      Double.class,  true  ),
            SB       ( "In SB?",    Boolean.class, true  ),
            MID      ( "MID",       Integer.class, false );

            public Boolean isVisible;
            public Integer val = this.ordinal();;
            public String name;
            private Class m_class;

            private static ArrayList<String> m_names = new ArrayList<String>();
            private static Integer m_visibleCols = 0;
            static {
                for( PriceCols col : EnumSet.allOf( PriceCols.class ) ) {
                    m_names.add( col.name );
                    if( col.isVisible ) {
                        m_visibleCols++;
                    }
                }
            }

            private PriceCols( String nm, Class cls, boolean vis ) {
                isVisible = vis;
                m_class = cls;
                name = nm;
            }

            public Class getColClass() {
                return m_class;
            }

            public static String[] getNames() {
                return m_names.toArray( new String[] {} );
            }

            public static PriceCols valueOf( int value ) {
                return PriceCols.values()[ value ];
            }

            public static int getVisibleCount() {
                return m_visibleCols;
            }
        }
        
        public PricesTableModel() {
            super( new Object[][] {}, PriceCols.getNames() );
        }

        public void addPrice( c_Price price, c_Card card, int amt, boolean toDeck ) {
            addMID( (toDeck ? 1 : -1) * card.MID );
            insertRow( this.getRowCount(), new Object[] { amt, card.Name, card.Expansion, price.Low(), price.Average(), price.High(), !toDeck } );
        }

        public void updateCardAmount( c_Card card, int amt, boolean toDeck ) {
            int row = getMIDRow( (toDeck ? 1 : -1) * card.MID );
            if( row >= 0 ) {
                setValueAt( amt, row, PriceCols.Amount.val );
            }
        }

        public void deleteCard( c_Card card ) {
            int row = getMIDRow( card.MID );
            if( row >= 0 ) {
                removeRow( row );
                delMID( row );
            }
        }

        @Override
        public int getColumnCount() {
            return PriceCols.getVisibleCount();
        }

        @Override
        public Class getColumnClass( int col ) {
            return PriceCols.valueOf( col ).getColClass();
        }

        @Override
        public boolean isCellEditable( int row, int col ) {
            return( col == PriceCols.Amount.val );
        }
    }

    public static class ProxiesTableModel extends CardTableModel {

        public enum ProxyCols {
            SB           ( "In SB?",    Boolean.class, true  ),
            Name         ( "Name",      String.class,  true  ),
            Type         ( "Type",      String.class,  true  ),
            Expansion    ( "Expansion", String.class,  true  ),
            Deck_Amount  ( "Deck Amt",  Integer.class, true  ),
            Print_Amount ( "Print Amt", Integer.class, true  ),
            MID          ( "MID",       Integer.class, false );

            public Boolean isVisible;
            public Integer val = this.ordinal();
            public String name;
            private Class m_class;

            private static ArrayList<String> m_names = new ArrayList<String>();
            private static Integer m_visibleCols = 0;
            static {
                for( ProxyCols col : EnumSet.allOf( ProxyCols.class ) ) {
                    m_names.add( col.name );
                    if( col.isVisible ) {
                        m_visibleCols++;
                    }
                }
            }

            private ProxyCols( String nm, Class cls, boolean vis ) {
                isVisible = vis;
                m_class = cls;
                name = nm;
            }

            public Class getColClass() {
                return m_class;
            }

            public static String[] getNames() {
                return m_names.toArray( new String[] {} );
            }

            public static ProxyCols valueOf( int value ) {
                return ProxyCols.values()[ value ];
            }

            public static int getVisibleCount() {
                return m_visibleCols;
            }
        }

        public ProxiesTableModel() {
            super( new Object[][] {}, ProxyCols.getNames() );
        }

        @Override
        public int getColumnCount() {
            return ProxyCols.getVisibleCount();
        }

        @Override
        public Class getColumnClass( int col ) {
            return ProxyCols.valueOf( col ).getColClass();
        }

        @Override
        public boolean isCellEditable( int row, int col ) {
            return( col == ProxyCols.Print_Amount.val );
        }
    }


    public static class RecentlyViewedTableModel extends CardTableModel {

        public enum RVCols {
            ManaCost( "Mana Cost", ImageIcon.class, true  ),
            Name    ( "Name",      String.class,    true  ),
            Type    ( "Type",      String.class,    true  ),
            MID     ( "MID",       Integer.class,   false );

            public Boolean isVisible;
            public Integer val = this.ordinal();
            public String name;
            private Class m_class;

            private static ArrayList<String> m_names = new ArrayList<String>();
            private static Integer m_visibleCols = 0;
            static {
                for( RVCols col : EnumSet.allOf( RVCols.class ) ) {
                    m_names.add( col.name );
                    if( col.isVisible ) {
                        m_visibleCols++;
                    }
                }
            }

            private RVCols( String nm, Class cls, boolean vis ) {
                isVisible = vis;
                m_class = cls;
                name = nm;
            }

            public Class getColClass() {
                return m_class;
            }

            public static String[] getNames() {
                return m_names.toArray( new String[] {} );
            }

            public static RVCols valueOf( int value ) {
                return RVCols.values()[ value ];
            }

            public static int getVisibleCount() {
                return m_visibleCols;
            }
        }

        public RecentlyViewedTableModel() {
            super( new Object[][] {}, RVCols.getNames() );
        }

        public void addCard( c_Card card ) {
            addMID( card.MID );
            insertRow( getRowCount(), new Object[] { card.CastingCost.getImage(), card.Name, card.Type } );
        }

        @Override
        public int getColumnCount() {
            return RVCols.getVisibleCount();
        }

        @Override
        public Class getColumnClass( int col ) {
            return RVCols.valueOf( col ).getColClass();
        }

        @Override
        public boolean isCellEditable( int row, int col ) {
            return false;
        }
    }
}
