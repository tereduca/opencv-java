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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;
import sj.opencv.jna.cxcore.CvSize.ByValue;
import sj.opencv.jna.cxcore.JNAIplImage;
import sj.opencv.jna.highgui.HighguiLibrary.CvArr;

import com.sun.jna.Pointer;




/**
 * Author: siggi
 * Date: Jul 28, 2010
 */
public class IplImage extends BasePointer{

	private PixelDepth depth;
	private ByteBuffer imageData;
	private ColorModel colorModel;
	private int width, height, widthStep;
	private CvArr cvarr;
	private JNAIplImage jnaiplimage;

	protected IplImage(JNAIplImage jnaiplimage, int width, int height, int widthStep, ByteBuffer data_buffer, PixelDepth pixel_depth, ColorModel color_model) {
		super(jnaiplimage.getPointer());
		this.widthStep = widthStep;
		this.depth = pixel_depth;
		this.imageData = data_buffer;
		this.colorModel = color_model;
		this.width = width;
		this.height = height;
		cvarr = new CvArr(getPointer());
		this.jnaiplimage = jnaiplimage;
	}

	protected JNAIplImage getJNAIPLImage(){
		return jnaiplimage;
	}

	protected CvArr getCvArr(){
		return cvarr;
	}

	public ByteBuffer getByteBuffer(){
		return imageData;
	}

	/**
	 * This method compares the formats of two different images (width, height, widthstep, color model and pixel depth
	 * @param another
	 * @return true if images have the same format
	 */
	public boolean isSameFormatAs(IplImage another){
		if( getWidth() == another.getWidth() &&
				getHeight() == another.getHeight() &&
				widthStep == another.widthStep &&
				getPixelDepth() == another.getPixelDepth() &&
				getColorModel() == another.getColorModel() )
		{
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Returns the depth of the image. The possible depths are specified
	 * by the enum <code>Depth</code>.
	 *
	 * @return An enum <code>Depth</code> value indicating the image depth.
	 */
	public PixelDepth getPixelDepth(){
		return depth;
	}

	/**
	 * Returns the color model of the image. The possible color models are
	 * specified by the enum <code>ColorModel</code>. If the
	 * image was constructed without a color model the method returns <code>null</code>.
	 *
	 * @return An enum <code>ColorModel</code> value indicating the color
	 * model of the image, or <code>null</code> if the image was constructed without
	 * a color model.
	 */
	public ColorModel getColorModel(){
		return colorModel;
	}

	/**
	 * Returns the width of the image.
	 *
	 * @return An integer indicating the image width.
	 */
	public int getWidth(){
		return width;
	}

	/**
	 * Returns the height of the image.
	 *
	 * @return An integer indicating the image height.
	 */
	public int getHeight(){
		return height;
	}

	public int getWidthStep(){
		return widthStep;
	}

	/**
	 * Returns the byte buffer containing the pixel data of the image.
	 * Any change to this buffer will be visible in the image.
	 *
	 * @return A byte buffer containing the image data.
	 */
	public ByteBuffer getImageData() {
		return imageData;
	}

	/**
	 * Returns a view to the image byte buffer as a short buffer. This
	 * operation is only available when the image has depth <code>SHORT</code>.
	 *
	 * @return A short buffer containing the image data.
	 * @throws UnsupportedOperationException If the image is not of depth <code>SHORT</code>.
	 */
	public ShortBuffer getImageDataAsShort() throws UnsupportedOperationException {
		checkDepth(PixelDepth.IPL_DEPTH_16U);
		return imageData.asShortBuffer();
	}

	/**
	 * Returns a view to the image byte buffer as a float buffer. This
	 * operation is only available when the image has depth <code>FLOAT</code>.
	 *
	 * @return A float buffer containing the image data.
	 * @throws UnsupportedOperationException If the image is not of depth <code>FLOAT</code>.
	 */
	public FloatBuffer getImageDataAsFloat() throws UnsupportedOperationException {
		checkDepth(PixelDepth.IPL_DEPTH_32F);
		return imageData.asFloatBuffer();
	}

	/**
	 * Returns a view to the image byte buffer as a double buffer. This
	 * operation is only available when the image has depth <code>DOUBLE</code>.
	 *
	 * @return A double buffer containing the image data.
	 * @throws UnsupportedOperationException If the image is not of depth <code>DOUBLE</code>.
	 */
	public DoubleBuffer getImageDataAsDouble() throws UnsupportedOperationException {
		checkDepth(PixelDepth.IPL_DEPTH_64F);
		return imageData.asDoubleBuffer();
	}

	/**
	 * Returns the number of channels in the image.
	 *
	 * @return An integer indicating the number of channels in the image.
	 */
	public int getNumberOfChannels() {
		return colorModel.getNumberOfChannels();
	}

	/**
	 * Copies the given byte buffer into the image's byte buffer. Both
	 * buffers should have the same capacity.
	 *
	 * @param src A byte buffer with image data.
	 * @throws IllegalArgumentException If the given byte buffer has a different
	 * capacity from the image's byte buffer.
	 */
	public void putImageData(ByteBuffer src) throws IllegalArgumentException{
		checkCapacity(src);
		src.rewind();
		imageData.clear();
		imageData.put(src);
		imageData.rewind();
		src.rewind();
	}

	/**
	 * Copies the given short buffer into the image's byte buffer. The capacity
	 * of the short buffer should be equivalent to the capacity of the byte buffer.
	 * This operation is only available to images with depth <code>SHORT</code>.
	 *
	 * @param src A short buffer with image data.
	 * @throws UnsupportedOperationException If the image is not of depth <code>SHORT</code>.
	 * @throws IllegalArgumentException If the capacity of the short buffer does not match
	 * the capacity of the image's byte buffer.
	 */
	public void putImageData(ShortBuffer src)
		throws UnsupportedOperationException, IllegalArgumentException {

		checkDepth(PixelDepth.IPL_DEPTH_16U);
		checkCapacity(src);
		src.rewind();
		imageData.clear();
		for (int i = 0 ; i < src.capacity() ; i++) {
			imageData.putShort(src.get(i));
		}
		imageData.rewind();
		src.rewind();
	}

	/**
	 * Copies the given float buffer into the image's byte buffer. The capacity
	 * of the float buffer should be equivalent to the capacity of the byte buffer.
	 * This operation is only available to images with depth <code>FLOAT</code>.
	 *
	 * @param src A float buffer with image data.
	 * @throws UnsupportedOperationException If the image is not of depth <code>FLOAT</code>.
	 * @throws IllegalArgumentException If the capacity of the float buffer does not match
	 * the capacity of the image's byte buffer.
	 */
	public void putImageData(FloatBuffer src)
		throws UnsupportedOperationException, IllegalArgumentException {

		checkDepth(PixelDepth.IPL_DEPTH_32F);
		checkCapacity(src);
		src.rewind();
		imageData.clear();
		for (int i = 0 ; i < src.capacity() ; i++) {
			imageData.putFloat(src.get(i));
		}
		imageData.rewind();
		src.rewind();
	}

	/**
	 * Copies the given double buffer into the image's byte buffer. The capacity
	 * of the double buffer should be equivalent to the capacity of the byte buffer.
	 * This operation is only available to images with depth <code>DOUBLE</code>.
	 *
	 * @param src A double buffer with image data.
	 * @throws UnsupportedOperationException If the image is not of depth <code>DOUBLE</code>.
	 * @throws IllegalArgumentException If the capacity of the double buffer does not match
	 * the capacity of the image's byte buffer.
	 */
	public void putImageData(DoubleBuffer src)  throws UnsupportedOperationException, IllegalArgumentException {
		checkDepth(PixelDepth.IPL_DEPTH_64F);
		checkCapacity(src);
		src.rewind();
		imageData.clear();
		for (int i = 0 ; i < src.capacity() ; i++) {
			imageData.putDouble(src.get(i));
		}
		imageData.rewind();
		src.rewind();
	}


	/**
	 * Returns the pixel values at the given coordinate pair. This operation is
	 * only available for images of depth <code>BYTE</code>. The returned byte array
	 * has length equal to the number of channels of the image.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @return A byte array containing the pixel values for each channel.
	 * @throws UnsupportedOperationException If the image is not of depth <code>BYTE</code>.
	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
	 */
	public byte[] getBytePixel(int x, int y) throws UnsupportedOperationException, IndexOutOfBoundsException {

		if (getPixelDepth() != PixelDepth.IPL_DEPTH_8U) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + PixelDepth.IPL_DEPTH_8U.toString());
		}
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IndexOutOfBoundsException("Pixels coordinates (" + x + "," + y +
					") are outside of this image's bounds.");
		}

