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
 
package sj.opencv.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

import sj.opencv.OpenCVLibLoader;
import sj.opencv.PUtils;
import sj.opencv.jna.calib3d.Calib3dLibrary;
import sj.opencv.jna.cxcore.CxcoreLibrary;
import sj.opencv.jna.highgui.HighguiLibrary;
import sj.opencv.jna.imgproc.ImgprocLibrary;
import sj.opencv.jna.objdetect.ObjdetectLibrary;

/**
 * @author siggi
 * @date Jul 5, 2012
 */
public class JNAOpenCV {

	public static CxcoreLibrary CXCORE = (CxcoreLibrary) Native.synchronizedLibrary( (Library)Native.loadLibrary("opencv_core"+OpenCVLibLoader.getPlatformLibraryVersion(), CxcoreLibrary.class) );
	public static HighguiLibrary HIGHGUI = (HighguiLibrary) Native.synchronizedLibrary( (Library)Native.loadLibrary("opencv_highgui"+OpenCVLibLoader.getPlatformLibraryVersion(), HighguiLibrary.class) );
	public static ImgprocLibrary IMGPROC = (ImgprocLibrary) Native.synchronizedLibrary( (Library)Native.loadLibrary("opencv_imgproc"+OpenCVLibLoader.getPlatformLibraryVersion(), ImgprocLibrary.class) );
	public static ObjdetectLibrary OBJDETECT = (ObjdetectLibrary) Native.synchronizedLibrary( (Library)Native.loadLibrary("opencv_objdetect"+OpenCVLibLoader.getPlatformLibraryVersion(), ObjdetectLibrary.class) );
	public static Calib3dLibrary CALIB3D = (Calib3dLibrary) Native.synchronizedLibrary( (Library)Native.loadLibrary("opencv_calib3d"+OpenCVLibLoader.getPlatformLibraryVersion(), Calib3dLibrary.class) );
}
