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



import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author siggi
 * @date Sep 26, 2011
 */
public class FileManipulation {

	private static String[] known_imports_to_keep = new String[]{
		"java.awt.Dimension",
		"java.awt.Rectangle",
		"java.awt.Point",
		"java.awt.geom.Point2D",
	};

	private static String[] known_super_imports = new String[]{
		"sj.opencv.Calib3D",
		"sj.opencv.HighGui",
		"sj.opencv.ObjDetect",
		"sj.opencv.ImgProc",
		"sj.opencv.CxCore",
		"sj.opencv.Constants",
	};


	private static void emptyDirectory(File dir){
		for (File f : dir.listFiles()) {
			if( f.isDirectory() ){
				emptyDirectory(f);
			}

			f.delete();
		}
	}


	public static void main2(String[] args) {
		System.out.println( java2pde( new File("/Users/siggi/Development/Software/opencv-java/src/examples-src/examples/AdaptiveThreshold.java") ) );
	}

	public static void main(String[] args) {

//		args = new String[]{
//				"-license",
//				"/Users/siggi/Development/Software/opencv-java/res/license_source_clause.txt",
//				"/Users/siggi/Development/Software/opencv-java/src"
//		};

		if( args.length >= 1 ){
			if( args[0].equals("-pde") ){
				if( args.length >= 3 ){
					createPDEFiles(args[1], args[2]);
				}
				else{
					System.err.println("Missing input files");
				}
			}
			else if( args[0].equals("-license") ){
				if( args.length >= 3 ){
					addLicense(args[1], args[2]);
				}
				else{
					System.err.println("Missing input files");
				}
			}
		}
		else{
			System.err.println("Missing parameters");
		}
	}

	public static void addLicense(String license_file, String source_folder){
		if( !license_file.endsWith(".txt") || !new File(license_file).exists() ){
			throw new RuntimeException("Invalid license file");
		}
		if( !new File(source_folder).isDirectory() ){
			throw new RuntimeException("Invalid source folder");
		}

		/*
		 * Read license file
		 */
		ArrayList<String> license = getContents(new File(license_file));
		if( !license.get(0).startsWith("/* Description and License") ){
			throw new RuntimeException("License clause file not properly coded");
		}
		/*
		 * Iterate through all source files
		 */
		addLicenseRecursive(license, new File(source_folder));

		System.out.println("Done");
	}

