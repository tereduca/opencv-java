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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;
import sj.opencv.Constants.TermCriteriaType;
import sj.opencv.jna.cxcore.CvBox2D;
import sj.opencv.jna.cxcore.CvFont;
import sj.opencv.jna.cxcore.CvMat;
import sj.opencv.jna.cxcore.CvMemStorage;
import sj.opencv.jna.cxcore.CvPoint;
import sj.opencv.jna.cxcore.CvPoint2D32f;
import sj.opencv.jna.cxcore.CvRect;
import sj.opencv.jna.cxcore.CvScalar;
import sj.opencv.jna.cxcore.CvScalar.ByValue;
import sj.opencv.jna.cxcore.CvSize;
import sj.opencv.jna.cxcore.CvSize2D32f;
import sj.opencv.jna.cxcore.CvTermCriteria;
import sj.opencv.jna.cxcore.CxcoreLibrary;
import sj.opencv.jna.cxcore.JNAIplImage;
import sj.opencv.jna.highgui.HighguiLibrary.CvArr;

import static sj.opencv.jna.JNAOpenCV.*;

/**
 * @author siggi
 * @date Jul 5, 2012
 */
public class CxCore {

	static{
		OpenCV.initialize();
	}

	/**
	 * Creates a CvMat of type CV_32SC1, backed by a java buffer
	 * @param rows
	 * @param cols
	 * @param row_aligned_mat
	 * @return
	 */
	public static Mat createMat(int rows, int cols, int[] row_aligned_mat){
		ByteBuffer bb = ByteBuffer.allocateDirect( row_aligned_mat.length * Integer.SIZE/8 );
		bb.order(ByteOrder.nativeOrder());
		bb.asIntBuffer().put(row_aligned_mat);

		CvMat mat_header = CXCORE.cvCreateMatHeader(rows, cols, CxcoreLibrary.CV_32SC1);
		CXCORE.cvSetData(new CvArr(mat_header.getPointer()), Native.getDirectBufferPointer(bb), 0);
		mat_header.read();
		return new Mat(mat_header);
	}

	/**
	 * Creates a CvMat of type CV_32FC1, backed by a java buffer
	 * @param rows
	 * @param cols
	 * @param row_aligned_mat
	 * @return
	 */
	public static Mat createMat(int rows, int cols, float[] row_aligned_mat){
		ByteBuffer bb = ByteBuffer.allocateDirect( row_aligned_mat.length * Float.SIZE/8 );
		bb.order(ByteOrder.nativeOrder());
		bb.asFloatBuffer().put(row_aligned_mat);

		CvMat mat_header = CXCORE.cvCreateMatHeader(rows, cols, CxcoreLibrary.CV_32FC1);
		CXCORE.cvSetData(new CvArr(mat_header.getPointer()), Native.getDirectBufferPointer(bb), 0);
		mat_header.read();
		return new Mat(mat_header);
	}


	/* ******************************************************************************
	 *  						DATA ACCESS METHODS	                                *
	 * ******************************************************************************/

	/**
	 * Return a specific array element.
	 *
	 * @param img - the input array
	 * @param idx0 - the first zero-based component of the element index
	 * @return the array element
	 */
	public static Scalar get1D(IplImage img, int idx0) {
		ByValue cvGet1D = CXCORE.cvGet1D(img.getCvArr(), idx0);
		return new Scalar(cvGet1D.val);
	}

	/**
	 * Return a specific array element.
	 *
	 * @param img - the input array
	 * @param idx0 - the first zero-based component of the element index
	 * @param idx1 - the second zero-based component of the element index
	 * @return the array element
	 */
	public static Scalar get2D(IplImage img, int idx0, int idx1) {
		ByValue cv = CXCORE.cvGet2D(img.getCvArr(), idx0, idx1);
		return new Scalar(cv.val);
	}

	/**
	 * Return a specific array element.
	 *
	 * @param img - the input array
	 * @param idx0 - the first zero-based component of the element index
	 * @param idx1 - the second zero-based component of the element index
	 * @param idx2 - the third zero-based component of the element index
	 * @return the array element
	 */
	public static Scalar get3D(IplImage img, int idx0, int idx1, int idx2) {
		ByValue cv = CXCORE.cvGet3D(img.getCvArr(), idx0, idx1, idx2);
		return new Scalar(cv.val);
	}

	/**
	 * Return a specific array element.
	 *
	 * @param img - the input array
	 * @param idx - array of element indices
	 * @return the array element
	 */
	public static Scalar getND(IplImage img, int[] idx) {
		ByValue cv = CXCORE.cvGetND(img.getCvArr(), idx);
		return new Scalar(cv.val);
	}

	/**
	 * Return a specific element of a single-channel 1D array.
	 *
	 * @param img - the input array. Must have a single channel.
	 * @param idx0 - the first zero-based component of the element index
	 * @return - the array element
	 */
	public static double getReal1D(IplImage img, int idx0) {
		return CXCORE.cvGetReal1D(img.getCvArr(), idx0);
	}

	/**
	 * Return a specific element of a single-channel 2D array.
	 *
	 * @param img - the input array. Must have a single channel.
	 * @param idx0 - the first zero-based component of the element index
	 * @param idx1 - the second zero-based component of the element index
	 * @return - the array element
	 */
	public static double getReal2D(IplImage img, int idx0, int idx1) {
		return CXCORE.cvGetReal2D(img.getCvArr(), idx0, idx1);
	}

	/**
	 * Return a specific element of a single-channel 3D array.
	 *
	 * @param img - the input array. Must have a single channel.
	 * @param idx0 - the first zero-based component of the element index
	 * @param idx1 - the second zero-based component of the element index
	 * @param idx2 - the third zero-based component of the element index
	 * @return - the array element
	 */
	public static double getReal3D(IplImage img, int idx0, int idx1, int idx2) {
		return CXCORE.cvGetReal3D(img.getCvArr(), idx0, idx1, idx2);
	}

	/**
	 * Return a specific element of a single-channel n-dimensional array.
	 *
	 * @param img - the input array
	 * @param idx - array of the element indices
	 * @return the array element
	 */
	public static double getRealND(IplImage img, int[] idx) {
		return CXCORE.cvGetRealND(img.getCvArr(), idx);
	}

	/**
	 * Returns the particular dimension size (number of elements per that dimension).
	 *
	 * @param img - the input array
	 * @param index - zero-based dimension index (for images 0 means height, 1 means width)
	 * @return the dimension size
	 */
	public static int getDimSize(IplImage img, int index) {
		return CXCORE.cvGetDimSize(img.getCvArr(), index);
	}

	/**
	 * Returns the index of the channel of interest of an image.
	 *
	 * @param img - the image
	 * @return the channel of interest of in an <code>IplImage</code>
	 */
	public static int getImageCOI(IplImage img) {
		return CXCORE.cvGetImageCOI(img.getJNAIPLImage());
	}

	/**
	 * Returns the image region of interest (ROI). If there is no ROI set,
	 * <code>Rectangle(0, 0, img.getWidth(), img.getHeight())</code> is returned.
	 *
	 * @param img - the image.
	 * @return a rectangle specifying the ROI.
	 */
	public static Rectangle getImageROI(IplImage img) {
		sj.opencv.jna.cxcore.CvRect.ByValue cv = CXCORE.cvGetImageROI(img.getJNAIPLImage());
		return new Rectangle(cv.x, cv.y, cv.width, cv.height);
	}


