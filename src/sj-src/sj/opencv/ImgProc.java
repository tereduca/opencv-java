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

import static sj.opencv.jna.JNAOpenCV.*;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import sj.opencv.Constants.HistogramType;
import sj.opencv.Constants.InterpolationMode;
import sj.opencv.Constants.TermCriteriaType;
import sj.opencv.Constants.WarpMode;
import sj.opencv.CxCore.CompareMode;
import sj.opencv.jna.cxcore.CvHistogram;
import sj.opencv.jna.cxcore.CvMat;
import sj.opencv.jna.cxcore.CvPoint;
import sj.opencv.jna.cxcore.CvPoint2D32f;
import sj.opencv.jna.cxcore.CvScalar;
import sj.opencv.jna.cxcore.CvSize;
import sj.opencv.jna.cxcore.JNAIplImage;
import sj.opencv.jna.cxcore.CvScalar.ByValue;
import sj.opencv.jna.cxcore.CvTermCriteria;
import sj.opencv.jna.cxcore.CxcoreLibrary;
import sj.opencv.jna.highgui.HighguiLibrary.CvArr;
import sj.opencv.jna.imgproc.ImgprocLibrary;

/**
 * @author siggi
 * @date Jul 5, 2012
 */
public class ImgProc {

	static{
		OpenCV.initialize();
	}

	public enum SmoothType{
		CV_BLUR_NO_SCALE( ImgprocLibrary.CV_BLUR_NO_SCALE ),
		CV_BLUR( ImgprocLibrary.CV_BLUR ),
		CV_GAUSSIAN( ImgprocLibrary.CV_GAUSSIAN ),
		CV_MEDIAN( ImgprocLibrary.CV_MEDIAN ),
		CV_BILATERAL( ImgprocLibrary.CV_BILATERAL );

		private final int open_cv_constant;
		SmoothType(int constant){this.open_cv_constant=constant;}
	}

	public enum InterpType{
		CV_INTER_NN( ImgprocLibrary.CV_INTER_NN ),
		CV_INTER_LINEAR( ImgprocLibrary.CV_INTER_LINEAR ),
		CV_INTER_CUBIC( ImgprocLibrary.CV_INTER_CUBIC ),
		CV_INTER_AREA( ImgprocLibrary.CV_INTER_AREA ),
		CV_INTER_LANCZOS4( ImgprocLibrary.CV_INTER_LANCZOS4 );

		private final int open_cv_constant;
		InterpType(int constant){this.open_cv_constant=constant;}
	}

	/**
	 * Transforms the source image using the specified map:
	 * <p>
	 * <code>dst(x,y) = src(mapx(x,y), mapy(x,y))</code>
	 * <p>
	 * The specified interpolation method is used to extract pixels with non-integer coordinates.
	 * Those pixels in the destination image, for which there is no correspondent pixels in the
	 * source image, are filled with the specified fill value.
	 *
	 *
	 * @param src - The source image.
	 * @param dst - The destination image.
	 * @param mapx - The map of x-coordinates (must be a 32-bit float image).
	 * @param mapy - The map of y-coordinated (must be a 32-bit float image).
	 * @param mode - The interpolation mode.
	 * @param fillOutliers - A boolean value indicating if destination pixels that correspond
	 * to outliers in the source image should be set to <code>fillValue</code>.
	 * @param - A pixel value to fill outliers.
	 */
	public static void remap(IplImage src, IplImage dst, IplImage mapx, IplImage mapy, InterpolationMode mode, boolean fillOutliers, Scalar fillValue) {
		long flags = mode.getConstant();
		if (fillOutliers){
			flags = flags | WarpMode.CV_WARP_FILL_OUTLIERS.getConstant();
		}

		IMGPROC.cvRemap(src.getCvArr(), dst.getCvArr(), mapx.getCvArr(), mapy.getCvArr(), (int)flags, new ByValue(fillValue.getArray()));
	}

	public static void calcHist(IplImage image, Histogram hist, int accumulate, IplImage mask) {
		IMGPROC.cvCalcArrHist(new JNAIplImage.ByReference(image.getPointer()), new CvHistogram.ByReference(hist.getPointer()), accumulate, mask==null?null:mask.getCvArr());
	}

	public static void resize(IplImage src, IplImage dst){
		resize(src, dst, InterpType.CV_INTER_NN);
	}

	public static void resize(IplImage src, IplImage dst, InterpType interpolation){
		IMGPROC.cvResize(src.getCvArr(), dst.getCvArr(), interpolation.open_cv_constant);
	}

	/**
	 * Smooths the image in one of several ways (see opencv docs)
	 */
	public static void smooth(IplImage src, IplImage dst, SmoothType smooth_type, int size1, int size2, double sigma1, double sigma2){
		if( size1%2 == 0 || size2%2 == 0) throw new IllegalArgumentException("size1 and size2 need to be odd numbers");
		IMGPROC.cvSmooth(src.getCvArr(), dst.getCvArr(), smooth_type.open_cv_constant, size1, size2, sigma1, sigma2);
	}


