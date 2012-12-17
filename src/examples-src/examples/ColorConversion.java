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
import processing.core.PApplet;
import processing.core.PImage;
import sj.opencv.Capture;
import sj.opencv.CxCore;
import sj.opencv.HighGui;
import sj.opencv.ImgProc;
import sj.opencv.IplImage;
import sj.opencv.PUtils;
import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;

/**
 * @author siggi
 * @date Jul 29, 2010
 */
public class ColorConversion extends PApplet{

	int w = 640;
	int h = 480;

	IplImage im_bgr;
	IplImage im_gray;
	IplImage im_bgra;

	Capture capture;

	@Override
	public void setup(){
		size(3*w, h);

		// Camera initiated to capture from device
		capture = HighGui.captureFromCAM(0);

		im_bgr = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
		im_gray = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.GRAY);
		im_bgra = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGRA);
	}

	PImage im = new PImage(w, h, ARGB);

	@Override
	public void draw(){
		// When a frame becomes available
		if( HighGui.queryFrame(capture, im_bgr) ){

			PUtils.getPImage(im_bgr, im);
			// Draw it
			image(im, 0, 0);

			// Draw gray image
			ImgProc.cvtColor(im_bgr, im_gray);
			image(PUtils.getPImage(im_gray), w, 0);

			// Draw gray bgra image
			ImgProc.cvtColor(im_gray, im_bgra);
			image(PUtils.getPImage(im_bgra), 2*w, 0);
		}
	}
}