	/**
	 * Returns the size of a matrix or the size of an image ROI.
	 *
	 * @param img - the input matrix or image
	 * @return the number of rows and columns of the input matrix or image.
	 * In the case of an image the size of ROI is returned.
	 */
	public static Dimension getSize(IplImage img) {
		sj.opencv.jna.cxcore.CvSize.ByValue cv = CXCORE.cvGetSize(img.getCvArr());
		return new Dimension(cv.width, cv.height);
	}

	/**
	 * Resets the image Region of Interest (ROI) to include the entire image and releases
	 * the ROI structure.
	 *
	 * @param img - the image.
	 */
	public static void resetImageROI(IplImage img) {
		CXCORE.cvResetImageROI(img.getJNAIPLImage());
	}

//	/**
//	 * Sets a certain channel in an image to a value (mostly useful to set alpha to 255 after color conversion to 4byte pixel depth)
//	 * @param im
//	 * @param channel
//	 * @param value
//	 */
//	public static void setChannel(IplImage im, int channel, int value){
//		if( channel >= im.getColorModel().getNumberOfChannels() )
//			throw new RuntimeException("channel must fit within the channel of im");
////		setChannelNative(im.getPointer(), channel, value);
//	}

	/**
	 * Sets the channel of interest in an image.
	 * <p>
	 * Most OpenCV functions do not support the COI setting, so to process an individual
	 * image/matrix channel one may copy -via <copy>copy()</code> or <code>split()</code>- the
	 * channel to a separate image/matrix, process it and then copy the result back
	 * -via <code>copy()</code> or <code>merge()</code>- if needed.
	 *
	 * @param img - the image
	 * @param coi - the channel of interest
	 * <ul>
	 * <li> 0 - all channels are selected
	 * <li> 1 - first channel is selected
	 * <li> etc.
	 * <ul>
	 */
	public static void setImageCOI(IplImage img, int coi) {
		CXCORE.cvSetImageCOI(img.getJNAIPLImage(), coi);
	}


	/**
	 * Sets an image Region Of Interest (ROI).
	 * <p>
	 * Most OpenCV functions support the use of ROI and treat the image rectangle as
	 * a separate image. For example, all of the pixel coordinates are counted from the
	 * top-left (or bottom-left) corner of the ROI, not the original image.
	 *
	 * @param img - the image.
	 * @param roi - a rectangle specifying the ROI.
	 */
	public static void setImageROI(IplImage img, Rectangle roi) {
		CXCORE.cvSetImageROI(img.getJNAIPLImage(), new CvRect.ByValue(roi.x, roi.y, roi.width, roi.height));
	}

    public enum SolveMethod {
        CV_LU(0),
        CV_SVD(1),
        CV_SVD_SYM(2);

        private final int open_cv_constant;
        SolveMethod(int constant){this.open_cv_constant=constant;}
    }


	public static int solve(float[] A, float[] b, float[] x, int rows, int cols, SolveMethod method) {
		ByteBuffer A_buf = ByteBuffer.allocateDirect(A.length * Float.SIZE/8);
		A_buf.asFloatBuffer().put(A);
		ByteBuffer b_buf = ByteBuffer.allocateDirect(b.length * Float.SIZE/8);
		b_buf.asFloatBuffer().put(b);
		ByteBuffer x_buf = ByteBuffer.allocateDirect(x.length * Float.SIZE/8);
		x_buf.asFloatBuffer().put(x);

		CvMat cvA = CXCORE.cvMat(rows, cols, CxcoreLibrary.CV_32FC1, Native.getDirectBufferPointer(A_buf));
		CvMat cvb = CXCORE.cvMat(rows, 1, CxcoreLibrary.CV_32FC1, Native.getDirectBufferPointer(b_buf));
		CvMat cvx = CXCORE.cvMat(cols, 1, CxcoreLibrary.CV_32FC1, Native.getDirectBufferPointer(x_buf));

		return CXCORE.cvSolve(new CvArr(cvA.getPointer()), new CvArr(cvb.getPointer()), new CvArr(cvx.getPointer()), method.open_cv_constant);
	}

	public static void split(IplImage src, IplImage dst0, IplImage dst1, IplImage dst2, IplImage dst3) {
//		System.out.println("src: " + src.getNumberOfChannels() + " dst0: " + dst0.getNumberOfChannels());
		CXCORE.cvSplit(src.getCvArr(), dst0==null?null:dst0.getCvArr(), dst1==null?null:dst1.getCvArr(), dst2==null?null:dst2.getCvArr(), dst3==null?null:dst3.getCvArr());
//		System.out.println("past ----");
	}


	/* ******************************************************************************
	 *  						OPERATIONS ON ARRAYS                                *
	 * ******************************************************************************/

	/**
	 *
		Copies specified channels from input arrays to the specified channels of output arrays
		The functions mixChannels provide an advanced mechanism for shuffling image channels. split() and merge() and some forms of cvtColor() are partial cases of mixChannels .

		Parameters:
			srcv – The input array or vector of matrices. All the matrices must have the same size and the same depth
			nsrc – The number of elements in srcv
			dstv – The output array or vector of matrices. All the matrices must be allocated , their size and depth must be the same as in srcv[0]
			ndst – The number of elements in dstv
			fromTo – The array of index pairs, specifying which channels are copied and where. fromTo[k*2] is the 0-based index of the input channel in srcv and fromTo[k*2+1] is the index of the output channel in dstv . Here the continuous channel numbering is used, that is, the first input image channels are indexed from 0 to srcv[0].channels()-1 , the second input image channels are indexed from srcv[0].channels() to srcv[0].channels() + srcv[1].channels()-1 etc., and the same scheme is used for the output image channels. As a special case, when fromTo[k*2] is negative, the corresponding output channel is filled with zero.
			npairs
	*/
	public static void mixChannels(IplImage src, IplImage dst, int[] channelsFromTo){
		if( channelsFromTo.length % 2 != 0 ){
			
		}
		
		CXCORE.cvMixChannels(new PointerByReference(src.getPointer()), 1, new PointerByReference(dst.getPointer()), 1, channelsFromTo, channelsFromTo.length/2);
	}

	/**
	 * Sets every value of an image to a scalar
	 * @param im
	 * @param val
	 * @param mask
	 */
	public static void set(IplImage im, Scalar val, IplImage mask){
		CXCORE.cvSet(im.getCvArr(), new ByValue(val.getArray()), mask==null?null:mask.getCvArr());
	}

	/**
	 * Makes a full copy of an image, including the header, data, and ROI.
	 *
	 * @param img - the original image
	 * @return the cloned image
	 */
	public static IplImage cloneImage(IplImage img) {
		JNAIplImage clone = CXCORE.cvCloneImage(img.getJNAIPLImage());
		ByteBuffer buffer = clone.imageData.getByteBuffer(0, clone.imageSize);
		buffer.order( ByteOrder.BIG_ENDIAN );

		IplImage im = new IplImage(clone, img.getWidth(), img.getHeight(), img.getWidthStep(), buffer, img.getPixelDepth(), img.getColorModel());

		return im;
	}

