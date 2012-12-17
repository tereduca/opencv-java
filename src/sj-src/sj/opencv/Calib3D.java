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
import java.awt.geom.Point2D;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static sj.opencv.jna.JNAOpenCV.*;

import sj.opencv.jna.calib3d.Calib3dLibrary;
import sj.opencv.jna.cxcore.CvMat;
import sj.opencv.jna.cxcore.CvPoint2D32f;
import sj.opencv.jna.cxcore.CvSize;

/**
 * @author siggi
 * @date Jul 9, 2012
 */
public class Calib3D {

	static{
		OpenCV.initialize();
	}

	public enum CalibrationFlag {
		CV_CALIB_CB_FILTER_QUADS	( Calib3dLibrary.CV_CALIB_CB_FILTER_QUADS ),
		CV_CALIB_USE_INTRINSIC_GUESS	( Calib3dLibrary.CV_CALIB_USE_INTRINSIC_GUESS ),
		CV_CALIB_FIX_ASPECT_RATIO	( Calib3dLibrary.CV_CALIB_FIX_ASPECT_RATIO ),
		CV_CALIB_FIX_INTRINSIC	( Calib3dLibrary.CV_CALIB_FIX_INTRINSIC ),
		CV_CALIB_FIX_PRINCIPAL_POINT	( Calib3dLibrary.CV_CALIB_FIX_PRINCIPAL_POINT ),
		CV_CALIB_FIX_FOCAL_LENGTH	( Calib3dLibrary.CV_CALIB_FIX_FOCAL_LENGTH ),
		CV_CALIB_ZERO_TANGENT_DIST	( Calib3dLibrary.CV_CALIB_ZERO_TANGENT_DIST ),
		CV_CALIB_SAME_FOCAL_LENGTH	( Calib3dLibrary.CV_CALIB_SAME_FOCAL_LENGTH ),
		CV_CALIB_CB_NORMALIZE_IMAGE	( Calib3dLibrary.CV_CALIB_CB_NORMALIZE_IMAGE ),
		CV_CALIB_ZERO_DISPARITY	( Calib3dLibrary.CV_CALIB_ZERO_DISPARITY ),
		CV_CALIB_CB_ADAPTIVE_THRESH	( Calib3dLibrary.CV_CALIB_CB_ADAPTIVE_THRESH ),
		CV_CALIB_RATIONAL_MODEL	( Calib3dLibrary.CV_CALIB_RATIONAL_MODEL ),
		CV_CALIB_CB_FAST_CHECK	( Calib3dLibrary.CV_CALIB_CB_FAST_CHECK ),
		CV_CALIB_FIX_K6	( Calib3dLibrary.CV_CALIB_FIX_K6 ),
		CV_CALIB_FIX_K5	( Calib3dLibrary.CV_CALIB_FIX_K5 ),
		CV_CALIB_FIX_K4	( Calib3dLibrary.CV_CALIB_FIX_K4 ),
		CV_CALIB_FIX_K3	( Calib3dLibrary.CV_CALIB_FIX_K3 ),
		CV_CALIB_FIX_K2	( Calib3dLibrary.CV_CALIB_FIX_K2 ),
		CV_CALIB_FIX_K1	( Calib3dLibrary.CV_CALIB_FIX_K1 );

        private final int open_cv_constant;
        CalibrationFlag(int constant) {this.open_cv_constant = constant;}
    }

	/**
	 * Finds chessboard pattern of patternSize in frame and returns the list of detected corners
	 * @param img
	 * @param patternSize
	 * @param flags
	 * @return
	 */
	public static Point2D.Float[] findChessboardCorners(IplImage img, Dimension patternSize, CalibrationFlag[] flags) {
		int intflags = 0;
		for (CalibrationFlag f : flags) {
			intflags = intflags | f.open_cv_constant;
		}

		ByteBuffer cornerCount_buf = ByteBuffer.allocateDirect(Integer.SIZE/8);
		cornerCount_buf.order(ByteOrder.nativeOrder());

		CvPoint2D32f corners_pointer = new CvPoint2D32f();
		int success = CALIB3D.cvFindChessboardCorners(img.getPointer(), new CvSize.ByValue(patternSize.width, patternSize.height), corners_pointer, cornerCount_buf.asIntBuffer(), intflags);
		if( success == 0 ) return null;

		int count = cornerCount_buf.asIntBuffer().get();
		CvPoint2D32f[] cornercv = (CvPoint2D32f[]) corners_pointer.toArray(count);
		Point2D.Float[] out = new Point2D.Float[count];
		for(int i = 0; i < count; i++){
			out[i] = new Point2D.Float(cornercv[i].x, cornercv[i].y);
		}

		return out;
	}

