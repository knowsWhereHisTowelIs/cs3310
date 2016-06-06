// APPLICATION:  BinRWRandom         SEPARATE SHARABLE CLASS:  BRWRBinFile
// 
// DESCRIPTION:  This class handles the binary file used by the 3 programs.
//      It provides a constructor for opening the file, and methods to close the file,
//      do a WriteARec (RANDOM ACCESS version used by Create),
//      do a ReadARec (SEQUENTIAL ACCESS version used by Display,
//                      & RANDOM ACCESS version used by Query).
//
// THE FILE is both a BINARY file and a RANDOM ACCESS file - those are 2 separate,
//              independent choices the designer made.
//      Since it's a random access file, it needs FIXED-LENGTH RECORDS and so fixed-
//      length fields.
//              - That's automatic for int/float/long/.../char/bool fields since they're
//                  they're all the same size for every record (int's and floats are
//                  4 bytes, longs and doubles are 8 bytes, etc.).
//              - However, STRING fields must be padded/truncated to ensure that all
//                  records are fixed-length.
//
// VIEWING THE FILE:
//      1) use the DisplayProgram
//      2) use a HexEditor.  I've put a .pdf copy of the HexEditor's display of the
//          binary file is the top-level folder of the app.  See:
//                      XVI32 - BinaryFile.pdf
//          (XVI32 is the HexEditor - see the course webpage to download the software).
//
// NOTE:  This BinFile class is NOT THE SAME as the BinFile class in the prior example
//      application.  This example:
//          - uses a binary file which is also RANDOM ACCESS
//                  so it has FIXED-LENGTH records (with fixed-length fields)
//          - creates the file using random access
//                  so WriteARec needs an RRN parameter to calculate OFFSET for the SEEK
//          - also has a QueryProgram which uses random access
//                  so it needs a 2nd ReadARec for doing random access which needs an
//                  RRN parameter to calculate OFFSET for the SEEK
//          - needs to deal with possible EMPTY LOCATIONS ("holes") within the file
//                  due to the DIRECT ADDRESS file structure
//                  so the 1st ReadARec (for doing sequential access) needs to do
//                  SPECIAL HANDLING of any STRING fields because of the way they're
//                  stored
//***************************************************************************************

package BinRWClasses;

import java.io.*;

public class BRWRBinFile 
{
    //*************************  ATTRIBUTES  ****************************************
    private static RandomAccessFile bWriter;
    private static RandomAccessFile bReader;
    public int id;
    public String name; 
    public float gpa;
    
    // -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -
    // The following are needed for Random Access
    // -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -
    private final int SIZE_OF_ID = Integer.SIZE / Byte.SIZE;    // 4 BYTES
    private final int SIZE_OF_NAME = 8;
    private final int SIZE_OF_GPA = Float.SIZE / Byte.SIZE;     // 4 BYTES

    private final int SIZE_OF_REC = SIZE_OF_ID + SIZE_OF_NAME + SIZE_OF_GPA;
        
    //*************************  CONSTRUCTOR  ***************************************
    // This opens the file when the 2 programs create an object for this file.
    // The file is opened only ONCE (for a particular program), which is done before
    //      any actual I/O to the file happens.
    // For the parameter, Create sends in a 'W', Display & Query send in an 'R'.
    //*******************************************************************************
    public BRWRBinFile(char ReadOrWrite) throws FileNotFoundException
    {
        if (ReadOrWrite == 'R')
        {
            bReader = new RandomAccessFile(new File("BinaryFile.bin"), "r");
        }
        else
        {
            bWriter = new RandomAccessFile(new File("BinaryFile.bin"), "rws");
        }
    }
    //************************  FINISH WITH OBJECT  ********************************
    public void FinishWithObject(char ReadOrWrite) throws IOException
    {
        if (ReadOrWrite == 'R')
        {
            bReader.close();
        }
        else
        {
            bWriter.close();
        }
    }
    //*************************  WRITE A REC (RANDOM ACCESS version) ****************
    public void WriteARec(String aLine, int rrn) throws IOException
    {            
        id = Integer.parseInt(aLine.substring(0, 2));
        name = aLine.substring(2, 10);
        gpa = Float.parseFloat(aLine.substring(10, 14));
        
        long offset = (rrn - 1) * SIZE_OF_REC;
        bWriter.seek(offset);

        bWriter.writeInt(id);
        bWriter.write(name.getBytes());
        bWriter.writeFloat(gpa);
    }
    //*************************  READ A REC  ****************************************
    // 2 overloaded methods:
    //      1) for sequential access
    //      2) for random access (where the caller supplies the RRN
    //              used to calculate offset used for the Seek).
    // This reads 1 record's fields (in their proper binary format) from the file
    //      and assigns them to the public variables.
    //*******************************************************************************

    //*************************  READ A REC (SEQUENTIAL ACCESS version) *************
    // What's sent back to the caller:
    //      - a bool RETURN value:
    //              1) true for a successful read of a good record,
    //              2) true for an empty location - an "all 0 bits" hole in the file
    //              3) false for an empty location - a "read failed" case (past EOF)
    // Individual fields are assigned to public variables
    //              (or 0, "", 0.0 for empty locations or past eof case) 
    // NOTE case #2 - this ReadARec version's return value is for NotYetAtEOF
    //*******************************************************************************
    public boolean ReadARec()
    {
        byte[] nameArray = new byte[SIZE_OF_NAME];
        try
        {
            id = bReader.readInt();
            if (id == 0)                    // TO SKIP OVER THE "all 0 bits"
            {                               // name FIELD (which looks like a null)
                bReader.read(nameArray);
                name = "";
            }
            else    
            {
                bReader.read(nameArray);    // TO READ IN GOOD RECORD'S NAME FIELD
                name = new String(nameArray);
            }
            gpa = bReader.readFloat();
            return true;
        }
        catch (Exception e)                 // READ PAST EOF
        {
            id = 0;
            name = "";
            gpa = 0;
            return false;
        }
    }
    //*************************  READ A REC (RANDOM ACCESS version) *****************
    // What's sent back to the caller:
    //      - a bool RETURN value:
    //              1) true for a successful read of a good record,
    //              2) false for an empty location - an "all 0 bits" hole in the file
    //              3) false for an empty location - a "read failed" case (past EOF)
    // Individual fields are assigned to public variables
    //              (or 0, "", 0.0 for empty locations or past eof case) 
    // NOTE case #2 - this ReadARec version's return value is for ReadAGoodRec
    //*******************************************************************************
    public boolean ReadARec(int rrn) throws IOException
    {
        long offset = (rrn - 1) * SIZE_OF_REC;
        bReader.seek(offset);
        
        byte[] nameArray = new byte[SIZE_OF_NAME];
        try
        {
            id = bReader.readInt();
            bReader.read(nameArray);
            name = new String(nameArray);
            gpa = bReader.readFloat();
            if ("".equals(name.trim()))
                return false;               // IT'S AN EMPTY LOCATION ("ALL-0-BITS")
            else
                return true;                // IT'S A GOOD RECORD
        }
        catch (Exception e)                 // IT'S AN EMPTY LOCATION ("READ FAILED")
        {                                   //          (TRIED READING PAST EOF)
            id = 0;
            name = "";
            gpa = 0;
            return false;
        }
    }
}