	/**
	 * Copies one array to another.
	 *<p>
	 *<code>dst(i) = src(i)</code>
	 *<p>
	 * Both arrays must have the same type, the same number of dimensions, and the same size.
	 * If any of the passed arrays is of <code>IplImage</code> type, then its ROI and COI
	 * fields are used.
	 *
	 * @param src - the source array
	 * @param dst - the destination array
	 */
	public static void copy(IplImage src, IplImage dst) {
		CXCORE.cvCopy(src.getCvArr(), dst.getCvArr(), null);
	}

	/**
	 * Copies one array to another.
	 *<p>
	 *<code>dst(i) = src(i), if mask(i) != 0</code>
	 *<p>
	 * The operation mask is optional, and a <code>null</code> value can be passed as mask argument.
	 * Both arrays must have the same type, the same number of dimensions, and the same size.
	 * If any of the passed arrays is of <code>IplImage</code> type, then its ROI and
	 * COI fields are used.
	 *
	 * @param src - the source array
	 * @param dst - the destination array
	 * @param mask - the optional operation mask, 8-bit single channel array; specifies elements
	 * of the destination array to be changed
	 */
	public static void copy(IplImage src, IplImage dst, IplImage mask) {
		CXCORE.cvCopy(src.getCvArr(), dst.getCvArr(), mask==null?null:mask.getCvArr());
	}

	/**
	 * Counts non-zero array elements.
	 * <p>
	 * In the case of <code>IplImage</code> both ROI and COI are supported.
	 *
	 * @param img - the array must be a single-channel array or a multi-channel image with COI set
	 * @return the number of non-zero elements in <code>img</code>
	 */
	public static int countNonZero(IplImage img) {
		return CXCORE.cvCountNonZero(img.getCvArr());
	}


	/**
	 * Checks that array elements lie between the elements of two other arrays
	 * dst(I) is set to 0xff (all 1 -bits) if src(I) is within the range and 0 otherwise. All the arrays must have the same type, except the destination, and the same size (or ROI size).	 * @param src – The first source array
	 *
	 * @param src – The source array
    * @param lower – The inclusive lower boundary array
    * @param upper – The exclusive upper boundary array
    * @param dst – The destination array, must have 8u or 8s type
	 */
	public static void inRange(IplImage src, IplImage lower, IplImage upper, IplImage dst){
		CXCORE.cvInRange(src.getCvArr(), lower.getCvArr(), upper.getCvArr(), dst.getCvArr());
	}


	/**
	 * Checks that array elements lie between two scalars
	 * dst(I)’ is set to 0xff (all 1 -bits) if ‘src(I)’ is within the range and 0 otherwise. All the arrays must have the same size (or ROI size).
	 *
	 * @param src – The source array
    * @param lower – The inclusive lower boundary
    * @param upper – The exclusive upper boundary
    * @param dst – The destination array, must have 8u or 8s type
	 */
	public static void inRangeS(IplImage src, Scalar lower, Scalar upper, IplImage dst){
		CXCORE.cvInRangeS(src.getCvArr(), new CvScalar.ByValue(lower.getArray()), new CvScalar.ByValue(upper.getArray()), dst.getCvArr());
	}

	public enum CompareMode{
		CV_CMP_EQUAL( CxcoreLibrary.CV_CMP_EQ ),
		CV_CMP_GREATER_THAN( CxcoreLibrary.CV_CMP_GT ),
		CV_CMP_GREATER_OR_EQUAL( CxcoreLibrary.CV_CMP_GE ),
		CV_CMP_LESS_THAN( CxcoreLibrary.CV_CMP_LT ),
		CV_CMP_LESS_OR_EQUAL( CxcoreLibrary.CV_CMP_LE ),
		CV_CMP_NOT_EQUAL( CxcoreLibrary.CV_CMP_EQ );

		private final int open_cv_constant;
		CompareMode(int constant){this.open_cv_constant=constant;}
		public final int getConstant(){return open_cv_constant;};
	}

	/**
	 * Computes the per-element difference between two arrays
	 * The function subtracts one array from another one
	 * dst(I)=src1(I)-src2(I) if mask(I)!=0
	 * All the arrays must have the same type, except the mask, and the same size (or ROI size). For types that have limited range this operation is saturating.
	 *
	 * @param src1 – The first source array
	 * @param src2 – The second source array
	 * @param dst – The destination array
	 * @param mask – Operation mask, 8-bit single channel array; specifies elements of the destination array to be changed
	 */
	public static void sub(IplImage src1, IplImage src2, IplImage dst, IplImage mask){
		CXCORE.cvSub(src1.getCvArr(), src2.getCvArr(), dst.getCvArr(), mask==null?null:mask.getCvArr());
	}

	/**
	 * Computes the difference between an array and a scalar.
	 * The function subtracts a scalar from every element of the source array
	 * dst(I)=src(I)-value if mask(I)!=0
	 * All the arrays must have the same type, except the mask, and the same size (or ROI size). For types that have limited range this operation is saturating.
	 *
	 * @param src – The first source array
	 * @param value – Subtracted scalar
	 * @param dst – The destination array
	 * @param mask – Operation mask, 8-bit single channel array; specifies elements of the destination array to be changed
	 */
	public static void subS(IplImage src1, Scalar value, IplImage dst, IplImage mask){
		throw new RuntimeException("subS dropped from opencv.2.4.0 (not implemented in arith.cpp)");
		//		CXCORE.cvSubS(src1.getCvArr(), new ByValue(value.getArray()), dst.getCvArr(), mask==null?null:mask.getCvArr());
	}

	/**
	 * Computes the difference between a scalar and an array.
	 * The function subtracts every element of source array from a scalar
	 * dst(I)=value-src(I) if mask(I)!=0
	 * All the arrays must have the same type, except the mask, and the same size (or ROI size). For types that have limited range this operation is saturating.
	 *
	 * @param src – The first source array
	 * @param value – Scalar to subtract from
	 * @param dst – The destination array
	 * @param mask – Operation mask, 8-bit single channel array; specifies elements of the destination array to be changed
	 */
	public static void subRS(IplImage src1, Scalar value, IplImage dst, IplImage mask){
		CXCORE.cvSubRS(src1.getCvArr(), new ByValue(value.getArray()), dst.getCvArr(), mask==null?null:mask.getCvArr());
	}

	/**
	 * Adds up array elements
	 * The function calculates the sum S of array elements, independently for each channel
	 * the array is IplImage and COI is set, the function processes the selected channel only
	 * and stores the sum to the first scalar component.
	 * @param im
	 * @return
	 */
	public static Scalar sum(IplImage im){
		ByValue cvSum = CXCORE.cvSum(im.getCvArr());
		return new Scalar(cvSum.val);
	}

	public enum SVDFlag {
		CV_SVD_MODIFY_A	( CxcoreLibrary.CV_SVD_MODIFY_A ),
		CV_SVD_SYM	( CxcoreLibrary.CV_SVD_SYM ),
		CV_SVD_U_T	( CxcoreLibrary.CV_SVD_U_T ),
		CV_SVD_V_T	( CxcoreLibrary.CV_SVD_V_T ),
		CV_SVD	( CxcoreLibrary.CV_SVD );

		private final int open_cv_constant;
		SVDFlag(int constant){this.open_cv_constant=constant;}
	}

