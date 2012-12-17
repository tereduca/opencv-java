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

import static sj.opencv.jna.JNAOpenCV.CXCORE;
import static sj.opencv.jna.JNAOpenCV.HIGHGUI;
import static sj.opencv.jna.JNAOpenCV.IMGPROC;

import java.awt.Dimension;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.sun.jna.Native;

import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;
import sj.opencv.jna.cxcore.CvSize;
import sj.opencv.jna.cxcore.JNAIplImage;
import sj.opencv.jna.highgui.HighguiLibrary;
import sj.opencv.jna.highgui.HighguiLibrary.CvArr;
import sj.opencv.jna.highgui.HighguiLibrary.CvCapture;
import sj.opencv.jna.highgui.HighguiLibrary.CvVideoWriter;
import sj.opencv.jna.imgproc.ImgprocLibrary;

/**
 * @author siggi
 * @date Jul 5, 2012
 */
public class HighGui {

	static{
		OpenCV.initialize();
	}


	/* ******************************************************************************
	 *  							MEDIA I/O	                                    *
	 * ******************************************************************************/

    public enum VideoCodec {
        VIDEO_CODEC_MPEG_1("PIM1"),
        VIDEO_CODEC_MOTION_JPEG("MJPG"),
        VIDEO_CODEC_MPEG_4_2("MP42"),
        VIDEO_CODEC_MPEG_4_3("DIV3"),
        VIDEO_CODEC_MPEG_4("DIVX"),
        VIDEO_CODEC_H263("U263"),
        VIDEO_CODEC_H263I("I263"),
        VIDEO_CODEC_FLV1("FLV1"),
        VIDEO_CODEC_RGB("DIB "),
        VIDEO_CODEC_RAW_I420("I420"),
        VIDEO_CODEC_HUFFYUV("HFYU"),
        VIDEO_CODEC_FFMPEG("FFV1");

        private final String fourcc;
        VideoCodec(String fourcc) {
            this.fourcc = fourcc;
            if( fourcc.length() != 4 ) throw new IllegalArgumentException("VideoCodec: The FOURCC needs to be four letters representing the codec");
        }
    }

    
	/**
	 * create window<br>
	 * @param name Name of the window to create
     * @param flags
     * @return
	 */    
    public static int namedWindow(String name, int flags) {
    	return HIGHGUI.cvNamedWindow(name, flags);
    }
    
    
	/**
	 * display image within window (highgui windows remember their content)<br>
     * @param name Name of window to display to
     * @param im Image to display
	 */
    public static void showImage(String name, IplImage im) { 	
    	HIGHGUI.cvShowImage(name, im.getCvArr());
    }
    

    /**
     * Creates a video writer that can write frames into a movie file
     * @param filename
     * @param videoCodec
     * @param fps
     * @param frameSize
     * @param isColor
     * @return
     */
	public static VideoWriter createVideoWriter(String filename, VideoCodec videoCodec, double fps, Dimension frameSize, boolean isColor) {

		if( !new File(filename).getParentFile().isDirectory() ){
			throw new RuntimeException("Invalid filename");
		}

		ByteBuffer bb = ByteBuffer.wrap( videoCodec.fourcc.getBytes(Charset.forName("UTF8") ) );
		bb.order(ByteOrder.nativeOrder());
		int cv_FOURCC =  bb.asIntBuffer().get();

		CvVideoWriter jnawriter = HIGHGUI.cvCreateVideoWriter(filename, cv_FOURCC, fps, new CvSize.ByValue(frameSize.width, frameSize.height), (isColor)?1:0);
		if( jnawriter == null ){
			throw new RuntimeException("Can't instantiate video writer");
		}

		VideoWriter writer = new VideoWriter(jnawriter);

		return writer;
	}

	public static int writeFrame(VideoWriter writer, IplImage img) {
		int ret = -1;
		synchronized (writer) {
			ret = HIGHGUI.cvWriteFrame(writer.getJNACvVideoWriter(), img.getJNAIPLImage());
		}
		return ret;
	}

