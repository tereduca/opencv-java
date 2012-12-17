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
 
package examples;

import java.util.Arrays;


import processing.core.PApplet;
import sj.opencv.Capture;
import sj.opencv.CxCore;
import sj.opencv.HighGui;
import sj.opencv.IplImage;
import sj.opencv.PUtils;
import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;

public class ImageManipulation extends PApplet {

	int w = 320;
	int h = 240;

	IplImage im;
	Capture capture;
	byte[] pixel;

	@Override
	public void setup(){
		size(w, h);

		// Camera initiated to capture from device
		capture = HighGui.captureFromCAM(0);

		im = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
		pixel = new byte[PixelDepth.IPL_DEPTH_8U.getBytesPerPixel() * ColorModel.BGR.getNumberOfChannels()];
	}

	@Override
	public void draw(){
		// When a frame becomes available
		if( HighGui.queryFrame(capture, im) ){

			// Loop through all pixels, and change the value of the red channel to 255
			for(int x=0 ; x < im.getWidth(); x++) {
				for(int y=0 ; y < im.getHeight() ; y++) {
					im.getBytePixel(x,y, pixel);
					pixel[2] = (byte) 255;
					im.setBytePixel(x,y, pixel);
				}
			}

			// Set a black pixel
			Arrays.fill(pixel, (byte) 0);

			// Draw vertical line every 10 lines
			for (int x=0 ; x < im.getWidth() ; x++) {
				for (int y=0 ; y < im.getHeight() ; y++) {
					if (x % 10 == 0) {
						im.setBytePixel(x, y, pixel);
					}
				}
			}

			// Draw it
			image(PUtils.getPImage(im), 0, 0);
		}
	}
}
