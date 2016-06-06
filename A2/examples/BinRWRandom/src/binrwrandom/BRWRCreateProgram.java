// APPLICATION:  BinRWRandom        PROGRAM:  BRWRCreateProgram
// AUTHOR:  D. Kaminski
// DESCRIPTION:  This application creates a binary file which is also a RANDOM ACCESS
//      file (Query uses Random Access).  Create also uses RANDOM ACCESS when inserting
//      records into the file (since the order of the input file is NOT in the order
//      needed to create it using SEQUENTIAL ACCESS).
// RANDOM ACCESS FILE STRUCTURE:  A direct address file structure based on ID as the key.
//      That is, the record with ID 04 goes in location 4 in the file.
// RRN's start with 1, not 0.
//***************************************************************************************

package binrwrandom;

import java.io.*;
import BinRWClasses.BRWRBinFile;

public class BRWRCreateProgram 
{
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
        FileReader inFile = new FileReader("InputFile.txt");
        BufferedReader input = new BufferedReader(inFile);

        BRWRBinFile outFile = new BRWRBinFile('W');              // W for WriteMode

        String inputLine;
        int id;                         // id is used as the RRN for random access
                                        // since the file's DIRECT ADDRESS on ID

        while ((inputLine = input.readLine()) != null)
        {
            id = Integer.parseInt(inputLine.substring(0, 2));
            outFile.WriteARec(inputLine, id);       // sending in the RRN too, so 
                                                    // it's calling a RANDOM ACCESS
                                                    // version of WriteARec
        }

        System.out.println("The Binary File was created.");
        System.out.println("Run DisplayProgram to view it.");
        System.out.println("Also view it in a HexEditor: \n\t" +
            "(see XVI32 - BinaryFile.pdf in the app folder)."); 
        System.out.println("\nRun QueryProgram to see that random access works.");

        outFile.FinishWithObject('W');
        input.close();
    }
}
