
package com.pplosstudio.avroralogicgame.client;

    class XorGateElm extends OrGateElm {
    	
	public XorGateElm(int xx, int yy) { super(xx, yy); }
	
	public XorGateElm(int xa, int ya, int xb, int yb, int f,int inputcount) {
	    super(xa, ya, xb, yb, f, inputcount);
	}
	
	String getGateName() { return "XOR gate"; }
	
	String getGateText() { return "=1"; }
	
	boolean calcFunction() {
	    int i;
	    boolean f = false;
	    for (i = 0; i != inputCount; i++)
		f ^= getInput(i);
	    return f;
	}
	
	int getDumpType() { return 154; }
	
	int getShortcut() { return '4'; }
	
}