		byte[] pixel = new byte[getNumberOfChannels()];
		int pos = y * getWidthStep() + x * getNumberOfChannels();
		for (int i=0 ; i < getNumberOfChannels() ; i++) {
			pixel[i] = imageData.get(pos + i);
		}
		return pixel;
	}

	/**
	 * Copies the pixel values at the given coordinate pair into the given destination array.
	 * This operation is only available for images of depth <code>BYTE</code>. The length of
	 * the byte array must be equal to the number of channels of the image.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @param pixel A byte array with length equal to the number of channels of the image.
	 * @throws UnsupportedOperationException If the image is not of depth <code>BYTE</code>.
	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
	 */
	public void getBytePixel(int x, int y, byte[] pixel) throws UnsupportedOperationException,
		IndexOutOfBoundsException, IllegalArgumentException {

		if (getPixelDepth() != PixelDepth.IPL_DEPTH_8U) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + PixelDepth.IPL_DEPTH_8U.toString());
		}
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IndexOutOfBoundsException("Pixels coordinates (" + x + "," + y +
					") are outside of this image's bounds.");
		}
		if (getNumberOfChannels() != pixel.length) {
			throw new IllegalArgumentException("Expected pixel array of length "
					+ getNumberOfChannels() + " and not " + pixel.length);
		}

		int pos = y * getWidthStep() + x * getNumberOfChannels();
		for (int i=0 ; i < getNumberOfChannels() ; i++) {
			pixel[i] = imageData.get(pos + i);
		}
	}