	/**
	 * Implements the Canny algorithm for edge detection
	 * The function finds the edges on the input image image and marks them in the output image edges using the Canny algorithm.
	 * The smallest value between threshold1 and threshold2 is used for edge linking, the largest value is used to find the initial segments of strong edges.
	 * @param src
	 * @param dst
	 * @param threshold1
	 * @param threshold2
	 * @param aperture_size (1, 3, 5 or 7)
	 */
	public static void canny(IplImage src, IplImage dst, double threshold1, double threshold2, int aperture_size){
		if( !(aperture_size == 3 || aperture_size == 5 || aperture_size == 7)) throw new IllegalArgumentException("aperture_size needs to be one of 1, 3, 5 or 7");

		IMGPROC.cvCanny(src.getCvArr(), dst.getCvArr(), threshold1, threshold2, aperture_size);
	}


	public enum ThresholdType {
		CV_THRESH_BINARY	( ImgprocLibrary.CV_THRESH_BINARY ),
		CV_THRESH_BINARY_INV	( ImgprocLibrary.CV_THRESH_BINARY_INV ),
		CV_THRESH_TRUNC	( ImgprocLibrary.CV_THRESH_TRUNC ),
		CV_THRESH_TOZERO	( ImgprocLibrary.CV_THRESH_TOZERO ),
		CV_THRESH_TOZERO_INV	( ImgprocLibrary.CV_THRESH_TOZERO_INV ),
		CV_THRESH_MASK	( ImgprocLibrary.CV_THRESH_MASK ),
		CV_THRESH_OTSU	( ImgprocLibrary.CV_THRESH_OTSU );

		private final int open_cv_constant;
		ThresholdType(int constant){this.open_cv_constant=constant;}
		public final int getConstant(){return open_cv_constant;};
	};

	/**
	 * Applies a fixed-level threshold to array elements
	 * @param src – Source array (single-channel, 8-bit or 32-bit floating point)
	 * @param dst – Destination array; must be either the same type as src or 8-bit
	 * @param threshold – Threshold value
	 * @param maxValue – Maximum value to use with CV_THRESH_BINARY and CV_THRESH_BINARY_INV thresholding types
	 * @param thresholdType – Thresholding type (see the discussion)
	 */
	public static void threshold(IplImage src, IplImage dst, double threshold, double maxValue, ThresholdType threshold_type){
		IMGPROC.cvThreshold(src.getCvArr(), dst.getCvArr(), threshold, maxValue, threshold_type.open_cv_constant);
	}
	
	/**
	 * Convolves the image with the kernel<br>
	 * @param src Src image
	 * @param dst Dst Image
	 * @param kernel Kernel to convolve
	 * @param anchor Anchor point
	 */
	public static void filter2d(IplImage src, IplImage dst, Mat kernel, Point anchor){
		CvMat cvKernel = kernel.getJNACvMat();
		IMGPROC.cvFilter2D(src.getCvArr(), dst.getCvArr(), cvKernel, new CvPoint.ByValue(anchor.x, anchor.y));
	}
	
	

	public enum FloodFillConnectivity{
		FOUR(4),
		EIGHT(8);

		private final int open_cv_constant;
		FloodFillConnectivity(int constant){this.open_cv_constant=constant;}
	}

	public enum FloodFillFlags{
		CV_FLOODFILL_FIXED_RANGE( ImgprocLibrary.CV_FLOODFILL_FIXED_RANGE ),
		CV_FLOODFILL_MASK_ONLY( ImgprocLibrary.CV_FLOODFILL_MASK_ONLY );

		private final int open_cv_constant;
		FloodFillFlags(int constant){this.open_cv_constant=constant;}
	}

	/**
	 * Fills a connected component with the given color
	 *
	 * @param image – Input 1- or 3-channel, 8-bit or floating-point image. It is modified by the function unless the CV_FLOODFILL_MASK_ONLY flag is set (see below)
	 * @param seed_point – The starting point
	 * @param lo_diff – Maximal lower brightness/color difference between the currently observed pixel and one of its neighbors belonging to the component, or a seed pixel being added to the component. In the case of 8-bit color images it is a packed value
	 * @param up_diff – Maximal upper brightness/color difference between the currently observed pixel and one of its neighbors belonging to the component, or a seed pixel being added to the component. In the case of 8-bit color images it is a packed value
	 * @param mask – Operation mask, should be a single-channel 8-bit image, 2 pixels wider and 2 pixels taller than image . If not NULL, the function uses and updates the mask, so the user takes responsibility of initializing the mask content. Floodfilling can’t go across non-zero pixels in the mask, for example, an edge detector output can be used as a mask to stop filling at edges. It is possible to use the same mask in multiple calls to the function to make sure the filled area do not overlap. Note : because the mask is larger than the filled image, a pixel in mask that corresponds to (x,y) pixel in image will have coordinates
	 */
	public static void floodFill(IplImage src, Point seed_point, Scalar lo_diff, Scalar up_diff, IplImage mask){
		floodFill(src, seed_point, new Scalar(0), lo_diff, up_diff, new FloodFillFlags[]{FloodFillFlags.CV_FLOODFILL_MASK_ONLY}, mask);
	}

