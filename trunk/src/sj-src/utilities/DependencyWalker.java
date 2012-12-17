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



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import utilities.ProcessUtils.ProcessContainer;



/**
 * @author siggi
 * @date Jul 15, 2012
 */
public class DependencyWalker {

	static String lib_ending = "dylib";
	static String output_folder = "/Users/siggi/bla/";

	static String opencvlib = "libopencv";

	static String deps_folder = "deps";

	static HashSet<String> libraries_done = new HashSet<String>();

	static String SEP = System.getProperty("file.separator");

	static String[] skip_folders = new String[]{
		"/System",
		"/usr"
	};



	public static void main(String[] args) throws IOException {
		String base_path = "/Users/siggi/Downloads/OpenCV-2.4.0/build/lib/";

		File output = new File(output_folder);
		output.delete();
		output.mkdir();
		File output_rel = new File(output.getAbsolutePath()+SEP+deps_folder);
		output_rel.delete();
		output_rel.mkdir();

		File[] libs = new File(base_path).listFiles();

		for (File lib : libs) {
			if( lib.getCanonicalPath().equals( lib.getAbsolutePath() ) && lib.getName().endsWith(lib_ending)){
				ProcessContainer p3 = ProcessUtils.executeProcess("cp \""+lib.getCanonicalPath()+"\" \""+output.getAbsolutePath()+"\" ", 10);
				p3.waitToFinish();

				fixLib(output.getAbsolutePath()+SEP+lib.getName(), deps_folder);
			}
		}
	}

	public static void fixLib(String lib_name, String relative_outfolder) throws IOException{
		System.out.println("Fixing lib: "+lib_name);

		ProcessContainer p = ProcessUtils.executeProcess("otool -l \""+lib_name +"\"", 10);
		p.waitToFinish();
		String[] lines = p.getOutput().split("\n");

		ArrayList<String> lib_paths = new ArrayList<String>();
		for (String string : lines) {
			if(string.contains(" name")){
				int start_ind = string.indexOf(" name ");
				int stop_ind = string.indexOf(" (offset 24)");
				lib_paths.add( string.substring(start_ind+6, stop_ind).trim() );
			}
		}

		for (String string : lib_paths) {
			File file = new File(string);

			if( !file.exists() ) continue;
			if( file.getAbsolutePath().contains("@loader_path") ) continue;

			// Skip the file itself
			if( file.getCanonicalFile().getName().equals( new File(lib_name).getName() ) ){
				continue;
			}

			System.out.println("looking at dependency: "+file.getCanonicalPath());

			String actual_filename = file.getCanonicalFile().getName();

			boolean skip = false;
			for (String skipfile : skip_folders) {
				if( string.startsWith( skipfile ) ){
					skip = true;
				}
			}
			if( skip ) continue;

			// First we change the install name of the original library

			String bla = "";
			if( relative_outfolder.length() == 0 || actual_filename.startsWith(opencvlib) ){
				bla = SEP;
			}
			else{
				bla = SEP+relative_outfolder+SEP;
			}

			ProcessContainer p2 = ProcessUtils.executeProcess("install_name_tool -change \""+file.getAbsolutePath()+"\" \"@loader_path"+bla+actual_filename+"\" \""+lib_name+"\"", 10);
			p2.waitToFinish();

			if( !libraries_done.contains(actual_filename) && !actual_filename.startsWith(opencvlib)){
				// Now we copy the actual file to the output location
				System.out.println("cp \""+file.getCanonicalPath()+"\" \""+output_folder+deps_folder+"\"");
				ProcessContainer p3 = ProcessUtils.executeProcess("cp \""+file.getCanonicalPath()+"\" \""+output_folder+deps_folder+"\"", 10);
				p3.waitToFinish();
				libraries_done.add( actual_filename );

				fixLib( output_folder+deps_folder+SEP+file.getCanonicalFile().getName(), "" );
			}
		}
	}
}
