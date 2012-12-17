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

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import processing.core.PApplet;
import sj.opencv.Calib3D;
import sj.opencv.Calib3D.CalibrationFlag;
import sj.opencv.Capture;
import sj.opencv.CxCore;
import sj.opencv.HighGui;
import sj.opencv.IplImage;
import sj.opencv.PUtils;
import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;


public class FindAndDrawCorners extends PApplet {

	int w = 320;
	int h = 240;

	IplImage im;
	Capture capture;

	CalibrationFlag[] findCornersFlags = new CalibrationFlag[]{
			CalibrationFlag.CV_CALIB_CB_ADAPTIVE_THRESH,
			CalibrationFlag.CV_CALIB_CB_NORMALIZE_IMAGE};

	Dimension patternDim = new Dimension(8,6);

	@Override
	public void setup(){
		size(w, h);

		// Camera initiated to capture from device
		capture = HighGui.captureFromCAM(0);
		im = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
	}

	@Override
	public void draw(){
		// When a frame becomes available
		if( HighGui.queryFrame(capture, im) ){

			Point2D.Float[] corners = Calib3D.findChessboardCorners(im, patternDim, findCornersFlags);
			if( corners != null ){
				Calib3D.drawChessboardCorners(im, patternDim, corners, true);
			}

			// Draw it
			image(PUtils.getPImage(im), 0, 0);
		}
	}

}