	/**
	 * Fills a connected component with the given color
	 *
	 * @param image – Input 1- or 3-channel, 8-bit or floating-point image. It is modified by the function unless the CV_FLOODFILL_MASK_ONLY flag is set (see below)
	 * @param seed_point – The starting point
	 * @param new_val – New value of the repainted domain pixels
	 * @param lo_diff – Maximal lower brightness/color difference between the currently observed pixel and one of its neighbors belonging to the component, or a seed pixel being added to the component. In the case of 8-bit color images it is a packed value
	 * @param up_diff – Maximal upper brightness/color difference between the currently observed pixel and one of its neighbors belonging to the component, or a seed pixel being added to the component. In the case of 8-bit color images it is a packed value
	 * @param flags – The operation flags. Lower bits contain connectivity value, 4 (by default) or 8, used within the function. Connectivity determines which neighbors of a pixel are considered. Upper bits can be 0 or a combination of the following flags:
	 * 		- CV_FLOODFILL_FIXED_RANGE if set, the difference between the current pixel and seed pixel is considered, otherwise the difference between neighbor pixels is considered (the range is floating)
	 * 		- CV_FLOODFILL_MASK_ONLY if set, the function does not fill the image ( new_val is ignored), but fills the mask (that must be non-NULL in this case)
	 * @param mask – Operation mask, should be a single-channel 8-bit image, 2 pixels wider and 2 pixels taller than image . If not NULL, the function uses and updates the mask, so the user takes responsibility of initializing the mask content. Floodfilling can’t go across non-zero pixels in the mask, for example, an edge detector output can be used as a mask to stop filling at edges. It is possible to use the same mask in multiple calls to the function to make sure the filled area do not overlap. Note : because the mask is larger than the filled image, a pixel in mask that corresponds to (x,y) pixel in image will have coordinates
	 */
	public static void floodFill(IplImage src, Point seed_point, Scalar new_val, Scalar lo_diff, Scalar up_diff, FloodFillFlags[] flags, IplImage mask){
		int intflag = 0;
		for (FloodFillFlags floodFillFlags : flags) {
			intflag |= floodFillFlags.open_cv_constant;
		}
		IMGPROC.cvFloodFill(src.getCvArr(), new CvPoint.ByValue(seed_point.x, seed_point.y), new CvScalar.ByValue(new_val.getArray()), new CvScalar.ByValue(lo_diff.getArray()), new CvScalar.ByValue(up_diff.getArray()), null, intflag, mask==null?null:mask.getCvArr());
	}

	public enum AdaptiveThreshAlg{
		CV_ADAPTIVE_THRESH_MEAN_C		( ImgprocLibrary.CV_ADAPTIVE_THRESH_MEAN_C ),
		CV_ADAPTIVE_THRESH_GAUSSIAN_C	( ImgprocLibrary.CV_ADAPTIVE_THRESH_GAUSSIAN_C );

		private final int open_cv_constant;
		AdaptiveThreshAlg(int constant){this.open_cv_constant=constant;}
		public final int getConstant(){return open_cv_constant;};
	}

	public enum AdaptiveThreshType{
		CV_THRESH_BINARY				( ImgprocLibrary.CV_THRESH_BINARY ),
		CV_THRESH_BINARY_INV			( ImgprocLibrary.CV_THRESH_BINARY_INV );

		private final int open_cv_constant;
		AdaptiveThreshType(int constant){this.open_cv_constant=constant;}
		public final int getConstant(){return open_cv_constant;};
	}

	/**
	 * Applies an adaptive threshold to an array with default values.
	 * @param src – Source image
	 * @param dst – Destination image
	 * @param maxValue – Maximum value that is used with CV_THRESH_BINARY and CV_THRESH_BINARY_INV
	 */
	public static void adaptiveThreshold(IplImage src, IplImage dst, double maxValue){
		adaptiveThreshold(src, dst, maxValue, AdaptiveThreshAlg.CV_ADAPTIVE_THRESH_MEAN_C, AdaptiveThreshType.CV_THRESH_BINARY, 3, 5);
	}

	/**
	 * Applies an adaptive threshold to an array.
	 * @param src – Source image
	 * @param dst – Destination image
	 * @param maxValue – Maximum value that is used with CV_THRESH_BINARY and CV_THRESH_BINARY_INV
	 * @param adaptive_method – Adaptive thresholding algorithm to use: CV_ADAPTIVE_THRESH_MEAN_C or CV_ADAPTIVE_THRESH_GAUSSIAN_C (see the discussion)
	 * @param thresholdType –Thresholding type;
	 * @param blockSize – The size of a pixel neighborhood that is used to calculate a threshold value for the pixel: 3, 5, 7, and so on
	 * @param param1 – The method-dependent parameter. For the methods CV_ADAPTIVE_THRESH_MEAN_C and CV_ADAPTIVE_THRESH_GAUSSIAN_C it is a constant subtracted from the mean or weighted mean (see the discussion), though it may be negative
	 */
	public static void adaptiveThreshold(IplImage src, IplImage dst, double maxValue, AdaptiveThreshAlg thresh_alg, AdaptiveThreshType thresh_type, int block_size, double param1){
		if( block_size % 2 != 1 ) throw new IllegalArgumentException("adaptiveThreshold: Block size must be an odd number: "+block_size);

		IMGPROC.cvAdaptiveThreshold(src.getCvArr(), dst.getCvArr(), maxValue, thresh_alg.getConstant(), thresh_type.getConstant(), block_size, param1);
	}