	public static void svd(float[] A, float[] W, float[] U, float[] V, int rows, int cols, SVDFlag[] flags) {

		int intflags = 0;
		for (SVDFlag f : flags) {
			intflags = intflags | f.open_cv_constant;
		}

		ByteBuffer A_buf = ByteBuffer.allocateDirect(A.length * Float.SIZE/8);
		A_buf.asFloatBuffer().put(A);
		ByteBuffer W_buf = ByteBuffer.allocateDirect(W.length * Float.SIZE/8);
		W_buf.asFloatBuffer().put(W);
		ByteBuffer U_buf = ByteBuffer.allocateDirect(U.length * Float.SIZE/8);
		U_buf.asFloatBuffer().put(U);
		ByteBuffer V_buf = ByteBuffer.allocateDirect(V.length * Float.SIZE/8);
		V_buf.asFloatBuffer().put(V);

		sj.opencv.jna.cxcore.CvMat.ByValue AMat = CXCORE.cvMat(rows, cols, CxcoreLibrary.CV_32FC1, Native.getDirectBufferPointer(A_buf));
		sj.opencv.jna.cxcore.CvMat.ByValue WMat = CXCORE.cvMat(rows, cols, CxcoreLibrary.CV_32FC1, Native.getDirectBufferPointer(W_buf));
		sj.opencv.jna.cxcore.CvMat.ByValue UMat = CXCORE.cvMat(rows, cols, CxcoreLibrary.CV_32FC1, Native.getDirectBufferPointer(U_buf));
		sj.opencv.jna.cxcore.CvMat.ByValue VMat = CXCORE.cvMat(rows, cols, CxcoreLibrary.CV_32FC1, Native.getDirectBufferPointer(V_buf));

		CXCORE.cvSVD(new CvArr(AMat.getPointer()), new CvArr(WMat.getPointer()), new CvArr(UMat.getPointer()), new CvArr(VMat.getPointer()), intflags);
	}

	/**
	 * Calculates the absolute difference between two arrays.
	 * <p>
	 * <code>dst(i) = | src1(i) - src2(i) |</code>
	 * <p>
	 * The arrays must have the same data type and the same size (or ROI size).
	 *
	 * @param src1 – the first source image
	 * @param src2 – the second source image
	 * @param dst – the destination image
	 */
	public static void absDiff(IplImage src1, IplImage src2, IplImage dst){
		CXCORE.cvAbsDiff(src1.getCvArr(), src2.getCvArr(), dst.getCvArr());
	}

	/**
	 * Calculates the absolute difference between an array and a scalar.
	 * <p>
	 * <code>dst(i) = | src1(i) - value |</code>
	 * <p>
	 * The arrays must have the same pixel depth and the same size (or ROI size).
	 *
	 * @param src – the source image
	 * @param dst – the destination image
	 * @param value – the scalar
	 */
	public static void absDiffS(IplImage src1, IplImage dst, Scalar value){
		CXCORE.cvAbsDiffS(src1.getCvArr(), dst.getCvArr(), new ByValue(value.getArray()));
	}

	/**
	 * Computes the per-element sum of two arrays.
	 * <p>
	 * <code>dst(i) = src1(i) + src2(i)</code>
	 * <p>
	 * All the arrays must have the same type and the same size (or ROI size).
	 * For types that have limited range this operation is saturating.
	 *
	 * @param src1 - the first source array
	 * @param src2 - the second source array
	 * @param dst - the destination array
	 */
	public static void add(IplImage src1, IplImage src2, IplImage dst) {
		CXCORE.cvAdd(src1.getCvArr(), src2.getCvArr(), dst.getCvArr(), null);
	}
	
	/**
	 * Computes the per-element sum of two arrays.
	 * <p>
	 * <code>dst(i) = src1(i) + src2(i)</code>
	 * <p>
	 * All the arrays must have the same type and the same size (or ROI size).
	 * For types that have limited range this operation is saturating.
	 *
	 * @param src1 - the first source array
	 * @param src2 - the second source array
	 * @param dst - the destination array
	 */
	public static void add(CvArr src1, CvArr src2, CvArr dst) {
		CXCORE.cvAdd(src1, src2, dst, null);
	}

	/**
	 * Computes the per-element sum of two arrays.
	 * <p>
	 * <code>dst(i) = src1(i) + src2(i), if mask(i) != 0</code>
	 * <p>
	 * The operation mask is optional, and a <code>null</code> value can be passed as mask argument.
	 * All the arrays must have the same type, except the mask, and the same size (or ROI size).
	 * For types that have limited range this operation is saturating.
	 *
	 * @param src1 - the first source array
	 * @param src2 - the second source array
	 * @param dst - the destination array
	 * @param mask - the optional operation mask, 8-bit single channel array; specifies elements of
	 * the destination array to be changed
	 */
	public static void add(IplImage src1, IplImage src2, IplImage dst, IplImage mask) {
		CXCORE.cvAdd(src1.getCvArr(), src2.getCvArr(), dst.getCvArr(), mask==null?null:mask.getCvArr());
	}

	/**
	 * Computes the sum of an array and a scalar.
	 * <p>
	 * <code>dst(i) = src(i) + value</code>
	 * <p>
	 * All the arrays must have the same type and the same size (or ROI size).
	 * For types that have limited range this operation is saturating.
	 *
	 * @param src - the source array
	 * @param value - the scalar
	 * @param dst - the destination array
	 */
	public static void addS(IplImage src, Scalar value, IplImage dst) {
		CXCORE.cvAddS(src.getCvArr(), new ByValue(value.getArray()), dst.getCvArr(), null);
	}

	/**
	 * Computes the sum of an array and a scalar.
	 * <p>
	 * <code>dst(i) = src(i) + value, if mask(i) != 0</code>
	 * <p>
	 * The operation mask is optional, and a <code>null</code> value can be passed as mask argument.
	 * All the arrays must have the same type, except the mask, and the same size (or ROI size).
	 * For types that have limited range this operation is saturating.
	 *
	 * @param src - the source array
	 * @param value - the scalar
	 * @param dst - the destination array
	 * @param mask - the optional operation mask, 8-bit single channel array; specifies elements of
	 * the destination array to be changed
	 */
	public static void addS(IplImage src, Scalar value, IplImage dst, IplImage mask) {
		CXCORE.cvAddS(src.getCvArr(), new ByValue(value.getArray()), dst.getCvArr(), mask==null?null:mask.getCvArr());
	}

	/**
	 * Computes the weighted sum of two arrays.
	 * <p>
	 * <code>dst(i) = src1(i)*alpha + src2(i)*beta + gamma</code>
	 * <p>
	 * All the arrays must have the same type and the same size (or ROI size).
	 * For types that have limited range this operation is saturating.
	 *
	 * @param src1 - the first source array
	 * @param alpha - weight of the first array's elements
	 * @param src2 - the second source array
	 * @param beta - weight of the second array's elements
	 * @param gamma - scalar added to each sum
	 * @param dst - the destination array
	 */
	public static void addWeighted(IplImage src1, double alpha, IplImage src2, double beta, double gamma, IplImage dst) {
		CXCORE.cvAddWeighted(src1.getCvArr(), alpha, src2.getCvArr(), beta, gamma, dst.getCvArr());
	}

