/**
 * This class allows to read in and write out a Bitmap image
 */

import java.io.*;

public class Bitmap {
	private char[] type = new char[2];	// always contains 'B' and 'M'
	private int	size;					// total size of file
	private int	reserved;				// always 0
	private int	offset;					// start of data from front of file, should be 54
	private int	header;					// size of header, always 40
	private int	width;					// width of image in pixels
	private int	height;					// height of image in pixels
	private short planes;				// planes in image, always 1
	private short bits;					// color bit depths, always 24
	private int	compression;			// always 0		
	private int	dataSize;				// size of color data in bytes
	private int	horizontalResolution;	// unreliable, use 72 when writing
	private int	verticalResolution;		// unreliable, use 72 when writing
	private int	colors;					// colors in palette, use 0 when writing
	private int	importantColors;		// important colors, use 0 when writing
	
	//image Data
	private byte[][] data;
	
	/**
	 * creates empty Bitmap, puts default values in header
	 *
	 * @param  height	height of the bitmap as int
	 * @param  width	width of the bitmap as int
	 */
	public Bitmap(int height, int width) {
		type[0]					= 'B';
		type[1]					= 'M';
		size					= width * 3 * height + 54;
		reserved				= 0;
		offset					= 54;
		header					= 40;
		this.width				= width;
		this.height				= height;
		planes					= 1;
		bits					= 24;
		compression				= 0;
		dataSize				= width * 3 * height;
		horizontalResolution	= 72;
		verticalResolution		= 72;
		colors					= 0;
		importantColors			= 0;
		 
		this.data = new byte[height][width*3];
	}

	/**
	 * creates Bitmap from a file
	 *
	 * @param  filename file to read in from as String
	 */
	public Bitmap(String filename) throws IOException {
		FileInputStream fIn = new FileInputStream(filename);
		readHeader(fIn);
		data = new byte[height][width*3];
		readData(fIn);
		fIn.close(); 
	}
	
	/**
	 * writes out the bitmap
	 *
	 * @param  filename file to write to
	 */
	public void writeToFile(String filename) throws IOException {
		FileOutputStream fOut = new FileOutputStream(filename);
		//header
		writeNumber(fOut, type[0], 1);
		writeNumber(fOut, type[1], 1);
		writeNumber(fOut, size, 4);
		writeNumber(fOut, reserved, 4);
		writeNumber(fOut, offset, 4);
		writeNumber(fOut, header, 4);
		writeNumber(fOut, width, 4);
		writeNumber(fOut, height, 4);
		writeNumber(fOut, planes, 2);
		writeNumber(fOut, bits, 2);
		writeNumber(fOut, compression, 4);
		writeNumber(fOut, dataSize, 4);
		writeNumber(fOut, horizontalResolution, 4);
		writeNumber(fOut, verticalResolution, 4);
		writeNumber(fOut, colors, 4);
		writeNumber(fOut, importantColors, 4);

		//data
		for(int i = 0; i < height; ++i) {
			fOut.write(data[i]);
			//if padding required write right amount of zeros bytes after each row
			if((width*3)%4 != 0)
				for(int j = 0; j < 4 - (width*3)%4; ++j)
					fOut.write(0);
		}
		//put two zero bytes at the end of the file
		fOut.write(0);
		fOut.write(0);
	}
	
	/**
	 * gets the width
	 *
	 * @return returns width as int
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * gets the height
	 *
	 * @return returns height as int
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * sets a byte in the color data
	 *
	 * @param row		row of the byte as int
	 * @param column	column of the byte as int
	 * @param b			content to set
	 */
	public void setByte(int row, int column, byte b) {
		data[row][column] = b;
	}

	/**
	 * gets a byte from the color data
	 *
	 * @param row		row of the byte as int
	 * @param column	column of the byte as int
	 *
	 * @return returns value of byte as byte
	 */
	public byte getByte(int row, int column) {
		return data[row][column];
	}
	
	/**
	 * reads in the header
	 *
	 * @param fIn	FileInputStream to use
	 */
	private void readHeader(FileInputStream fIn) throws IOException {
		 type[0]				= (char) 	readUnsignedNumber(fIn, 1);
		 type[1]				= (char) 	readUnsignedNumber(fIn, 1);
		 size					= 			readUnsignedNumber(fIn, 4);
		 reserved				= 			readUnsignedNumber(fIn, 4);
		 offset					= 			readUnsignedNumber(fIn, 4);
		 header					= 			readUnsignedNumber(fIn, 4);
		 width					= 			readUnsignedNumber(fIn, 4);
		 height					= 			readUnsignedNumber(fIn, 4);
		 planes					= (short)	readUnsignedNumber(fIn, 2);
		 bits					= (short)	readUnsignedNumber(fIn, 2);
		 compression			= 			readUnsignedNumber(fIn, 4);
		 dataSize				= 			readUnsignedNumber(fIn, 4);
		 horizontalResolution	= 			readUnsignedNumber(fIn, 4);
		 verticalResolution		= 			readUnsignedNumber(fIn, 4);
		 colors					= 			readUnsignedNumber(fIn, 4);
		 importantColors		= 			readUnsignedNumber(fIn, 4);
	}
	
	/**
	 * reads in the colordata
	 *
	 * @param fIn	FileInputStream to use
	 */
	private void readData(FileInputStream fIn) throws IOException {
		//skip to colordata
		fIn.skip(54 - offset);
		for(int i = 0; i < height; ++i) {
			fIn.read(data[i]);
			//if padding, skip over the padding
		 	if((width*3)%4 != 0)
				fIn.skip(4 - ((width*3)%4));
		}
	}
	
	/**
	 * reads a number from the file
	 *
	 * @param fIn	FileInputStream to use
	 * @param bytes	length of the number in bytes
	 * 
	 * @return returns value of the number as int
	 */
	private int readUnsignedNumber(FileInputStream fIn, int bytes) throws IOException {
		byte[] data = new byte[bytes];
		fIn.read(data);
		return bytesToNumber(data);
	}
	
	/**
	 * writes a number to the file
	 *
	 * @param fOut		FileOutputStream to use
	 * @param number	number to write
	 * @param size		size of the number in bytes
	 */
	private void writeNumber(FileOutputStream fOut, int number, int size) throws IOException {
		byte[] data = numberToBytes(number, size);
		fOut.write(data);
	}
	
	/**
	 * takes array of bytes that represent little endian number and returns that number
	 *
	 * @param data	data to convert as byte array
	 * 
	 * @return returns number represented by the data as int
	 */
	private int bytesToNumber(byte[] data) {
		int sum = 0;
		for(int i = 0; i < data.length; ++i) {
			short temp2 = data[i];
			if(temp2 < 0)
				temp2 += 256;
			sum += temp2 << 8*i;
		}
		return sum;
	}
	
	/**
	 * takes number and converts it to its little endian representation
	 *
	 * @param number	number to convert
	 * @param size		size of the number in bytes as an int
	 * 
	 * @return returns little endian representation of number as byte array
	 */
	private byte[] numberToBytes(int number, int size) {
		byte[] data = new byte[size];
		int digit;
		for(int i = size -1; i >= 0; --i) {
			digit = number >>> 8*i;
			number -= (digit) << 8*i;
			if(digit > 127)
				digit -= 256;
			data[i] = (byte)digit;
		}
		return data;
	}
}