	public enum ColorConversion {

		CV_BGR2BGRA(3,4, ImgprocLibrary.CV_BGR2BGRA),
		CV_RGB2RGBA(3,4, ImgprocLibrary.CV_RGB2RGBA),

		CV_BGRA2BGR(4,3, ImgprocLibrary.CV_BGRA2BGR),
		CV_RGBA2RGB(4,3, ImgprocLibrary.CV_BGRA2BGR),

		CV_BGR2RGBA(3,4, ImgprocLibrary.CV_BGR2RGBA),
		CV_RGB2BGRA(3,4, ImgprocLibrary.CV_RGB2BGRA),

		CV_RGBA2BGR(4,3, ImgprocLibrary.CV_RGBA2BGR),
		CV_BGRA2RGB(4,3, ImgprocLibrary.CV_BGRA2RGB),

		CV_BGR2RGB(3,3, ImgprocLibrary.CV_BGR2RGB),
		CV_RGB2BGR(3,3, ImgprocLibrary.CV_RGB2BGR),

		CV_BGRA2RGBA(4,4, ImgprocLibrary.CV_BGRA2RGBA),
		CV_RGBA2BGRA(4,4, ImgprocLibrary.CV_RGBA2BGRA),

		CV_BGR2GRAY(3,1, ImgprocLibrary.CV_BGR2GRAY),
		CV_RGB2GRAY(3,1, ImgprocLibrary.CV_RGB2GRAY),
		CV_GRAY2BGR(1,3, ImgprocLibrary.CV_GRAY2BGR),
		CV_GRAY2RGB(1,3, ImgprocLibrary.CV_GRAY2RGB),
		CV_GRAY2BGRA(1,4, ImgprocLibrary.CV_GRAY2BGRA),
		CV_GRAY2RGBA(1,4, ImgprocLibrary.CV_GRAY2RGBA),
		CV_BGRA2GRAY(4,1, ImgprocLibrary.CV_BGRA2GRAY),
		CV_RGBA2GRAY(4,1,  ImgprocLibrary.CV_RGBA2GRAY),

		CV_BGR2BGR565(3,2, ImgprocLibrary.CV_BGR2BGR565),
		CV_RGB2BGR565(3,2, ImgprocLibrary.CV_RGB2BGR565),
		CV_BGR5652BGR(2,3, ImgprocLibrary.CV_BGR5652BGR),
		CV_BGR5652RGB(2,3, ImgprocLibrary.CV_BGR5652RGB),
		CV_BGRA2BGR565(4,2, ImgprocLibrary.CV_BGRA2BGR565),
		CV_RGBA2BGR565(4,2, ImgprocLibrary.CV_RGBA2BGR565),
		CV_BGR5652BGRA(2,4, ImgprocLibrary.CV_BGR5652BGRA),
		CV_BGR5652RGBA(2,4, ImgprocLibrary.CV_BGR5652RGBA),

		CV_GRAY2BGR565(1,2, ImgprocLibrary.CV_GRAY2BGR565),
		CV_BGR5652GRAY(2,1, ImgprocLibrary.CV_BGR5652GRAY),

		CV_BGR2BGR555(3,2, ImgprocLibrary.CV_BGR2BGR555),
		CV_RGB2BGR555(3,2, ImgprocLibrary.CV_RGB2BGR555),
		CV_BGR5552BGR(2,3, ImgprocLibrary.CV_BGR5552BGR),
		CV_BGR5552RGB(2,3, ImgprocLibrary.CV_BGR5552RGB),
		CV_BGRA2BGR555(4,2, ImgprocLibrary.CV_BGRA2BGR555),
		CV_RGBA2BGR555(4,2, ImgprocLibrary.CV_RGBA2BGR555),
		CV_BGR5552BGRA(2,4, ImgprocLibrary.CV_BGR5552BGRA),
		CV_BGR5552RGBA(2,4, ImgprocLibrary.CV_BGR5552RGBA),

		CV_GRAY2BGR555(1,2, ImgprocLibrary.CV_GRAY2BGR555),
		CV_BGR5552GRAY(2,1, ImgprocLibrary.CV_BGR5552GRAY),

		CV_BGR2XYZ(3,3, ImgprocLibrary.CV_BGR2XYZ),
		CV_RGB2XYZ(3,3, ImgprocLibrary.CV_RGB2XYZ),
		CV_XYZ2BGR(3,3, ImgprocLibrary.CV_XYZ2BGR),
		CV_XYZ2RGB(3,3, ImgprocLibrary.CV_XYZ2RGB),

		CV_BGR2YCrCb(3,3, ImgprocLibrary.CV_BGR2YCrCb),
		CV_RGB2YCrCb(3,3, ImgprocLibrary.CV_RGB2YCrCb),
		CV_YCrCb2BGR(3,3, ImgprocLibrary.CV_YCrCb2BGR),
		CV_YCrCb2RGB(3,3, ImgprocLibrary.CV_YCrCb2RGB),

		CV_BGR2HSV(3,3, ImgprocLibrary.CV_BGR2HSV),
		CV_RGB2HSV(3,3, ImgprocLibrary.CV_RGB2HSV),

