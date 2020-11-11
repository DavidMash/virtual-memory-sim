/*
*	This is a data structure that is used to simulate virtual memory with the second chance scheduling algorithm.
*	It utilizes an arraylist for its O(1) retrieval using indexing and a hashmap for O(1) searching.
*/

import java.util.*;

public class PageTable{
	List<MemoryAccess>[] list; //holds memory accesses for each process
	Map<Integer, Integer>[] indexMap; //map page numbers to table indexes
	int frames[]; //maximum number of pages for each process
	int pointer[]; //pointers for second chance scheduling algorithm
	int diskWrites[]; //counter for disk writes for each process
	int pageFaults[]; //counter for page faults for each process
	int accesses[];
	int numProcs; //number of processes using the page table

	//init with size in KB, and the memory split between processes
	@SuppressWarnings("unchecked")
	public PageTable(int numFrames, int[] split){
		numProcs  = split.length;
		frames = new int[numProcs];

		list = (ArrayList<MemoryAccess>[]) new ArrayList[numProcs];
		indexMap = (HashMap<Integer, Integer>[]) new HashMap[numProcs];

		int totalShares = 0; //total number of memory units
		for(int shares : split) totalShares += shares; //add all together

		pointer = new int[numProcs];
		diskWrites = new int[numProcs];
		pageFaults = new int[numProcs];
		accesses = new int[numProcs];

		//seperate frames according to split and set init processes specific stuff
		for(int i = 0; i < numProcs; i++){
			frames[i] = (int)(numFrames * ((double)split[i] / totalShares));
			list[i] = new ArrayList<>(frames[i]);
			indexMap[i] = new HashMap<>(frames[i]);

			pointer[i] = 0;
			diskWrites[i] = 0;
			pageFaults[i] = 0;
			accesses[i] = 0;

			//if a table ended up with no frames then we can't run the simulation
			if(frames[i] <= 0){
				throw new IllegalArgumentException("Not enough frames for that split.");
			}
		}
	}

	public void add(MemoryAccess access){
		int p = access.getProcess();

		accesses[p]++;//count this access

		if(this.contains(access)){ //page hit

			if(access.dirtyBit()){
				//update dirty bit of page if necessary
				list[p].get(indexMap[p].get(access.getPage())).setDirtyBit(true);
			}

			list[p].get(indexMap[p].get(access.getPage())).setRefBit(true); //update ref bit for second chance algorithm
		}else{ //page fault

			pageFaults[p]++;

			if(list[p].size() < frames[p]){
				//add normally if not full
				indexMap[p].put(access.getPage(), list[p].size());//map the page num to the index of the access in the table
				list[p].add(access);
			}else{
				//replace one if full
				int index = indexToEvict(p);
				indexMap[p].remove(list[p].get(index).getPage());//remove mapping
				MemoryAccess old = list[p].get(index); //remember old element
				list[p].set(index, access); //replace it with new
				indexMap[p].put(access.getPage(), index); //add new mapping

				if(old.dirtyBit()){ //check if the dirty bit of the old access was set
					diskWrites[p]++; //if so we need to write to disk
				}
			}
		}
	}

	private int indexToEvict(int p){
		boolean found = false;
		MemoryAccess a;
		while(!found){ //search until we find index to evict

			if(pointer[p] >= list[p].size()) pointer[p] = 0;

			for(; pointer[p] < list[p].size() && !found; pointer[p]++){
				a = list[p].get(pointer[p]);

				if(!a.refBit()){ //if the ref bit is 0
					found = true;
					break;
				}else{ //ref bit is 1, so we need to make it 0
					a.setRefBit(false);
				}
			}
		}

		return pointer[p]++; //return pointer then move ahead one
	}

	//returns true if the page of the access is in the table and false otherwise
	public boolean contains(MemoryAccess access){
		//we just need to check if the page is mapped to an index
		return indexMap[access.getProcess()].containsKey(access.getPage());
	}

	public int diskWrites(int process){
		return diskWrites[process];
	}

	public int pageFaults(int process){
		return pageFaults[process];
	}

	public int accesses(int process){
		return accesses[process];
	}

	public int frames(int process){
		return frames[process];
	}

	//print all the stats on this page table
	public String toString(){
		String output = "";
		for(int i = 0; i < numProcs; i++){
			if(i > 0) output += "\n-\n";
			output += "For process " + i + ".";
			output += "\nNumber of Frames: " + frames[i];
			output += "\nTotal memory accesses: " + accesses[i];
			output += "\nTotal page faults: " + pageFaults[i];
			output += "\nTotal writes to disk: " + diskWrites[i];
		}
		return output;
	}
}