	public enum CaptureProperty {
		CV_CAP_PROP_DC1394_OFF	( HighguiLibrary.CV_CAP_PROP_DC1394_OFF ),
		CV_CAP_PROP_DC1394_MODE_MANUAL	( HighguiLibrary.CV_CAP_PROP_DC1394_MODE_MANUAL ),
		CV_CAP_PROP_DC1394_MODE_AUTO	( HighguiLibrary.CV_CAP_PROP_DC1394_MODE_AUTO ),
		CV_CAP_PROP_DC1394_MODE_ONE_PUSH_AUTO	( HighguiLibrary.CV_CAP_PROP_DC1394_MODE_ONE_PUSH_AUTO ),
		CV_CAP_PROP_POS_MSEC	( HighguiLibrary.CV_CAP_PROP_POS_MSEC ),
		CV_CAP_PROP_POS_FRAMES	( HighguiLibrary.CV_CAP_PROP_POS_FRAMES ),
		CV_CAP_PROP_POS_AVI_RATIO	( HighguiLibrary.CV_CAP_PROP_POS_AVI_RATIO ),
		CV_CAP_PROP_FRAME_WIDTH	( HighguiLibrary.CV_CAP_PROP_FRAME_WIDTH ),
		CV_CAP_PROP_FRAME_HEIGHT	( HighguiLibrary.CV_CAP_PROP_FRAME_HEIGHT ),
		CV_CAP_PROP_FPS	( HighguiLibrary.CV_CAP_PROP_FPS ),
		CV_CAP_PROP_FOURCC	( HighguiLibrary.CV_CAP_PROP_FOURCC ),
		CV_CAP_PROP_FRAME_COUNT	( HighguiLibrary.CV_CAP_PROP_FRAME_COUNT ),
		CV_CAP_PROP_FORMAT	( HighguiLibrary.CV_CAP_PROP_FORMAT ),
		CV_CAP_PROP_MODE	( HighguiLibrary.CV_CAP_PROP_MODE ),
		CV_CAP_PROP_BRIGHTNESS	( HighguiLibrary.CV_CAP_PROP_BRIGHTNESS ),
		CV_CAP_PROP_CONTRAST	( HighguiLibrary.CV_CAP_PROP_CONTRAST ),
		CV_CAP_PROP_SATURATION	( HighguiLibrary.CV_CAP_PROP_SATURATION ),
		CV_CAP_PROP_HUE	( HighguiLibrary.CV_CAP_PROP_HUE ),
		CV_CAP_PROP_GAIN	( HighguiLibrary.CV_CAP_PROP_GAIN ),
		CV_CAP_PROP_EXPOSURE	( HighguiLibrary.CV_CAP_PROP_EXPOSURE ),
		CV_CAP_PROP_CONVERT_RGB	( HighguiLibrary.CV_CAP_PROP_CONVERT_RGB ),
		CV_CAP_PROP_WHITE_BALANCE_BLUE_U	( HighguiLibrary.CV_CAP_PROP_WHITE_BALANCE_BLUE_U ),
		CV_CAP_PROP_RECTIFICATION	( HighguiLibrary.CV_CAP_PROP_RECTIFICATION ),
		CV_CAP_PROP_MONOCROME	( HighguiLibrary.CV_CAP_PROP_MONOCROME ),
		CV_CAP_PROP_SHARPNESS	( HighguiLibrary.CV_CAP_PROP_SHARPNESS ),
		CV_CAP_PROP_AUTO_EXPOSURE	( HighguiLibrary.CV_CAP_PROP_AUTO_EXPOSURE ),
		CV_CAP_PROP_GAMMA	( HighguiLibrary.CV_CAP_PROP_GAMMA ),
		CV_CAP_PROP_TEMPERATURE	( HighguiLibrary.CV_CAP_PROP_TEMPERATURE ),
		CV_CAP_PROP_TRIGGER	( HighguiLibrary.CV_CAP_PROP_TRIGGER ),
		CV_CAP_PROP_TRIGGER_DELAY	( HighguiLibrary.CV_CAP_PROP_TRIGGER_DELAY ),
		CV_CAP_PROP_WHITE_BALANCE_RED_V	( HighguiLibrary.CV_CAP_PROP_WHITE_BALANCE_RED_V ),
		CV_CAP_PROP_ZOOM	( HighguiLibrary.CV_CAP_PROP_ZOOM ),
		CV_CAP_PROP_FOCUS	( HighguiLibrary.CV_CAP_PROP_FOCUS ),
		CV_CAP_PROP_GUID	( HighguiLibrary.CV_CAP_PROP_GUID ),
		CV_CAP_PROP_ISO_SPEED	( HighguiLibrary.CV_CAP_PROP_ISO_SPEED ),
		CV_CAP_PROP_MAX_DC1394	( HighguiLibrary.CV_CAP_PROP_MAX_DC1394 ),
		CV_CAP_PROP_BACKLIGHT	( HighguiLibrary.CV_CAP_PROP_BACKLIGHT ),
		CV_CAP_PROP_PAN	( HighguiLibrary.CV_CAP_PROP_PAN ),
		CV_CAP_PROP_TILT	( HighguiLibrary.CV_CAP_PROP_TILT ),
		CV_CAP_PROP_ROLL	( HighguiLibrary.CV_CAP_PROP_ROLL ),
		CV_CAP_PROP_IRIS	( HighguiLibrary.CV_CAP_PROP_IRIS ),
		CV_CAP_PROP_SETTINGS	( HighguiLibrary.CV_CAP_PROP_SETTINGS ),
		CV_CAP_PROP_AUTOGRAB	( HighguiLibrary.CV_CAP_PROP_AUTOGRAB ),
		CV_CAP_PROP_SUPPORTED_PREVIEW_SIZES_STRING	( HighguiLibrary.CV_CAP_PROP_SUPPORTED_PREVIEW_SIZES_STRING ),
		CV_CAP_PROP_PREVIEW_FORMAT	( HighguiLibrary.CV_CAP_PROP_PREVIEW_FORMAT ),
		CV_CAP_PROP_OPENNI_OUTPUT_MODE	( HighguiLibrary.CV_CAP_PROP_OPENNI_OUTPUT_MODE ),
		CV_CAP_PROP_OPENNI_FRAME_MAX_DEPTH	( HighguiLibrary.CV_CAP_PROP_OPENNI_FRAME_MAX_DEPTH ),
		CV_CAP_PROP_OPENNI_BASELINE	( HighguiLibrary.CV_CAP_PROP_OPENNI_BASELINE ),
		CV_CAP_PROP_OPENNI_FOCAL_LENGTH	( HighguiLibrary.CV_CAP_PROP_OPENNI_FOCAL_LENGTH ),
		CV_CAP_PROP_OPENNI_REGISTRATION	( HighguiLibrary.CV_CAP_PROP_OPENNI_REGISTRATION ),
		CV_CAP_PROP_OPENNI_REGISTRATION_ON	( HighguiLibrary.CV_CAP_PROP_OPENNI_REGISTRATION_ON ),
		CV_CAP_PROP_OPENNI_APPROX_FRAME_SYNC	( HighguiLibrary.CV_CAP_PROP_OPENNI_APPROX_FRAME_SYNC ),
		CV_CAP_PROP_OPENNI_MAX_BUFFER_SIZE	( HighguiLibrary.CV_CAP_PROP_OPENNI_MAX_BUFFER_SIZE ),
		CV_CAP_PROP_OPENNI_CIRCLE_BUFFER	( HighguiLibrary.CV_CAP_PROP_OPENNI_CIRCLE_BUFFER ),
		CV_CAP_PROP_OPENNI_MAX_TIME_DURATION	( HighguiLibrary.CV_CAP_PROP_OPENNI_MAX_TIME_DURATION ),
		CV_CAP_PROP_OPENNI_GENERATOR_PRESENT	( HighguiLibrary.CV_CAP_PROP_OPENNI_GENERATOR_PRESENT ),
		CV_CAP_PROP_PVAPI_MULTICASTIP	( HighguiLibrary.CV_CAP_PROP_PVAPI_MULTICASTIP ),
		CV_CAP_PROP_XI_DOWNSAMPLING	( HighguiLibrary.CV_CAP_PROP_XI_DOWNSAMPLING ),
		CV_CAP_PROP_XI_DATA_FORMAT	( HighguiLibrary.CV_CAP_PROP_XI_DATA_FORMAT ),
		CV_CAP_PROP_XI_OFFSET_X	( HighguiLibrary.CV_CAP_PROP_XI_OFFSET_X ),
		CV_CAP_PROP_XI_OFFSET_Y	( HighguiLibrary.CV_CAP_PROP_XI_OFFSET_Y ),
		CV_CAP_PROP_XI_TRG_SOURCE	( HighguiLibrary.CV_CAP_PROP_XI_TRG_SOURCE ),
		CV_CAP_PROP_XI_TRG_SOFTWARE	( HighguiLibrary.CV_CAP_PROP_XI_TRG_SOFTWARE ),
		CV_CAP_PROP_XI_GPI_SELECTOR	( HighguiLibrary.CV_CAP_PROP_XI_GPI_SELECTOR ),
		CV_CAP_PROP_XI_GPI_MODE	( HighguiLibrary.CV_CAP_PROP_XI_GPI_MODE ),
		CV_CAP_PROP_XI_GPI_LEVEL	( HighguiLibrary.CV_CAP_PROP_XI_GPI_LEVEL ),
		CV_CAP_PROP_XI_GPO_SELECTOR	( HighguiLibrary.CV_CAP_PROP_XI_GPO_SELECTOR ),
		CV_CAP_PROP_XI_GPO_MODE	( HighguiLibrary.CV_CAP_PROP_XI_GPO_MODE ),
		CV_CAP_PROP_XI_LED_SELECTOR	( HighguiLibrary.CV_CAP_PROP_XI_LED_SELECTOR ),
		CV_CAP_PROP_XI_LED_MODE	( HighguiLibrary.CV_CAP_PROP_XI_LED_MODE ),
		CV_CAP_PROP_XI_MANUAL_WB	( HighguiLibrary.CV_CAP_PROP_XI_MANUAL_WB ),
		CV_CAP_PROP_XI_AUTO_WB	( HighguiLibrary.CV_CAP_PROP_XI_AUTO_WB ),
		CV_CAP_PROP_XI_AEAG	( HighguiLibrary.CV_CAP_PROP_XI_AEAG ),
		CV_CAP_PROP_XI_EXP_PRIORITY	( HighguiLibrary.CV_CAP_PROP_XI_EXP_PRIORITY ),
		CV_CAP_PROP_XI_AE_MAX_LIMIT	( HighguiLibrary.CV_CAP_PROP_XI_AE_MAX_LIMIT ),
		CV_CAP_PROP_XI_AG_MAX_LIMIT	( HighguiLibrary.CV_CAP_PROP_XI_AG_MAX_LIMIT ),
		CV_CAP_PROP_XI_AEAG_LEVEL	( HighguiLibrary.CV_CAP_PROP_XI_AEAG_LEVEL ),
		CV_CAP_PROP_XI_TIMEOUT	( HighguiLibrary.CV_CAP_PROP_XI_TIMEOUT ),
		CV_CAP_PROP_ANDROID_FLASH_MODE	( HighguiLibrary.CV_CAP_PROP_ANDROID_FLASH_MODE ),
		CV_CAP_PROP_ANDROID_FOCUS_MODE	( HighguiLibrary.CV_CAP_PROP_ANDROID_FOCUS_MODE ),
		CV_CAP_PROP_ANDROID_WHITE_BALANCE	( HighguiLibrary.CV_CAP_PROP_ANDROID_WHITE_BALANCE ),
		CV_CAP_PROP_ANDROID_ANTIBANDING	( HighguiLibrary.CV_CAP_PROP_ANDROID_ANTIBANDING ),
		CV_CAP_PROP_ANDROID_FOCAL_LENGTH	( HighguiLibrary.CV_CAP_PROP_ANDROID_FOCAL_LENGTH ),
		CV_CAP_PROP_ANDROID_FOCUS_DISTANCE_NEAR	( HighguiLibrary.CV_CAP_PROP_ANDROID_FOCUS_DISTANCE_NEAR ),
		CV_CAP_PROP_ANDROID_FOCUS_DISTANCE_OPTIMAL	( HighguiLibrary.CV_CAP_PROP_ANDROID_FOCUS_DISTANCE_OPTIMAL ),
		CV_CAP_PROP_ANDROID_FOCUS_DISTANCE_FAR	( HighguiLibrary.CV_CAP_PROP_ANDROID_FOCUS_DISTANCE_FAR ),
		CV_CAP_PROP_IOS_DEVICE_FOCUS	( HighguiLibrary.CV_CAP_PROP_IOS_DEVICE_FOCUS ),
		CV_CAP_PROP_IOS_DEVICE_EXPOSURE	( HighguiLibrary.CV_CAP_PROP_IOS_DEVICE_EXPOSURE ),
		CV_CAP_PROP_IOS_DEVICE_FLASH	( HighguiLibrary.CV_CAP_PROP_IOS_DEVICE_FLASH ),
		CV_CAP_PROP_IOS_DEVICE_WHITEBALANCE	( HighguiLibrary.CV_CAP_PROP_IOS_DEVICE_WHITEBALANCE ),
		CV_CAP_PROP_IOS_DEVICE_TORCH	( HighguiLibrary.CV_CAP_PROP_IOS_DEVICE_TORCH );



