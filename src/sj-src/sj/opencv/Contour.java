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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Contour implements Iterable<Point>, Serializable{

	private List<Point> vertices;

	public Contour() {
		vertices = new ArrayList<Point>();
	}

	public void add(Point p) {
		vertices.add(p);
	}

	public Point get(int index) {
		return vertices.get(index);
	}

	public int size() {
		return vertices.size();
	}

	public Iterator<Point> iterator() {
		return vertices.iterator();
	}
}
