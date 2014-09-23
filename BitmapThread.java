/**
 * This class provides different methods to manipulate bitmap images using threads
 */
public class BitmapThread extends Thread {
	private Bitmap source, result;
	private int number, threads;
	private char command;
	
	/**
	 * creates Bitmap Thread
	 *
	 * @param  source	bitmap to be maniplated as Bitmap object  
	 * @param  result	bitmap object to store result of operation in
	 * @param  command	command that should be executed, when thread runs
	 * @param  number	number of the thread
	 * @param  threads	total number of all threads
	 */
	public BitmapThread(Bitmap source, Bitmap result, char command, int number, int threads) {
		this.source = source;
		this.result = result;
		this.number = number;
		this.threads = threads;
		this.command = command;
	}
	
	/**
	 *	perform the specified command
	 */ 
	@Override
	public void run() {
		//call right method for command
		switch(command) {
		case 'i':
			inverse();
			break;
		case 'g':
			grayscale();
			break;
		case 'b':
			blur();
			break;
		case 'h':
			mirror();
			break;
		case 's':
			shrink();
			break;
		case 'd':
			doubleSize();
			break;
		case 'r':
			rotate();
			break;
		}
	}
	
	/**
	 *	puts inverse of source in result
	 */ 
	public void inverse() {
		//loop through each byte and set its value to 255 mius itself
        for(int i = number; i < source.getHeight(); i+=threads)
        	for(int j = 0; j < source.getWidth() * 3; j++) {
        		byte inverted = (byte) (255 - source.getByte(i, j));
        		result.setByte(i, j, inverted);
        	}
    }
	
	/**
	 *	puts grayscale of source in result
	 */ 
	public void grayscale() {
		//loop through all pixels, apply the grayscale formula to each one and set it to the new color
        for(int i = number; i < source.getHeight(); i+=threads)
        	for(int j = 0; j < source.getWidth(); j++) {
        		int[] temp = getPixel(i, j);		
        		int newColor = (int)((0.3 * temp[2]) + (0.59 * temp[1]) + (0.11 * temp[0]));
        		setPixel(i, j, newColor, newColor, newColor);
        	}
    }
	
	/**
	 *	puts blur of source in result
	 */ 
	public void blur() {
		//loop through all pixels
		for(int i = number; i < source.getHeight(); i+=threads)
        	for(int j = 0; j < source.getWidth(); j++) {
        		//sum up color values from 5x5 square around pixel
        		int blueSum = 0;
        		int greenSum = 0;
        		int redSum = 0;
        		int pixels = 0;
        		for(int k = i - 2; k < i + 2; ++k)
        			for(int m = j - 2; m < j + 2; ++m) {
        				//check for corner pixels
        				if(k >= 0 && k <= source.getHeight() - 1 && m >= 0 && m <= source.getWidth() - 1 ) {
        					int[] temp = getPixel(k, m);
        					blueSum += temp[0];
        					greenSum +=temp[1];
        					redSum += temp[2];
        					pixels++;
        				}
        			}
        		//set average of surrounding pixels as new color
        		setPixel(i, j, (byte) (blueSum / pixels), (byte) (greenSum / pixels), (byte) (redSum / pixels));
        	}
    }
	
	/**
	 *	puts mirror of source in result
	 */ 
	public void mirror() {
		//loop through rows and put them in result backwards
		for(int i = number; i < source.getHeight(); i+=threads)
        	for(int j = source.getWidth()-1; j >= 0; j--) {
        		int[] temp = getPixel(i, j);
        		setPixel(i, source.getWidth() - 1 - j, temp[0], temp[1], temp[2]);
        	}
    }
	
	/**
	 *	puts image half the size of source in result
	 */ 
	public void shrink() {
		//loop through all pixels in the result(which is half the width and height of source)
		//sum up the 4 corresponding pixels in source and put them into the pixel 
		for(int i = number; i < result.getHeight(); i+=threads)
        	for(int j = 0; j < result.getWidth(); j++) {
        		double blueSum = 0;
        		double greenSum = 0;
        		double redSum = 0;
        		
        		int[] temp = getPixel(i*2, j*2);
        		blueSum += temp[0];
        		greenSum += temp[1];
        		redSum += temp[2];
        		temp = getPixel(i*2+1, j*2);
        		blueSum += temp[0];
        		greenSum += temp[1];
        		redSum += temp[2];
        		temp = getPixel(i*2, j*2+1);
        		blueSum += temp[0];
        		greenSum += temp[1];
        		redSum += temp[2];
        		temp = getPixel(i*2+1, j*2+1);
        		blueSum += temp[0];
        		greenSum += temp[1];
        		redSum += temp[2];
        		
        		setPixel(i, j, (byte)(blueSum/4.0), (byte)(greenSum/4.0), (byte)(redSum/4.0));
        	}
    }
	
	/**
	 *	puts image double the size of source in result
	 */ 
	public void doubleSize() {
		//loop through all pixels in source and put the same pixel in the 4 corresponding locations in result;
		for(int i = number; i < source.getHeight(); i+=threads)
        	for(int j = 0; j < source.getWidth(); j++) {
        		int[] temp = getPixel(i, j);
        		setPixel(i*2, j*2, temp[0], temp[1], temp[2]);
        		setPixel(i*2+1, j*2, temp[0], temp[1], temp[2]);
        		setPixel(i*2, j*2+1, temp[0], temp[1], temp[2]);
        		setPixel(i*2+1, j*2+1, temp[0], temp[1], temp[2]);
        	}
    }
	
	/**
	 *	puts source rotated by 90 degrees to the right in result
	 */ 
	public void rotate() {
		//loop through all pixels of source and put them in to result, but swap row and column position
		//also flips the order of rows
		for(int i = number; i < source.getHeight(); i+=threads)
        	for(int j = 0; j < source.getWidth(); j++) {
        		int[] temp = getPixel(i, j);
        		setPixel(result.getHeight() - j - 1, i, temp[0], temp[1], temp[2]);
        	}
    }
	
	/**
	 * sets a pixel in result
	 *
	 * @param  i		row of pixel to set
	 * @param  j		column of pixel to set
	 * @param  blue		blue value of the pixel
	 * @param  green	green value of the pixel
	 * @param  red		red value of the pixel
	 */
	public void setPixel(int i, int j, int blue, int green, int red) {
		result.setByte(i, j*3, (byte)blue);
		result.setByte(i, j*3+1, (byte)green);
		result.setByte(i, j*3+2, (byte)red);
	}
	
	/**
	 * gets a pixel from source
	 *
	 * @param  i		row of pixel to get
	 * @param  j		column of pixel to get
	 * @return returns array containing three ints: the blue, green and red value of the pixel
	 */
	public int[] getPixel(int i, int j) {
		//get the byte from the data array
		//and change it from signed to unsigned
		int[] temp = new int[3];
		temp[0] = source.getByte(i, j*3);
		temp[0] += (temp[0] < 0) ? 256 : 0;
		temp[1] = source.getByte(i, j*3+1);
		temp[1] += (temp[1] < 0) ? 256 : 0;
		temp[2] = source.getByte(i, j*3+2);
		temp[2] += (temp[2] < 0) ? 256 : 0;
		return temp;
	}
}
