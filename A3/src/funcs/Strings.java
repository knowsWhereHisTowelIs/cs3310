/**
 * CS3310 - A1
 * Author: Caleb Slater
 */
package funcs;

import java.util.Arrays;
import java.util.List;

/**
 * Misc String functions
 */
public class Strings {

    /**
     * Take string and force it to be a certain length, clip overflow or fill with nulls
     * @param length
     * @param str
     * @return 
     */
    public static String forceStrLength(int length, String str) {
        int strLength = str.length();
        if (strLength < length) {
            char[] nullChars = new char[length - strLength];
            Arrays.fill(nullChars, ' ');
            for (int i = 0; i < length - strLength; i++) {
                str += " ";
            }
            return str;
            //return str + String.valueOf(nullChars);
        } else if (strLength > length) {
            return str.substring(0, length);
        } else {
            return str;
        }
    }

    /**
     * Mimic's PHP's implode function.
     * 
     * @param glue The character(s) you want to stick the peices together with
     * @param parts The subject
     * @return A string concatenation of the List items
     */
    final public static String implode(String glue, String[] parts) {
        boolean first = true;
        StringBuilder str = new StringBuilder();
        for (String part : parts) {
            if( ! first ) {
                str.append(glue);
            }
            str.append(part);
            first = false;
        }
        return str.toString();
    }
}
