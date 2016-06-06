// PROGRAM:  CreateSerBinFile
// AUTHOR:  D. Kaminski
// DESCRIPTION:  This application creates a SERIALIZED binary file rather than a PLAIN
//      binary file (using InputFile.txt).
// A SERIALIZED file contains META-data, i.e., extra data about the actual data so the
//      data file is "self-interpretable" because the meta data indicates the data types
//      (and other information) regarding the actual data in the file).
// VIEWING THE FILE:  Use a HexEditor to view the SerBinFile.bin and compare that to the
//      BinaryFile.bin in the prior BinaryStreamReadWrite application.
//***************************************************************************************

package createserbinfile;

import java.io.*;

public class CreateSerBinFile 
{
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
        FileReader inFile = new FileReader("InputFile.txt");
        BufferedReader input = new BufferedReader(inFile);

        SerBinFile serBinFile = new SerBinFile("SerBinFile.bin");

        String inputLine;

        while ((inputLine = input.readLine()) != null)
        {
            serBinFile.WriteARec(inputLine);
        }

        System.out.println("The Serialized Binary File was created.");
        System.out.println("View it in a HexEditor: \n\t" +
            "(see XVI32 - SerBinaryFile.pdf in the app folder).");

        serBinFile.CloseIt();
        inFile.close();
    }
}