// Note: These methods don't handle unconventional widthSteps so I commented them out (this happens when width and widthstep aren't the same, when the width is not a power of 2)
//	/**
//	 * Returns the pixel values at the given index. This operation is
//	 * only available for images of depth <code>BYTE</code>. The returned byte array
//	 * has length equal to the number of channels of the image.
//	 * <p>
//	 * <i><b>Note</b></i>: Images are packed row by row.
//	 *
//	 * @param n The index of the pixel.
//	 * @return A byte array containing the pixel values for each channel.
//	 * @throws UnsupportedOperationException If the image is not of depth <code>BYTE</code>.
//	 * @throws IndexOutOfBoundsException If the given index is out of the image's bounds.
//	 */
//	public byte[] getBytePixel(int n) throws UnsupportedOperationException, IndexOutOfBoundsException {
//
//		if (getPixelDepth() != PixelDepth.IPL_DEPTH_8U) {
//			throw new UnsupportedOperationException("Operation only available for " +
//					"images of depth " + PixelDepth.IPL_DEPTH_8U.toString());
//		}
//		if (n < 0 || n > getWidth() * getHeight()) {
//			throw new IndexOutOfBoundsException("Pixel with position " + n +
//				"is outside of this image's bounds.");
//		}
//
//		byte[] pixel = new byte[getNumberOfChannels()];
//		int pos = getNumberOfChannels() * n;
//		for (int i=0 ; i < getNumberOfChannels() ; i++) {
//			pixel[i] = imageData.get(pos + i);
//		}
//		return pixel;
//	}
//
//	/**
//	 * Copies the pixel values at the given index into the given destination array.
//	 * This operation is only available for images of depth <code>BYTE</code>. The length of
//	 * the byte array must be equal to the number of channels of the image.
//	 * <p>
//	 * <i><b>Note</b></i>: Images are packed row by row.
//	 *
//	 * @param n The index of the pixel.
//	 * @param pixel A byte array with length equal to the number of channels of the image.
//	 * @throws UnsupportedOperationException If the image is not of depth <code>BYTE</code>.
//	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
//	 * @throws IndexOutOfBoundsException If the given index is out of the image's bounds.
//	 */
//	public void getBytePixel(int n, byte[] pixel) throws UnsupportedOperationException,
//		IndexOutOfBoundsException, IllegalArgumentException {
//
//		if (getPixelDepth() != PixelDepth.IPL_DEPTH_8U) {
//			throw new UnsupportedOperationException("Operation only available for " +
//					"images of depth " + PixelDepth.IPL_DEPTH_8U.toString());
//		}
//		if (n < 0 || n > getWidth() * getHeight()) {
//			throw new IndexOutOfBoundsException("Pixel with position " + n +
//				"is outside of this image's bounds.");
//		}
//		if (getNumberOfChannels() != pixel.length) {
//			throw new IllegalArgumentException("Expected pixel array of length "
//					+ getNumberOfChannels() + " and not " + pixel.length);
//		}
//
//		int pos = getNumberOfChannels() * n;
//		for (int i=0 ; i < getNumberOfChannels() ; i++) {
//			pixel[i] = imageData.get(pos + i);
//		}
//	}

	/**
	 * Returns the pixel values at the given coordinate pair. This operation is
	 * only available for images of depth <code>SHORT</code>. The returned short array
	 * has length equal to the number of channels of the image.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @return A short array containing the pixel values for each channel.
	 * @throws UnsupportedOperationException If the image is not of depth <code>SHORT</code>.
	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
	 */
	public short[] getShortPixel(int x, int y) throws UnsupportedOperationException, IndexOutOfBoundsException {

		if (getPixelDepth() != PixelDepth.IPL_DEPTH_16U) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + PixelDepth.IPL_DEPTH_16U.toString());
		}
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IndexOutOfBoundsException("Pixels coordinates (" + x + "," + y +
					") are outside of this image's bounds.");
		}

		short[] pixel = new short[getNumberOfChannels()];
		int pos = y * getWidthStep() + x * getNumberOfChannels();
		for (int i=0 ; i < getNumberOfChannels() ; i++) {
			pixel[i] = imageData.asShortBuffer().get(pos + i);
		}
		return pixel;
	}

	/**
	 * Copies the pixel values at the given coordinate pair into the given destination array.
	 * This operation is only available for images of depth <code>SHORT</code>. The length of
	 * the short array must be equal to the number of channels of the image.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @param pixel A short array with length equal to the number of channels of the image.
	 * @throws UnsupportedOperationException If the image is not of depth <code>SHORT</code>.
	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
	 */
	public void getShortPixel(int x, int y, short[] pixel) throws UnsupportedOperationException,
		IndexOutOfBoundsException, IllegalArgumentException {

		if (getPixelDepth() != PixelDepth.IPL_DEPTH_16U) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + PixelDepth.IPL_DEPTH_16U.toString());
		}
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IndexOutOfBoundsException("Pixels coordinates (" + x + "," + y +
					") are outside of this image's bounds.");
		}
		if (getNumberOfChannels() != pixel.length) {
			throw new IllegalArgumentException("Expected pixel array of length "
					+ getNumberOfChannels() + " and not " + pixel.length);
		}

		int pos = y * getWidthStep() + x * getNumberOfChannels();
		for (int i=0 ; i < getNumberOfChannels() ; i++) {
			pixel[i] = imageData.asShortBuffer().get(pos + i);
		}
	}