		CV_BGR2Lab(3,3, ImgprocLibrary.CV_BGR2Lab),
		CV_RGB2Lab(3,3, ImgprocLibrary.CV_RGB2Lab),

		CV_BayerBG2BGR(2,3, ImgprocLibrary.CV_BayerBG2BGR),
		CV_BayerGB2BGR(2,3, ImgprocLibrary.CV_BayerGB2BGR),
		CV_BayerRG2BGR(2,3, ImgprocLibrary.CV_BayerRG2BGR),
		CV_BayerGR2BGR(2,3, ImgprocLibrary.CV_BayerGR2BGR),

		CV_BayerBG2RGB(2,3, ImgprocLibrary.CV_BayerBG2RGB),
		CV_BayerGB2RGB(2,3, ImgprocLibrary.CV_BayerGB2RGB),
		CV_BayerRG2RGB(2,3, ImgprocLibrary.CV_BayerRG2RGB),
		CV_BayerGR2RGB(2,3, ImgprocLibrary.CV_BayerGR2RGB),

		CV_BGR2Luv(3,3, ImgprocLibrary.CV_BGR2Luv),
		CV_RGB2Luv(3,3, ImgprocLibrary.CV_RGB2Luv),
		CV_BGR2HLS(3,3, ImgprocLibrary.CV_BGR2HLS),
		CV_RGB2HLS(3,3, ImgprocLibrary.CV_BGR2HLS),

		CV_HSV2BGR(3,3, ImgprocLibrary.CV_HSV2BGR),
		CV_HSV2RGB(3,3, ImgprocLibrary.CV_HSV2RGB),

		CV_Lab2BGR(3,3, ImgprocLibrary.CV_Lab2BGR),
		CV_Lab2RGB(3,3, ImgprocLibrary.CV_Lab2RGB),
		CV_Luv2BGR(3,3, ImgprocLibrary.CV_Luv2BGR),
		CV_Luv2RGB(3,3, ImgprocLibrary.CV_Luv2RGB),
		CV_HLS2BGR(3,3, ImgprocLibrary.CV_HLS2BGR),
		CV_HLS2RGB(3,3, ImgprocLibrary.CV_HLS2RGB),

