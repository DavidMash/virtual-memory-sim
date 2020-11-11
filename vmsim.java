import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class vmsim{

	private static int pointer = 0;

    public static void main(String[] args) throws Exception {

		//declare stuff for command line args
		int numFrames = -1;
		int pageSize = -1;
		int pageOffsetSz = -1;
		int[] memSplit = null;

		//try to parse command line args
        try{
			numFrames = Integer.parseInt(args[1]);
			pageSize = Integer.parseInt(args[3]);

			pageOffsetSz = calculatePageOffset(pageSize); //send it in bytes

			String[] splitString = line.split(":");
			memSplit = new memSplit[splitString.length];
			for(int i = 0; i < memSplit.length; i++){
				memSplit[i] = Integer.parseInt(splitString[i]);
			}
		}catch(Exception e){
			throw new IllegalArgumentException("Incorrect command line argument format.");
		}

        File trace = null;
		Scanner traceReader = null;
		try {
			trace = new File(args[6]);
			traceReader = new Scanner(trace);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Trace file not found.");
        }

		PageTable pageTable = new PageTable(numFrames, memSplit);

		while (traceReader.hasNextLine()) {
			String line = traceReader.nextLine();

			String[] lineSplit = line.split(" ");
			MemoryAccess memoryAccess = null;
			try{
				memoryAccess = new MemoryAccess(line.charAt(0), lineSplit[1], Integer.parseInt(lineSplit[2]), pageOffsetSz); //(mode, addess, process, offsetSize)
			}catch(Exception e){
				throw new Exception("Trace file is not formatted correctly.");
			}

			pageTable.add(memoryAccess);

		}
		traceReader.close();

		//output stats
		System.out.println(pageTable);

    }

    private static int calculateOffset(int size) //just log base 2 of the bytes that make up size
	{
	    return (int)(Math.log(size * 1024) / Math.log(2));
	}
}