	/**
	 * Performs per-element comparison of an array and a scalar.
	 * <p>
	 * <code>dst(i) = src1(i) op value</code>
	 * <p>
	 * <code>dst(i)</code> is set to <code>0xff</code> (all 1-bits) if the specific relation
	 * between the elements is true and 0 otherwise. All the arrays must have the same
	 * size (or ROI size).
	 *
	 * @param src1 - the source array, must have a single channel
	 * @param value -  the scalar value to compare each array element with
	 * @param dst - the destination array, must have 8u or 8s type
	 * @param mode - the relation to be checked between the two elements
	 * <ul>
	 * <li> CV_CMP_EQ: src1(i) “equal to” value
	 * <li> CV_CMP_GT: src1(i) “greater than” value
	 * <li> CV_CMP_GE: src1(i) “greater or equal” value
	 * <li> CV_CMP_LT: src1(i) “less than” value
	 * <li> CV_CMP_LE: src1(i) “less or equal” value
	 * <li> CV_CMP_NE: src1(i) “not equal” value
	 * </ul>
	 */
	public static void cmpS(IplImage src, double value, IplImage dst, CompareMode mode){
		CXCORE.cvCmpS(src.getCvArr(), value, dst.getCvArr(), mode.getConstant());
	}

	/**
	 * Calculates the per-element bit-wise conjunction of two arrays.
	 * <p>
	 * <code>dst(i) = src1(i) & src2(i)</code>
	 * <p>
	 * In the case of floating-point arrays their bit representations are used for the operation.
	 * All the arrays must have the same type and the same size.
	 *
	 * @param src1 - the first source array
	 * @param src2 - the second source arrray
	 * @param dst - the destination array
	 */
	public static void and(IplImage src1, IplImage src2, IplImage dst, IplImage mask) {
		CXCORE.cvAnd(src1.getCvArr(), src2.getCvArr(), dst.getCvArr(), mask==null?null:mask.getCvArr());
	}

	/**
	 * Calculates the per-element bit-wise conjunction of an array and a scalar.
	 * <p>
	 * <code>dst(i) = src(i) & value, if mask(i) != 0</code>
	 * <p>
	 * The operation mask is optional, and a <code>null</code> value can be passed as mask argument.
	 * Prior to the actual operation, the scalar is converted to the same type as that of
	 * the array(s). In the case of floating-point arrays their bit representations are used
	 * for the operation. All the arrays must have the same type, except the mask, and the same size.
	 *
	 * @param src - the source array
	 * @param value - the scalar
	 * @param dst - the destination array
	 * @param mask - the optional operation mask, 8-bit single channel array; specifies elements
	 * of the destination array to be changed
	 */
	public static void andS(IplImage src, Scalar value, IplImage dst, IplImage mask) {
		CXCORE.cvAndS(src.getCvArr(), new ByValue(value.getArray()), dst.getCvArr(), mask==null?null:mask.getCvArr());
	}


	/**
	 * Calculates the average (mean) of array elements, independently for each channel.
	 *
	 * @param img - the array
	 * @param mask - the optional operation mask
	 * @return the <code>Scalar</code> containing the average for each channel
	 */
	public static Scalar avg(IplImage img, IplImage mask) {
		CvScalar.ByValue cvscalar = CXCORE.cvAvg(img.getCvArr(), mask==null?null:mask.getCvArr());
		return new Scalar(cvscalar.val);
	}


	/**
	 * Calculates the average value and standard deviation of array elements,
	 * independently for each channel.
	 *
	 * @param img - the array
	 * @param mean - the <code>Scalar</code> where the average values will be stored
	 * @param stdDev - the <code>Scalar</code> where the standard deviations will be stored
	 */
	public static void avgSdv(IplImage img, Scalar mean, Scalar stdDev, IplImage mask) {

		CvScalar cvmean = new CvScalar();
		CvScalar cvstd_dev = new CvScalar();
		CXCORE.cvAvgSdv(img.getCvArr(), cvmean, cvstd_dev, mask==null?null:mask.getCvArr());
		cvmean.read();
		cvstd_dev.read();

		for(int i=0; i<4; i++){
			mean.set(i, cvmean.val[i]);
			stdDev.set(i, cvstd_dev.val[i]);
		}
	}

	/**
	 * Performs per-element comparison of two arrays.
	 * <p>
	 * <code>dst(i) = src1(i) op src2(i)</code>
	 * <p>
	 * <code>dst(i)</code> is set to <code>0xff</code> (all 1-bits) if the specific relation
	 * between the elements is true and 0 otherwise. All the arrays must have the same
	 * size (or ROI size). The source arrays must have a single channel.
	 *
	 * @param src1 - the first source array, must have a single channel
	 * @param src2 - the second source array, must have a single channel
	 * @param dst - the destination array, must have 8u or 8s type
	 * @param mode - the relation to be checked between the two elements
	 * <ul>
	 * <li> CV_CMP_EQ: src1(i) “equal to” src2(i)
	 * <li> CV_CMP_GT: src1(i) “greater than” src2(i)
	 * <li> CV_CMP_GE: src1(i) “greater or equal” src2(i)
	 * <li> CV_CMP_LT: src1(i) “less than” src2(i)
	 * <li> CV_CMP_LE: src1(i) “less or equal” src2(i)
	 * <li> CV_CMP_NE: src1(i) “not equal” src2(i)
	 * </ul>
	 */
	public static void cmp(IplImage src1, IplImage src2, IplImage dst, CompareMode mode){
		CXCORE.cvCmp(src1.getCvArr(), src2.getCvArr(), dst.getCvArr(), mode.getConstant());
	}

	/**
	 * Copies one array to another and performs type conversion. All the channels of
	 * multi-channel arrays are processed independently.
	 * <p>
	 * The type of conversion is done with rounding and saturation. That is, if the result
	 * of scaling plus conversion cannot be represented exactly by a value of the destination
	 * array element type, it is set to the nearest representable value on the real axis.
	 * <p>
	 * This is equivalent to calling <code>convertScale(src, dst, 1.0, 0)</code>.
	 *
	 * @param src - the source array
	 * @param dst - the destination array
	 */
	public static void convertScale(IplImage src, IplImage dst) {
		convertScale(src, dst, 1, 0);
	}

	/**
	 * Copies one array to another with optional scaling, which is performed first,
	 * and/or optional type conversion, performed after. All the channels of multi-channel
	 * arrays are processed independently.
	 * <p>
	 * The type of conversion is done with rounding and saturation. That is, if the result
	 * of scaling plus conversion cannot be represented exactly by a value of the destination
	 * array element type, it is set to the nearest representable value on the real axis.
	 * <p>
	 * In the case of <code>scale=1</code>, <code>shift=0</code> no pre-scaling is done.
	 * This is a specially optimized case and it has the appropriate <i>Convert</i> name.
	 * If the source and destination arrays have equal types, this is also a special case
	 * that can be used to scale and shift a matrix or an image and that is called <i>Scale</i> .
	 *
	 * @param src - the source array
	 * @param dst - the destination array
	 * @param scale - the scale factor
	 * @param shift - the value added to the scaled source array elements
	 */
	public static void convertScale(IplImage src, IplImage dst, double scale, double shift) {
		CXCORE.cvConvertScale(src.getCvArr(), dst.getCvArr(), scale, shift);
	}
	