// Note: These methods don't handle unconventional widthSteps so I commented them out (this happens when width and widthstep aren't the same, when the width is not a power of 2)
//	/**
//	 * Returns the pixel values at the given index. This operation is
//	 * only available for images of depth <code>SHORT</code>. The returned short array
//	 * has length equal to the number of channels of the image.
//	 * <p>
//	 * <i><b>Note</b></i>: Images are packed row by row.
//	 *
//	 * @param n The index of the pixel.
//	 * @return A short array containing the pixel values for each channel.
//	 * @throws UnsupportedOperationException If the image is not of depth <code>SHORT</code>.
//	 * @throws IndexOutOfBoundsException If the given index is out of the image's bounds.
//	 */
//	public short[] getShortPixel(int n) throws UnsupportedOperationException, IndexOutOfBoundsException {
//
//		if (getPixelDepth() != PixelDepth.IPL_DEPTH_16U) {
//			throw new UnsupportedOperationException("Operation only available for " +
//					"images of depth " + PixelDepth.IPL_DEPTH_16U.toString());
//		}
//		if (n < 0 || n > getWidth() * getHeight()) {
//			throw new IndexOutOfBoundsException("Pixel with position " + n +
//				"is outside of this image's bounds.");
//		}
//
//		short[] pixel = new short[getNumberOfChannels()];
//		int pos = getNumberOfChannels() * n;
//		for (int i=0 ; i < getNumberOfChannels() ; i++) {
//			pixel[i] = imageData.asShortBuffer().get(pos + i);
//		}
//		return pixel;
//	}
//
//	/**
//	 * Copies the pixel values at the given index into the given destination array.
//	 * This operation is only available for images of depth <code>SHORT</code>. The length of
//	 * the short array must be equal to the number of channels of the image.
//	 * <p>
//	 * <i><b>Note</b></i>: Images are packed row by row.
//	 *
//	 * @param n The index of the pixel.
//	 * @param pixel A short array with length equal to the number of channels of the image.
//	 * @throws UnsupportedOperationException If the image is not of depth <code>SHORT</code>.
//	 * @throws IndexOutOfBoundsException If the given index is out of the image's bounds.
//	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
//	 */
//	public void getShortPixel(int n, short[] pixel) throws UnsupportedOperationException,
//		IndexOutOfBoundsException, IllegalArgumentException {
//
//		if (getPixelDepth() != PixelDepth.IPL_DEPTH_16U) {
//			throw new UnsupportedOperationException("Operation only available for " +
//					"images of depth " + PixelDepth.IPL_DEPTH_16U.toString());
//		}
//		if (n < 0 || n > getWidth() * getHeight()) {
//			throw new IndexOutOfBoundsException("Pixel with position " + n +
//				"is outside of this image's bounds.");
//		}
//		if (getNumberOfChannels() != pixel.length) {
//			throw new IllegalArgumentException("Expected pixel array of length "
//					+ getNumberOfChannels() + " and not " + pixel.length);
//		}
//
//		int pos = getNumberOfChannels() * n;
//		for (int i=0 ; i < getNumberOfChannels() ; i++) {
//			pixel[i] = imageData.asShortBuffer().get(pos + i);
//		}
//	}

	/**
	 * Returns the pixel values at the given coordinate pair. This operation is
	 * only available for images of depth <code>FLOAT</code>. The returned float array
	 * has length equal to the number of channels of the image.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @return A float array containing the pixel values for each channel.
	 * @throws UnsupportedOperationException If the image is not of depth <code>FLOAT</code>.
	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
	 */
	public float[] getFloatPixel(int x, int y) throws UnsupportedOperationException, IndexOutOfBoundsException {

		if (getPixelDepth() != PixelDepth.IPL_DEPTH_32F) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + PixelDepth.IPL_DEPTH_32F.toString());
		}
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IndexOutOfBoundsException("Pixels coordinates (" + x + "," + y +
					") are outside of this image's bounds.");
		}

		float[] pixel = new float[getNumberOfChannels()];
		int pos = y * getWidthStep() + x * getNumberOfChannels();
		for (int i=0 ; i < getNumberOfChannels() ; i++) {
			pixel[i] = imageData.asFloatBuffer().get(pos + i);
		}
		return pixel;
	}

	/**
	 * Copies the pixel values at the given coordinate pair into the given destination array.
	 * This operation is only available for images of depth <code>FLOAT</code>. The length of
	 * the float array must be equal to the number of channels of the image.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @param pixel A float array with length equal to the number of channels of the image.
	 * @throws UnsupportedOperationException If the image is not of depth <code>FLOAT</code>.
	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
	 */
	public void getFloatPixel(int x, int y, float[] pixel) throws UnsupportedOperationException,
		IndexOutOfBoundsException, IllegalArgumentException {

		if (getPixelDepth() != PixelDepth.IPL_DEPTH_32F) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + PixelDepth.IPL_DEPTH_32F.toString());
		}
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IndexOutOfBoundsException("Pixels coordinates (" + x + "," + y +
					") are outside of this image's bounds.");
		}
		if (getNumberOfChannels() != pixel.length) {
			throw new IllegalArgumentException("Expected pixel array of length "
					+ getNumberOfChannels() + " and not " + pixel.length);
		}

		int pos = y * getWidthStep() + x * getNumberOfChannels();
		for (int i=0 ; i < getNumberOfChannels() ; i++) {
			pixel[i] = imageData.asFloatBuffer().get(pos + i);
		}
	}

