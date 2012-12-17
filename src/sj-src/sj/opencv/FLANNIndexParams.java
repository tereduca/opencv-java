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

public class FLANNIndexParams {

	
	public static class LinearIndexParams extends FLANNIndexParams {

		public LinearIndexParams() {}
		
	}

	public static class KDTreeIndexParams extends FLANNIndexParams {

		int trees;
		
		public KDTreeIndexParams(int trees) {
			this.trees = trees;
		}
		
		public KDTreeIndexParams() {
			this(4);
		}
		
	}

	public static class KMeansIndexParams extends FLANNIndexParams {
		
		int branching;
        int iterations;
        CentersInit centersInit;
        float cbIndex;
        
        public KMeansIndexParams(int branching, int iterations, CentersInit centersInit, float cbIndex) {
        	this.branching = branching;
        	this.iterations = iterations;
        	this.centersInit = centersInit;
        	this.cbIndex = cbIndex;
        }
        
        public KMeansIndexParams() {
        	this(32, 11, CentersInit.CENTERS_RANDOM, 0.2f);
        }
      
	}

	public static class CompositeIndexParams extends FLANNIndexParams {

		int trees;
		int branching;
        int iterations;
        CentersInit centersInit;
        float cbIndex;
        
        public CompositeIndexParams(int trees, int branching, int iterations, CentersInit centersInit, float cbIndex) {
        	this.trees = trees;
        	this.branching = branching;
        	this.iterations = iterations;
        	this.centersInit = centersInit;
        	this.cbIndex = cbIndex;
        }
        
        public CompositeIndexParams() {
        	this(4, 32, 11, CentersInit.CENTERS_RANDOM, 0.2f);
        }
		
	}

	public static class AutotunedIndexParams extends FLANNIndexParams {
		
		float targetPrecision;
        float buildWeight;
        float memoryWeight;
        float sampleFraction;
        
        public AutotunedIndexParams(float targetPrecision, float buildWeight, float memoryWeight, float sampleFraction) {
        	this.targetPrecision = targetPrecision;
        	this.buildWeight = buildWeight;
        	this.memoryWeight = memoryWeight;
        	this.sampleFraction = sampleFraction;
        }
        
        public AutotunedIndexParams() {
        	this(0.9f, 0.01f, 0, 0.1f);
        }
        
	}

	public static class SavedIndexParams extends FLANNIndexParams {

		String filename;
		
		public SavedIndexParams(String filename) {
			this.filename = filename;
		}
		
	}
	
	public enum CentersInit {
    	CENTERS_RANDOM(0),
    	CENTERS_GONZALES(1),
    	CENTERS_KMEANSPP(2);
    	
    	private final long open_cv_constant;
    	CentersInit(long constant){this.open_cv_constant=constant;}
		public final long getConstant(){return open_cv_constant;};
    }

}