	/**
	 * Copies one array to another with optional scaling, which is performed first,
	 * and/or optional type conversion, performed after. All the channels of multi-channel
	 * arrays are processed independently.
	 * <p>
	 * The type of conversion is done with rounding and saturation. That is, if the result
	 * of scaling plus conversion cannot be represented exactly by a value of the destination
	 * array element type, it is set to the nearest representable value on the real axis.
	 * <p>
	 * In the case of <code>scale=1</code>, <code>shift=0</code> no pre-scaling is done.
	 * This is a specially optimized case and it has the appropriate <i>Convert</i> name.
	 * If the source and destination arrays have equal types, this is also a special case
	 * that can be used to scale and shift a matrix or an image and that is called <i>Scale</i> .
	 *
	 * @param src - the source array
	 * @param dst - the destination array
	 * @param scale - the scale factor
	 * @param shift - the value added to the scaled source array elements
	 */
	public static void convertScale(CvArr src, CvArr dst, double scale, double shift) {
		CXCORE.cvConvertScale(src, dst, scale, shift);
	}

	/**
	 * Converts input array elements to another 8-bit unsigned integer. The function is similar
	 * to <code>convertScale()</code> , but it stores absolute values of the conversion results.
	 * It is equivalent to calling <code>cvtScaleAbs(src, dst, 1.0, 0)</code>.
	 * <p>
	 * The function supports only destination arrays of 8u (8-bit unsigned integers) type; for other
	 * types the function can be emulated by a combination of <code>convertScale()</code> and
	 * <code>abs()</code> functions.
	 *
	 * @param src - the source array
	 * @param dst - the destination array
	 */
	public static void convertScaleAbs(IplImage src, IplImage dst) {
		convertScaleAbs(src, dst, 1.0, 0);
	}

	/**
	 * Converts input array elements to another 8-bit unsigned integer with optional linear
	 * transformation. The function is similar to <code>convertScale()</code> , but it stores absolute
	 * values of the conversion results.
	 * <p>
	 * The function supports only destination arrays of 8u (8-bit unsigned integers) type; for other
	 * types the function can be emulated by a combination of <code>convertScale()</code> and
	 * <code>abs()</code> functions.
	 *
	 * @param src - the source array
	 * @param dst - the destination array
	 * @param scale - the scale factor
	 * @param shift - the value added to the scaled source array elements
	 */
	public static void convertScaleAbs(IplImage src, IplImage dst, double scale, double shift) {
		CXCORE.cvConvertScaleAbs(src.getCvArr(), dst.getCvArr(), scale, shift);
	}

	/**
	 * Performs per-element division of two arrays.
	 * <p>
	 * <code>dst(i) = src1(i) / src2(i), if src1 != null</code>
	 * <p>
	 * <code>dst(i) = 1 / src2(i), otherwise</code>
	 * <p>
	 * This is equivalent to calling <code>div(src1, src2, dst, 1.0)</code>.
	 * All the arrays must have the same type and the same size (or ROI size).
	 *
	 * @param src1 - the first source array. If <code>null</code>, the array is assumed to be all 1’s.
	 * @param src2 - the second source array
	 * @param dst - the destination array
	 */
	public static void div(IplImage src1, IplImage src2, IplImage dst) {
		div(src1, src2, dst, 1);
	}

	/**
	 * Performs per-element division of two arrays.
	 * <p>
	 * <code>dst(i) = scale * src1(i) / src2(i), if src1 != null</code>
	 * <p>
	 * <code>dst(i) = scale / src2(i), otherwise</code>
	 * <p>
	 * All the arrays must have the same type and the same size (or ROI size).
	 *
	 * @param src1 - the first source array. If <code>null</code>, the array is assumed to be all 1’s.
	 * @param src2 - the second source array
	 * @param dst - the destination array
	 * @param scale - the optional scale factor
	 */
	public static void div(IplImage src1, IplImage src2, IplImage dst, double scale) {
		CXCORE.cvDiv(src1==null?null:src1.getCvArr(), src2.getCvArr(), dst.getCvArr(), scale);
	}

	/**
	 * Calculates the exponent of every array element.
	 * <p>
	 * <code>dst(i) = e^(src(i))</code>
	 * <p>
	 * The maximum relative error is about 7x10e-6 . Currently, the function converts denormalized
	 * values to zeros on output.
	 *
	 * @param src - the source array, it should have float or double type
	 * @param dst - the destination array, it should have float or double type

	 */
	public static void exp(IplImage src, IplImage dst) {
		CXCORE.cvExp(src.getCvArr(), dst.getCvArr());
	}

    public enum FlipMode{
        FLIP_X_AXIS(0),
        FLIP_Y_AXIS(1),
        FLIP_BOTH_AXES(-1);

        private final int open_cv_constant;
        FlipMode(int constant){this.open_cv_constant=constant;}
    }

	/**
	 * Flips an image around the x-axis, y-axis, or both.
	 *
	 * @param src - The source image.
	 * @param dst - The destination image.
	 * @param mode - The flip mode.
	 *
	 */
	public static void flip(IplImage src, IplImage dst, FlipMode mode){
		CXCORE.cvFlip(src.getCvArr(), dst.getCvArr(), mode.open_cv_constant);
	}

    public enum GEMMFlag {
        CV_GEMM_A_T( CxcoreLibrary.CV_GEMM_A_T ),
        CV_GEMM_B_T( CxcoreLibrary.CV_GEMM_B_T ),
        CV_GEMM_C_T( CxcoreLibrary.CV_GEMM_C_T);

        private final int open_cv_constant;
        GEMMFlag(int constant){this.open_cv_constant=constant;}
    }

	/**
	 * Performs generalized matrix multiplication.
	 * <p>
	 * <code>dst = alpha * op(src1) * op(src2) + beta * op(src3), where op(X) is X or transpose(X) </code>
	 * <p>
	 * All the matrices should have the same data type and coordinated sizes.
	 * Real or complex floating-point matrices are supported.
	 *
	 * @param src1 - the first source array
	 * @param src2 - the second source array
	 * @param alpha - the alpha value
	 * @param src3 - the third source array (shift). Can be <code>null</code> if there is no shift.
	 * @param beta - the beta value
	 * @param dst - the destination array
	 * @param gemmFlags - the operation flags that can be a combination of the following values:
	 * <ul>
	 * <li>CV_GEMM_A_T - transpose of src1
	 * <li>CV_GEMM_B_T - transpose of src2
	 * <li>CV_GEMM_C_T - transpose of src3
	 * <ul>
	 */
	public static void GEMM(IplImage src1, IplImage src2, double alpha, IplImage src3, double beta, IplImage dst, GEMMFlag[] gemmFlags) {

		int flags = 0;
		if( gemmFlags != null ){
			for (GEMMFlag f : gemmFlags) {
				flags |= f.open_cv_constant;
			}
		}

		CXCORE.cvGEMM(src1.getCvArr(), src2.getCvArr(), alpha, src3==null?null:src3.getCvArr(), beta, dst.getCvArr(), flags);
	}