// Note: These methods don't handle unconventional widthSteps so I commented them out (this happens when width and widthstep aren't the same, when the width is not a power of 2)
//	/**
//	 * Returns the pixel values at the given index. This operation is
//	 * only available for images of depth <code>FLOAT</code>. The returned float array
//	 * has length equal to the number of channels of the image.
//	 * <p>
//	 * <i><b>Note</b></i>: Images are packed row by row.
//	 *
//	 * @param n The index of the pixel.
//	 * @return A float array containing the pixel values for each channel.
//	 * @throws UnsupportedOperationException If the image is not of depth <code>FLOAT</code>.
//	 * @throws IndexOutOfBoundsException If the given index is out of the image's bounds.
//	 */
//	public float[] getFloatPixel(int n) throws UnsupportedOperationException, IndexOutOfBoundsException {
//
//		if (getPixelDepth() != PixelDepth.IPL_DEPTH_32F) {
//			throw new UnsupportedOperationException("Operation only available for " +
//					"images of depth " + PixelDepth.IPL_DEPTH_32F.toString());
//		}
//		if (n < 0 || n > getWidth() * getHeight()) {
//			throw new IndexOutOfBoundsException("Pixel with position " + n +
//				"is outside of this image's bounds.");
//		}
//
//		float[] pixel = new float[getNumberOfChannels()];
//		int pos = getNumberOfChannels() * n;
//		for (int i=0 ; i < getNumberOfChannels() ; i++) {
//			pixel[i] = imageData.asFloatBuffer().get(pos + i);
//		}
//		return pixel;
//	}
//
//	/**
//	 * Copies the pixel values at the given index into the given destination array.
//	 * This operation is only available for images of depth <code>FLOAT</code>. The length of
//	 * the float array must be equal to the number of channels of the image.
//	 * <p>
//	 * <i><b>Note</b></i>: Images are packed row by row.
//	 *
//	 * @param n The index of the pixel.
//	 * @param pixel A float array with length equal to the number of channels of the image.
//	 * @throws UnsupportedOperationException If the image is not of depth <code>FLOAT</code>.
//	 * @throws IndexOutOfBoundsException If the given index is out of the image's bounds.
//	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
//	 */
//	public void getFloatPixel(int n, float[] pixel) throws UnsupportedOperationException,
//		IndexOutOfBoundsException, IllegalArgumentException {
//
//		if (getPixelDepth() != PixelDepth.IPL_DEPTH_32F) {
//			throw new UnsupportedOperationException("Operation only available for " +
//					"images of depth " + PixelDepth.IPL_DEPTH_32F.toString());
//		}
//		if (n < 0 || n > getWidth() * getHeight()) {
//			throw new IndexOutOfBoundsException("Pixel with position " + n +
//				"is outside of this image's bounds.");
//		}
//		if (getNumberOfChannels() != pixel.length) {
//			throw new IllegalArgumentException("Expected pixel array of length "
//					+ getNumberOfChannels() + " and not " + pixel.length);
//		}
//
//		int pos = getNumberOfChannels() * n;
//		for (int i=0 ; i < getNumberOfChannels() ; i++) {
//			pixel[i] = imageData.asFloatBuffer().get(pos + i);
//		}
//	}

	/**
	 * Returns the pixel values at the given coordinate pair. This operation is
	 * only available for images of depth <code>DOUBLE</code>. The returned double array
	 * has length equal to the number of channels of the image.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @return A double array containing the pixel values for each channel.
	 * @throws UnsupportedOperationException If the image is not of depth <code>DOUBLE</code>.
	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
	 */
	public double[] getDoublePixel(int x, int y) throws UnsupportedOperationException, IndexOutOfBoundsException {

		if (getPixelDepth() != PixelDepth.IPL_DEPTH_64F) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + PixelDepth.IPL_DEPTH_64F.toString());
		}
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IndexOutOfBoundsException("Pixels coordinates (" + x + "," + y +
					") are outside of this image's bounds.");
		}

		double[] pixel = new double[getNumberOfChannels()];
		int pos = y * getWidthStep() + x * getNumberOfChannels();
		for (int i=0 ; i < getNumberOfChannels() ; i++) {
			pixel[i] = imageData.asDoubleBuffer().get(pos + i);
		}
		return pixel;
	}

	/**
	 * Copies the pixel values at the given coordinate pair into the given destination array.
	 * This operation is only available for images of depth <code>DOUBLE</code>. The length of
	 * the double array must be equal to the number of channels of the image.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @param pixel A double array with length equal to the number of channels of the image.
	 * @throws UnsupportedOperationException If the image is not of depth <code>DOUBLE</code>.
	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
	 */
	public void getDoublePixel(int x, int y, double[] pixel) throws UnsupportedOperationException,
		IndexOutOfBoundsException, IllegalArgumentException {

		if (getPixelDepth() != PixelDepth.IPL_DEPTH_64F) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + PixelDepth.IPL_DEPTH_64F.toString());
		}
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IndexOutOfBoundsException("Pixels coordinates (" + x + "," + y +
					") are outside of this image's bounds.");
		}
		if (getNumberOfChannels() != pixel.length) {
			throw new IllegalArgumentException("Expected pixel array of length "
					+ getNumberOfChannels() + " and not " + pixel.length);
		}

		int pos = y * getWidthStep() + x * getNumberOfChannels();
		for (int i=0 ; i < getNumberOfChannels() ; i++) {
			pixel[i] = imageData.asDoubleBuffer().get(pos + i);
		}
	}