		private final int open_cv_constant;
		CaptureProperty(int constant){
			this.open_cv_constant = constant;
		}
		public final int getConstant(){return open_cv_constant;};
	}

	/**
	 * Gets video capturing properties.
	 * @param capture
	 * @param property
	 * @return
	 */
	public static double getCaptureProperty(Capture capture, CaptureProperty property){
		return HIGHGUI.cvGetCaptureProperty(capture.getJNACvCapture(), property.getConstant());
	}

	//private static native double getCapturePropertyNative(long capture_pointer, long property_constant);

	/**
	 * Sets video capture properties
	 * @param capture
	 * @param property
	 * @param value
	 */
	public static void setCaptureProperty(Capture capture, CaptureProperty property, double value){
		HIGHGUI.cvSetCaptureProperty(capture.getJNACvCapture(), property.getConstant(), value);
	}


	/**
	 * Initializes capturing a video from a file
	 * @param String absolute path and filename of movie
	 * @return
	 */
	public static Capture captureFromFile(String filename){
		CvCapture jnacap = HIGHGUI.cvCreateFileCapture(filename);
		if( jnacap == null )
			throw new RuntimeException("Couldn't load movie from: "+filename);

		Capture ret = new Capture(jnacap);

		return ret;
	}


	public enum ImageLoadColorMode{
		CV_LOAD_IMAGE_ANYCOLOR			( HighguiLibrary.CV_LOAD_IMAGE_ANYCOLOR ),
		CV_LOAD_IMAGE_ANYDEPTH			( HighguiLibrary.CV_LOAD_IMAGE_ANYDEPTH ),
		CV_LOAD_IMAGE_COLOR			( HighguiLibrary.CV_LOAD_IMAGE_COLOR ),
		CV_LOAD_IMAGE_GRAYSCALE		( HighguiLibrary.CV_LOAD_IMAGE_GRAYSCALE ),
		CV_LOAD_IMAGE_UNCHANGED		( HighguiLibrary.CV_LOAD_IMAGE_UNCHANGED );

