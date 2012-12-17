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
import java.awt.geom.Point2D;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.PointerByReference;

import sj.opencv.Constants.ColorModel;
import sj.opencv.jna.cxcore.CvContour;
import sj.opencv.jna.cxcore.CvMat;
import sj.opencv.jna.cxcore.CvMemStorage;
import sj.opencv.jna.cxcore.CvPoint;
import sj.opencv.jna.cxcore.CvRect;
import sj.opencv.jna.cxcore.CvScalar;
import sj.opencv.jna.cxcore.CvSeq;
import sj.opencv.jna.cxcore.CvSeqBlock;
import sj.opencv.jna.cxcore.CvSeqReader;
import sj.opencv.jna.cxcore.CvSlice;
import sj.opencv.jna.cxcore.CxcoreLibrary;
import sj.opencv.jna.highgui.HighguiLibrary;
import sj.opencv.jna.highgui.HighguiLibrary.CvArr;
import sj.opencv.jna.highgui.HighguiLibrary.CvCapture;
import sj.opencv.jna.imgproc.CvMoments;
import sj.opencv.jna.imgproc.ImgprocLibrary;
import static sj.opencv.jna.JNAOpenCV.*;

/**
 * Author: Siggi & Julián
 * Date: Jul 28, 2010
 */
public class OpenCV {

	public static String getOpenCVVersion(){
		return "2.4.0";
	}

	public static String getWrapperVersion(){
		return "0.2";
	}

	private static boolean initialized = false;

	static {
		initialize();
	}

	/**
	 * This method simply initializes some static structures and loads libraries etc.
	 */
	public static void initialize(){
		if( initialized ) return;

		OpenCVLibLoader.loadNative();
		BasePointer.initialize();

		Native.setCallbackExceptionHandler(new Callback.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Callback arg0, Throwable arg1) {
				throw new RuntimeException("Uncaught (but caught) exception from native library: "+arg1.getMessage());
			}
		});

		initialized = true;
	}


	/**
	 * A convenience method that converts in incoming image into the ARGB color model
	 * @param im_src
	 * @param im_dest
	 */
	public static void convert2ARGB(IplImage im_src, IplImage im_dest){

		if( im_dest.getColorModel() != ColorModel.GENERIC_4_CHANNEL ){
			throw new RuntimeException("Destination image must be of color type "+ColorModel.GENERIC_4_CHANNEL);
		}

		long type = -1;
		if		( im_src.getColorModel() == ColorModel.GRAY || im_src.getColorModel() == ColorModel.GENERIC_1_CHANNEL ) type = 0;
		else if	( im_src.getColorModel() == ColorModel.RGB ) type = 1;
		else if	( im_src.getColorModel() == ColorModel.BGR || im_src.getColorModel() == ColorModel.GENERIC_3_CHANNEL ) type = 2;
		else if	( im_src.getColorModel() == ColorModel.BGRA || im_src.getColorModel() == ColorModel.GENERIC_4_CHANNEL ) type = 3;
		else{
			throw new RuntimeException("Can not convert input color model of "+im_src.getColorModel());
		}

		CXCORE.cvSet(new CvArr(im_dest.getPointer()), new CvScalar.ByValue(new double[]{255,255,255,255}), null);

		if( type == 0 ){
			int from_to[] = { 0,1,  0,2,  0,3 };
			CXCORE.cvMixChannels(new PointerByReference(im_src.getPointer()), 1, new PointerByReference(im_dest.getPointer()), 1, from_to, 3);
		}
		else if( type == 1 ){
			int from_to[] = { 0,1,  1,2,  2,3 };
			CXCORE.cvMixChannels(new PointerByReference(im_src.getPointer()), 1, new PointerByReference(im_dest.getPointer()), 1, from_to, 3);
		}
		else if( type == 2 ){
			int from_to[] = { 0,3,  1,2,  2,1 };
			CXCORE.cvMixChannels(new PointerByReference(im_src.getPointer()), 1, new PointerByReference(im_dest.getPointer()), 1, from_to, 3);
		}
		else if( type == 3 ){
			int from_to[] = { 0,3,  1,2,  2,1,  3,0 };
			CXCORE.cvMixChannels(new PointerByReference(im_src.getPointer()), 1, new PointerByReference(im_dest.getPointer()), 1, from_to, 4);
		}
	}


