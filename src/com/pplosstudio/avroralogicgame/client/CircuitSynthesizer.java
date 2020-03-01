package com.pplosstudio.avroralogicgame.client;

import java.util.Vector;

public class CircuitSynthesizer {

	public Vector<CircuitElm> elmList = new Vector<CircuitElm>();;
	
	
	
	public void Synthesis() {
		
			LogicFunctionGenerator n = new LogicFunctionGenerator(2,1);
		
	
			CircuitElm newce3 = createCe("Linput",250, 100, 200, 100, 0, 2);
			newce3.setPoints();
			newce3.getConnectionPoints();
			elmList.add(newce3);
				
			CircuitElm newce4 = createCe("Loutput",300, 125, 350, 125, 0, 2);
			newce4.setPoints();
			newce4.getConnectionPoints();
			elmList.add(newce4);	
			
			
			ConnectElements(newce3,newce4);
			
		
	}
	
	
	
  	public void ConnectElements(CircuitElm out, CircuitElm in) {
  		
  		
  		if(out.OperativePoints.get(0).y != in.OperativePoints.get(0).y) {
  			
  			int xDiff = Math.abs(in.OperativePoints.get(0).x - out.OperativePoints.get(0).x)/2+out.OperativePoints.get(0).x;
  			//int yDiff
  			
  			CircuitElm shit1 = createCe("Wire",out.OperativePoints.get(0).x, out.OperativePoints.get(0).y, xDiff, out.OperativePoints.get(0).y, 0, 0);
  			shit1.setPoints();
  			elmList.add(shit1);
  			
  			
  			CircuitElm shit2 = createCe("Wire",xDiff, out.OperativePoints.get(0).y, xDiff, in.OperativePoints.get(0).y, 0, 0);
  			shit2.setPoints();
  			elmList.add(shit2);
  			
  			
  			CircuitElm shit3 = createCe("Wire",xDiff, in.OperativePoints.get(0).y, in.OperativePoints.get(0).x, in.OperativePoints.get(0).y, 0, 0);
  			shit3.setPoints();
  			elmList.add(shit3);

  		}
  		else {
  		
	  		CircuitElm newce99 = createCe("Wire",out.OperativePoints.get(0).x, out.OperativePoints.get(0).y, in.OperativePoints.get(0).x, in.OperativePoints.get(0).y, 0, 0);
			newce99.setPoints();
			elmList.add(newce99);
		
  		}
		
  		
  	}
	
	
	
	
	
  	public static CircuitElm createCe(String marker, int x1, int y1, int x2, int y2, int f, int inputcount) {
    	
    	if(marker.equals("Wire")) {
    		return (CircuitElm) new WireElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("And")) {
        	return (CircuitElm) new AndGateElm(x1, y1, x2, y2, f, inputcount);
        }
    	if(marker.equals("Linput")) {
    		return (CircuitElm) new LogicInputElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("Loutput")) {
    		return (CircuitElm) new LogicOutputElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("Or")) {
    		return (CircuitElm) new OrGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	if(marker.equals("XOR")) {
    		return (CircuitElm) new XorGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	if(marker.equals("Nor")) {
    		return (CircuitElm) new NorGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	if(marker.equals("Nand")) {
    		return (CircuitElm) new NandGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	if(marker.equals("Invertor")) {
    		return (CircuitElm) new InverterElm(x1, y1, x2, y2, f);
    	}
    	else {return null;}
    	
    }
	
	
	
}
