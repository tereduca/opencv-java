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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.ochafik.lang.jnaerator.runtime.This;
import com.sun.jna.Platform;

/**
 * @author siggi
 * @d {date}
 */
public class OpenCVLibLoader {

	private static boolean DEBUG = false;

	private static String SEP = System.getProperty("file.separator");
	private static String native_lib_path = "";
	private static String native_lib_folder_name = "opencv.2.4.0";
	private static String relative_lib_path = "";
	private static String platform_specific_library_ending = "";

	static{
		if( Platform.isMac() ){
			relative_lib_path += "osx" + SEP;
			platform_specific_library_ending = ".2.4.0";
		}
		else if( Platform.isLinux() ){
			relative_lib_path += "linux" + SEP;
			platform_specific_library_ending = "";
		}
		else if( Platform.isWindows() ){
			relative_lib_path += "windows" + SEP;
			platform_specific_library_ending = "240";
		}
		else{
			throw new RuntimeException("Don't have a native library for your platform: "+System.getProperty("os.name"));
		}

		if( Platform.is64Bit() ){
			relative_lib_path += "64bit" + SEP;
		}
		else {
			relative_lib_path += "32bit" + SEP;
		}
	}

	public static String getPlatformSpecificLibraryEnding(){
		return platform_specific_library_ending;
	}

	/**
	 * Call this before any reference to the OpenCV library to tell it not to try to load its native library
	 * (note, you will probably have to load it yourself then, using System.load() or System.loadLibrary())
	 */
	public static void disableNativeLoading(){
		native_lib_path = null;
	}

	/**
	 * Call this before any reference to the OpenCV library to tell it to load the native library from a specific location
	 * Should be provided with a folder where the opencv-java library is located
	 * If this method is not called then a classic System.loadLibrary("opencv-java") will be used which expects
	 * the native library to exist in a folder that is in the java.library.path
	 * @param absolute_path_to_lib_folder
	 */
	public static void setLibraryLocation(String absolute_path_to_lib_folder){
		native_lib_path = absolute_path_to_lib_folder;
	}

	/**
	 * This method should only be called from the OpenCV class internally
	 */
	protected static void loadNative(){

		// If native loading has not been disabled then we "guess" the absolute location of the lib folder
		if( native_lib_path != null && native_lib_path.length() == 0 ){
			native_lib_path = guessAbsoluteLibFolderLocation();

			//TODO HACK fix this by fixing binaries and putting them in their right place
			if( relative_lib_path.endsWith("linux/64bit/") ){
				native_lib_path = "/mnt/hgfs/siggiMac/Downloads/OpenCV-2.4.0/build-linux64/lib";
			}

			if( native_lib_path == null ){
				throw new RuntimeException("Can't guess absolute location of opencv native libraries");
			}

		}

		if( native_lib_path != null ){
			// If no custom path provided, load library from standard java library path
			if( native_lib_path.length()==0 ){
				throw new RuntimeException("Can't guess absolute location of opencv native libraries");
			}
			else {
				if( DEBUG ) System.err.println("Setting jna lib path to: "+native_lib_path);
				System.setProperty("jna.library.path", native_lib_path);
			}
		}
	}


	private static boolean folderContainsLibraryFolder(File folder){
		File[] files = folder.listFiles();
		if( files == null ){
			return false;
		}

		for (File file : files) {
			if( file.isDirectory() && file.getName().equals( native_lib_folder_name ) ){
				return true;
			}
		}

		return false;
	}

	private static String guessAbsoluteLibFolderLocation(){
		String[] LIB_FOLDERS = new String[]{"lib"+SEP, "lib"+SEP+"opencv-java"+SEP, "library"+SEP, "libraries"+SEP+"lib"+SEP, "lib"+SEP+"native"+SEP};

		// Here we check to see if we are being called from the Processing environment
		boolean are_in_processing = true;
		try {
			Class.forName("processing.app.Base");
		} catch (ClassNotFoundException e) {
			are_in_processing = false;
		}

		if( are_in_processing ){
			String prefspath = null;
			if( Platform.isMac() ) prefspath = System.getProperty("user.home") + SEP + "Library" +SEP+ "Processing" +SEP+ "preferences.txt";
			else if( Platform.isLinux() ){
				throw new RuntimeException("Can't yet load the Processing preference file for Linux");
			}
			else if( Platform.isWindows() ){
				throw new RuntimeException("Can't yet load the Processing preference file for Windows");
			}
			if( !new File(prefspath).exists() ) throw new RuntimeException("Can't find Processing preference file");
			Properties prop = new Properties();
			try {
				FileInputStream fi = new FileInputStream(new File(prefspath));
				prop.load(fi);
			} catch (Exception e1) {
				throw new RuntimeException("Can't process Processing preference file");
			}

			String sketchbook_path = prop.getProperty("sketchbook.path");
			if( sketchbook_path == null ) throw new RuntimeException("Key \"sketchbook.path\" missing from Processing preference file at: "+prefspath);
			return sketchbook_path +SEP+ "libraries" +SEP+ "opencv_java" +SEP+ "library" +SEP+ relative_lib_path + native_lib_folder_name;
		}
		// If not in processing then we proceed to look in the execution folder
		else{
			String path = PUtils.getExecutionFolder();
			path = fixPath(path);

			// Check to see if we have a jar and get out of it
			if( new File(path).isFile() ){
				if(DEBUG) System.err.println("LibLoc: are in jar");
				path = new File(path).getParent() + SEP;
			}

			if(DEBUG) System.err.println("LibLoc: original path: "+path);

			for(int level=0; level<3; level++){
				if(DEBUG) System.err.println("LibLoc: searching here: "+path+relative_lib_path);

				// Check current folder
				if( folderContainsLibraryFolder(new File(path + relative_lib_path) ) ){
					return path + relative_lib_path + native_lib_folder_name;
				}

				// Check for folders
				// Check if there is a lib folder
				for (String lib_folder : LIB_FOLDERS) {
					if( new File(path + lib_folder).exists() ){
						if(DEBUG) System.err.println("LibLoc: checking path: "+path+lib_folder+relative_lib_path);
						if( folderContainsLibraryFolder( new File(path + lib_folder + relative_lib_path) ) ){
							return path+lib_folder + relative_lib_path + native_lib_folder_name;
						}
					}
				}
				// Now we go one folder below for next iteration
				path = new File(path).getParent() + SEP;
			}
		}

		return null;
	}

//	private static void writeLineToFile(String filename, String text){
//
//		try {
//			FileWriter writer = new FileWriter(filename, true);
//			writer.write(text + System.getProperty("line.separator"));
//			writer.flush();
//			writer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	private static class DummyClass{}

	private static String filename = "/Users/siggi/bla.txt";

	private static String fixPath(String path){
		int index = path.indexOf("%20");
		while(index != -1){
			path = path.substring(0, index) + " " +path.substring(index+3);
			index = path.indexOf("%20");
		}

		return path;
	}
}