//	/* ******************************************************************************
//	 *  							CLUSTERING                                      *
//	 * ******************************************************************************/
//
//	// TODO: Add the remaining parameters of this function.
////	public static void kMeans2(float[] samples, int sampleSize, int nSamples, int clusterCount,
////			int[] labels, TermCriteria criteria) {
////
////		long criteriaType = 0;
////		for (TermCriteriaType t : criteria.type) {
////			criteriaType = criteriaType | t.getConstant();
////		}
////
////		kMeans2Native(samples, sampleSize, nSamples, clusterCount, labels, criteriaType,
////				criteria.maxIter, criteria.epsilon);
////	}
////
////	//private native static void kMeans2Native(float[] samples, int sampleSize, int nSamples,
////			int clusterCount, int[] labels, long termCriteriaType, int termCriteriaMaxIter,
////			double termCriteriaEpsilon);
//
//
//	public static FLANNIndex FLANNIndex(float[] features, int featureSize, int nFeatures, FLANNIndexParams params) {
//
//		long indexPointer = 0;
//
//		if (params instanceof FLANNIndexParams.LinearIndexParams) {
//
//			indexPointer = FLANNLinearIndexNative(features, featureSize, nFeatures);
//
//		} else if (params instanceof FLANNIndexParams.KDTreeIndexParams) {
//
//			FLANNIndexParams.KDTreeIndexParams p = (FLANNIndexParams.KDTreeIndexParams) params;
//			indexPointer = FLANNKDTreeIndexNative(features, featureSize, nFeatures, p.trees);
//		}
//
//		return new FLANNIndex(indexPointer);
//	}
//
//	//private native static long FLANNLinearIndexNative(float[] features, int featureSize, int nFeatures);
//	//private native static long FLANNKDTreeIndexNative(float[] features, int featureSize, int nFeatures, int trees);
//
//	public static void knnSearch(FLANNIndex index, float[] queries, int querySize, int nQueries,
//			int[] indices, float[] dists, int knn, FLANNSearchParams params) {
//
//		knnSearchNative(index.getPointer(), queries, querySize, nQueries, indices, dists, knn, params.checks);
//	}
//
//	//private native static void knnSearchNative(long indexPointer, float[] queries, int querySize, int nQueries, int[] indices, float[] dists, int knn, int searchParamsChecks);
//
//
//	/* ******************************************************************************
//	 *  						GEOMETRIC TRANSFORMATIONS                           *
//	 * ******************************************************************************/
//
//	/**
//	 * Transforms the source image using the specified map:
//	 * <p>
//	 * <code>dst(x,y) = src(mapx(x,y), mapy(x,y))</code>
//	 * <p>
//	 * The specified interpolation method is used to extract pixels with non-integer coordinates.
//	 * Those pixels in the destination image, for which there is no correspondent pixels in the
//	 * source image, are filled with the specified fill value.
//	 *
//	 *
//	 * @param src - The source image.
//	 * @param dst - The destination image.
//	 * @param mapx - The map of x-coordinates (must be a 32-bit float image).
//	 * @param mapy - The map of y-coordinated (must be a 32-bit float image).
//	 * @param mode - The interpolation mode.
//	 * @param fillOutliers - A boolean value indicating if destination pixels that correspond
//	 * to outliers in the source image should be set to <code>fillValue</code>.
//	 * @param - A pixel value to fill outliers.
//	 */
//	public static void remap(IplImage src, IplImage dst, IplImage mapx, IplImage mapy,
//			InterpolationMode mode, boolean fillOutliers, Scalar fillValue) {
//
//		long flags = mode.getConstant();
//		if (fillOutliers)
//			flags = flags | WarpMode.CV_WARP_FILL_OUTLIERS.getConstant();
//
//		remapNative(src.getPointer(), dst.getPointer(), mapx.getPointer(), mapy.getPointer(), flags, fillValue);
//	}
//
//	//private native static void remapNative(long src_pointer, long dst_pointer, long mapx_pointer, long mapy_pointer, long flags, Scalar fillVal);
//
//
//	/**
//	 * Resizes the source image to the destination image's dimensions using linear interpolation.
//	 * @param src - The source image.
//	 * @param dst - The destination image.
//	 */
//	public static void resize(IplImage src, IplImage dst){
//		resize(src, dst, InterpolationMode.CV_INTER_LINEAR);
//	}
//
//	/**
//	 * Resizes the source image to the destination image's dimensions using the
//	 * specified linear interpolation.
//	 *
//	 * @param src - The source image.
//	 * @param dst - The destination image.
//	 * @param mode - The interpolation mode.
//	 */
//	public static void resize(IplImage src, IplImage dst, InterpolationMode mode){
//		resizeNative(src.getPointer(), dst.getPointer(), mode.getConstant());
//	}
//
//	//private static native void resizeNative(long src_pointer, long dst_pointer, long interpolation_mode);
//
//

