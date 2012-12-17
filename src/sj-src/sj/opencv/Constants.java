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

import java.util.HashMap;

import sj.opencv.jna.cxcore.CxcoreLibrary;
import sj.opencv.jna.highgui.HighguiLibrary;
import sj.opencv.jna.imgproc.ImgprocLibrary;
import sj.opencv.jna.objdetect.ObjdetectLibrary;

/**
 * @author siggi
 * @d {date}
 */
public class Constants {

    private static HashMap<Integer, PixelDepth> pixel_depth_map = new HashMap<Integer, PixelDepth>();

    public enum PixelDepth{
        IPL_DEPTH_8U 	(CxcoreLibrary.IPL_DEPTH_8U,	1),
        IPL_DEPTH_8S 	(CxcoreLibrary.IPL_DEPTH_8S, 	1),
        IPL_DEPTH_16U 	(CxcoreLibrary.IPL_DEPTH_16U, 	2),
        IPL_DEPTH_16S 	(CxcoreLibrary.IPL_DEPTH_16S, 	2),
        IPL_DEPTH_32S 	(CxcoreLibrary.IPL_DEPTH_32S, 	4),
        IPL_DEPTH_32F 	(CxcoreLibrary.IPL_DEPTH_32F,	4),
        IPL_DEPTH_64F 	(CxcoreLibrary.IPL_DEPTH_64F, 	8);

        private final int open_cv_constant;
        private final int nr_bytes_per_pixel;

        PixelDepth(int open_cv_constant, int nr_bytes){
            this.open_cv_constant=open_cv_constant;
            this.nr_bytes_per_pixel=nr_bytes;
            pixel_depth_map.put(open_cv_constant, this);
        }

        public static PixelDepth getByConstant(int constant){
        	return pixel_depth_map.get( constant );
        }

        public final int getConstant(){return open_cv_constant;};
        public final int getBytesPerPixel(){return nr_bytes_per_pixel;};
    }

    public enum ColorModel{
        GRAY(1),
        RGB(3),
        BGR(3),
        RGBA(4),
        BGRA(4),
        HSV(3),
        YCrCb(3),
        HLS(3),
        GENERIC_1_CHANNEL(1),
        GENERIC_2_CHANNEL(2),
        GENERIC_3_CHANNEL(3),
        GENERIC_4_CHANNEL(4);

        public static ColorModel getGeneric(int channels){
        	if( channels == 1 ) return GENERIC_1_CHANNEL;
        	else if( channels == 2 ) return GENERIC_2_CHANNEL;
        	else if( channels == 3 ) return GENERIC_3_CHANNEL;
        	else if( channels == 4 ) return GENERIC_4_CHANNEL;
        	else return null;
        }

        private final int nr_channels;
        ColorModel(int nr_channels){this.nr_channels=nr_channels;}
        public final int getNumberOfChannels(){return nr_channels;};
    }

    public enum InterpolationMode{
        CV_INTER_NN(0),
        CV_INTER_LINEAR(1),
        CV_INTER_CUBIC(2),
        CV_INTER_AREA(3);

        private final long open_cv_constant;
        InterpolationMode(long constant){this.open_cv_constant=constant;}
        public final long getConstant(){return open_cv_constant;};
    }

    public enum WarpMode {
        CV_WARP_FILL_OUTLIERS(8),
        CV_WARP_INVERSE_MAP(16);

        private final long open_cv_constant;
        WarpMode(long constant) {this.open_cv_constant = constant;}
        public final long getConstant() {return open_cv_constant;}
    }

    public enum TermCriteriaType {
        CV_TERMCRIT_ITER(1),
        CV_TERMCRIT_EPS(2);

        private final long open_cv_constant;
        TermCriteriaType(long constant) {this.open_cv_constant = constant;}
        public final long getConstant() {return open_cv_constant;}
    }

    public enum OpticalFlowPyrLKFlag {
        CV_LKFLOW_PYR_A_READY(1),
        CV_LKFLOW_PYR_B_READY(2),
        CV_LKFLOW_INITIAL_GUESSES(4),
        CV_LKFLOW_GET_MIN_EIGENVALS(8);

        private final long open_cv_constant;
        OpticalFlowPyrLKFlag(long constant){this.open_cv_constant=constant;}
        public final long getConstant(){return open_cv_constant;};
    }



    public enum FindContoursMode {
        CV_RETR_EXTERNAL(0),
        CV_RETR_LIST(1);
        //CV_RETR_CCOMP(2),
        //CV_RETR_TREE(3);

        private final long open_cv_constant;
        FindContoursMode(long constant){this.open_cv_constant=constant;}
        public final long getConstant(){return open_cv_constant;};
    }

    public enum FindContoursMethod {
        //CV_CHAIN_CODE(0),
        CV_CHAIN_APPROX_NONE(1),
        CV_CHAIN_APPROX_SIMPLE(2),
        CV_CHAIN_APPROX_TC89_L1(3),
        CV_CHAIN_APPROX_TC89_KCOS(4),
        CV_LINK_RUNS(5);

        private final long open_cv_constant;
        FindContoursMethod(long constant){this.open_cv_constant=constant;}
        public final long getConstant(){return open_cv_constant;};
    }


    public enum HistogramType {

        CV_HIST_ARRAY(0),
        CV_HIST_SPARSE(1);

        private final long open_cv_constant;
        HistogramType(long constant){this.open_cv_constant=constant;}
        public final long getConstant(){return open_cv_constant;};
    }
}
