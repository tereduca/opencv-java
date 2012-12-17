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
import sj.opencv.CxCore;
import sj.opencv.HighGui;
import sj.opencv.HighGui.CaptureProperty;
import sj.opencv.Capture;
import sj.opencv.IplImage;
import sj.opencv.PUtils;
import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;

/**
 * @author siggi
 * @date Jul 29, 2010
 */
public class CaptureFromMovie extends PApplet{

	IplImage im;
	Capture capture;

	int movie_frames;
	boolean seek = false;



	@Override
	public void setup(){

		// Camera initiated to capture from device
		capture = HighGui.captureFromFile( PUtils.guessDataLocation(this, "media/i_am_ironman.mov") );

		int mov_width = (int)HighGui.getCaptureProperty(capture, CaptureProperty.CV_CAP_PROP_FRAME_WIDTH);
		int mov_height = (int)HighGui.getCaptureProperty(capture, CaptureProperty.CV_CAP_PROP_FRAME_HEIGHT);
		int fps = (int)HighGui.getCaptureProperty(capture, CaptureProperty.CV_CAP_PROP_FPS);
		movie_frames = (int)HighGui.getCaptureProperty(capture, CaptureProperty.CV_CAP_PROP_FRAME_COUNT);

		// Do size here so we can initialize to the size of the movie
		size(mov_width, mov_height);

		// Set the fps of our applet to  the movie fps
		frameRate( fps );

		im = CxCore.createImage(mov_width, mov_height, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);

		System.out.println("Move mouse across width to seek to a different frame");
		System.out.println("Press 't' to toggle playback mode and seek mode");
	}

	@Override
	public void draw(){
		// When a frame becomes available

		if( seek ) HighGui.setCaptureProperty(capture, CaptureProperty.CV_CAP_PROP_POS_FRAMES, (float)mouseX/(float)width * movie_frames);

		if( HighGui.queryFrame(capture, im) ){
			// Draw it
			image(PUtils.getPImage(im), 0, 0);

		}
	}

	@Override
	public void mouseDragged() {
		HighGui.setCaptureProperty(capture, CaptureProperty.CV_CAP_PROP_POS_AVI_RATIO, mouseX / (float)width);
	}

	@Override
	public void keyPressed(){
		if( key == 't' ){
			seek ^= true;
		}
	}
}
