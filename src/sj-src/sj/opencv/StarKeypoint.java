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

import java.awt.Point;

public class StarKeypoint {
	
	/**
	 * Coordinates of the feature.
	 */
	public Point pt;
	
	/**
	 * Feature size, see StarDetectorParams.maxSize.
	 */
    public int size;
    
    /**
     * The approximated Laplacian value at that point.
     */
    public float response; 
    
    
    protected StarKeypoint(Point pt, int size, float response) {
    	this.pt = pt;
    	this.size = size;
    	this.response = response;
    }
    
    @Override
	public StarKeypoint clone() { 
    	return new StarKeypoint(this.pt, this.size, this.response);
    }
}
