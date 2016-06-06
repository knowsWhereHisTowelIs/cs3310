// PROGRAM:  CreateSerBinFile       CLASS:  SerBinFile
// AUTHOR:  D. Kaminski
// DESCRIPTION:  This class handles all file IO for the Serialized Binary File.
//      This class is much DIFFERENT than the BinFile class in the earlier example
//      (BinaryStreamReadWrite) which created a PLAIN Binary file:
//      - additional libraries are declared here to allow for serialization                         ??????
//      - a BinaryFormatter object is declared
//      - the record is built from the fields by a method differently (in WriteARec)
//      - the actual writing of the record is done by formatter.Serialize method
//          (in the BinaryFormatter class)
//***************************************************************************************

package createserbinfile;

import java.io.*;

public class SerBinFile 
{
    //*************************  STATIC MEMBERS  ************************************
    private static ObjectOutputStream serBinFile;
    private static SerBinRec serBinRec;

    //*************************  CONSTRUCTOR  ***************************************
    public SerBinFile(String fileName) throws FileNotFoundException, IOException
    {
        FileOutputStream fs = new FileOutputStream(fileName);
        serBinFile = new ObjectOutputStream(fs);
    }
    //************************  CLOSE FILE  *****************************************
    public void CloseIt() throws IOException
    {
        serBinFile.close();
    }
    //*************************  WRITE A REC  ***************************************
    public void WriteARec(String aLine) throws IOException
    {
                                    // SEPARATE THE FIELDS & CONVERT TO "BINARY"
        String[] field = aLine.split(",");

        int id = Integer.parseInt(field[0]);
        String name = field[1];
        float gpa = Float.parseFloat(field[2]);

                                    // GATHER THE FIELDS FOR THE BINARY RECORD
        serBinRec = new SerBinRec(id, name, gpa);

                                // WRITE THE RECORD TO FileOutputStream, BUT FIRST
                                //   SERIALIZE THE RECORD serBinRec OBJECT
        serBinFile.writeObject(serBinRec);
    }
}
