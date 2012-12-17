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

public class StarDetectorParams {

	/**
	 * Maximal size of the features detected. The following values 
	 * of the parameter are supported: 
	 * 4, 6, 8, 11, 12, 16, 22, 23, 32, 45, 46, 64, 90, 128
	 */
	public int maxSize; 
	
	/**
	 * Threshold for the approximated Laplacian, used to eliminate
	 * weak features
	 */
	public int responseThreshold; 
     
	/**
	 * Another threshold for Laplacian to eliminate edges
	 */
	public int lineThresholdProjected; 
	
	/**
	 * Another threshold for the feature scale to eliminate edges
	 */
	public int lineThresholdBinarized;  

	/**
	 * Linear size of a pixel neighborhood for non-maxima suppression
	 */
	public int suppressNonmaxSize;
	
	
	public StarDetectorParams() {
		this(45, 30, 10, 8, 5);
	}
	
	public StarDetectorParams(int maxSize) {
		this(maxSize, 30, 10, 8, 5);
	}
	
	public StarDetectorParams(int maxSize, int responseThreshold, int lineThresholdProjected, 
			int lineThresholdBinarized, int suppressNonmaxSize) {
		this.maxSize = maxSize;
		this.responseThreshold = responseThreshold;
		this.lineThresholdProjected = lineThresholdProjected;
		this.lineThresholdBinarized = lineThresholdBinarized;
		this.suppressNonmaxSize = suppressNonmaxSize;
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	public int getResponseThreshold() {
		return responseThreshold;
	}
	
	public int getLineThresholdProjected() {
		return lineThresholdProjected;
	}
	
	public int getLineThresholdBinarized() {
		return lineThresholdBinarized;
	}
	
	public int getSuppressNonmaxSize() {
		return suppressNonmaxSize;
	}
}
