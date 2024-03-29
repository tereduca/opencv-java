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
 
package sj.opencv.jna.cxcore;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
/**
 * <i>native declaration : modules/core/include/opencv2/core/types_c.h:1752</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class CvSeqBlock extends Structure {
	/**
	 * Previous sequence block.<br>
	 * C type : CvSeqBlock*
	 */
	public CvSeqBlock.ByReference prev;
	/**
	 * Next sequence block.<br>
	 * C type : CvSeqBlock*
	 */
	public CvSeqBlock.ByReference next;
	/// Index of the first element in the block +
	public int start_index;
	/**
	 * sequence->first->start_index.<br>
	 * Number of elements in the block.
	 */
	public int count;
	/**
	 * Pointer to the first element of the block.<br>
	 * C type : schar*
	 */
	public Pointer data;
	public CvSeqBlock() {
		super();
		initFieldOrder();
	}

	public CvSeqBlock(Pointer p) {
		super(p);
		// TODO Auto-generated constructor stub
	}

	protected void initFieldOrder() {
		setFieldOrder(new String[]{"prev", "next", "start_index", "count", "data"});
	}
	/**
	 * @param prev Previous sequence block.<br>
	 * C type : CvSeqBlock*<br>
	 * @param next Next sequence block.<br>
	 * C type : CvSeqBlock*<br>
	 * @param start_index Index of the first element in the block +<br>
	 * @param count sequence->first->start_index.<br>
	 * Number of elements in the block.<br>
	 * @param data Pointer to the first element of the block.<br>
	 * C type : schar*
	 */
	public CvSeqBlock(CvSeqBlock.ByReference prev, CvSeqBlock.ByReference next, int start_index, int count, Pointer data) {
		super();
		this.prev = prev;
		this.next = next;
		this.start_index = start_index;
		this.count = count;
		this.data = data;
		initFieldOrder();
	}
	public static class ByReference extends CvSeqBlock implements Structure.ByReference {

	};
	public static class ByValue extends CvSeqBlock implements Structure.ByValue {

	};
}