		CV_BayerBG2BGR_VNG	( 2,3,ImgprocLibrary.CV_BayerBG2BGR_VNG ),
		CV_BayerGB2BGR_VNG	( 2,3,ImgprocLibrary.CV_BayerGB2BGR_VNG ),
		CV_BayerRG2BGR_VNG	( 2,3,ImgprocLibrary.CV_BayerRG2BGR_VNG ),
		CV_BayerGR2BGR_VNG	( 2,3,ImgprocLibrary.CV_BayerGR2BGR_VNG ),
		CV_BayerBG2RGB_VNG	( 2,3,ImgprocLibrary.CV_BayerBG2RGB_VNG ),
		CV_BayerGB2RGB_VNG	( 2,3,ImgprocLibrary.CV_BayerGB2RGB_VNG ),
		CV_BayerRG2RGB_VNG	( 2,3,ImgprocLibrary.CV_BayerRG2RGB_VNG ),
		CV_BayerGR2RGB_VNG	( 2,3,ImgprocLibrary.CV_BayerGR2RGB_VNG ),
		CV_BGR2HSV_FULL	( 3,3,ImgprocLibrary.CV_BGR2HSV_FULL ),
		CV_RGB2HSV_FULL	( 3,3,ImgprocLibrary.CV_RGB2HSV_FULL ),
		CV_BGR2HLS_FULL	( 3,3,ImgprocLibrary.CV_BGR2HLS_FULL ),
		CV_RGB2HLS_FULL	( 3,3,ImgprocLibrary.CV_RGB2HLS_FULL ),
		CV_HSV2BGR_FULL	( 3,3,ImgprocLibrary.CV_HSV2BGR_FULL ),
		CV_HSV2RGB_FULL	( 3,3,ImgprocLibrary.CV_HSV2RGB_FULL ),
		CV_HLS2BGR_FULL	( 3,3,ImgprocLibrary.CV_HLS2BGR_FULL ),
		CV_HLS2RGB_FULL	( 3,3,ImgprocLibrary.CV_HLS2RGB_FULL ),
		CV_LBGR2Lab	( 4,3,ImgprocLibrary.CV_LBGR2Lab ),
		CV_LRGB2Lab	( 4,3,ImgprocLibrary.CV_LRGB2Lab ),
		CV_LBGR2Luv	( 4,3,ImgprocLibrary.CV_LBGR2Luv ),
		CV_LRGB2Luv	( 4,3,ImgprocLibrary.CV_LRGB2Luv ),
		CV_Lab2LBGR	( 3,4,ImgprocLibrary.CV_Lab2LBGR ),
		CV_Lab2LRGB	( 3,4,ImgprocLibrary.CV_Lab2LRGB ),
		CV_Luv2LBGR	( 3,4,ImgprocLibrary.CV_Luv2LBGR ),
		CV_Luv2LRGB	( 3,4,ImgprocLibrary.CV_Luv2LRGB ),
		CV_BGR2YUV	( 3,3,ImgprocLibrary.CV_BGR2YUV ),
		CV_RGB2YUV	( 3,3,ImgprocLibrary.CV_RGB2YUV ),
		CV_YUV2BGR	( 3,3,ImgprocLibrary.CV_YUV2BGR ),
		CV_YUV2RGB	( 3,3,ImgprocLibrary.CV_YUV2RGB ),
		CV_BayerBG2GRAY	( 2,1,ImgprocLibrary.CV_BayerBG2GRAY ),
		CV_BayerGB2GRAY	( 2,1,ImgprocLibrary.CV_BayerGB2GRAY ),
		CV_BayerRG2GRAY	( 2,1,ImgprocLibrary.CV_BayerRG2GRAY ),
		CV_BayerGR2GRAY	( 2,1,ImgprocLibrary.CV_BayerGR2GRAY ),
		CV_YUV2RGB_NV12	( 3,3,ImgprocLibrary.CV_YUV2RGB_NV12 ),
		CV_YUV2BGR_NV12	( 3,3,ImgprocLibrary.CV_YUV2BGR_NV12 ),
		CV_YUV2RGB_NV21	( 3,3,ImgprocLibrary.CV_YUV2RGB_NV21 ),
		CV_YUV2BGR_NV21	( 3,3,ImgprocLibrary.CV_YUV2BGR_NV21 ),
		CV_YUV420sp2RGB	( 3,3,ImgprocLibrary.CV_YUV420sp2RGB ),
		CV_YUV420sp2BGR	( 3,3,ImgprocLibrary.CV_YUV420sp2BGR ),
		CV_YUV2RGBA_NV12	( 3,4,ImgprocLibrary.CV_YUV2RGBA_NV12 ),
		CV_YUV2BGRA_NV12	( 3,4,ImgprocLibrary.CV_YUV2BGRA_NV12 ),
		CV_YUV2RGBA_NV21	( 3,4,ImgprocLibrary.CV_YUV2RGBA_NV21 ),
		CV_YUV2BGRA_NV21	( 3,4,ImgprocLibrary.CV_YUV2BGRA_NV21 ),
		CV_YUV420sp2RGBA	( 3,4,ImgprocLibrary.CV_YUV420sp2RGBA ),
		CV_YUV420sp2BGRA	( 3,4,ImgprocLibrary.CV_YUV420sp2BGRA ),
		CV_YUV2RGB_YV12	( 3,3,ImgprocLibrary.CV_YUV2RGB_YV12 ),
		CV_YUV2BGR_YV12	( 3,3,ImgprocLibrary.CV_YUV2BGR_YV12 ),
		CV_YUV2RGB_IYUV	( 3,3,ImgprocLibrary.CV_YUV2RGB_IYUV ),
		CV_YUV2BGR_IYUV	( 3,3,ImgprocLibrary.CV_YUV2BGR_IYUV ),
		CV_YUV2RGB_I420	( 3,3,ImgprocLibrary.CV_YUV2RGB_I420 ),
		CV_YUV2BGR_I420	( 3,3,ImgprocLibrary.CV_YUV2BGR_I420 ),
		CV_YUV420p2RGB	( 3,3,ImgprocLibrary.CV_YUV420p2RGB ),
		CV_YUV420p2BGR	( 3,3,ImgprocLibrary.CV_YUV420p2BGR ),
		CV_YUV2RGBA_YV12	( 3,4,ImgprocLibrary.CV_YUV2RGBA_YV12 ),
		CV_YUV2BGRA_YV12	( 3,4,ImgprocLibrary.CV_YUV2BGRA_YV12 ),
		CV_YUV2RGBA_IYUV	( 3,4,ImgprocLibrary.CV_YUV2RGBA_IYUV ),
		CV_YUV2BGRA_IYUV	( 3,4,ImgprocLibrary.CV_YUV2BGRA_IYUV ),
		CV_YUV2RGBA_I420	( 3,4,ImgprocLibrary.CV_YUV2RGBA_I420 ),
		CV_YUV2BGRA_I420	( 3,4,ImgprocLibrary.CV_YUV2BGRA_I420 ),
		CV_YUV420p2RGBA	( 3,4,ImgprocLibrary.CV_YUV420p2RGBA ),
		CV_YUV420p2BGRA	( 3,4,ImgprocLibrary.CV_YUV420p2BGRA ),
		CV_YUV2GRAY_420	( 3,1,ImgprocLibrary.CV_YUV2GRAY_420 ),
		CV_YUV2GRAY_NV21	( 3,1,ImgprocLibrary.CV_YUV2GRAY_NV21 ),
		CV_YUV2GRAY_NV12	( 3,1,ImgprocLibrary.CV_YUV2GRAY_NV12 ),
		CV_YUV2GRAY_YV12	( 3,1,ImgprocLibrary.CV_YUV2GRAY_YV12 ),
		CV_YUV2GRAY_IYUV	( 3,1,ImgprocLibrary.CV_YUV2GRAY_IYUV ),
		CV_YUV2GRAY_I420	( 3,1,ImgprocLibrary.CV_YUV2GRAY_I420 ),
		CV_YUV420sp2GRAY	( 3,1,ImgprocLibrary.CV_YUV420sp2GRAY ),
		CV_YUV420p2GRAY	( 3,1,ImgprocLibrary.CV_YUV420p2GRAY ),
		CV_YUV2RGB_UYVY	( 3,3,ImgprocLibrary.CV_YUV2RGB_UYVY ),
		CV_YUV2BGR_UYVY	( 3,3,ImgprocLibrary.CV_YUV2BGR_UYVY ),
		CV_YUV2RGB_Y422	( 3,3,ImgprocLibrary.CV_YUV2RGB_Y422 ),
		CV_YUV2BGR_Y422	( 3,3,ImgprocLibrary.CV_YUV2BGR_Y422 ),
		CV_YUV2RGB_UYNV	( 3,3,ImgprocLibrary.CV_YUV2RGB_UYNV ),
		CV_YUV2BGR_UYNV	( 3,3,ImgprocLibrary.CV_YUV2BGR_UYNV ),
		CV_YUV2RGBA_UYVY	( 3,4,ImgprocLibrary.CV_YUV2RGBA_UYVY ),
		CV_YUV2BGRA_UYVY	( 3,4,ImgprocLibrary.CV_YUV2BGRA_UYVY ),
		CV_YUV2RGBA_Y422	( 3,4,ImgprocLibrary.CV_YUV2RGBA_Y422 ),
		CV_YUV2BGRA_Y422	( 3,4,ImgprocLibrary.CV_YUV2BGRA_Y422 ),
		CV_YUV2RGBA_UYNV	( 3,4,ImgprocLibrary.CV_YUV2RGBA_UYNV ),
		CV_YUV2BGRA_UYNV	( 3,4,ImgprocLibrary.CV_YUV2BGRA_UYNV ),
		CV_YUV2RGB_YUY2	( 3,3,ImgprocLibrary.CV_YUV2RGB_YUY2 ),
		CV_YUV2BGR_YUY2	( 3,3,ImgprocLibrary.CV_YUV2BGR_YUY2 ),
		CV_YUV2RGB_YVYU	( 3,3,ImgprocLibrary.CV_YUV2RGB_YVYU ),
		CV_YUV2BGR_YVYU	( 3,3,ImgprocLibrary.CV_YUV2BGR_YVYU ),
		CV_YUV2RGB_YUYV	( 3,3,ImgprocLibrary.CV_YUV2RGB_YUYV ),
		CV_YUV2BGR_YUYV	( 3,3,ImgprocLibrary.CV_YUV2BGR_YUYV ),
		CV_YUV2RGB_YUNV	( 3,3,ImgprocLibrary.CV_YUV2RGB_YUNV ),
		CV_YUV2BGR_YUNV	( 3,3,ImgprocLibrary.CV_YUV2BGR_YUNV ),
		CV_YUV2RGBA_YUY2	( 3,4,ImgprocLibrary.CV_YUV2RGBA_YUY2 ),
		CV_YUV2BGRA_YUY2	( 3,4,ImgprocLibrary.CV_YUV2BGRA_YUY2 ),
		CV_YUV2RGBA_YVYU	( 3,4,ImgprocLibrary.CV_YUV2RGBA_YVYU ),
		CV_YUV2BGRA_YVYU	( 3,4,ImgprocLibrary.CV_YUV2BGRA_YVYU ),
		CV_YUV2RGBA_YUYV	( 3,4,ImgprocLibrary.CV_YUV2RGBA_YUYV ),
		CV_YUV2BGRA_YUYV	( 3,4,ImgprocLibrary.CV_YUV2BGRA_YUYV ),
		CV_YUV2RGBA_YUNV	( 3,4,ImgprocLibrary.CV_YUV2RGBA_YUNV ),
		CV_YUV2BGRA_YUNV	( 3,4,ImgprocLibrary.CV_YUV2BGRA_YUNV ),
		CV_YUV2GRAY_UYVY	( 3,1,ImgprocLibrary.CV_YUV2GRAY_UYVY ),
		CV_YUV2GRAY_YUY2	( 3,1,ImgprocLibrary.CV_YUV2GRAY_YUY2 ),
		CV_YUV2GRAY_Y422	( 3,1,ImgprocLibrary.CV_YUV2GRAY_Y422 ),
		CV_YUV2GRAY_UYNV	( 3,1,ImgprocLibrary.CV_YUV2GRAY_UYNV ),
		CV_YUV2GRAY_YVYU	( 3,1,ImgprocLibrary.CV_YUV2GRAY_YVYU ),
		CV_YUV2GRAY_YUYV	( 3,1,ImgprocLibrary.CV_YUV2GRAY_YUYV ),
		CV_YUV2GRAY_YUNV	( 3,1,ImgprocLibrary.CV_YUV2GRAY_YUNV );