	/**
	 * Performs matrix multiplication with optional shifting. This is equivalent to
	 * calling <code>GEMM(src1, src2, 1, src3, 1, dst, new GEMMFlag[]{})</code>.
	 * <p>
	 * <code>dst = src1 * src2 + src3</code>
	 * <p>
	 * All the matrices should have the same data type and coordinated sizes.
	 * Real or complex floating-point matrices are supported.
	 *
	 * @param src1 - the first source array
	 * @param src2 - the second source array
	 * @param src3 - the third source array (shift). Can be <code>null</code> if there is no shift.
	 * @param dst - the destination array
	 */
	public static void matMulAdd(IplImage src1, IplImage src2, IplImage src3, IplImage dst) {
		GEMM(src1, src2, 1, src3, 1, dst, null);
	}

	/**
	 * Performs matrix multiplication. This is equivalent to calling
	 * <code>mulMatAdd(src1, src2, null, dst)</code>.
	 * <p>
	 * <code>dst = src1 * src2</code>
	 * <p>
	 *
	 * @param src1 - the first source array
	 * @param src2 - the second source array
	 * @param dst - the destination array
	 */
	public static void matMul(IplImage src1, IplImage src2, IplImage dst) {
		matMulAdd(src1, src2, null, dst);
	}

	/* ******************************************************************************
	 *  							DYNAMIC STRUCTURES                              *
	 * ******************************************************************************/

	/**
	 * Creates an empty memory storage.
	 *
	 * @param blockSize -  Size of the storage blocks in bytes. If it is 0, the block
	 * size is set to a default value - currently it is about 64K.
	 */
	public static MemStorage createMemStorage(int blockSize) {
		CvMemStorage cvCreateMemStorage = CXCORE.cvCreateMemStorage(blockSize);
		MemStorage mem = new MemStorage(cvCreateMemStorage);

		return mem;
	}

	/* ******************************************************************************
	 *  								DRAWING                                     *
	 * ******************************************************************************/

	public enum LineType {

		DEFAULT(8);

		private final int open_cv_constant;
		LineType(int constant){this.open_cv_constant=constant;}
		public final int getConstant(){return open_cv_constant;};
	}

	public enum FontType {
		CV_FONT_HERSHEY_DUPLEX	( CxcoreLibrary.CV_FONT_HERSHEY_DUPLEX ),
		CV_FONT_HERSHEY_COMPLEX	( CxcoreLibrary.CV_FONT_HERSHEY_COMPLEX ),
		CV_FONT_HERSHEY_SIMPLEX	( CxcoreLibrary.CV_FONT_HERSHEY_SIMPLEX ),
		CV_FONT_HERSHEY_SCRIPT_SIMPLEX	( CxcoreLibrary.CV_FONT_HERSHEY_SCRIPT_SIMPLEX ),
		CV_FONT_HERSHEY_PLAIN	( CxcoreLibrary.CV_FONT_HERSHEY_PLAIN ),
		CV_FONT_HERSHEY_COMPLEX_SMALL	( CxcoreLibrary.CV_FONT_HERSHEY_COMPLEX_SMALL ),
		CV_FONT_HERSHEY_TRIPLEX	( CxcoreLibrary.CV_FONT_HERSHEY_TRIPLEX ),
		CV_FONT_HERSHEY_SCRIPT_COMPLEX	( CxcoreLibrary.CV_FONT_HERSHEY_SCRIPT_COMPLEX ),
		CV_FONT_VECTOR0	( CxcoreLibrary.CV_FONT_VECTOR0 ),
		CV_FONT_ITALIC	( CxcoreLibrary.CV_FONT_ITALIC );

		private final int open_cv_constant;
		FontType(int constant){this.open_cv_constant=constant;}
		public final int getConstant(){return open_cv_constant;};
	}

	public static Font initFont(FontType[] fontType, double hScale, double vScale, double shear, int thickness) {
		int intFontType = 0;
		for (FontType f : fontType) {
			intFontType = intFontType | f.getConstant();
		}

		CvFont font = new CvFont();
		CXCORE.cvInitFont(font, intFontType, hScale, vScale, shear, thickness, LineType.DEFAULT.getConstant());

		Font ret = new Font(font);
		return ret;
	}

	public static void putText(IplImage img, String text, Point pos, Font font, Scalar color) {
		CXCORE.cvPutText(img.getCvArr(), text, new CvPoint.ByValue(pos.x, pos.y), font.getJNACvFont(), new ByValue(color.getArray()));
	}

	/**
	 * Draws a simple or filled circle with a given center and radius using
	 * default parameters: thickness equals 1, line type equals <code>CV_8</code>,
	 * and shift is 0.
	 */
	public static void circle(IplImage img, Point center, int radius, Scalar color) {
		circle(img, center, radius, color, 1, LineType.DEFAULT, 0);
	}

	/**
	 * Draws a simple or filled circle with a given center and radius.
	 *
	 * @param img - Image where the circle is drawn.
	 * @param center - Center of the circle.
	 * @param radius - Radius of the circle.
	 * @param color - Circle color.
	 * @param thickness - Thickness of the circle outline if positive,
	 * otherwise this indicates that a filled circle is to be drawn.
	 * @param lineType -  Type of the circle boundary.
	 * @param shift - Number of fractional bits in the center coordinates and radius value.
	 */
	public static void circle(IplImage img, Point center, int radius, Scalar color, int thickness, LineType lineType, int shift) {
		CXCORE.cvCircle(img.getCvArr(), new CvPoint.ByValue(center.x, center.y), radius, new CvScalar.ByValue(color.getArray()), thickness, lineType.open_cv_constant, shift);
	}

	public static void ellipseBox(IplImage img, float x, float y, float width, float height, float angle, Scalar color) {
		ellipseBox(img, x, y, width, height, angle, color, 1, LineType.DEFAULT, 0);
	}

	public static void ellipseBox(IplImage img, float x, float y, float width, float height, float angle, Scalar color, int thickness, LineType lineType, int shift) {
		CvBox2D.ByValue box2 = new CvBox2D.ByValue(new CvPoint2D32f(x, y), new CvSize2D32f(width, height), angle);
		CXCORE.cvEllipseBox(img.getCvArr(), box2, new CvScalar.ByValue(color.getArray()), thickness, lineType.open_cv_constant, shift);
	}

	public static void line(IplImage img, Point pt1, Point pt2, Scalar color) {
		line(img, pt1, pt2, color, 1, LineType.DEFAULT, 0);
	}

	/**
	 * The function draws the line segment between pt1 and pt2 points in the image.
	 * The line is clipped by the image or ROI rectangle. For non-antialiased lines with
	 * integer coordinates the 8-connected or 4-connected Bresenham algorithm is used.
	 * Thick lines are drawn with rounding endings. Antialiased lines are drawn using
	 * Gaussian filtering.
	 *
	 * @param img
	 * @param p1
	 * @param p2
	 * @param color
	 * @param thickness
	 * @param lineType
	 * @param shift
	 */
	public static void line(IplImage img, Point pt1, Point pt2, Scalar color, int thickness, LineType lineType, int shift) {
		CXCORE.cvLine(img.getCvArr(), new CvPoint.ByValue(pt1.x, pt1.y), new CvPoint.ByValue(pt2.x, pt2.y), new CvScalar.ByValue(color.getArray()), thickness, lineType.open_cv_constant, shift);
	}

