/**
 * This class handles the interaction with the user
 * 
 * Course:	CS221
 * Assignment:	Project 1
 *
 * @author	Matt Lane, Till Krischer
 * @version 	09/17/2014 
 */

import java.io.*;
import java.util.Scanner;

public class Manipulator {
	
	/**
	 * ask user for input filename
	 * 
	 *	@param n		Scanner to use for the input
	 *	@return returns entered filename as String
	 */ 
	private static String askInputFilename(Scanner n) {
		System.out.print("What image file would you like to edit: ");
		return n.nextLine();
	}

	/**
	 * ask user for number of threads 
	 * 
	 *	@param n		Scanner to use for the input
	 *	@return returns entered number of threads as int
	 */ 
	private static int askThreads(Scanner n) {
		int threads;
		System.out.print("How many threads would you like to use: ");
		threads = n.nextInt();
		n.nextLine();
		return threads;
	}
	
	/**
	 * ask user for command
	 * 
	 *	@param n		Scanner to use for the input
	 *	@return returns command as a String
	 */ 
	private static String askCommand(Scanner n) {
		System.out.print("What command would you like to perform (i, g, b, h, s, d, r, or q) ");
		return n.nextLine();
	}
	
	/**
	 * ask user for output filename
	 * 
	 *	@param n		Scanner to use for the input
	 *	@return returns entered filename as String
	 */ 
	private static String askOutputFilename(Scanner n) {
		System.out.print("What do you want to name your new image file: ");
		return n.nextLine();
	}
	
	/**
	 * converts a bitmap according to command using multiple threads
	 * 
	 *	@param b				bitmap to convert
	 *	@param command			conversion command as char
	 *	@param numberOfThreads 	number of threads
	 *	@return returns the converted Bitmap
	 */ 
	private static Bitmap convert(Bitmap b, char command, int numberOfThreads) throws InterruptedException {
		//will not stay null because command will be one of the switch cases
		Bitmap converted = null;
		BitmapThread[] threads = new BitmapThread[numberOfThreads];
		//create new Bitmap with right size
		switch(command) {
		case 'i': case 'g': case 'b': case 'h':
			converted = new Bitmap(b.getHeight(), b.getWidth());
			break;
		case 's':
			converted = new Bitmap(b.getHeight()/2, b.getWidth()/2);
			break;
		case 'd':
			converted = new Bitmap(b.getHeight()*2, b.getWidth()*2);
			break;
		case 'r':
			converted = new Bitmap(b.getWidth(), b.getHeight());
			break;
		}
		//create and launch threads
		for(int i = 0; i < numberOfThreads; ++i)
			threads[i] = new BitmapThread(b, converted, command, i, numberOfThreads);
		for(Thread t : threads)
			t.start();
		//wait for threads to finish
		for(Thread t : threads)
			t.join();
		
		return converted;
	}
	
	public static void main(String [] args) {
		int threads;
		String inputFilename, outputFilename, command;
		Bitmap b;
		Scanner n = new Scanner(System.in);
		
		inputFilename = askInputFilename(n);
		try {
			// try to read in bitmap from file
			b = new Bitmap(inputFilename);
			threads = askThreads(n);
			//loop until user selects 'q' for quit
			while(! (command = askCommand(n)).equals("q")) {
				//check if command is valid, if it is convert image and take time
				switch(command) {
				case "i": case "g": case "b": case "h": case "s": case "d": case "r":
					long start = System.nanoTime();
					try {
						b = convert(b, command.charAt(0), threads);
					} catch (InterruptedException e) {
						System.out.println("Error: interrupted");
					}
					double time = (System.nanoTime() - start) / 1_000_000_000.0;
					System.out.printf("Command took %.3f seconds to execute\n", time);
					break;
				default:
					System.out.println("unknown command");
				}
			}
			outputFilename = askOutputFilename(n);
			try {
				//try to write the file
				b.writeToFile(outputFilename);
			} catch (IOException e) {
				System.out.println("Error writing the file");
			}
		} catch (IOException e) {
			System.out.println("Error reading the file");
		}
		//close Scanner
		n.close();
	}
}