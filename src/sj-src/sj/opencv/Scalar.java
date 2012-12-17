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

import java.io.Serializable;

/**
 * @author siggi
 * @date Jul 30, 2010
 */
public class Scalar implements Serializable{
	private double[] val;

	/**
	 * Creates a Scalar using an incoming array without copying
	 * @param vals
	 */
	public Scalar(double[] vals){
		if(vals.length != 4)
			throw new RuntimeException("Incoming values must be of length 4");
		val = vals;
	}

	public Scalar(double v1, double v2, double v3, double v4){
		val = new double[4];
		val[0] = v1;
		val[1] = v2;
		val[2] = v3;
		val[3] = v4;
	}

	public Scalar(double v1, double v2, double v3) {
		this(v1, v2, v3, 0);
	}

	public Scalar(double v1, double v2) {
		this(v1, v2, 0, 0);
	}

	public Scalar(double value){
		this(value,value,value,value);
	}

	public Scalar(){
		this(0,0,0,0);
	}

	/**
	 *
	 * @return the first value in the array
	 */
	public double get(){
		return val[0];
	}

	public double get(int i){
		return val[i];
	}

	/**
	 * Sets the first value in the array
	 * @param value
	 */
	public void set(double value){
		val[0] = value;
	}

	public void set(int i, double value){
		val[i] = value;
	}

	public double[] getArray(){
		return val;
	}

	@Override
	public String toString(){
		return "{\t"+val[0]+",\t"+val[1]+",\t"+val[2]+",\t"+val[3]+"\t}";
	}
}
