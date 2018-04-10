/**
 * @author Stephen Pollo
 * 
 * Program written to get the average coverage in peaks found by csaw.
 * Program first reads the list of peaks from csaw and stores the coordinates in parallel arrays
 * It then reads through the coverage at every base and calculates the average coverage
 * for each of the peaks
 * These coverages are printed to the console
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class GetPeakAvgCov {

	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.out.println("Error! You must provide a file of peaks and a file of coverage at every base");
			return;
		}
		
		ArrayList<String> chrNames = new ArrayList<String>();
		ArrayList<Integer> peakStartCoords = new ArrayList<Integer>();
		ArrayList<Integer> peakEndCoords = new ArrayList<Integer>();
		ArrayList<Double> avCov = new ArrayList<Double>();
		
		/*
		 * Attempt to read the peaks file provided on the command line.
		 * Catch exceptions thrown if errors occur
		 */
		try {
		    File inputFile = new File(args[0]);
		    Scanner in = new Scanner(inputFile);

		    /*
		     * Read the file of peaks into the corresponding array lists
		     */
		    while (in.hasNextLine()) {
		    	String line = in.nextLine();
		    	
		    	String chName = line.substring(0, line.indexOf('\t'));
		    	
		    	String line2 = line.substring(line.indexOf('\t') + 1);
		    	String start = line2.substring(0, line2.indexOf('\t'));
		    	
		    	String line3 = line2.substring(line2.indexOf('\t') + 1);
		    	String end = line3.substring(0, line3.indexOf('\t'));
			
		    	int startNum = Integer.parseInt(start);
		    	int endNum = Integer.parseInt(end);
		    	chrNames.add(chName);
		    	peakStartCoords.add(startNum);
		    	peakEndCoords.add(endNum);
		    	
		    } // end while reading file
		    
		    in.close();
		    
		} // end file reading try block

		/*
		 * Print the error if one was found and terminate
		 * the program
		 */
		catch(FileNotFoundException e) {
		    System.out.println(e);
		    System.exit(0);
		}
		
		// Print number of peaks in file
		System.out.println(chrNames.size());
		
		/*
		 * Attempt to read the coverage file provided on the command line.
		 * Catch exceptions thrown if errors occur
		 */
		try {
		    File inputFile2 = new File(args[1]);
		    Scanner in2 = new Scanner(inputFile2);

		    int peakIndex = 0;
		    boolean included = false;
	    	double average = 0;
	    	int count = 0;
		    
		    while (in2.hasNextLine()) {
		    	/*
		    	 * For each line, read it in and parse the needed values.
		    	 * Then find which peak region it belongs in and add its coverage to 
		    	 * that total to get the average per region
		    	 */
		    	String line4 = in2.nextLine();
		    	included = false;
		    	if (peakIndex >= peakStartCoords.size()) {
		    		// Nothing left yo
		    		break;
		    	}
		    	
		    	String chr = line4.substring(0, line4.indexOf('\t'));
		    	String line5 = line4.substring(line4.indexOf('\t') + 1);
		    	String begin = line5.substring(0, line5.indexOf('\t'));
		    	String line6 = line5.substring(line5.indexOf('\t') + 1);
		    	String coverage = line6.substring(line6.indexOf('\t') + 1);
		    	int startCoord = Integer.parseInt(begin);
		    	int cov = Integer.parseInt(coverage);
		    	
		    	// Begin finding if this line belongs to a peak in this group
		    	while (!included) {
		    		if (chr.equalsIgnoreCase(chrNames.get(peakIndex))) {
		    			// Line might belong to current peak in arrays
			    		if (startCoord >= peakStartCoords.get(peakIndex)) {
			    			// Line might belong to current peak in arrays
			    			if (startCoord <= peakEndCoords.get(peakIndex)) {
			    				/*
			    				 * This line belongs to the peak described in the peakIndex cell
			    				 * of the parallel arrays, add its coverage
			    				 */
			    				average += cov;
			    				count++;
			    				included = true;
			    				if (startCoord + 1 == peakEndCoords.get(peakIndex)) {
			    					//The peak is done, add it to the output array
			    					double toAdd = average / count;
				    				if (Double.isNaN(toAdd)) {
				    					System.out.println("larger than end");
				    					System.out.println(average);
				    					System.out.println(count);
				    					System.out.println(peakStartCoords.get(peakIndex - 1));
				    					System.out.println();
				    				}
				    				// Reset values for next peak
				    				average = 0;
				    				count = 0;
				    				avCov.add(toAdd);
			    				}
			    				continue;
			    			}
			    			else {
			    				// Line not in a peak in this file, move to next peak
			    				peakIndex++;
			    				
			    				if (peakIndex >= peakStartCoords.size()) {
			    		    		// Nothing left yo
			    		    		break;
			    		    	}
			    			
			    				continue;
			    			}
			    		}
			    		else {
			    			// Not in this peak group move on to next line of coordinates
			    			included = true;
			    			continue;
			    		}
			    	}
			    	else {
			    		// chromosome doesn't match, find next lines where it does match
			    		String num = chr.substring(3);
			    		String refNum = chrNames.get(peakIndex).substring(3);
			    		int num1 = Integer.parseInt(num);
			    		int refNum1 = Integer.parseInt(refNum);
			    		
			    		if (num1 < refNum1) {
			    			// Not in this peak group move onto the next line of coordinates
			    			included = true;
			    			continue;
			    		}
			    		
			    		while (!chr.equalsIgnoreCase(chrNames.get(peakIndex))) {
			    			// Skip through peaks until we reach first one on this chromosome
			    			peakIndex++;
			    			
			    			if (peakIndex >= peakStartCoords.size()) {
		    		    		// Nothing left yo
		    		    		break;
		    		    	}
			    		}
	    				
			    		continue;
			    	}
		    	}
		    	
		    } // end while reading file
		    
		    in2.close();
		    
		} // end file reading try block

		/*
		 * Print the error if one was found and terminate
		 * the program
		 */
		catch(FileNotFoundException e) {
		    System.out.println(e);
		    System.exit(0);
		}
		
		// Print size of output array so we can see if it matches number of peaks
		System.out.println(avCov.size());
		
		/*
		 * Output array is constructed, print it
		 */
		for (int i = 0; i < avCov.size(); i++) {
			System.out.println(avCov.get(i));
		}
		
	} // end main
	
} // end class