//	/* ******************************************************************************
//	 *  				STRUCTURAL ANALYSIS AND SHAPE DESCRIPTORS					*
//	 * ******************************************************************************/
//
//	public static Contour[] findContours(IplImage img, MemStorage storage, FindContoursMode mode,
//			FindContoursMethod method, Point offset) {
//
//		return findContoursNative(img.getPointer(), storage.getPointer(), mode.getConstant(),
//				method.getConstant(), offset);
//	}
//
//	public static Contour[] findContours(IplImage img, MemStorage storage) {
//
//		return findContoursNative(img.getPointer(), storage.getPointer(),
//				FindContoursMode.CV_RETR_LIST.getConstant(),
//				FindContoursMethod.CV_CHAIN_APPROX_SIMPLE.getConstant(), new Point(0,0));
//	}
//
//	//private native static Contour[] findContoursNative(long impPointer, long storagePointer, long mode, long method, Point offset);
//
//	public static Box2D fitEllipse2(List<Point> points) {
//
//		float[] pointsArray = new float[points.size() * 2];
//		for (int i=0 ; i < points.size() ; i++) {
//			pointsArray[2*i] = points.get(i).x;
//			pointsArray[2*i+1] = points.get(i).y;
//		}
//
//		return fitEllipse2Native(pointsArray);
//	}
//
//	public static Box2D fitEllipse2(float[] points) {
//		return fitEllipse2Native(points);
//	}
//
//	//private native static Box2D fitEllipse2Native(float[] points);
//
//	/* ******************************************************************************
//	 *  						FEATURE DETECTION                                   *
//	 * ******************************************************************************/
//
//	public static void extractSURF(IplImage img, IplImage mask, List<SURFPoint> keypoints,
//			List<float[]> descriptors, MemStorage storage, SURFParams params) {
//
//		long maskPointer = 0;
//		if (mask != null) {
//			maskPointer = mask.getPointer();
//		}
//
//		extractSURFNative(img.getPointer(), maskPointer, keypoints, descriptors,
//				storage.getPointer(), params.extended, params.hessianThreshold, params.nOctaves,
//				params.nOctaveLayers);
//	}
//
//	//private native static void extractSURFNative(long img_pointer, long mask_pointer, List<SURFPoint> keypoints,
//			List<float[]> descriptors, long storage_pointer, int extended, double hessianThreshold,
//			int nOctaves, int nOctaveLayers);
//
//	public static void findCornerSubPix(IplImage img, float[] corners, int cornerCount,
//			Dimension winSize, Dimension zeroZoneSize, TermCriteria criteria) {
//
//		long criteriaType = 0;
//		for (TermCriteriaType t : criteria.type) {
//			criteriaType = criteriaType | t.getConstant();
//		}
//
//		findCornerSubPixNative(img.getPointer(), corners, cornerCount, winSize.width, winSize.height,
//				zeroZoneSize.width, zeroZoneSize.height, criteriaType, criteria.maxIter,
//				criteria.epsilon);
//	}
//
//	//private native static void findCornerSubPixNative(long img_pointer, float[] corners, int cornerCount, int winWidth, int winHeight, int zeroZoneWidth, int zeroZoneHeight, long termCriteriaType, int termCriteriaMaxIter, double termCriteriaEpsilon);
//
//	public static StarKeypoint[] getStarKeypoints(IplImage img, MemStorage storage, StarDetectorParams params) {
//		return getStarKeypointsNative(img.getPointer(), storage.getPointer(), params.maxSize,
//				params.responseThreshold, params.lineThresholdProjected, params.lineThresholdBinarized,
//				params.suppressNonmaxSize);
//	}
//
//	//private native static StarKeypoint[] getStarKeypointsNative(long img_pointer, long storage_pointer, int maxSize, int responseThreshold, int lineThresholdProjected, int lineThresholdBinarized, int suppressNonmaxSize);
//
//	/* ******************************************************************************
//	 *  						 MOTION AND TRACKING                                *
//	 * ******************************************************************************/
//
////	public static void calcOpticalFlowPyrLK(IplImage previous, IplImage current, IplImage previousPyramid,
////			IplImage currentPyramid, Point2D.Float[] previousFeatures, Point2D.Float[] currentFeatures,
////			int count, Dimension winSize, int level, int[] status, double[] trackError,
////			TermCriteria criteria, OpticalFlowPyrLKFlag[] flags) {
////
////		long criteriaType = 0;
////		for (TermCriteriaType t : criteria.type) {
////			criteriaType = criteriaType | t.getConstant();
////		}
////
////		long intflags = 0;
////		for (OpticalFlowPyrLKFlag f : flags) {
////			intflags = intflags | f.getConstant();
////		}
////
////		calcOpticalFlowPyrLKNative(previous.getPointer(), current.getPointer(), previousPyramid.getPointer(),
////				currentPyramid.getPointer(), previousFeatures, currentFeatures, count, winSize.width,
////				winSize.height, level, status, trackError, criteriaType, criteria.maxIter, criteria.epsilon,
////				intflags);
////
////		// TODO: implement this on JNI side
////		throw new RuntimeException("Not yet implemented!");
////	}
//
//	//private native static void calcOpticalFlowPyrLKNative(long previous_pointer, long current_pointer, long previousPyramid_pointer, long currentPyramid_pointer, Point2D.Float[] previousFeatures, Point2D.Float[] currentFeatures, int count, int winWidth, int winHeight, int level, int[] status, double[] trackError, long termCriteriaType, int termCriteriaMaxIter, double termCriteriaEpsilon, long flags);
//
//	public static int camshift(IplImage probImage, Rectangle window, TermCriteria criteria,
//			ConnectedComp comp, Box2D box) {
//
//		long criteriaType = 0;
//		for (TermCriteriaType t : criteria.type) {
//			criteriaType = criteriaType | t.getConstant();
//		}
//
//		return camshiftNative(probImage.getPointer(), window, criteriaType,
//				criteria.maxIter, criteria.epsilon, comp, box);
//	}
//
//	//private native static int camshiftNative(long probImagePointer, Rectangle window, long termCriteriaType, int termCriteriaMaxIter, double termCriteriaEpsilon, ConnectedComp comp, Box2D box);
//
//
//
//

