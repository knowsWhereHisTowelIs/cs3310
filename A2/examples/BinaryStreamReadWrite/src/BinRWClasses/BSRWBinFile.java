// APPLICATION:  BinaryStreamReadWrite         SEPARATE SHARABLE CLASS:  BSRWBinFile
// 
// DESCRIPTION:  This class handles the binary file used by the 2 programs.
//      It provides methods for opening the file, closing the file and ReadARec and
//      WriteARec.
// NOTE:  If there were more record/field-handling in this app, then I would have
//      set up a SEPARATE CLASS for BinRec in addition to the BinFile class
//      (as was shown in an earlier example on the course webpage).
// -------------------------------------------------------------------------------------
// A BINARY FILE:  (read the DumpingABinaryFile.pdf document on the course website)
//      A file is referred to as a "binary file" when it is not a text file
//          (i.e., it doesn't contain only printable ASCII/Unicode characters).
//      A binary file
//          - contains fields which are int/double/. . . (non-char, non-string fields)
//          - does not have a <CR><LF> after each record (which is just a nice-ity for
//              human readers, really)
//          - can use fixed-length or variable-length records, but the program must know
//              how many bytes to read or write.
//              DataOutputStream has Write methods for writing out most Java primitives,
//                  therefore its size (in bytes) can usually be determined by the method.
//              DataInputStream has ReadInt, ReadFloat, ReadLong, . . . ReadChar
//                  methods, but does not have a ReadString or ReadLine method.
//                  The number of bytes is thus specified by the method used.
//
// VIEWING A BINARY FILE:  A binary file can't just be printed or viewed in NotePad.
//      There are 2 ways to view its contents:
//      1) use a HexEditor (or OS dump utility) - see course website for details.
//      2) write a simple Dump/Display utility program.
//
// NOTE on STRING HANDLING: When writing strings to binary files, Java can prefix
//      its strings with two bytes representing the number of bytes to be written.
//      (See .writeUTF : http://docs.oracle.com/javase/1.4.2/docs/api/java/io/DataOutputStream.html#writeUTF%28java.lang.String%29)
//      Otherwise, with variable length records, there is no way of knowing how long
//      the string will be.  In this example, a length byte is manually written and 
//      read from the file to emulate the way C# handles binary strings. 
//      (Disclaimer: This is NOT necessarily the best way to handle this scenario.)
//
// A NOTE on STORAGE in a BINARY FILE for THIS TYPE (#1) of binary file
//          - numeric multi-byte fields (int's, double's, float's, . . .) are stored as
//              BIG-ENDIAN on the file.
//          - string fields are stored with a preceeding 8-bit byte counter (see above)
// -------------------------------------------------------------------------------------
// THE BINARY FILE here: 
//      - the file name is hard-coded in the constructor, the callers don't supply it
//      - it contains variable-length records, variable-length fields, no field-separators
//          and no <LF><CR>
//      - contains the same fields in the same order as the input file: id, name, gpa
//      - however, for the binary file:
//              id is stored as an actual int (4 bytes)
//              name is stored as char's, same as the input file
//              gpa is stored as an actual float (4 bytes) with the decimal point
// To view the file,
//      1) run the DisplayProgram
//      2) view it in a HexEditor.  (There's also a pdf of the HexEditor's display - 
//          see "XVII32 - BinaryFile" in the main folder of this app.
//***************************************************************************************

package BinRWClasses;

import java.io.*;

public class BSRWBinFile 
{
    //*************************  ATTRIBUTES  ****************************************
    private static DataOutputStream bWriter;
    private static DataInputStream bReader;
    public int id;
    public String name; 
    public float gpa;
        
    //*************************  CONSTRUCTOR  ***************************************
    // This opens the file when the 2 programs create an object for this file.
    // The file is opened only ONCE (for a particular program), which is done before
    //      any actual I/O to the file happens.
    // For the parameter, Create sends in a 'W', Display sends in an 'R'.
    //*******************************************************************************
    public BSRWBinFile(char ReadOrWrite) throws FileNotFoundException
    {
        if (ReadOrWrite == 'R')
        {
            FileInputStream in = new FileInputStream("BinaryFile.bin");
            bReader = new DataInputStream(in); //DataStream wraps around 
                    //FileStream to enable you to read/write Java primitives 
                    //instead of only bytes.
        }
        else
        {
            FileOutputStream out = new FileOutputStream("BinaryFile.bin");
            bWriter = new DataOutputStream(out);
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
    //*************************  WRITE A REC  ***************************************
    public void WriteARec(String aLine) throws IOException
    {
        String[] field = aLine.split(",");
            
        id = Integer.parseInt(field[0]);
        name = field[1];
        gpa = Float.parseFloat(field[2]);

        bWriter.writeInt(id);
        bWriter.writeByte(name.length()); //prefix string with byte representing length
        bWriter.write(name.getBytes());
        bWriter.writeFloat(gpa);
    }
    //*************************  READ A REC  ****************************************
    // Send back a bool RETURN value:  true (for successfully read), false for EOF.
    //*******************************************************************************
    public boolean ReadARec()
    {
        try
        {
            id = bReader.readInt();
            int length = (int)bReader.readByte(); //find length of string
            byte[] array = new byte[length]; //read into byte array
            bReader.read(array); //read specified # of bytes into byte array
            name = new String(array); //convert to string
            gpa = bReader.readFloat();
            return true;
        }
        catch (Exception e)                                  // READ PAST EOF
        {
            id = 0;
            name = "";
            gpa = 0;
            return false;
        }
    }
}
