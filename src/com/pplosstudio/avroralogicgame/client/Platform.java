/*    
Copyright (C) Pplos Studio
    
    This file is a part of Avrora Logic Game, which based on CircuitJS1
    https://github.com/Pe3aTeJlb/Avrora-logic-game
    
    CircuitJS1 was originally written by Paul Falstad.
	http://www.falstad.com/

	JavaScript conversion by Iain Sharp.
	http://lushprojects.com/
    
    Avrora Logic Game is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 1, 2 of the License, or
    (at your option) any later version.
    Avrora Logic Game is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License 
    along with Avrora Logic Game.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.pplosstudio.avroralogicgame.client;

class Platform extends CircuitElm{
	
	final int FLAG_TERNARY = 1;
	final int FLAG_NUMERIC = 2;
	final int FLAG_PULLDOWN = 4;
	double threshold = 2.5;
	public String value;
	public Point InputPoint = new Point();
	private boolean highVol = false;
	
	Point a = new Point(0,0);
	Point b = new Point(0,0);
	Point c = new Point(0,0);
	Point d = new Point(0,0);
	
	double prevVal;

	public Platform(int xx, int yy) {
	    super(xx, yy);
	    threshold = 2.5;
	}
	
	public Platform(int xa, int ya, int xb, int yb, int f) {
	    super(xa, ya, xb, yb, f);
	    InputPoint.x = xa;
	    InputPoint.y = ya;
		threshold = 2.5;
		prevVal = 0;
	}
	
	String dump() {return super.dump() + " " + threshold;}
	
	int getDumpType() { return 'M'; }
	
	int getPostCount() { return 1; }
	
	boolean isNumeric() { return true; }
	
	boolean needsPullDown() { return (flags & FLAG_PULLDOWN) != 0; }
	
	void setPoints() {
	    super.setPoints();
	    lead1 = interpPoint(point1, point2, 1-12/dn);
	}
	
	void draw(Graphics g) {
	
	    g.setColor(lightGrayColor);
	    
	    String s = (volts[0] < threshold) ? "0" : "1";
	    value = s;
	    
	    setBbox(point1, lead1, 0);
	    
	    if(highVol)
	    setVoltageColor(g, 5);
	    else
	    setVoltageColor(g, 0);
	    
	   //Обработка грёбаных щелчков платформы
	    if(prevVal == 0 && s.equals("1")) {
	    	
	    	if(sim.tickCounter>5) {
	    		
	    		a = new Point(point1.x+100, point1.y+15);
			 	b = new Point(point1.x+200, point1.y+15);
			 	c = new Point(point1.x+100, point1.y-15);
			 	d = new Point(point1.x+200, point1.y-15);

		    	drawThickLine(g, point1, new Point(point1.x+100, point1.y));
		    	
			    drawThickLine(g, a, c);
			    drawThickLine(g, b, d);
			    drawThickLine(g, a, b);
			    drawThickLine(g, c, d);
			    
			    highVol = true;
			    prevVal = volts[0];
			    
	    	}else {
	    		
	    		 	a = new Point(point1.x, point1.y+15);
				 	b = new Point(point1.x+100, point1.y+15);
				 	c = new Point(point1.x, point1.y-15);
				 	d = new Point(point1.x+100, point1.y-15);
			    	
				    drawThickLine(g, a, c);
				    drawThickLine(g, b, d);
				    drawThickLine(g, a, b);
				    drawThickLine(g, c, d);
	    		
				    highVol = false;
				    prevVal = 0;
	    	}
	    	
	    	
		    
	    }else if(prevVal == 5 && s.equals("0")){
	    	
	    	if(sim.tickCounter>5) {
	    		
			    a = new Point(point1.x, point1.y+15);
			 	b = new Point(point1.x+100, point1.y+15);
			 	c = new Point(point1.x, point1.y-15);
			 	d = new Point(point1.x+100, point1.y-15);
		    	
			    drawThickLine(g, a, c);
			    drawThickLine(g, b, d);
			    drawThickLine(g, a, b);
			    drawThickLine(g, c, d);
			    
			    highVol = false;
			    prevVal = volts[0];
			    
	    	}else {
	    	    
				a = new Point(point1.x+100, point1.y+15);
			 	b = new Point(point1.x+200, point1.y+15);
			 	c = new Point(point1.x+100, point1.y-15);
			 	d = new Point(point1.x+200, point1.y-15);

		    	drawThickLine(g, point1, new Point(point1.x+100, point1.y));
		    	
			    drawThickLine(g, a, c);
			    drawThickLine(g, b, d);
			    drawThickLine(g, a, b);
			    drawThickLine(g, c, d);
	    		
			    highVol = true;
				prevVal = 5;
	    	}
	       
	    }else if(prevVal == 0 && s.equals("0")){
	    	
	    	    a = new Point(point1.x, point1.y+15);
			 	b = new Point(point1.x+100, point1.y+15);
			 	c = new Point(point1.x, point1.y-15);
			 	d = new Point(point1.x+100, point1.y-15);
		    	
			    drawThickLine(g, a, c);
			    drawThickLine(g, b, d);
			    drawThickLine(g, a, b);
			    drawThickLine(g, c, d);
			    
			    highVol = false;
			    prevVal = volts[0];
	    	
	    }else if(prevVal == 5 && s.equals("1")){
	    	
	    	a = new Point(point1.x+100, point1.y+15);
		 	b = new Point(point1.x+200, point1.y+15);
		 	c = new Point(point1.x+100, point1.y-15);
		 	d = new Point(point1.x+200, point1.y-15);

	    	drawThickLine(g, point1, new Point(point1.x+100, point1.y));
	    	
		    drawThickLine(g, a, c);
		    drawThickLine(g, b, d);
		    drawThickLine(g, a, b);
		    drawThickLine(g, c, d);
		    
		    highVol = true;
		    
		    prevVal = volts[0];
	    }

	    drawPosts(g);

	}
	
	void stamp() {
	    if (needsPullDown())
		sim.stampResistor(nodes[0], 0, 1e6);
	}
	
	double getVoltageDiff() { return volts[0]; }
	
	void getInfo(String arr[]) {
	    arr[0] = "logic output";
	    arr[1] = (volts[0] < threshold) ? "low" : "high";
	    if (isNumeric())
		arr[1] = value;
	    arr[2] = "V = " + getVoltageText(volts[0]);
	}
	
	int getShortcut() { return 'o'; }
	
}
