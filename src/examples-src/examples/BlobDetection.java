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

import java.util.List;

import controlP5.ControlP5;
import controlP5.Slider;

import processing.core.PApplet;
import sj.opencv.*;
import sj.opencv.ImgProc.*;
import sj.opencv.Constants.*;

/**
 * @author siggi
 * @date Jul 29, 2010
 */
public class BlobDetection extends PApplet{
	
	int w = 320;
	int h = 240;

	IplImage im;
	IplImage im_thresh;
	Capture capture;

	// Slider values
	Slider thresh_val;
	Slider min_area;
	Slider max_area;

	@Override
	public void setup(){
		size(4*w, h+150);

		// Camera initiated to capture from device
		capture = HighGui.captureFromCAM(0);

		im = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
		im_thresh = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.GRAY);

		// Init gui
		ControlP5 controlP5 = new ControlP5(this);
		thresh_val = controlP5.addSlider("thresh_val", 	0, 255, 	100,	20, 		h+20, 	10, 	100);
		min_area = controlP5.addSlider("min_area", 		0, 1000, 	10,		20 +80, 	h+20, 	10, 	100);
		max_area = controlP5.addSlider("max_area", 		0, 100000, 	10000,	20 +2*80, 	h+20, 	10, 	100);
	}

	@Override
	public void draw(){
		// When a frame becomes available
		if( HighGui.queryFrame(capture, im) ){
			background(70);

			// Draw it
			image(PUtils.getPImage(im), 0, 0);

			ImgProc.cvtColor(im, im_thresh);
			image(PUtils.getPImage(im_thresh), w, 0);

			ImgProc.threshold(im_thresh, im_thresh, thresh_val.value(), 255, ThresholdType.CV_THRESH_BINARY);
			image(PUtils.getPImage(im_thresh), 2*w, 0);

			// Do the blob detection
			List<Blob> blobs = OpenCV.detectBlobs(im_thresh, (int)min_area.value(), (int)max_area.value(), false, 1024);
			if(blobs == null) return;

			System.out.println();
			for (Blob blob : blobs) {
				System.out.println(blob.area);
			}

			// Draw blobs
			fill(color(255));
			int offs_x = 3*w;
			int offs_y = 0;
			rect(offs_x, offs_y, w, h);
			for (Blob blob : blobs) {
				noStroke();
				fill(color(200, 0, 0));
				beginShape();
				for( int j=0; j<blob.points.length; j++ ) {
					vertex( offs_x + blob.points[j].x, offs_y + blob.points[j].y );
				}
				endShape(CLOSE);

				// Draw major axis
				stroke(color(0,0,255));
				fill(color(0,0,255));
				line( offs_x + blob.centroid.x, offs_y + blob.centroid.y, offs_x + blob.centroid.x + blob.major_axis_length*(float)Math.cos(blob.major_axis_angle), offs_y + blob.centroid.y  + blob.major_axis_length*(float)Math.sin(blob.major_axis_angle) );
				line( offs_x + blob.centroid.x, offs_y + blob.centroid.y, offs_x + blob.centroid.x + blob.major_axis_length*(float)Math.cos(blob.major_axis_angle+Math.PI), offs_y + blob.centroid.y  + blob.major_axis_length*(float)Math.sin(blob.major_axis_angle+Math.PI) );

				// Draw minor axis
				stroke(color(0,255,255));
				fill(color(0,255,255));
				line( offs_x + blob.centroid.x, offs_y + blob.centroid.y, offs_x + blob.centroid.x + blob.minor_axis_length*(float)Math.cos(blob.major_axis_angle+Math.PI/2), offs_y + blob.centroid.y  + blob.minor_axis_length*(float)Math.sin(blob.major_axis_angle+Math.PI/2) );
				line( offs_x + blob.centroid.x, offs_y + blob.centroid.y, offs_x + blob.centroid.x + blob.minor_axis_length*(float)Math.cos(blob.major_axis_angle-Math.PI/2), offs_y + blob.centroid.y  + blob.minor_axis_length*(float)Math.sin(blob.major_axis_angle-Math.PI/2) );

			}
		}
	}
}
