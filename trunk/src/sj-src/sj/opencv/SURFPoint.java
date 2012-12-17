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

import java.awt.geom.Point2D;

public class SURFPoint {

	/**
	 * Position of the feature within the image
	 */
	public Point2D.Float pt;
	
	/**
	 * -1, 0 or +1. sign of the laplacian at the point.
	 * can be used to speedup feature comparison
	 * (normally features with laplacians of different
	 * signs can not match)
	 */
	public int laplacian;
	
	/**
	 * Size of the feature
	 */
	public int size;       
	
	/**
	 * Orientation of the feature: 0..360 degrees
	 */
	public float dir;
	
	/**
	 * Value of the hessian (can be used to
	 * approximately estimate the feature strengths;
	 * see also params.hessianThreshold)
	 */
	public float hessian;  
	
	
	protected SURFPoint(Point2D.Float pt, int laplacian, int size, float dir, float hessian) {
		this.pt = pt;
		this.laplacian = laplacian;
		this.size = size;
		this.dir = dir;
		this.hessian = hessian;
	}
	
}
