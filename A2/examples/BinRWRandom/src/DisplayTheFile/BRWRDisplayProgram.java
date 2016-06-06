// APPLICATION:  BinRWRandom     PROGRAM:  BRWRDisplayProgram
// AUTHOR:  D. Kaminski
// DESCRIPTION:  (see detailed notes about the app at the top of CreateProgram)
//      This program displays the binary file.  It relies on BinFile class's ReadARec
//      method to:
//          - actually READ the record
//          - SEPARATE it into fields and assign them to the public variables
//          - RETURN the EOF value as a boolean
// NOTE:  This program doesn't really need to access the numeric fields as int or float,
//      but it is shown here for demonstration purposes.
//***************************************************************************************

package DisplayTheFile;

import java.io.*;
import BinRWClasses.BRWRBinFile;

public class BRWRDisplayProgram 
{
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
        BRWRBinFile theFile = new BRWRBinFile('R');              // R for ReadMode

        int counter = 1;

        while (theFile.ReadARec())
        {
            System.out.format("RRN %02d > ", counter);
            if ("".equals(theFile.name))
                System.out.println("empty");
            else
                System.out.format("id: %02d, name: %s, gpa: %.2f\n", theFile.id, 
                                                    theFile.name, theFile.gpa);
            counter++;
        }
        theFile.FinishWithObject('R');
    }
}
