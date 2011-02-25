/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Deck_Builder;

/**
 *
 * @author Phillip
 */
public class str {

    str() {}

    public static String left( String str, int chars ) {
        return str.substring( 0, chars );
    }

    public static String right( String str, int chars ) {
        return str.substring( str.length() - chars );
    }

    public static String leftOf( String str, String leftof ) {
        if( str.contains( leftof ) ) {
            return left( str, str.indexOf( leftof ) );
        }
        return str;
    }

    public static String rightOf( String str, String rightof ) {
        if( str.contains( rightof ) ) {
            return right( str, str.length() - str.indexOf( rightof ) - rightof.length() );
        }
        return str;
    }

    public static String middleOf( String leftof, String str, String rightof ) {
        return rightOf( leftOf( str, rightof ), leftof );
    }
}