		private final int open_cv_constant;
		private final int source_channels;
		private final int dest_channels;
		ColorConversion(int src_channels, int dest_channels, int constant){
			this.open_cv_constant = constant;
			this.source_channels = src_channels;
			this.dest_channels = dest_channels;
		}
		public final int getConstant(){return open_cv_constant;};
		public final int getSrcChannels(){return source_channels;};
		public final int getDstChannels(){return dest_channels;};
	}

	/**
	 * Converts the colormode of src according to the conversion method provided.
	 *
	 * @param src source image
	 * @param dst result is stored in this image
	 * @param conversion color conversion method
	 */
	public static void cvtColor(IplImage src, IplImage dst, ColorConversion conversion){
		if( src.getColorModel().getNumberOfChannels() != conversion.getSrcChannels() || dst.getColorModel().getNumberOfChannels() != conversion.getDstChannels() )
			throw new RuntimeException("Number of channels must match between IPLImages and conversion code");

		IMGPROC.cvCvtColor(new CvArr(src.getPointer()), new CvArr(dst.getPointer()), conversion.getConstant());
	}

	/**
	 * Converts the colormode of src to the colormode of dst and stores the result in dst
	 * @param src source image
	 * @param dst result will have the colormode of dst and stored in dst
	 */
	public static void cvtColor(IplImage src, IplImage dst){
		ColorConversion convert = null;

		if( src.getColorModel().equals( dst.getColorModel() ) ){
			CxCore.copy(src, dst);
		}
		else{
			try{
				convert = ColorConversion.valueOf( "CV_" + src.getColorModel().toString()+"2"+dst.getColorModel().toString() );
			}
			catch (IllegalArgumentException e) {
				throw new RuntimeException("Can't convert from "+src.getColorModel().toString()+" to "+dst.getColorModel().toString());
			}

			IMGPROC.cvCvtColor(new CvArr(src.getPointer()), new CvArr(dst.getPointer()), convert.getConstant());
		}
	}


