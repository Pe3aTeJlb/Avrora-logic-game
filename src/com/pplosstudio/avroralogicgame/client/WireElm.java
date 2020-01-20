
package com.pplosstudio.avroralogicgame.client;

    class WireElm extends CircuitElm {
	boolean hasWireInfo; // used in CirSim to calculate wire currents
	
	public WireElm(int xx, int yy) { super(xx, yy); }
	public WireElm(int xa, int ya, int xb, int yb, int f)		  
	{
	    super(xa, ya, xb, yb, f);
	}
	
	static final int FLAG_SHOWCURRENT = 1;
	
	static final int FLAG_SHOWVOLTAGE = 2;
	
	void draw(Graphics g) {
		
	    setVoltageColor(g, volts[0]);
	    drawThickLine(g, point1, point2);
	    //doDots(g);
	    setBbox(point1, point2, 3);
	    String s = "";
	    if (mustShowCurrent()) {
	        s = getShortUnitText(Math.abs(getCurrent()), "A");
	    } 
	    if (mustShowVoltage()) {
	        s = (s.length() > 0 ? s + " " : "") + getShortUnitText(volts[0], "V");
	    }
	    drawValues(g, s, 4);
	    drawPosts(g);
	}
	
	void stamp() {
	    //sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
	}
	
	boolean mustShowCurrent() {
	    return (flags & FLAG_SHOWCURRENT) != 0;
	}
	
	boolean mustShowVoltage() {
	    return (flags & FLAG_SHOWVOLTAGE) != 0;
	}
	
	//int getVoltageSourceCount() { return 1; }
	
	void getInfo(String arr[]) {
	    arr[0] = "wire";
	    arr[1] = "I = " + getCurrentDText(getCurrent());
	    arr[2] = "V = " + getVoltageText(volts[0]);
	}
	
	int getDumpType() { return 'w'; }
	
	double getPower() { return 0; }
	
	double getVoltageDiff() { return volts[0]; }
	
	boolean isWire() { return true; }
	
}