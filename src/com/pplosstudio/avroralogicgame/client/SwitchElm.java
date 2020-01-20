
package com.pplosstudio.avroralogicgame.client;

// SPST switch
class SwitchElm extends CircuitElm {
    
	boolean momentary;
    // position 0 == closed, position 1 == open
    int position, posCount;
    
    public SwitchElm(int xx, int yy) {
		super(xx, yy);
		momentary = false;
		position = 0;
		posCount = 2;
    }
    
    SwitchElm(int xx, int yy, boolean mm) {
		super(xx, yy);
		position = (mm) ? 1 : 0;
		momentary = mm;
		posCount = 2;
    }
    
    public SwitchElm(int xa, int ya, int xb, int yb, int f) {
    	super(xa, ya, xb, yb, f);
	    posCount = 2;
    }
    
    int getDumpType() { return 's'; }
    
    String dump() {
    	return super.dump() + " " + position + " " + momentary;
    }

    Point ps, ps2;
    
    void setPoints() {
		super.setPoints();
		calcLeads(32);
		ps  = new Point();
		ps2 = new Point();
    }
    
    final int openhs = 16;
	
    void draw(Graphics g) {
		int hs1 = (position == 1) ? 0 : 2;
		int hs2 = (position == 1) ? openhs : 2;
		setBbox(point1, point2, openhs);
	
		draw2Leads(g);
		    
		if (position == 0)
		    doDots(g);
		    
		if (!needsHighlight())
		    g.setColor(whiteColor);
		interpPoint(lead1, lead2, ps,  0, hs1);
		interpPoint(lead1, lead2, ps2, 1, hs2);
		    
		drawThickLine(g, ps, ps2);
		drawPosts(g);
    }
    
    Rectangle getSwitchRect() {
		interpPoint(lead1, lead2, ps,  0, openhs);
		return new Rectangle(lead1).union(new Rectangle(lead2)).union(new Rectangle(ps));
    }
    
    void calculateCurrent() {
		if (position == 1)
		    current = 0;
    }
    
    void stamp() {
    	if (position == 0)
	    sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
    }
    
    int getVoltageSourceCount() {
    	return (position == 1) ? 0 : 1;
    }
    
    void mouseUp() {
    	if (momentary)
    		toggle();
    }
    
    void toggle() {
		position++;
		if (position >= posCount)
		    position = 0;
		//sim.analyzeCircuit();
    }
    
    
    void getInfo(String arr[]) {
		arr[0] = (momentary) ? "push switch (SPST)" : "switch (SPST)";
		if (position == 1) {
		    arr[1] = "open";
		    arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
		} else {
		    arr[1] = "closed";
		    arr[2] = "V = " + getVoltageText(volts[0]);
		    arr[3] = "I = " + getCurrentDText(getCurrent());
		}
    }
    
    
    boolean getConnection(int n1, int n2) { return position == 0; }
    
    boolean isWire() { return position == 0; }
    
    int getShortcut() { return 's'; }
    
}