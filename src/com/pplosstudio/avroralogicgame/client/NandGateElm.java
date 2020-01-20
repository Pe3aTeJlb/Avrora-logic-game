
package com.pplosstudio.avroralogicgame.client;

    class NandGateElm extends AndGateElm {
    	
	public NandGateElm(int xx, int yy) { super(xx, yy); }
	
	public NandGateElm(int xa, int ya, int xb, int yb, int f,int inputcount) {
	    super(xa, ya, xb, yb, f, inputcount);
	}
	
	boolean isInverting() { return true; }
	
	String getGateName() { return "NAND gate"; }
	
	int getDumpType() { return 151; }
	
	int getShortcut() { return '@'; }
	
}