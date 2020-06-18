package com.pplosstudio.avroralogicgame.client;

public class Crystal extends CircuitElm{
	
	Point a;
    Point b;
    Point c;
    Point d;

	Crystal(int xa, int ya, int xb, int yb, int f) {
		super(xa, ya, xb, yb, f);
	}
	
	void setPoints() {
	    super.setPoints();
	    lead1 = interpPoint(point1, point2, 1-12/dn);
	}
	
	void draw(Graphics g) {
		
	    g.setColor(lightGrayColor);
	 
	    setBbox(point1, lead1, 0);
	    setVoltageColor(g, volts[0]);
	    
	    a = new Point(point1.x,point1.y);
		b = new Point(lead1.x-point1.x,point1.y+10);
		c = new Point(lead1.x,lead1.y);
		d = new Point(point1.x-lead1.x,lead1.y-10);
	     
	    drawThickLine(g, a, b);
	    drawThickLine(g, b, c);
	    drawThickLine(g, c, d);
	    drawThickLine(g, d, a);
	    
	    //drawPosts(g);
	    
	}

}