	private static void addLicenseRecursive(ArrayList<String> license, File folder){
		if( !folder.isDirectory() ) throw new RuntimeException("Isn't directory: "+folder.getAbsolutePath());
		for (File file : folder.listFiles()) {
			if( file.isDirectory() ) addLicenseRecursive(license, file);
			else if( file.getName().endsWith(".java") ){
				ArrayList<String> source_file = getContents(file);
				// If file has license already, remove it
				if( source_file.get(0).trim().startsWith("/* Description and License") ){
					Iterator<String> it = source_file.iterator();
					while(it.hasNext()){
						String line = it.next();
						it.remove();
						if( line.trim().startsWith("*/") ){
							break;
						}
					}
					if( source_file.get(0).trim().length() == 0 ){
						source_file.remove(0);
					}
				}
				/*
				 * Here we add the license
				 */
				StringBuilder strb = new StringBuilder();
				for (String string : license) {
					strb.append(string);
					strb.append(System.getProperty("line.separator"));
				}
				strb.append(" " + System.getProperty("line.separator"));
				for (String string : source_file) {
					strb.append(string);
					strb.append(System.getProperty("line.separator"));
				}
				try {
					write(strb.toString(), file.getAbsolutePath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void createPDEFiles(String input_folder, String output_folder){
		if( !(new File(input_folder).isDirectory()) ){
			throw new RuntimeException( "Invalid input directory: "+input_folder );
		}

		if( !(new File(output_folder).isDirectory()) ){
			throw new RuntimeException( "Invalid output directory: "+output_folder );
		}

		File[] files = new File(input_folder).listFiles();
		File output = new File(output_folder);

		for (File file : files) {
			if( file.getName().endsWith(".java") ){
				String name = file.getName().substring(0, file.getName().length()-5);


				String pde = java2pde(file);

				String folder_name = output.getAbsolutePath() + "/" + name + "/";
				new File(folder_name).mkdir();

				try {
					write(pde, folder_name +name+ ".pde");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		System.out.println("Success");
	}

	public static class StringContainer{
		public String string;
		public StringContainer(String string){
			this.string = string;
		}
	}

	private static String java2pde(File file){
		LinkedList<StringContainer> imports = new LinkedList<StringContainer>();
		LinkedList<StringContainer> comment = new LinkedList<StringContainer>();
		LinkedList<StringContainer> lines = new LinkedList<FileManipulation.StringContainer>();
		if( file.exists() && file.getName().endsWith(".java") ){
			ArrayList<String> lines2 = getContents(file);
			for (String string : lines2) {
				lines.add(new StringContainer(string));
			}
		}

		/*
		 * We start by finding the main class and parsing the initial comment and imports
		 */
		Iterator<StringContainer> it = lines.iterator();
		while(it.hasNext()){
			StringContainer str = it.next();
			it.remove();
			if( str.string.contains("public class") ) break;

			if( str.string.trim().startsWith("/*") || str.string.trim().startsWith("*") ){
				comment.add(str);
			}
			else if( str.string.trim().startsWith("import") ){
				imports.add(str);
			}
		}
		// Remove last "}"
		StringContainer running = lines.getLast();
		lines.removeLast();
		while(!running.string.trim().startsWith("}")){
			lines.removeLast();
			running = lines.getLast();
		}

		/*
		 *  Do various fixes to the lines
		 */
		it = lines.iterator();
		while(it.hasNext()){
			StringContainer l = it.next();

			// Now we remove first indentaion
			if( !(l.string.length() == 0 || (byte)(l.string.charAt(0)) != (byte)9) ){
				l.string = l.string.substring(1, l.string.length() );
			}
			// Take the "public" out of method declarations
			if( l.string.trim().startsWith("public") ){
				l.string = replace(l.string, "public ", "");
			}
			if( l.string.trim().startsWith("private") ){
				l.string = replace(l.string, "private ", "");
			}
			if( l.string.trim().startsWith("protected") ){
				l.string = replace(l.string, "protected ", "");
			}
			// Remove Override
			if( l.string.trim().equals("@Override") ){
				it.remove();
				continue;
			}
			// Replace PUtils.guessDataLocation
			String replace = replace(l.string, "PUtils.guessDataLocation", "\"", "dataPath(\"");
			if( replace != null ) l.string = replace;
		}


		// Here we add the imports
		lines.addFirst(new StringContainer(" "));
		for (String known : known_imports_to_keep) {
			for (StringContainer existing : imports) {
				if( existing.string.contains(known) ){
					lines.addFirst(new StringContainer("import "+known+";"));
					break;
				}
			}
		}
		for (String known : known_super_imports) {
			for (StringContainer existing : imports) {
				if( existing.string.contains(known) ){
					lines.addFirst(new StringContainer("import "+known+".*;"));
					break;
				}
			}
		}
		lines.addFirst(new StringContainer("import sj.opencv.*;"));
		lines.addFirst(new StringContainer("import controlP5.*;"));

		/*
		 * Create the next file
		 */
		StringBuilder strb = new StringBuilder();
		for (StringContainer string : lines) {
			strb.append(string.string);
			strb.append(System.getProperty("line.separator"));
		}

		return strb.toString();
	}


	/**
	 * Fetch the entire contents of a text file, and return it in a String.
	 * This style of implementation does not throw Exceptions to the caller.
	 *
	 * @param aFile is a file which already exists and can be read.
	 */
	private static ArrayList<String> getContents(File aFile) {

		ArrayList<String> con = new ArrayList<String>();

		try {
			//use buffering, reading one line at a time
			//FileReader always assumes default encoding is OK!
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try {
				String line = null; //not declared within while loop
				while (( line = input.readLine()) != null){
					con.add(line);
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
			ex.printStackTrace();
		}

		return con;
	}

	private static String replace(String input, String to_replace, String replace_with){
		String ret = null;
		int begin = input.indexOf(to_replace);
		if( begin != -1 ){
			ret = input.substring(0, begin) + replace_with +input.substring(begin+to_replace.length(), input.length());
		}
		return ret;
	}

	private static String replace(String input, String to_replace_start, String to_replace_end, String replace_with){
		String ret = null;
		int begin = input.indexOf(to_replace_start);
		if( begin != -1 ){
			int end = input.indexOf(to_replace_end, begin);
			if( end == -1 ) return null;
			ret = input.substring(0, begin) + replace_with +input.substring(end+to_replace_end.length(), input.length());
		}
		return ret;
	}

	/** Write fixed content to the given file. */
	private static void write(String text, String filename) throws IOException  {

		Writer out = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
		try {
			out.write(text);
		}
		finally {
			out.close();
		}
	}
}
