import java.math.*;

public class MemoryAccess{
	private char mode; //'s' or 'l'
	private String address; //32 bit address in hexadecimal
	private int offset; //page offset as integer (decimal)
	private int page; //page number as integer (decimal)
	private int process; //the process that made the access
	private boolean dirtyBit; //do we need to save to disk?
	private boolean refBit; //was this bit referenced (Second Chance Algorithm)

	public MemoryAccess(char m, String a, int p, int offsetSz){
		mode = m;
		address = a.substring(2, a.length()); //get rid of the "0x"
		process = p;

		String bits = new BigInteger(address, 16).toString(2); //turn address into bit string
		while(bits.length() < 32) bits = "0"+bits; //pad with zeros so we can seperate offset and page num
		offset = Integer.parseInt(bits.substring(bits.length()-offsetSz,bits.length()), 2); //seperate offset
		page = Integer.parseInt(bits.substring(0,bits.length()-offsetSz), 2); //seperate page number

		refBit = false;
		dirtyBit = (mode == 's');
	}

	//getters
	public char getMode(){
		return mode;
	}

	public String getAddress(){
		return address;
	}

	public int getOffset(){
		return offset;
	}

	public int getPage(){
		return page;
	}

	public int getProcess(){
		return process;
	}

	public boolean dirtyBit(){
		return dirtyBit;
	}

	public boolean refBit(){
		return refBit;
	}

	//setters
	public void setDirtyBit(boolean bit){
		dirtyBit = bit;
	}

	public void setRefBit(boolean bit){
			refBit = bit;
	}

	//prints the page number
	public String toString(){
		return "\npage: "+page+" DB: "+dirtyBit+" RB: "+refBit;
	}
}