		private final int open_cv_constant;
		ImageLoadColorMode(int constant){this.open_cv_constant=constant;}
		public final int getConstant(){return open_cv_constant;};
	}

	/**
	 * The function cvLoadImage()  loads an image from the specified file and returns the pointer to the loaded image. Currently the following file formats are supported:
	 *  - Windows bitmaps - BMP, DIB
	 *  - JPEG files - JPEG, JPG, JPE
	 *  - Portable Network Graphics - PNG
	 *  - Portable image format - PBM, PGM, PPM
	 *  - Sun rasters - SR, RAS
	 *  - TIFF files - TIFF, TIF
	 *
	 * @param filename ÔøΩ Name of file to be loaded.
	 * @param color_mode ÔøΩ Specific color type of the loaded image: if $ > 0 $, the loaded image is forced to be a 3-channel color image; if 0, the loaded image is forced to be grayscale; if $ < 0 $, the loaded image will be loaded as is (note that in the current implementation the alpha channel, if any, is stripped from the output image, e.g. 4-channel RGBA image will be loaded as RGB).
	 * @return an IPLImagem, note the pixel_depth will be assumed to be 8bit and color_mode will be generic
	 */
	public static IplImage loadImage(String location, ImageLoadColorMode color_mode){
		JNAIplImage jnaim = HIGHGUI.cvLoadImage(location, color_mode.getConstant());
		if( jnaim == null ){
			throw new RuntimeException("Can't load image");
		}

		IplImage im = CxCore.createImage(jnaim.width, jnaim.height, PixelDepth.getByConstant( jnaim.depth ), ColorModel.getGeneric( jnaim.nChannels ));

		CXCORE.cvCopy(new CvArr(jnaim.getPointer()), new CvArr(im.getPointer()), null);

		return im;
	}

