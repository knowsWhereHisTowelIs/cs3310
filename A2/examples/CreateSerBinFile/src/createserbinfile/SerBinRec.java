// PROGRAM:  CreateSerBinFile       CLASS:  SerBinRec
// AUTHOR:  D. Kaminski
// DESCRIPTION:  This class handles the record building for the Serialized Binary File.
//***************************************************************************************

package createserbinfile;

public class SerBinRec implements java.io.Serializable  // NOTE THIS
{
    //*************************  FIELDS IN THE RECORD  ******************************
    // properties
    // ******************************************************************************
    private int privateId;
    public final int getId()
    {
    	return privateId;
    }
    public final void setId(int value)
    {
    	privateId = value;
    }
    private String privateName;
    public final String getName()
    {
    	return privateName;
    }
    public final void setName(String value)
    {
    	privateName = value;
    }
    private float privateGpa;
    public final float getGpa()
    {
    	return privateGpa;
    }
    public final void setGpa(float value)
    {
    	privateGpa = value;
    }
    //*************************  CONSTRUCTORS  **************************************
    public SerBinRec(int id, String name, float gpa)
    {
	setId(id);
	setName(name);
	setGpa(gpa);
    }
}