// Note: These methods don't handle unconventional widthSteps so I commented them out (this happens when width and widthstep aren't the same, when the width is not a power of 2)
//	/**
//	 * Returns the pixel values at the given index. This operation is
//	 * only available for images of depth <code>DOUBLE</code>. The returned double array
//	 * has length equal to the number of channels of the image.
//	 * <p>
//	 * <i><b>Note</b></i>: Images are packed row by row.
//	 *
//	 * @param n The index of the pixel.
//	 * @return A double array containing the pixel values for each channel.
//	 * @throws UnsupportedOperationException If the image is not of depth <code>DOUBLE</code>.
//	 * @throws IndexOutOfBoundsException If the given index is out of the image's bounds.
//	 */
//	public double[] getDoublePixel(int n) throws UnsupportedOperationException, IndexOutOfBoundsException {
//
//		if (getPixelDepth() != PixelDepth.IPL_DEPTH_64F) {
//			throw new UnsupportedOperationException("Operation only available for " +
//					"images of depth " + PixelDepth.IPL_DEPTH_32F.toString());
//		}
//		if (n < 0 || n > getWidth() * getHeight()) {
//			throw new IndexOutOfBoundsException("Pixel with position " + n +
//				"is outside of this image's bounds.");
//		}
//
//		double[] pixel = new double[getNumberOfChannels()];
//		int pos = getNumberOfChannels() * n;
//		for (int i=0 ; i < getNumberOfChannels() ; i++) {
//			pixel[i] = imageData.asDoubleBuffer().get(pos + i);
//		}
//		return pixel;
//	}
//
//	/**
//	 * Copies the pixel values at the given index into the given destination array.
//	 * This operation is only available for images of depth <code>DOUBLE</code>. The length of
//	 * the double array must be equal to the number of channels of the image.
//	 * <p>
//	 * <i><b>Note</b></i>: Images are packed row by row.
//	 *
//	 * @param n The index of the pixel.
//	 * @param pixel A double array with length equal to the number of channels of the image.
//	 * @throws UnsupportedOperationException If the image is not of depth <code>DOUBLE</code>.
//	 * @throws IndexOutOfBoundsException If the given index is out of the image's bounds.
//	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
//	 */
//	public void getDoublePixel(int n, double[] pixel) throws UnsupportedOperationException,
//		IndexOutOfBoundsException, IllegalArgumentException {
//
//		if (getPixelDepth() != PixelDepth.IPL_DEPTH_64F) {
//			throw new UnsupportedOperationException("Operation only available for " +
//					"images of depth " + PixelDepth.IPL_DEPTH_32F.toString());
//		}
//		if (n < 0 || n > getWidth() * getHeight()) {
//			throw new IndexOutOfBoundsException("Pixel with position " + n +
//				"is outside of this image's bounds.");
//		}
//		if (getNumberOfChannels() != pixel.length) {
//			throw new IllegalArgumentException("Expected pixel array of length "
//					+ getNumberOfChannels() + " and not " + pixel.length);
//		}
//
//		int pos = getNumberOfChannels() * n;
//		for (int i=0 ; i < getNumberOfChannels() ; i++) {
//			pixel[i] = imageData.asDoubleBuffer().get(pos + i);
//		}
//	}

	/**
	 * Sets the pixel values at the given coordinate pair to the values in the given array.
	 * This operation is only available to images of depth <code>BYTE</code>. The byte
	 * array must have length equal to the number of channels of the image.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @param pixel A byte array containing the pixel data.
	 * @throws UnsupportedOperationException If the image is not of depth <code>BYTE</code>.
	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
	 */
	public void setBytePixel(int x, int y, byte[] pixel) throws UnsupportedOperationException,
		IndexOutOfBoundsException, IllegalArgumentException {

		if (getPixelDepth() != PixelDepth.IPL_DEPTH_8U) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + PixelDepth.IPL_DEPTH_8U.toString());
		}
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IndexOutOfBoundsException("Pixels coordinates (" + x + "," + y +
					") are outside of this image's bounds.");
		}
		if (getNumberOfChannels() != pixel.length) {
			throw new IllegalArgumentException("Expected pixel array of length "
					+ getNumberOfChannels() + " and not " + pixel.length);
		}

		int pos = y * getWidthStep() + x * getNumberOfChannels();
		for (int i=0 ; i < getNumberOfChannels() ; i++) {
			imageData.put(pos + i, pixel[i]);
		}
	}

// Note: These methods don't handle unconventional widthSteps so I commented them out (this happens when width and widthstep aren't the same, when the width is not a power of 2)
//	/**
//	 * Sets the pixel values at the given index to the values in the given array.
//	 * This operation is only available to images of depth <code>BYTE</code>. The byte
//	 * array must have length equal to the number of channels of the image.
//	 * <p>
//	 * <i><b>Note</b></i>: Images are packed row by row.
//	 *
//	 * @param n The index of the pixel.
//	 * @param pixel A byte array containing the pixel data.
//	 * @throws UnsupportedOperationException If the image is not of depth <code>BYTE</code>.
//	 * @throws IndexOutOfBoundsException If the given index is out of the image's bounds.
//	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
//	 */
//	public void setBytePixel(int n, byte[] pixel) throws UnsupportedOperationException,
//		IndexOutOfBoundsException, IllegalArgumentException {
//
//		if (getPixelDepth() != PixelDepth.IPL_DEPTH_8U) {
//			throw new UnsupportedOperationException("Operation only available for " +
//					"images of depth " + PixelDepth.IPL_DEPTH_8U.toString());
//		}
//		if (n < 0 || n > getWidth() * getHeight()) {
//			throw new IndexOutOfBoundsException("Pixel with position " + n +
//				"is outside of this image's bounds.");
//		}
//		if (getNumberOfChannels() != pixel.length) {
//			throw new IllegalArgumentException("Expected pixel array of length "
//					+ getNumberOfChannels() + " and not " + pixel.length);
//		}
//
//		int pos = getNumberOfChannels() * n;
//		for (int i=0 ; i < getNumberOfChannels() ; i++) {
//			imageData.put(pos + i, pixel[i]);
//		}
//	}

	/**
	 * Sets the pixel values at the given coordinate pair to the values in the given array.
	 * This operation is only available to images of depth <code>SHORT</code>. The short
	 * array must have length equal to the number of channels of the image.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @param pixel A short array containing the pixel data.
	 * @throws UnsupportedOperationException If the image is not of depth <code>SHORT</code>.
	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
	 */
	public void setShortPixel(int x, int y, short[] pixel) throws UnsupportedOperationException,
		IndexOutOfBoundsException, IllegalArgumentException {

		if (getPixelDepth() != PixelDepth.IPL_DEPTH_16U) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + PixelDepth.IPL_DEPTH_16U.toString());
		}
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IndexOutOfBoundsException("Pixels coordinates (" + x + "," + y +
					") are outside of this image's bounds.");
		}
		if (getNumberOfChannels() != pixel.length) {
			throw new IllegalArgumentException("Expected pixel array of length "
					+ getNumberOfChannels() + " and not " + pixel.length);
		}

		int pos = y * getWidthStep() + x * getNumberOfChannels();
		for (int i=0 ; i < getNumberOfChannels() ; i++) {
			imageData.asShortBuffer().put(pos + i, pixel[i]);
		}
	}

