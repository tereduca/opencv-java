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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author siggi
 * @date Nov 27, 2011
 */
public class ProcessUtils {

	public enum ExitCode {NOT_DONE, SUCCESS, TIMEOUT};

	public static void main(String[] args) {
		ProcessContainer p = ProcessUtils.executeProcess("python /Users/siggi/test.py", 5);
		p.waitToFinish();
		System.out.println(p.getOutput());
	}

	public static ProcessContainer executeProcess(final String cmd, final float timeout_sec){
		return new ProcessContainer(cmd, null, timeout_sec);
	}

	public static ProcessContainer executeProcess(final String cmd, final String working_dir, final float timeout_sec){
		return new ProcessContainer(cmd, working_dir, timeout_sec);
	}

	/*
	 * Only considers \" not \'
	 */
	private static ArrayList<String> splitCommandString(String command_string){
		command_string += " "; // For parantheses spitting

		/*
		 * First we consider \"
		 */
		String[] substrings = command_string.split("\"");
		if( substrings.length%2 == 0 ){
			throw new RuntimeException("Unmatched parantheses in: "+command_string);
		}

		ArrayList<String> output = new ArrayList<String>();
		for(int i=0; i<substrings.length; i++){

			// If this substring was inside parentheses
			if( (i%2) != 0 ){
				output.add( substrings[i] );
			}
			// Otherwise we just split them up
			else{
				String[] subsubstrings = substrings[i].trim().split(" ");
				for (String string : subsubstrings) {
					String string2 = string.trim();
					if( string2.length() > 0 ){
						output.add( string2 );
					}
				}
			}
		}

//		System.out.println("Parsing command string: "+command_string);
//		for (String string : output) {
//			System.out.println("Subcommand: "+string);
//		}

		return output;
	}

	public static class ProcessContainer{

		private Process process;
		private String output;
		private Object thread_lock;
		private boolean done;
		private ExitCode exit_code;

		private ProcessContainer(final String cmd, final String working_dir, final float timeout_sec){
			exit_code = ExitCode.NOT_DONE;
			thread_lock = new Object();
			done = false;

			output = "";

			final InputStream[] streams = new InputStream[2];
			Runnable run = new Runnable() {
				public void run() {
					try {
						/*
						 * Here we split the argument
						 */
						ProcessBuilder pb = new ProcessBuilder( splitCommandString(cmd) );
						if( working_dir != null ){
							pb.directory(new File(working_dir));
						}
						pb.environment().put("PATH", pb.environment().get("PATH")+":/opt/local/bin:/opt/local/sbin");

						process = pb.start();

						streams[0] = process.getInputStream();
						streams[1] = process.getErrorStream();

						long time_start = System.currentTimeMillis();

						while(true){
							for (InputStream inputStream : streams) {
								if( inputStream.available() > 0 ){
									byte[] data = new byte[inputStream.available()];
									inputStream.read(data);
									output += new String(data);
								}
							}

							Thread.sleep(5);


							int ret = Integer.MAX_VALUE;
							try {
								ret = process.exitValue();
							} catch (Exception e) {
								// TODO: handle exception
							}

							if( ret != Integer.MAX_VALUE ){
								break;
							}

							if( timeout_sec != -1 ){
								if( System.currentTimeMillis()-time_start > timeout_sec*1000f ){
									System.err.println("Timeout");
									synchronized (thread_lock) {
										exit_code = ExitCode.TIMEOUT;
									}
									break;
								}
							}
						}

						for (InputStream inputStream : streams) {
							if( inputStream.available() > 0 ){
								byte[] data = new byte[inputStream.available()];
								inputStream.read(data);
								output += new String(data);
							}
						}
						process.destroy();

						synchronized (thread_lock) {
							if( exit_code!=ExitCode.TIMEOUT )
								exit_code = ExitCode.SUCCESS;
							done = true;
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					synchronized (thread_lock) {
						thread_lock.notifyAll();
					}
				}
			};

			new Thread(run).start();
		}

		public String getOutput(){
			if( !isDone() ) return null;

			return output;
		}

		public ExitCode getExitCode(){
			ExitCode temp = null;
			synchronized (thread_lock) {
				temp = exit_code;
			}
			return temp;
		}

		public void waitToFinish(){
			try {
				synchronized (thread_lock) {
					thread_lock.wait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


//			while( !isDone() ){
//				try {
//					Thread.sleep(5);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
		}

		public boolean isDone(){
			boolean done2 = false;
			synchronized (thread_lock) {
				done2 = done;
			}
			return done2;
		}
	}

}
