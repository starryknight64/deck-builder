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
