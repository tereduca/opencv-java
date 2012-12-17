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
import sj.opencv.jna.cxcore.CvMemStorage;


public class MemStorage extends BasePointer {

	private CvMemStorage jnamemstorage;

	protected MemStorage(CvMemStorage jnamemstorage) {
		super(jnamemstorage.getPointer());

		this.jnamemstorage = jnamemstorage;

		addToManagedPointerSet();
	}

	protected CvMemStorage getJNACvMemStorage(){
		return jnamemstorage;
	}

	@Override
	protected void deAllocateNativeResource() {
		if( jnamemstorage != null ){
			CXCORE.cvReleaseMemStorage( new CvMemStorage.ByReference[]{ new CvMemStorage.ByReference(pointer) } );
			jnamemstorage = null;
		}
	}
}