//
//	/* ******************************************************************************
//	 *  						HISTOGRAMS					                        *
//	 * ******************************************************************************/
//
//	public static Histogram createHist(int dims, int[] sizes, HistogramType type,
//			float[] ranges, int uniform) {
//
//		return new Histogram(createHistNative(dims, sizes, type.getConstant(), ranges, uniform));
//	}
//
//	//private native static long createHistNative(int dims, int[] sizes, long type, float[] ranges, int uniform);
//
//	public static void releaseHist(Histogram hist) {
//		releaseHistNative(hist.getPointer());
//		native_pointers.remove(hist);
//	}
//
//	//private native static void releaseHistNative(long histPointer);
//
//	public static void calcHist(IplImage img, Histogram hist, int accumulate, IplImage mask) {
//
//		long[] pointers = new long[]{img.getPointer()};
//
//		calcHistNative(pointers, hist.getPointer(), accumulate,
//				(mask!=null)?mask.getPointer():0);
//	}
//
//	public static void calcHist(List<IplImage> images, Histogram hist, int accumulate, IplImage mask) {
//
//		long[] pointers = new long[images.size()];
//		for (int i=0 ; i < images.size() ; i++) {
//			pointers[i] = images.get(i).getPointer();
//		}
//
//		calcHistNative(pointers, hist.getPointer(), accumulate,
//				(mask!=null)?mask.getPointer():0);
//	}
//
//	//private native static void calcHistNative(long[] imgPointers, long histPointer, int accumulate, long maskPointer);
//
//	public static void clearHist(Histogram hist) {
//		clearHistNative(hist.getPointer());
//	}
//
//	//private native static void clearHistNative(long histPointer);
//
//	public static float getMinHistValue(Histogram hist) {
//		return getMinHistValueNative(hist.getPointer());
//	}
//
//	//private native static float getMinHistValueNative(long histPointer);
//
//	public static float getMaxHistValue(Histogram hist) {
//		return getMaxHistValueNative(hist.getPointer());
//	}
//
//	//private native static float getMaxHistValueNative(long histPointer);
//
//	public static void scaleHist(Histogram hist, double scale, double shift) {
//		scaleHistNative(hist.getPointer(), scale, shift);
//	}
//
//	//private native static void scaleHistNative(long histPointer, double scale, double shift);
//
//	public static void calcBackProject(IplImage img, IplImage backProject, Histogram hist) {
//
//		long[] pointers = new long[]{img.getPointer()};
//
//		calcBackProjectNative(pointers, backProject.getPointer(), hist.getPointer());
//	}
//
//	public static void calcBackProject(List<IplImage> images, IplImage backProject, Histogram hist) {
//
//		long[] pointers = new long[images.size()];
//		for (int i=0 ; i < images.size() ; i++) {
//			pointers[i] = images.get(i).getPointer();
//		}
//
//		calcBackProjectNative(pointers, backProject.getPointer(), hist.getPointer());
//	}
//
//	//private native static void calcBackProjectNative(long[] imgPointers, long backProjectPointer, long histPointer);
//
	/* ******************************************************************************
	 *  						HOMEMADE (or imported) GOODNESS                     *
	 * ******************************************************************************/

	private static class BlobInner{
		public float area;
		CvSeq.ByReference contour;
	}

	/**
	 * This method finds blobs in image, calculates their centroids, areas and moments and returns their contours
	 * @param src this array will get clobbered
	 * @param minArea
	 * @param maxArea
	 * @param maxBlobs
	 * @param findHoles
	 * @param maxVertices
	 * @return
	 */
	public static List<Blob> detectBlobs(IplImage im, int minArea, int maxArea, boolean findHoles, int maxVertices ){
		if( im.getColorModel().getNumberOfChannels() != 1 ) throw new RuntimeException("Image has to be grayscale to do the blob finding");

		CvSlice.ByValue CV_WHOLE_SEQ = new CvSlice.ByValue(0, CxcoreLibrary.CV_WHOLE_SEQ_END_INDEX);

		CvRect roi = CXCORE.cvGetImageROI(im.getJNAIPLImage());

		// memory storage for analysis
		CvMemStorage storage = CXCORE.cvCreateMemStorage(0);
		// to calculate gravity center, area, etcÔøΩ
		CvMoments moments = new CvMoments();
		// the valid contour list
		CvSeq.ByReference[] first_contour =  (sj.opencv.jna.cxcore.CvSeq.ByReference[]) new CvSeq.ByReference().toArray(256);
		// contour index that will walk through starting at 0 address

		int nr_contours = IMGPROC.cvFindContours(im.getCvArr(), storage, first_contour, new CvContour().size(), findHoles?1:0, 2, new CvPoint.ByValue(0, 0));

		List<BlobInner> blobs = new ArrayList<BlobInner>();

		CvSeq.ByReference index = first_contour[0];
		for(int i=0; i<nr_contours; i++){
			double area = Math.abs( IMGPROC.cvContourArea(index.getPointer(), CV_WHOLE_SEQ, 0) );
			if ( area >= minArea && area <= maxArea ){
				BlobInner b = new BlobInner();
				b.contour = index;
				b.area = (float) area;
				blobs.add(b);
			}
			index = index.h_next;
			if(index == null) break;
		}

		List<Blob> out = new ArrayList<Blob>();

		// convert the CV formatted contour data into our Java Blob object
		for (BlobInner blob : blobs) {
			CvArr cvarr = new CvArr(blob.contour.getPointer());

			// calculates all moments up to third order of a polygon or rasterized shape
			IMGPROC.cvMoments( cvarr, moments, 0 );

			// retrieve CV blob's properties (area, arc length, bounding rectangle, gravity center)
			float length = (float) IMGPROC.cvArcLength(blob.contour.getPointer(), CV_WHOLE_SEQ, -1);
			CvRect rect	 = IMGPROC.cvBoundingRect(cvarr, 0 );
			int center_x = (int) ( moments.m10/moments.m00 ) + roi.x;
			int center_y = (int) ( moments.m01/moments.m00 ) + roi.y;

			double mu00 = IMGPROC.cvGetCentralMoment(moments,0,0);
			double mu11 = IMGPROC.cvGetCentralMoment(moments,1,1)/mu00;
			double mu20 = IMGPROC.cvGetCentralMoment(moments,2,0)/mu00;
			double mu02 = IMGPROC.cvGetCentralMoment(moments,0,2)/mu00;

			float rMajor = (float) (2*Math.sqrt((((mu20+mu02)+Math.sqrt(((4*mu11*mu11)+((mu20-mu02)*(mu20-mu02))))/2))));
			float rMinor = (float) (2*Math.sqrt((((mu20+mu02)-Math.sqrt(((4*mu11*mu11)+((mu20-mu02)*(mu20-mu02))))/2))));

			float var1 = (float) (2*mu11);
			float var2 = (float) (mu20 - mu02);
			//		float rAngle = 0.5 * atan( var1 / var2 );
			float rAngle = (float) (0.5 * Math.atan2( var1, var2 ));

			// create Java objects : hypermedia.video.Blob, Rectangle, Point, Point array
			Blob blobout = new Blob();
			blobout.area = blob.area;
			out.add(blobout);

			blobout.points = new Point[blob.contour.total];

			System.out.println();

			// list contour points
			sj.opencv.jna.cxcore.CvSeqBlock.ByReference running = blob.contour.first;
			int cnt = 0;
			do{
				CvPoint rect_start = new CvPoint(running.data);
				CvPoint[] arr = (sj.opencv.jna.cxcore.CvPoint[]) rect_start.toArray(running.count);

				for (CvPoint byReference : arr) {
					byReference.read();
					blobout.points[cnt++] = new Point(byReference.x, byReference.y);
				}

				running = running.next;
			}while( running != blob.contour.first );

			blobout.centroid = new Point(center_x, center_y);
			blobout.rectangle = new Rectangle(rect.x, rect.y, rect.width, rect.height);
			blobout.length = length;
			blobout.isHole = blobout.area > 0;

			blobout.major_axis_length = rMajor;
			blobout.minor_axis_length = rMinor;
			blobout.major_axis_angle = rAngle;
		}

		CXCORE.cvReleaseMemStorage(new CvMemStorage.ByReference[]{ new CvMemStorage.ByReference(storage.getPointer()) });

		Collections.sort(out);

		return out;
	}

//	public static void findOutliers(float[] points, int pointSize, int nPoints, int[] outliers, int kdTrees, int knn, int searchChecks, double stdDevFactor) {
//
//		findOutliersNative(points, pointSize, nPoints, outliers, kdTrees, knn, searchChecks, stdDevFactor);
//	}
}
