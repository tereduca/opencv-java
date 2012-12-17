package utilities;
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
 


import java.lang.reflect.Field;

/**
 * @author siggi
 * @date Jul 6, 2012
 */
public class CreateEnum {
	public static void main(String[] args) {

		String libname = "Calib3d";
		String type_prefix = "CV_CALIB_";


		String package_name = "sj.opencv.jna."+libname.toLowerCase();
		String library_name = libname+"Library";


		String out = "";
		try {
			Class cl = Class.forName(package_name+"."+library_name);

			for (Field field : cl.getDeclaredFields()) {
				if( field.getType().getName().equals("int") && field.getName().startsWith(type_prefix) ){
					out += field.getName()
							+"\t( "
							+ library_name+"."+field.getName()
							+" ),\n";
				}
			}
			out = out.substring(0, out.length()-2)+";\n";

			System.out.println(out);


		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}
}