	public enum ImageSaveMode{
		CV_IMWRITE_JPEG_QUALITY	( HighguiLibrary.CV_IMWRITE_JPEG_QUALITY ),
		CV_IMWRITE_PNG_COMPRESSION	( HighguiLibrary.CV_IMWRITE_PNG_COMPRESSION ),
		CV_IMWRITE_PNG_STRATEGY	( HighguiLibrary.CV_IMWRITE_PNG_STRATEGY ),
		CV_IMWRITE_PNG_STRATEGY_DEFAULT	( HighguiLibrary.CV_IMWRITE_PNG_STRATEGY_DEFAULT ),
		CV_IMWRITE_PNG_STRATEGY_FILTERED	( HighguiLibrary.CV_IMWRITE_PNG_STRATEGY_FILTERED ),
		CV_IMWRITE_PNG_STRATEGY_HUFFMAN_ONLY	( HighguiLibrary.CV_IMWRITE_PNG_STRATEGY_HUFFMAN_ONLY ),
		CV_IMWRITE_PNG_STRATEGY_RLE	( HighguiLibrary.CV_IMWRITE_PNG_STRATEGY_RLE ),
		CV_IMWRITE_PNG_STRATEGY_FIXED	( HighguiLibrary.CV_IMWRITE_PNG_STRATEGY_FIXED ),
		CV_IMWRITE_PXM_BINARY	( HighguiLibrary.CV_IMWRITE_PXM_BINARY );

