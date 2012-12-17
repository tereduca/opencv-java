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

public class SURFParams {

	/**
	 * 0 means basic descriptors (64 elements each),
	 * 1 means extended descriptors (128 elements each)
	 */
	public int extended; 
		
	/**
	 * only features with keypoint.hessian larger than that 
	 * are extracted.good default value is ~300-500 (can depend 
	 * on the average local contrast and sharpness of the image).
	 * user can further filter out some features based on
	 * their hessian values and other characteristics.
	 */
	public double hessianThreshold; 
	
	/**
	 * the number of octaves to be used for extraction.
	 * With each next octave the feature size is doubled
	 * (3 by default)
	 */
	public int nOctaves = 3;
	
	/**
	 * The number of layers within each octave (4 by default)
	 */
	int nOctaveLayers = 4; 
	
	public SURFParams(double hessianThreshold) {
		this(hessianThreshold, 0);
	}
	
	public SURFParams(double hessianThreshold, int extended) {
		this.hessianThreshold = hessianThreshold;
		this.extended = extended;
	}
	
}
