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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import com.sun.jna.Pointer;

import sj.opencv.jna.cxcore.CvBox2D;
import sj.opencv.jna.cxcore.CvFont;
import sj.opencv.jna.cxcore.CvMemStorage;
import sj.opencv.jna.cxcore.CvPoint;
import sj.opencv.jna.cxcore.CvPoint2D32f;
import sj.opencv.jna.cxcore.CvRect;
import sj.opencv.jna.cxcore.CvScalar;
import sj.opencv.jna.cxcore.CvSeq;
import sj.opencv.jna.cxcore.CvSize;
import sj.opencv.jna.cxcore.CvSize.ByValue;
import sj.opencv.jna.cxcore.CvSize2D32f;
import sj.opencv.jna.cxcore.CxcoreLibrary;
import sj.opencv.jna.highgui.HighguiLibrary.CvCapture;
import sj.opencv.jna.objdetect.CvHaarClassifierCascade;
import sj.opencv.jna.objdetect.ObjdetectLibrary;
import static sj.opencv.jna.JNAOpenCV.*;

/**
 * @author siggi
 * @date Jul 5, 2012
 */
public class ObjDetect {

	static{
		OpenCV.initialize();
	}

	/* ******************************************************************************
	 *  							OBJECT DETECTION                                *
	 * ******************************************************************************/

    public enum HaarClassifierFlag{
    	CV_HAAR_FEATURE_MAX	( ObjdetectLibrary.CV_HAAR_FEATURE_MAX ),
    	CV_HAAR_DO_CANNY_PRUNING	( ObjdetectLibrary.CV_HAAR_DO_CANNY_PRUNING ),
    	CV_HAAR_FIND_BIGGEST_OBJECT	( ObjdetectLibrary.CV_HAAR_FIND_BIGGEST_OBJECT ),
    	CV_HAAR_MAGIC_VAL	( ObjdetectLibrary.CV_HAAR_MAGIC_VAL ),
    	CV_HAAR_DO_ROUGH_SEARCH	( ObjdetectLibrary.CV_HAAR_DO_ROUGH_SEARCH ),
    	CV_HAAR_SCALE_IMAGE	( ObjdetectLibrary.CV_HAAR_SCALE_IMAGE );

        private final int open_cv_constant;
        HaarClassifierFlag(int constant){this.open_cv_constant=constant;}
        public final int getConstant(){return open_cv_constant;};
    }

	/**
	 * Loads a trained cascade of Haar classifiers from a file.
	 *
	 * @param filename - The name of the file containing the Haar classifier cascade.
	 * @return A <code>HaarClassifierCascade</code> object.
	 */
	public static HaarClassifierCascade loadHaarClassifierCascade(String filename) {
		Pointer cvLoad = CXCORE.cvLoad(filename, null, null, null);
		CvHaarClassifierCascade casc = new CvHaarClassifierCascade(cvLoad);
		casc.read();

		HaarClassifierCascade ret = new  HaarClassifierCascade(casc);

		return ret;
	}

	/**
	 * Default version of <code>haarDetectObjects</code> with parameters suited
	 * for object detection in video images.
	 *
	 * @param img - The image to detect objects in.
	 * @param cascade - A Haar classifier cascade object.
	 * @param storage - Memory storage to store the resultant sequence of the object
	 * candidate rectangles.
	 * @return An array with the rectangles containing the detected objects.
	 */
	public static Rectangle[] haarDetectObjects(IplImage img, HaarClassifierCascade cascade, MemStorage storage) {
		return haarDetectObjects(img, cascade, storage, 1.2, 2, new HaarClassifierFlag[]{HaarClassifierFlag.CV_HAAR_DO_CANNY_PRUNING}, new Dimension(5,5), new Dimension(300,300));
	}

	/**
	 * Detects objects the cascade has been trained for and returns those regions as a
	 * sequence of rectangles.
	 *
	 * @param img - The image to detect objects in.
	 * @param cascade - A Haar classifier cascade object.
	 * @param storage - Memory storage to store the resultant sequence of the object
	 * candidate rectangles.
	 * @param scaleFactor - The factor by which the search window is scaled between the subsequent scans.
	 * @param minNeighbors - Minimum number of neighbor rectangles that makes up an object.
	 * @param flags - Mode of operation. Currently the only flag that may be specified is
	 * <code>CV_HAAR_DO_CANNY_PRUNING</code>.
	 * @param minSize - Minimum window size.
	 * @return An array with the rectangles containing the detected objects.
	 */
	public static Rectangle[] haarDetectObjects(IplImage img, HaarClassifierCascade cascade, MemStorage storage, double scaleFactor, int minNeighbors, HaarClassifierFlag[] flags, Dimension minSize, Dimension maxSize) {
		int flags_int = 0;
		for (HaarClassifierFlag f : flags) {
			flags_int = flags_int | f.getConstant();
		}

		CvSeq cvHaarDetectObjects = OBJDETECT.cvHaarDetectObjects(img.getCvArr(), cascade.getJNACvHaarCascade(), storage.getJNACvMemStorage(), scaleFactor, minNeighbors, flags_int, new CvSize.ByValue(minSize.width, minSize.height), new CvSize.ByValue(maxSize.width, maxSize.height));

		System.out.println();
		Rectangle[] ret = new Rectangle[cvHaarDetectObjects.total];
		for(int i=0; i<cvHaarDetectObjects.total; i++){
			Pointer cvGetSeqElem = CXCORE.cvGetSeqElem(cvHaarDetectObjects, i);
			CvRect rect = new CvRect(cvGetSeqElem);
			rect.read();
			ret[i] = new Rectangle(rect.x, rect.y, rect.width, rect.height);
		}

		return ret;
	}
}