	/**
	 * Draws the chessboard corners and patterns onto the image for visual verification
	 * @param img
	 * @param patternSize
	 * @param corners
	 * @param patternWasFound
	 */
	public static void drawChessboardCorners(IplImage img, Dimension patternSize, Point2D.Float[] corners, boolean patternWasFound) {
		CvPoint2D32f corner_pt = new CvPoint2D32f();
		CvPoint2D32f[] cornercv = (CvPoint2D32f[]) corner_pt.toArray(corners.length);
		for(int i=0; i<corners.length; i++){
			cornercv[i].x = corners[i].x;
			cornercv[i].y = corners[i].y;
	}

		CALIB3D.cvDrawChessboardCorners(img.getCvArr(), new CvSize.ByValue(patternSize.width, patternSize.height), corner_pt, corners.length, patternWasFound?1:0);
	}

	public static void calibrateCamera2(float[] objectPoints, float[] imagePoints, int[] pointCounts, Dimension imageSize, float[] cameraMatrix, float[] distortion, float[] rotations, float[] translations, CalibrationFlag[] flags) {
		int intflags = 0;
		for (CalibrationFlag f : flags) {
			intflags = intflags | f.open_cv_constant;
		}

		int nExamples = pointCounts.length;

		CvMat pointCountsMat = CxCore.createMat(nExamples, 1, pointCounts).getJNACvMat();
		CvMat cameraMatrixMat = CxCore.createMat(3,3, cameraMatrix).getJNACvMat();
		CvMat distortionMat = CxCore.createMat(distortion.length, 1, distortion).getJNACvMat();

		int totalPoints = 0;
		for(int i = 0; i < nExamples; i++){
			totalPoints += pointCounts[i];
		}

		CvMat imagePointsMat = CxCore.createMat(totalPoints, 1,imagePoints).getJNACvMat();
		CvMat objectPointsMat = CxCore.createMat(totalPoints, 1, objectPoints).getJNACvMat();
		CvMat rotationsMat = null;
		CvMat translationsMat = null;

		if(rotations != null) rotationsMat = CxCore.createMat(nExamples, 3, rotations).getJNACvMat();
		if(translations != null) translationsMat = CxCore.createMat(nExamples, 3, translations).getJNACvMat();

		CALIB3D.cvCalibrateCamera2(objectPointsMat, imagePointsMat, pointCountsMat, new CvSize.ByValue(imageSize.width, imageSize.height), cameraMatrixMat, distortionMat, rotationsMat, translationsMat, intflags, null);
	}

	/**
	 * This function performs the Rodrigues transform. It computes a rotation matrix
	 * from a rotation vector, and vice versa. To convert from vector to matrix,
	 * set <code>src</code> to be the 3-by-1 vector, <code>dst</code> to be the
	 * 3-by-3 matrix, and <code>mode</code> to be <code>CV_RODRIGUES_V2M</code>.
	 * To convert from matrix to vector, set <code>src</code> to be the 3-by-3 matrix,
	 * <code>dst</code> to be the 3-by-1 vector, and <code>mode</code> to be
	 * <code>CV_RODRIGUES_M2V</code>.
	 *
	 * @param src - Either a 3-by-1 vector or a 3-by-3 matrix.
	 * @param dst - Either a 3-by-3 matrix or a 3-by-1 vector.
	 * @param mode - And integer indicating the operation mode, i.e. either <code>RODRIGUES_VECTOR_TO_MATRIX</code> or <code>RODRIGUES_MATRIX_TO_VECTOR</code>.
	 */
	public static void rodrigues2(float[] src_arr, float[] dst_arr, float[] jacobian_arr) {
		CvMat src = null;
		CvMat dst = null;

		if (src_arr.length == 3) {
			src = CxCore.createMat(3, 1, src_arr).getJNACvMat();
			dst = CxCore.createMat(3, 3, dst_arr).getJNACvMat();
		} else if (src_arr.length == 9) {
			src = CxCore.createMat(3, 3, src_arr).getJNACvMat();
			dst = CxCore.createMat(3, 1, dst_arr).getJNACvMat();
		}
		else{
			throw new RuntimeException("src_arr needs to be either dim(3) or dim(9)");
		}

		if (jacobian_arr != null) {
			CvMat jacobian = CxCore.createMat(9, 3, jacobian_arr).getJNACvMat();
			CALIB3D.cvRodrigues2(src, dst, jacobian);
		} else {
			CALIB3D.cvRodrigues2(src, dst, null);
		}
	}
}