	/**
	 * Draws single or multiple polygonal curves using default parameters:
	 * thickness equals 1, line type equals <code>CV_8</code>, and shift is 0.
	 */
	public static void polyLine(IplImage img, Point[] pts, boolean isClosed, Scalar color) {
		polyLine(img, pts, isClosed, color, 1, LineType.DEFAULT, 0);
	}

	/**
	 * Draws single or multiple polygonal curves.
	 *
	 * @param img - The image that will be drawn.
	 * @param pts - List of arrays containing the polygons' vertices. The arrays have the form
	 * <code>[x0, y0, x1, y1, x2, y2, ...]</code>.
	 * @param isClosed - Indicates whether the polylines must be drawn closed. If closed, the
	 * function draws the line from the last vertex of every contour to the first vertex.
	 * @param color - Polyline color.
	 * @param thickness - Thickness of the polyline edges.
	 * @param lineType - Type of the line segments.
	 * @param shift - Number of fractional bits in the vertex coordinates.
	 */
	public static void polyLine(IplImage img, Point[] pts, boolean isClosed, Scalar color, int thickness, LineType lineType, int shift) {

		int[] npts = new int[]{pts.length};
		CvPoint.ByReference[] ptsArray = (sj.opencv.jna.cxcore.CvPoint.ByReference[]) new CvPoint.ByReference().toArray(pts.length);
		for(int j=0; j<pts.length; j++){
			ptsArray[j].x = pts[j].x;
			ptsArray[j].y = pts[j].y;
			ptsArray[j].write();
		}

		CXCORE.cvPolyLine(img.getCvArr(), ptsArray, npts, 1, isClosed?1:0, new CvScalar.ByValue(color.getArray()), thickness, lineType.open_cv_constant, shift);
	}

	public static void fillPoly(IplImage img, Point[] pts, Scalar color) {
		fillPoly(img, pts, color, LineType.DEFAULT, 0);
	}

	public static void fillPoly(IplImage img, Point[] pts, Scalar color, LineType lineType, int shift) {
		int[] npts = new int[]{pts.length};
		CvPoint.ByReference[] ptsArray = (sj.opencv.jna.cxcore.CvPoint.ByReference[]) new CvPoint.ByReference().toArray(pts.length);
		for(int j=0; j<pts.length; j++){
			ptsArray[j].x = pts[j].x;
			ptsArray[j].y = pts[j].y;
			ptsArray[j].write();
		}

		CXCORE.cvFillPoly(img.getCvArr(), ptsArray, npts, 1, new CvScalar.ByValue(color.getArray()), lineType.open_cv_constant, shift);
	}


	/**
	 * Draws a simple, thick, or filled rectangle using default parameters:
	 * thickness equals 1, line type equals <code>CV_8</code>, and shift is 0.
	 *
	 */
	public static void rectangle(IplImage img, Point pt1, Point pt2, Scalar color) {
		rectangle(img, pt1, pt2, color, 1, LineType.DEFAULT, 0);
	}

	/**
	 * Draws a simple, thick, or filled rectangle.
	 *
	 * @param img - The image that will be drawn.
	 * @param rect - The rectangle.
	 * @param color - Lines color.
	 * @param thickness - Thickness of the rectangle lines. Negative values cause the function to
	 * draw a filled rectangle.
	 * @param lineType - Type of the line segments.
	 * @param shift - Number of fractional bits in the point coordinates.
	 */
	public static void rectangle(IplImage img, Rectangle rect, Scalar color, int thickness, LineType lineType, int shift) {
		rectangle(img, new Point(rect.x, rect.y), new Point(rect.x+rect.width, rect.y+rect.height), color, thickness, lineType, shift);
	}

	/**
	 * Draws a simple, thick, or filled rectangle using default parameters:
	 * thickness equals 1, line type equals <code>CV_8</code>, and shift is 0.
	 *
	 */
	public static void rectangle(IplImage img, Rectangle rect, Scalar color) {
		rectangle(img, rect, color, 1, LineType.DEFAULT, 0);
	}

	/**
	 * Draws a simple, thick, or filled rectangle.
	 *
	 * @param img - The image that will be drawn.
	 * @param pt1 - One of the rectangle's vertices.
	 * @param pt2 - The vertex opposite to <code>pt1</code>.
	 * @param color - Lines color.
	 * @param thickness - Thickness of the rectangle lines. Negative values cause the function to
	 * draw a filled rectangle.
	 * @param lineType - Type of the line segments.
	 * @param shift - Number of fractional bits in the point coordinates.
	 */
	public static void rectangle(IplImage img, Point pt1, Point pt2, Scalar color, int thickness, LineType lineType, int shift) {
		CXCORE.cvRectangle(img.getCvArr(), new CvPoint.ByValue(pt1.x, pt1.y), new CvPoint.ByValue(pt2.x, pt2.y), new CvScalar.ByValue(color.getArray()), thickness, lineType.open_cv_constant, shift);
	}

	/**
	 * Natively allocates an IplImage and returns a pointer to it
	 * Also puts that into a collection that will be deallocated when application exits
	 * @param width of image
	 * @param height of image
	 * @param pixel_depth of image
	 * @param color_model of image
	 * @return an IplImage pointer
	 */
	public static IplImage createImage(int width, int height, PixelDepth pixel_depth, ColorModel color_model){

		JNAIplImage jnaim = CXCORE.cvCreateImage(new CvSize.ByValue(width, height), pixel_depth.getConstant(), color_model.getNumberOfChannels());

		// Retrieve the bytebuffer
		ByteBuffer buffer = jnaim.imageData.getByteBuffer(0, jnaim.imageSize);
		buffer.order( ByteOrder.BIG_ENDIAN );

		IplImage im = new IplImage(jnaim, width, height, jnaim.widthStep, buffer, pixel_depth, color_model);

		return im;
	}

	/**
	 * Natively allocates an IplImage with the given data buffer and returns a pointer to it
	 * Also puts that into a collection that will be deallocated when application exits
	 * @param width of image
	 * @param height of image
	 * @param pixel_depth of image
	 * @param color_model of image
	 * @param the data buffer
	 * @return an IplImage pointer
	 */
	public static IplImage createImage(int width, int height, PixelDepth pixel_depth, ColorModel color_model, ByteBuffer bytebuffer){

		JNAIplImage jnaim = CXCORE.cvCreateImageHeader(new CvSize.ByValue(width, height), pixel_depth.getConstant(), color_model.getNumberOfChannels());
		// Attach the bytebuffer
		jnaim.imageData = Native.getDirectBufferPointer(bytebuffer);
		jnaim.write();

		IplImage im = new IplImage(jnaim, width, height, jnaim.widthStep, bytebuffer, pixel_depth, color_model);

		return im;
	}

	public static void kMeans2(float[] samples, int sampleSize, int nSamples, int clusterCount,
			int[] labels, TermCriteriaType[] criteria, int max_iter, double epsilon) {

		long criteriaType = 0;
		for (TermCriteriaType t : criteria) {
			criteriaType = criteriaType | t.getConstant();
		}

		Mat samplesMat = createMat(nSamples, sampleSize, samples);
		Mat labelsMat = createMat(nSamples, 1, labels);

		CXCORE.cvKMeans2(new CvArr(samplesMat.pointer), clusterCount, new CvArr(labelsMat.pointer), new CvTermCriteria.ByValue((int)criteriaType, max_iter, epsilon), 1, null, 0, null, null);
	}

}