// Note: These methods don't handle unconventional widthSteps so I commented them out (this happens when width and widthstep aren't the same, when the width is not a power of 2)
//	/**
//	 * Sets the pixel values at the given index to the values in the given array.
//	 * This operation is only available to images of depth <code>SHORT</code>. The short
//	 * array must have length equal to the number of channels of the image.
//	 * <p>
//	 * <i><b>Note</b></i>: Images are packed row by row.
//	 *
//	 * @param n The index of the pixel.
//	 * @param pixel A short array containing the pixel data.
//	 * @throws UnsupportedOperationException If the image is not of depth <code>SHORT</code>.
//	 * @throws IndexOutOfBoundsException If the given index is out of the image's bounds.
//	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
//	 */
//	public void setShortPixel(int n, short[] pixel) throws UnsupportedOperationException,
//		IndexOutOfBoundsException, IllegalArgumentException {
//
//		if (getPixelDepth() != PixelDepth.IPL_DEPTH_16U) {
//			throw new UnsupportedOperationException("Operation only available for " +
//					"images of depth " + PixelDepth.IPL_DEPTH_16U.toString());
//		}
//		if (n < 0 || n > getWidth() * getHeight()) {
//			throw new IndexOutOfBoundsException("Pixel with position " + n +
//				"is outside of this image's bounds.");
//		}
//		if (getNumberOfChannels() != pixel.length) {
//			throw new IllegalArgumentException("Expected pixel array of length "
//					+ getNumberOfChannels() + " and not " + pixel.length);
//		}
//
//		int pos = getNumberOfChannels() * n;
//		for (int i=0 ; i < getNumberOfChannels() ; i++) {
//			imageData.asShortBuffer().put(pos + i, pixel[i]);
//		}
//	}

	/**
	 * Sets the pixel values at the given coordinate pair to the values in the given array.
	 * This operation is only available to images of depth <code>FLOAT</code>. The float
	 * array must have length equal to the number of channels of the image.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @param pixel A float array containing the pixel data.
	 * @throws UnsupportedOperationException If the image is not of depth <code>FLOAT</code>.
	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
	 */
	public void setFloatPixel(int x, int y, float[] pixel) throws UnsupportedOperationException,
		IndexOutOfBoundsException, IllegalArgumentException {

		if (getPixelDepth() != PixelDepth.IPL_DEPTH_32F) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + PixelDepth.IPL_DEPTH_32F.toString());
		}
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IndexOutOfBoundsException("Pixels coordinates (" + x + "," + y +
					") are outside of this image's bounds.");
		}
		if (getNumberOfChannels() != pixel.length) {
			throw new IllegalArgumentException("Expected pixel array of length "
					+ getNumberOfChannels() + " and not " + pixel.length);
		}

		int pos = y * getWidthStep() + x * getNumberOfChannels();
		for (int i=0 ; i < getNumberOfChannels() ; i++) {
			imageData.asFloatBuffer().put(pos + i, pixel[i]);
		}
	}

