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

import java.lang.ref.WeakReference;
import java.util.HashSet;

import com.sun.jna.Pointer;

/**
 * Author: siggi
 * Date: Jul 28, 2010
 */
public abstract class BasePointer {

	private static HashSet<WeakReference<BasePointer>> pointers_to_deallocate;
	private static boolean is_stopping = false;

	protected static void initialize(){

		if( pointers_to_deallocate == null ){
			pointers_to_deallocate = new HashSet<WeakReference<BasePointer>>();

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {
					stop();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					synchronized (pointers_to_deallocate) {
						for (WeakReference<BasePointer> ref : pointers_to_deallocate) {
							BasePointer p = ref.get();
							if( p != null ){
								synchronized (p) {
									p.deAllocate();
								}
							}
						}
					}
				}
			}));
		}
	}

	protected synchronized static void stop(){
		is_stopping = true;
	}

	protected synchronized static boolean isStopping(){
		return is_stopping;
	}

	protected Pointer pointer;

	protected BasePointer(Pointer pointer){
		this.pointer = pointer;
	}

	public Pointer getPointer(){
		return pointer;
	}

	/**
	 * This method should be called inside of constructors of resources that need to be deallocated
	 * when the process shuts down (capture devices, videowriters etc).
	 */
	protected void addToManagedPointerSet(){
		synchronized (pointers_to_deallocate) {
			pointers_to_deallocate.add( new WeakReference<BasePointer>(this) );
		}
	}

	/**
	 * This method should be implemented for each different basepointer and deallocate any native memory
	 * It should also set any reference to native memory or jna objects to null
	 * This method should only be called internally, use deAllocate externally
	 * @return true if successfully deallocated
	 */
	protected abstract void deAllocateNativeResource();

	/**
	 * Release native resources
	 */
	public void deAllocate(){
		if( pointer != null ){
			deAllocateNativeResource();
		}
		pointer = null;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		deAllocate();
	}
}
