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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



import processing.core.PApplet;
import sj.opencv.Capture;
import sj.opencv.CxCore;
import sj.opencv.HighGui;
import sj.opencv.IplImage;
import sj.opencv.PUtils;
import sj.opencv.Scalar;
import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;


public class Drawing extends PApplet {

	int w = 320;
	int h = 240;

	IplImage im;
	Capture capture;

	@Override
	public void setup(){
		size(w, 2*h);

		// Camera initiated to capture from device
		capture = HighGui.captureFromCAM(0);

		im = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
	}

	@Override
	public void draw(){
		// When a frame becomes available
		if( HighGui.queryFrame(capture, im) ){
			Scalar blue = new Scalar(255, 0, 0);

			Point[] triangle = new Point[3];
			triangle[0] = new Point(100,20);
			triangle[1] = new Point(100,80);
			triangle[2] = new Point(200,20);
			CxCore.polyLine(im, triangle, true, blue);

			Point[] square = new Point[4];
			square[0] = new Point(100,100);
			square[1] = new Point(100,200);
			square[2] = new Point(200,200);
			square[3] = new Point(200,100);
			CxCore.polyLine(im, square, true, blue);


			// Draw it
			image(PUtils.getPImage(im), 0, 0);

			Scalar red = new Scalar(0, 0, 255);
			CxCore.fillPoly(im, triangle, red);
			CxCore.fillPoly(im, square, red);

			// Draw it
			image(PUtils.getPImage(im), 0, h);
		}
	}

}
