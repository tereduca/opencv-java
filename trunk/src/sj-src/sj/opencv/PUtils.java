/* Description and License
 * A Java library that wraps the functionality of the native image 
 * processing library OpenCV
 *
 * (c) Sigurdur Orn Adalgeirsson (siggi@alum.mit.edu)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 */
 
package sj.opencv;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import sj.opencv.Constants.ColorModel;

/**
 * @author siggi
 * @date Sep 26, 2011
 */
public class PUtils {

	private static boolean DEBUG = false;

	private static String FILE_SEPARATOR = System.getProperty("file.separator");

	// Mem saving variables
	private static PImage temp_out = null;
	private static IplImage temp_im = null;

	public static PImage getPImage(IplImage im){
		if( temp_out == null ){
			temp_out = new PImage(im.getWidth(), im.getHeight(), PConstants.ARGB);
		}
		else if(temp_out.width != im.getWidth() || temp_out.height != im.getHeight()){
			temp_out = new PImage(im.getWidth(), im.getHeight(), PConstants.ARGB);
		}

		return getPImage(im, temp_out);
	}

	public static PImage getPImage(IplImage im, PImage out){
		if( im.getWidth() != out.width || im.getHeight() != out.height ){
			throw new RuntimeException("Can't get PImage because dimensions don't match");
		}

		IplImage im_to_use = null;

		// If the input image is a GENERIC_4_CHANNEL then we assume it is already in ARGB format
		if( im.getColorModel() == ColorModel.GENERIC_4_CHANNEL ){
			im_to_use = im;
		}
		// Otherwise we have to spend a bit more time converting color models
		else{
			if( temp_im == null ){
				temp_im = CxCore.createImage(im.getWidth(), im.getHeight(), im.getPixelDepth(), ColorModel.GENERIC_4_CHANNEL);
			}
			else if(temp_im.getWidth() != im.getWidth() || temp_im.getHeight() != im.getHeight()){
				temp_im.deAllocate();
				temp_im = CxCore.createImage(im.getWidth(), im.getHeight(), im.getPixelDepth(), ColorModel.GENERIC_4_CHANNEL);
			}

			im_to_use = temp_im;
			OpenCV.convert2ARGB(im, temp_im);
		}

		// Here we transfer the pixel data
		IntBuffer.wrap(out.pixels).put( im_to_use.getByteBuffer().asIntBuffer() );
		out.updatePixels();

		return out;
	}


	/**
	 * Converts an IPLImage to a PImage for processing
	 * @param im
	 * @return
	 */
	public static PImage getPImageOld(IplImage im){

		PImage pim = new PImage(im.getWidth(), im.getHeight(), PConstants.ARGB);
		int pixel_width_step = im.getWidthStep()/im.getColorModel().getNumberOfChannels();

		if( im.getColorModel().getNumberOfChannels() == 4 ){
			int ind = 0;
			for(int y=0; y<im.getHeight(); y++){
				for(int x=0; x<im.getWidth(); x++){
					int im_ind = y*im.getWidthStep() + x*im.getColorModel().getNumberOfChannels();
					byte hh = im.getByteBuffer().get( im_ind );
					byte hl = im.getByteBuffer().get( im_ind+1 );
					byte lh = im.getByteBuffer().get( im_ind+2 );
					byte ll = im.getByteBuffer().get( im_ind+3 );
					pim.pixels[ind++] = ((ll&0xff) << 24) | ((lh&0xff) << 16) | ((hl&0xff) << 8) | (hh&0xff);
				}
			}
		}
		else if( im.getColorModel().getNumberOfChannels() == 3 ){
			int ind = 0;
			for(int y=0; y<im.getHeight(); y++){
				for(int x=0; x<im.getWidth(); x++){
					int im_ind = y*im.getWidthStep() + x*im.getColorModel().getNumberOfChannels();
					byte hh = im.getByteBuffer().get(im_ind);
					byte hl = im.getByteBuffer().get(im_ind+1);
					byte lh = im.getByteBuffer().get(im_ind+2);
					pim.pixels[ind++] = (255 << 24) | ((lh&0xff) << 16) | ((hl&0xff) << 8) | (hh&0xff);
				}
			}
		}
		else if( im.getColorModel().getNumberOfChannels() == 1 ){
			int ind = 0;
			for(int y=0; y<im.getHeight(); y++){
				for(int x=0; x<im.getWidth(); x++){
					byte hh = im.getByteBuffer().get(y*pixel_width_step + x*im.getColorModel().getNumberOfChannels());
					pim.pixels[ind++] = (255 << 24) | ((hh&0xff) << 16) | ((hh&0xff) << 8) | (hh&0xff);
				}
			}
		}
		else{
			throw new RuntimeException("IplImage color format wrong");
		}
		pim.updatePixels();
		return pim;
	}

	/**
	 * This method figures out if we are being called from a java application or a processing application (from the pde) and
	 * gives us a guess for an absolute path of a resource that we need
	 * @param applet
	 * @param filename
	 * @return
	 */
	public static String guessDataLocation(PApplet applet, String filename){
		// We start by figuring out if we are being launched by the processing viewer or just a java application
		if( applet.args == null || applet.args.length == 0){
			// We are probably in a java application
			String project = guessProjectFolderLocation();
			if( project.endsWith(FILE_SEPARATOR) ){
				return project + "res" + FILE_SEPARATOR + filename;
			}
			else{
				return project + FILE_SEPARATOR + "res" + FILE_SEPARATOR + filename;
			}
		}
		else{
			// We are probably in a Processing application
			return applet.dataPath(filename);
		}
	}

	/**
	 * Returns the folder from which this code was loaded
	 * @return
	 */
	public static String getExecutionFolder(){
		try {
			String path = new File(".").getCanonicalPath();
			return path + "/";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method tries to determine the root folder of the code project by looking for the src folder relative to the class files
	 * @return
	 */
	public static String guessProjectFolderLocation(){
		String path = getExecutionFolder();

		if( DEBUG ) System.err.println("Looking in path: " +path);

		String[] folders_to_look_for = new String[]{"res","src","lib"};

		for(int level = 0; level < 3; level++){
			// Check to see if current folder contains the src folder
			for (String folder_to_look_for : folders_to_look_for) {
				if( DEBUG ) System.err.println("looking for: "+path + folder_to_look_for);
				if( new File(path + folder_to_look_for ).exists() && new File(path + folder_to_look_for ).isDirectory() ){
					return path;
				}
			}

			// Also we check a special location used in development
			if( new File(path + "java-src/examples-src" ).exists() && new File(path + "java-src/examples-src" ).isDirectory() ){
				return path;
			}

			// Next we try one directory below us
			path = new File(path).getParent() + FILE_SEPARATOR;
		}

		return null;
	}

	/**
	 * This is a fix for a problem where processing sometimes doesn't stop capturing from a camera right as a shutdown signal is sent
	 * @param papplet
	 */
	public static void installStopperShutdownHook(PApplet papplet){
		Runtime.getRuntime().addShutdownHook(new Thread(new StopThread(papplet)));
	}

	private static class DummyClass{}

	private static class StopThread implements Runnable{

		private PApplet papplet;

		public StopThread(PApplet papplet){
			this.papplet = papplet;
		}

		@Override
		public void run() {
			papplet.stop();
		}
	}

}