	/* ******************************************************************************
	 *  							CALIBRATION                                     *
	 * ******************************************************************************/

	/**
	 * Computes an undistortion map.
	 *
	 * @param cameraMatrix - A 3-by-3 matrix packed in row order containing the intrinsic parameters
	 * of the camera.
	 * @param distortion - The distortion coefficients of the camera.
	 * @param mapx - The output map of x-coordinates (must be a 32-bit float one channels image, or
	 * a 16-bit unsigned short one channel image).
	 * @param mapy - The output map of y-coordinates (must be a 32-bit float one channels image, or
	 * a 16-bit unsigned short one channel image).
	 */
	public static void initUndistortMap(float[] cameraMatrix, float[] distortion, IplImage mapx, IplImage mapy) {
		CvMat cam_mat = CxCore.createMat(3, 3, cameraMatrix).getJNACvMat();
		CvMat dist_mat = CxCore.createMat(4, 1, distortion).getJNACvMat();

		IMGPROC.cvInitUndistortMap(cam_mat, dist_mat, mapx.getCvArr(), mapy.getCvArr());
	}


	/**
	 * Transforms the image to compensate radial and tangential lens distortion.
	 *
	 * The function is simply a combination of <code>initUndistortRectifyMap()</code> (with unity R )
	 * and <code>remap</code> (with bilinear interpolation). Those pixels in the destination image,
	 * for which there is no correspondent pixels in the source image, are filled with 0’s (black color).
	 * <p>
	 * TODO: Add the <code>newCameraMatrix</code> parameter.
	 *
	 * @param src - The input (distorted) image.
	 * @param dst - The output (corrected) image; will have the same size and the same type as <code>src</code>.
	 * @param cameraMatrix - A 3-by-3 matrix packed in row order contaning the intrinsic camera parameters.
	 * @param distortion - The distortion coefficients of the camera.
	 */
	public static void undistort2(IplImage src, IplImage dst, float[] cameraMatrix, float[] distortion) {
		CvMat cam_mat = CxCore.createMat(3, 3, cameraMatrix).getJNACvMat();
		CvMat dist_mat = CxCore.createMat(4, 1, distortion).getJNACvMat();

		IMGPROC.cvUndistort2(src.getCvArr(), dst.getCvArr(), cam_mat, dist_mat, null);
	}

	public static void 	findCornerSubPix(IplImage img, Point2D.Float[] corners, Dimension winSize, Dimension zeroZoneSize, TermCriteriaType criteria[], int max_iter, double epsilon){
		CvPoint2D32f first_point = new CvPoint2D32f();
		CvPoint2D32f[] array = (CvPoint2D32f[]) first_point.toArray( corners.length );

		for(int i=0; i<corners.length; i++){
			array[i] = new CvPoint2D32f(corners[i].x, corners[i].y);
			array[i].write();
		}

		int criteria_type = 0;
		for (TermCriteriaType c : criteria) {
			criteria_type |= c.getConstant();
		}

		CvTermCriteria.ByValue crit = new CvTermCriteria.ByValue(criteria_type, max_iter, epsilon);
		IMGPROC.cvFindCornerSubPix(img.getCvArr(), first_point, corners.length, new CvSize.ByValue(winSize.width, winSize.height), new CvSize.ByValue(zeroZoneSize.width, zeroZoneSize.height), crit);

		for(int i=0; i<corners.length; i++){
			array[i].read();
			corners[i].setLocation( array[i].x , array[i].y );
		}
	}

	public static Histogram createHist(int dims, int[] sizes, HistogramType type, float[][] ranges, int uniform) {
		CvHistogram cvCreateHist = IMGPROC.cvCreateHist(dims, sizes, (int)type.getConstant(), ranges, uniform);
		return new Histogram(cvCreateHist);
	}


}
