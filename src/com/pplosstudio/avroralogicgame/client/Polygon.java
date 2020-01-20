
package com.pplosstudio.avroralogicgame.client;

class Polygon {
	
	private static final int MIN_LENGTH = 4;
	public int npoints;
	public int xpoints[];
	public int ypoints[];
	
	
	public Polygon(){
        xpoints = new int[MIN_LENGTH];
        ypoints = new int[MIN_LENGTH];
	}
	
    public void addPoint(int x, int y) {
    	
        if (npoints >= xpoints.length || npoints >= ypoints.length) {
            int newLength = npoints * 2;
            // Make sure that newLength will be greater than MIN_LENGTH and
            // aligned to the power of 2
            if (newLength < MIN_LENGTH) {
                newLength = MIN_LENGTH;
            } else if ((newLength & (newLength - 1)) != 0) {
                newLength = Integer.highestOneBit(newLength);
            }

            xpoints = expand(xpoints, newLength);
            ypoints = expand(ypoints, newLength);
        }
        xpoints[npoints] = x;
        ypoints[npoints] = y;
        npoints++;
    }
    
    private int[] expand(int[] in, int newlen) {
    	int[] out=new int[newlen];
    	
    	for(int i=0; i<in.length; i++) {
    		out[i]=in[i];
    	}
    	
    	return out;
    }
	
}