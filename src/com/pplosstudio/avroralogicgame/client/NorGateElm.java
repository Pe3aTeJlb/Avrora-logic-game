
package com.pplosstudio.avroralogicgame.client;

    class NorGateElm extends OrGateElm {
    	
	public NorGateElm(int xx, int yy) { super(xx, yy); }
	
	public NorGateElm(int xa, int ya, int xb, int yb, int f,int inputcount) {
	    super(xa, ya, xb, yb, f, inputcount);
	}
	
	String getGateName() { return "NOR gate"; }
	
	boolean isInverting() { return true; }
	
	int getDumpType() { return 153; }
	
	int getShortcut() { return '#'; }
	
}