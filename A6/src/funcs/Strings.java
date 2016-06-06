/**
 * CS3310 - A5
 * Author: Caleb Slater
 */
package funcs;

import java.util.Arrays;
import java.util.List;

/**
 * Misc String functions
 */
public class Strings {
    public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
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
            Arrays.fill(nullChars, '\0');
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
     * convert part of string array to int array
     * @param strArr
     * @param start
     * @param stop
     * @return 
     */
    final public static int[] strArrToInt(String[] strArr, int start, int stop) {
        int[] intArr = new int[strArr.length];
        for(int i = 0; (i+start) < strArr.length && (i+start) <= stop; i++ ){
            intArr[i] = Integer.parseInt(strArr[i+start]);
        }
        return intArr;
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
