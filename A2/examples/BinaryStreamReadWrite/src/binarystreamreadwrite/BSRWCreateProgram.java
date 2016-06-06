// APPLICATION:  BinaryStreamReadWrite     PROGRAM:  BSRWCreateProgram
// AUTHOR:  D. Kaminski
// DESCRIPTION:  This application creates a binary file and then reads that binary file
//          - handled in 2 separate programs within this app;
//          - and both programs use sequential access of the file;
//          - and all actual binary file handling is done within the BinFile class.
//              (NOTICE HOW THAT SIMPLIFIED THE 2 PROGRAMS - all issues/messiness/
//                  handling/... is buried within the BinFile class)
// NEW CONCEPTS:
//      1) This app uses DataInputStream and DataOutputStream - used inside
//          the BinFile class.
//      2) BinFile class is in a SEPARATE PACKAGE from either of the 2 PROGRAMS
//          (see "HOW TO..." instructions below).
//      3) A BINARY FILE (see notes at the top of BinFile class).
//
// INPUT FILE:
//      This is just a plain text file with no special handling.  So no separate class
//              is set up to handle it - FileReader and BufferedReader are used to
//              handle it right here in CreateProgram's Main.
//      It's a csv file and so has variable-length records with variable-length fields:
//              id, name, gpa
//
// DEBATABLE:  Who should be responsible for:
//      1) taking the inputLine apart into fields and converting them into their
//          proper (binary) data types:  Create program or a method in BinFile class?
//          In this app it's left to a BinFile method.  Create program just passes in
//          a line of data (an input file's RECORD).
//          This could've instead been set up to have Create do either:
//              - the field-separating and send in those individual text string fields
//          OR  - the field-separating and the converting into numerica data types
//                  and sending those binary int/float/string/... fields to WriteARec
//      2) dealing with the binary file's individual fields on reading a record:
//          a method inside BinFile class or the Display program?
//          In this app BinFile's ReadARec assigns the individual fields in their
//          plain binary format (int's, float's...) and let's the Display program deal
//          with their individual fields as it needs to.
//***************************************************************************************

package binarystreamreadwrite;

import java.io.*;
import BinRWClasses.BSRWBinFile;

public class BSRWCreateProgram 
{
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
        FileReader inFile = new FileReader("InputFile.txt");
        BufferedReader input = new BufferedReader(inFile);

        BSRWBinFile outFile = new BSRWBinFile('W');              // W for WriteMode

        String inputLine;

        while ((inputLine = input.readLine()) != null)
        {
            outFile.WriteARec(inputLine);
        }

        System.out.println("The Binary File was created.");
        System.out.println("Run DisplayProgram to view it.");
        System.out.println("Also view it in a HexEditor: \n\t" +
            "(see XVI32 - BinaryFile.pdf in the app folder)."); 

        outFile.FinishWithObject('W');
        input.close();
    }
}
