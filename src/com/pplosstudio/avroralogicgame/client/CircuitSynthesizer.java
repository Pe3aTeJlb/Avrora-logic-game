package com.pplosstudio.avroralogicgame.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.core.client.GWT;



public class CircuitSynthesizer {
	
	private boolean MDNF;
	private String basis = "";
	private int funcCount = 0;
	private int varCount = 0;
	
	private int startX, startY;
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	Map<String,CircuitElm> dictionary = new HashMap<String,CircuitElm>();
	
	public Vector<CircuitElm> elmList = new Vector<CircuitElm>();;
	
	
	public void Synthesis() {
		
		
		CircuitElm newce3 = createCe("Linput",250, 100, 200, 100, 0, 2);
		newce3.setPoints();
		newce3.getConnectionPoints();
		elmList.add(newce3);
			
		CircuitElm newce4 = createCe("Loutput",300, 125, 350, 125, 0, 2);
		newce4.setPoints();
		newce4.getConnectionPoints();
		elmList.add(newce4);	
		
		
		ConnectElements(newce3,newce4);
		
			startX = 10;
			startY= 100;
		
			GetConfigurationFile();
			ConstructCircuit();
			
	}
	
	void GetConfigurationFile() {
		
		 MDNF = true;
		 basis = "Default";
		 funcCount = 1;
		 varCount = 3;
		
	}
	
	void ConstructCircuit() {
		
			
        BasisConverter converter = new BasisConverter();
        Factorisator_V_2 factorizator = new Factorisator_V_2();
        ShuntingYard shuntingYard = new ShuntingYard();
		
		LogicFunctionGenerator generator = new LogicFunctionGenerator(varCount,funcCount);        
        
		
        Solver sol = new Solver(
              generator.VectorFunctions,
                				varCount,
                     generator.VarNames,
                     			funcCount,
                     generator.OutNames,
                             MDNF,
                            false,
                             true
        						);
        sol.run();
        
		GWT.log(sol.getSolution());
        CreateInput(generator.VarNames);
        	
        /*
        if(basis.equals("Default")) {
        
	        if(MDNF) {
	        	factorizator.PrepareData(sol.getSolution());
	        	shuntingYard.calculateExpression(factorizator.output);
	        	list = shuntingYard.list;
	        }
	        else {
	        	shuntingYard.calculateMKNF(factorizator.output);
	        	list = shuntingYard.list;
	        }
        }
        else if(basis.equals("Nor")) {
        	converter.ToNor(sol.getSolution(), MDNF);
        	list = converter.list;
        }
        else if(basis.equals("Nand")) {
        	converter.ToNand(sol.getSolution(), MDNF);
        	list = converter.list;
        }
        else if(basis.equals("Zhegalkin")) {
        	converter.ToZhegalkinPolynomial(generator.VectorFunctions,0,generator.VarNames);
        	list = converter.list;
        }
        
        CreateCircuit(list);
        */
		
		
	}
	
	
	void CreateCircuit(ArrayList<ArrayList<String>> list) {
		
		for(int i = 0; i<list.size(); i++) {
			
			
			
			
			
			
			
			
			
			
		}
		
		
		
	}
	
	void CreateInput(String[] varNames) {
		
		for(int i = 0; i<varNames.length; i++) {
			
			CircuitElm newce = createCe("Linput",startX+50,startY,startX,startY, 0, 2);
			newce.setPoints();
			newce.getConnectionPoints();
			
			elmList.add(newce);
			dictionary.put(varNames[i], newce);
			startY += 100;
		}
		
	}
	
	
	void CreateInverse() {
		
		
		
	}
	
	
  	void ConnectElements(CircuitElm out, CircuitElm in) {
  		
  		
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
	

  	static CircuitElm createCe(String marker, int x1, int y1, int x2, int y2, int f, int inputcount) {
    	
    	if(marker.equals("Wire")) {
    		return (CircuitElm) new WireElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("*")) {
        	return (CircuitElm) new AndGateElm(x1, y1, x2, y2, f, inputcount);
        }
    	if(marker.equals("Linput")) {
    		return (CircuitElm) new LogicInputElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("Loutput")) {
    		return (CircuitElm) new LogicOutputElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("+")) {
    		return (CircuitElm) new OrGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	if(marker.equals("Xor")) {
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
