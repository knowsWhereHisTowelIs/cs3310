// APPLICATION:  BinRWRandom        PROGRAM:  BRWRQueryProgram
// AUTHOR:  D. Kaminski
// DESCRIPTION:  (see detailed notes about the app at the top of CreateProgram).
//      This program displays individual records from the binary file based on hard-coded
//      requests of specific id values (i.e., the primary key field on which the driect
//      address file is based.  It thus has to use BinFile class's RANDOM ACCESS version
//      of the ReadARec method, where RRN has to be sent in, to:
//          - calculate the offset
//          - do the seek
//          - actually READ the record
//          - SEPARATE it into fields and assign them to public variables
//          - RETURN a bool value to indicate that the record with that id was
//              NOT IN THE FILE
//***************************************************************************************

package QueryProgram;

import java.io.*;
import BinRWClasses.BRWRBinFile;

public class BRWRQueryProgram
{
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        BRWRBinFile theFile = new BRWRBinFile('R');              // R for ReadMode

        Do1Query(theFile, 2);                   // ID 2 - OK
        Do1Query(theFile, 5);                   // ID 5 - OK
        Do1Query(theFile, 3);                   // ID 3 - not in file - a hole
        Do1Query(theFile, 4);                   // ID 4 - OK
        Do1Query(theFile, 35);                  // ID 35 - not in file - past EOF

        theFile.FinishWithObject('R');
    }
    //*******************************************************************************
    static void Do1Query(BRWRBinFile theFile, int targetId) throws IOException
    {
        boolean validRec = theFile.ReadARec(targetId);

        System.out.format("target ID:  %d >> ", targetId);

        if (validRec)
            System.out.format("id: %02d, name: %s, gpa: %.2f\n", theFile.id, 
                                                    theFile.name, theFile.gpa);
        else
            System.out.println("empty");
    }
}