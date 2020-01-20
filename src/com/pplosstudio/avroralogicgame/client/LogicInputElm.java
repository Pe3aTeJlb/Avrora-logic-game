
package com.pplosstudio.avroralogicgame.client;

    class LogicInputElm extends SwitchElm {
    	
	final int FLAG_TERNARY = 1;
	final int FLAG_NUMERIC = 2;
	double hiV=5, loV=0;

	
	public LogicInputElm(int xx, int yy) {
	    super(xx, yy, false);
	    numHandles=1;
	    hiV = 5;
	    loV = 0;
	    
	}
	
	public LogicInputElm(int xa, int ya, int xb, int yb, int f) {
	    super(xa, ya, xb, yb, f);
	    numHandles=1;		
	    hiV = 5;
		loV = 0;
	}
	
	boolean isNumeric() { return true;
	}
	
	int getDumpType() { return 'L'; }
	
	String dump() {
	    return super.dump() + " " + hiV + " " + loV;
	}
	
	int getPostCount() { return 1; }
	
	void setPoints() {
	    super.setPoints();
	    lead1 = interpPoint(point1, point2, 1-12/dn);
	}
	
	void draw(Graphics g) {
		
		Font oldf=g.getFont();
	    Font f = new Font("SansSerif", Font.BOLD, 20);
	    g.setFont(f);
	    g.setColor(needsHighlight() ? selectColor : whiteColor);
	    String s = position == 0 ? "L" : "H";
	    if (isNumeric())
		s = "" + position;
	    
	    setBbox(point1, lead1, 0);
	    drawCenteredText(g, s, x2, y2, true);
	    setVoltageColor(g, volts[0]);
	    drawThickLine(g, point1, lead1);
	    updateDotCount();
	    //drawDots(g, point1, lead1, curcount);
	    drawPosts(g);
	    g.setFont(oldf);
	    
	}
	
	Rectangle getSwitchRect() {
	    return new Rectangle(x2-10, y2-10, 20, 20);
	}	

	void setCurrent(int vs, double c) { current = -c; }
	
	void stamp() {
	    double v = (position == 0) ? loV : hiV;
	    sim.stampVoltageSource(0, nodes[0], voltSource, v);
	}
	
	int getVoltageSourceCount() { return 1; }
	
	double getVoltageDiff() { return volts[0]; }
	
	void getInfo(String arr[]) {
	    arr[0] = "logic input";
	    arr[1] = (position == 0) ? "low" : "high";
	    if (isNumeric())
		arr[1] = "" + position;
	    arr[1] += " (" + getVoltageText(volts[0]) + ")";
	    arr[2] = "I = " + getCurrentText(getCurrent());
	}
	
	boolean hasGroundConnection(int n1) { return true; }
	
	int getShortcut() { return 'i'; }
	
	double getCurrentIntoNode(int n) {
	    return -current;
	}
	
}