// Note: These methods don't handle unconventional widthSteps so I commented them out (this happens when width and widthstep aren't the same, when the width is not a power of 2)
//	/**
//	 * Sets the pixel values at the given index to the values in the given array.
//	 * This operation is only available to images of depth <code>FLOAT</code>. The float
//	 * array must have length equal to the number of channels of the image.
//	 * <p>
//	 * <i><b>Note</b></i>: Images are packed row by row.
//	 *
//	 * @param n The index of the pixel.
//	 * @param pixel A float array containing the pixel data.
//	 * @throws UnsupportedOperationException If the image is not of depth <code>FLOAT</code>.
//	 * @throws IndexOutOfBoundsException If the given index is out of the image's bounds.
//	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
//	 */
//	public void setFloatPixel(int n, float[] pixel) throws UnsupportedOperationException,
//		IndexOutOfBoundsException, IllegalArgumentException {
//
//		if (getPixelDepth() != PixelDepth.IPL_DEPTH_32F) {
//			throw new UnsupportedOperationException("Operation only available for " +
//					"images of depth " + PixelDepth.IPL_DEPTH_32F.toString());
//		}
//		if (n < 0 || n > getWidth() * getHeight()) {
//			throw new IndexOutOfBoundsException("Pixel with position " + n +
//			"is outside of this image's bounds.");
//		}
//		if (getNumberOfChannels() != pixel.length) {
//			throw new IllegalArgumentException("Expected pixel array of length "
//					+ getNumberOfChannels() + " and not " + pixel.length);
//		}
//
//		int pos = getNumberOfChannels() * n;
//		for (int i=0 ; i < getNumberOfChannels() ; i++) {
//			imageData.asFloatBuffer().put(pos + i, pixel[i]);
//		}
//	}

	/**
	 * Sets the pixel values at the given coordinate pair to the values in the given array.
	 * This operation is only available to images of depth <code>DOUBLE</code>. The double
	 * array must have length equal to the number of channels of the image.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @param pixel A double array containing the pixel data.
	 * @throws UnsupportedOperationException If the image is not of depth <code>DOUBLE</code>.
	 * @throws IndexOutOfBoundsException If the given coordinate pair is out of the image's bounds.
	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
	 */
	public void setDoublePixel(int x, int y, double[] pixel) throws UnsupportedOperationException,
		IndexOutOfBoundsException, IllegalArgumentException {

		if (getPixelDepth() != PixelDepth.IPL_DEPTH_64F) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + PixelDepth.IPL_DEPTH_64F.toString());
		}
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IndexOutOfBoundsException("Pixels coordinates (" + x + "," + y +
					") are outside of this image's bounds.");
		}
		if (getNumberOfChannels() != pixel.length) {
			throw new IllegalArgumentException("Expected pixel array of length "
					+ getNumberOfChannels() + " and not " + pixel.length);
		}

		int pos = y * getWidthStep() + x * getNumberOfChannels();
		for (int i=0 ; i < getNumberOfChannels() ; i++) {
			imageData.asDoubleBuffer().put(pos + i, pixel[i]);
		}
	}

// Note: These methods don't handle unconventional widthSteps so I commented them out (this happens when width and widthstep aren't the same, when the width is not a power of 2)
//	/**
//	 * Sets the pixel values at the given index to the values in the given array.
//	 * This operation is only available to images of depth <code>DOUBLE</code>. The double
//	 * array must have length equal to the number of channels of the image.
//	 * <p>
//	 * <i><b>Note</b></i>: Images are packed row by row.
//	 *
//	 * @param n The index of the pixel.
//	 * @param pixel A double array containing the pixel data.
//	 * @throws UnsupportedOperationException If the image is not of depth <code>DOUBLE</code>.
//	 * @throws IndexOutOfBoundsException If the given index is out of the image's bounds.
//	 * @throws IllegalArgumentException If the length of the array is not equal to the number of channels.
//	 */
//	public void setDoublePixel(int n, double[] pixel) throws UnsupportedOperationException,
//		IndexOutOfBoundsException, IllegalArgumentException {
//
//		if (getPixelDepth() != PixelDepth.IPL_DEPTH_64F) {
//			throw new UnsupportedOperationException("Operation only available for " +
//					"images of depth " + PixelDepth.IPL_DEPTH_64F.toString());
//		}
//		if (n < 0 || n > getWidth() * getHeight()) {
//			throw new IndexOutOfBoundsException("Pixel with position " + n +
//			"is outside of this image's bounds.");
//		}
//		if (getNumberOfChannels() != pixel.length) {
//			throw new IllegalArgumentException("Expected pixel array of length "
//					+ getNumberOfChannels() + " and not " + pixel.length);
//		}
//
//		int pos = getNumberOfChannels() * n;
//		for (int i=0 ; i < getNumberOfChannels() ; i++) {
//			imageData.asDoubleBuffer().put(pos + i, pixel[i]);
//		}
//	}

	/**
	 * Clears the image buffer by setting all values to zero.
	 */
	public void clear() {
		byte[] zeros = new byte[imageData.capacity()];
		imageData.clear();
		imageData.put(zeros);
		imageData.rewind();
	}

	/**
	 * Private method that checks if an input buffer has the expected capacity.
	 *
	 * @param src A buffer.
	 * @throws IllegalArgumentException If the buffer does not have the expected capacity.
	 */
	private void checkCapacity(Buffer src) throws IllegalArgumentException {

		int expectedCapacity;

		if (src instanceof ByteBuffer)
			expectedCapacity = getImageData().capacity();
		else
			expectedCapacity = getImageData().capacity() / getPixelDepth().getBytesPerPixel() ;

		if (src.capacity() != expectedCapacity) {
			throw new IllegalArgumentException("Expected buffer with capacity " +
					expectedCapacity + " and not " + src.capacity());
		}
	}

	/**
	 * Private method that checks if the input depth value matches the depth
	 * of this image.
	 *
	 * @param requiredDepth The expected depth value.
	 * @throws UnsupportedOperationException If the depth values do not match.
	 */
	private void checkDepth(PixelDepth requiredDepth) throws UnsupportedOperationException {
		if (getPixelDepth() != requiredDepth) {
			throw new UnsupportedOperationException("Operation only available for " +
					"images of depth " + requiredDepth.toString());
		}
	}

	@Override
	protected void deAllocateNativeResource() {
		if( jnaiplimage != null ){
			CXCORE.cvReleaseImage( new JNAIplImage.ByReference[]{ new JNAIplImage.ByReference(pointer) } );
			jnaiplimage = null;
			cvarr = null;
		}
	}
}
