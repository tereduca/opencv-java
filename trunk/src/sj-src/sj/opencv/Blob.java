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
 
/**
 * (./) Blob.java, 04/05/08
 * (by) Douglas Edric Stanley & Cousot St√©phane
 * (cc) some right reserved
 *
 * Part of the Processing/Java OpenCV Libraries project, for the Atelier Hypermedia, art
 * school of Aix-en-Provence, and for the Processing and Java community of course.
 *
 *
 * THIS LIBRARY (AND ALSO THIS FILE) IS RELEASED UNDER A CREATIVE COMMONS ATTRIBUTION 3.0 LICENSE
 * ‚Äπ http://creativecommons.org/licenses/by/3.0/ ‚Ä∫
 */

// package name
package sj.opencv;


// external librairies
import java.awt.*;
import java.io.Serializable;

import com.sun.jna.Pointer;


/**
 * A storage object containing a blob detected by OpenCV.
 * Returned by <code>blobs()</code> method.
 *
 * @example blobs
 * @see OpenCV#blobs( int, int, int, boolean )
 * @see OpenCV#blobs( int, int, int, boolean, int )
 * @usage Application
 */
public class Blob implements Serializable, Comparable<Blob>{
	/** The area of the blob in pixels */
	public float area			= 0f;
	/** The length of the perimeter in pixels */
	public float length			= 0f;
	/** The centroid or barycenter of the blob */
	public Point centroid		= new Point();
	/** The containing rectangle of the blob */
	public Rectangle rectangle	= new Rectangle();
	/** The list of points defining the shape of the blob */
	public Point[] points		= new Point[0];
	/** Is this blob a hole inside of another blob? */
	public boolean isHole		= false;
	/** Length of the axis of the first moment of the blob */
	public float major_axis_length	= 0f;
	/** Length of the axis of the second moment of the blob */
	public float minor_axis_length	= 0f;
	/** The angle of the axis of the first moment of the blob */
	public float major_axis_angle	= 0f;

	protected Pointer jna_pointer;

	/**
	 * Create a new Blob with the default properties.
	 */
	protected Blob() {}


	/**
	 * Create a new Blob with the given properties.
	 *
	 * @param area		the shape area
	 * @param length	the contour length
	 * @param centroid	the shape barycentre point
	 * @param rect		the shape rectangle
	 * @param points	the contour points
	 * @param isHole	true whether this blob is completly inside a bigger one
	 */
	protected Blob
	( float area, float length, Point centroid, Rectangle rect, Point[] points, boolean isHole, float major_axis_length, float minor_axis_length, float major_axis_angle )
	{
		this.area		= area;
		this.length		= length;
		this.centroid	= centroid;
		this.rectangle	= rect;
		this.points		= points;
		this.isHole		= isHole;
		this.major_axis_angle = major_axis_angle;
		this.major_axis_length = major_axis_length;
		this.minor_axis_length = minor_axis_length;
	}


	@Override
	public int compareTo(Blob o) {
		if( area < o.area ) return 1;
		return -1;
	}
}