		private final int open_cv_constant;
		ImageSaveMode(int constant){this.open_cv_constant=constant;}
		public final int getConstant(){return open_cv_constant;};
	}


	/**
	 * Saves an image to a specified file.
	 *
	 * The function cvSaveImage() saves the image to the specified file. The image format is chosen based on the filename extension, see LoadImage. Only 8-bit single-channel or 3-channel (with ÔøΩBGRÔøΩ channel order) images can be saved using this function. If the format, depth or channel order is different, use cvCvtScale() and cvCvtColor() to convert it before saving, or use universal cvSave() to save the image to XML or YAML format.
	 *
	 * @param location ÔøΩ Name of the file.
	 * @param image ÔøΩ Image to be saved.
	 */
	public static void saveImage(String location, IplImage image, ImageSaveMode save_mode){
		HIGHGUI.cvSaveImage(location, new CvArr(image.getPointer()), new int[]{save_mode.getConstant()});
	}

	public static void saveImage(String location, IplImage image){
		saveImage(location, image, ImageSaveMode.CV_IMWRITE_PNG_STRATEGY_DEFAULT);
	}


	/**
	 * Grabs and retrieves a frame from a capture source
	 * The function provides a modifiable image dst with the image data
	 * @param camera
	 * @param dst
	 * @return true if a frame was successfully copied
	 */
	public static boolean queryFrame(Capture capture, IplImage dst){
		if( BasePointer.isStopping() ) return false;
		JNAIplImage jnacamim = null;
		synchronized (capture) {
			if( capture.getJNACvCapture() == null ) return false;
			jnacamim = HIGHGUI.cvQueryFrame( capture.getJNACvCapture() );
			if( jnacamim == null ) return false;

			if( jnacamim.nChannels != dst.getNumberOfChannels() ) throw new RuntimeException("Capture image and dst image don't have the same number of channels: "+jnacamim.nChannels+" and "+dst.getNumberOfChannels());
			if( jnacamim.depth != dst.getPixelDepth().getConstant() ) throw new RuntimeException("Capture image and dst image don't have the same pixel depth");;

			if( jnacamim.width != dst.getWidth() || jnacamim.height != dst.getHeight() ){
				IMGPROC.cvResize(new CvArr(jnacamim.getPointer()), new CvArr(dst.getPointer()), ImgprocLibrary.CV_INTER_LINEAR);
			}
			else{
				CXCORE.cvCopy(new CvArr(jnacamim.getPointer()), dst.getCvArr(), null);
			}
		}

		return true;
	}

	/**
	 * Initializes capturing a video from a camera
	 * @param index of camera
	 * @return
	 */
	public static Capture captureFromCAM(int index){
		CvCapture jnacapture = HIGHGUI.cvCreateCameraCapture( index );

		if( jnacapture == null ) throw new RuntimeException("Couldn't instantiate camera with index: "+index);

		Capture ret = new Capture(jnacapture);

		return ret;
	}



}
