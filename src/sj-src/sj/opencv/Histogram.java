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

import sj.opencv.jna.JNAOpenCV;
import sj.opencv.jna.cxcore.CvHistogram;
import sj.opencv.jna.highgui.HighguiLibrary.CvArr;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;


public class Histogram extends BasePointer {

	CvHistogram hist;

	protected Histogram(CvHistogram hist) {
		super(hist.getPointer());
	}

	@Override
	protected void deAllocateNativeResource() {
		JNAOpenCV.IMGPROC.cvReleaseHist( new PointerByReference(hist.getPointer()) );
	}
	
	public CvArr getCvArr() {
		return hist.bins;
	}
}
