/*
*	This is a structure that simulates memory accesses in a virtual page table.
*	They include 32-bit addresses, a dirty bit, and a reference bit for scheduling.
*
*	David Mash
*/

import java.math.*;

public class MemoryAccess{
	private String address; //32 bit address in hexadecimal
	private int offset; //page offset as integer (decimal)
	private int page; //page number as integer (decimal)
	private int process; //the process that made the access
	private boolean dirtyBit; //do we need to save to disk?
	private boolean refBit; //was this bit referenced (Second Chance Algorithm)

	public MemoryAccess(char mode, String address, int proc, int offsetSz){
		address = address.substring(2, address.length()); //get rid of the "0x"
		process = proc;

		String bits = new BigInteger(address, 16).toString(2); //turn address into bit string
		while(bits.length() < 32) bits = "0"+bits; //pad with zeros so we can seperate offset and page num
		offset = Integer.parseInt(bits.substring(bits.length()-offsetSz,bits.length()), 2); //seperate offset
		page = Integer.parseInt(bits.substring(0,bits.length()-offsetSz), 2); //seperate page number

		refBit = false;
		dirtyBit = (mode == 's'); //if saving , we need to set the dirty bit
	}

	//getters
	public String getAddress(){
		return address;
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
		return "page: "+page;
	}
}