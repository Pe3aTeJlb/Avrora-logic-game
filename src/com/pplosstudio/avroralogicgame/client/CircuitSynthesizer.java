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
	
	private Point input_freeSpace = new Point(50,80);
	private Point freeSpace = new Point(10,100);
	private int startX, startY;
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	Map<String,CircuitElm> dictionary = new HashMap<String,CircuitElm>();
	
	public Vector<CircuitElm> elmList = new Vector<CircuitElm>();;
	
	//AWL = AdditionalWireLength
	private int AWL;
	
	int width, height;
	
	public void Synthesis(int w, int h) {
		
		/*
		CircuitElm newce3 = createCe("Linput",250, 100, 200, 100, 0, 2);
		newce3.setPoints();
		newce3.getConnectionPoints();
		elmList.add(newce3);
			
		CircuitElm newce4 = createCe("Loutput",300, 125, 350, 125, 0, 2);
		newce4.setPoints();
		newce4.getConnectionPoints();
		elmList.add(newce4);	
		
		CircuitElm newce5 = createCe("Wire",300, 125, 350, 125, 0, 2);
		newce5.setPoints();
		newce5.getConnectionPoints();
		GWT.log("Wire pointssize " + Integer.toString(newce5.OperativePoints.size()));	
		*/
		
		
		
		//ConnectElements(newce3,newce4);
		width = w;
		height = h;
		
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
        
       
        	
        if(basis.equals("Default")) {
	        if(MDNF) {
	        	//factorizator.PrepareData(sol.getSolution());
	        	shuntingYard.calculateExpression(sol.getSolution());
	        	//shuntingYard.calculateExpression(factorizator.output);
	        	//shuntingYard.calculateExpression("x0 * x1 * x2");
	        	GWT.log(factorizator.output);
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
        	
	}
	
	void CreateCircuit(ArrayList<ArrayList<String>> list) {
		
		GWT.log("list size is " + Integer.toString(list.size()));
		
		String blockName = "";
		
		for(int i = 0; i<list.size(); i++) {
			
				//GWT.log(Integer.toString(list.get(i).size())+ "!!!!!!!");
		
				int inputCount = list.get(i).size()-2;
				String operation= list.get(i).get(list.get(i).size()-2);
				blockName = list.get(i).get(list.get(i).size()-1);
				GWT.log("new Block Name " + blockName);
				
				if(i>=1 && list.get(i-1).get(0).length()<blockName.length() && list.get(i).get(0).length()>3) {
					
					freeSpace.x += 200;
					freeSpace.y = 150;
				}
				else {
					
					freeSpace.y += 150;
				}
				
				
				String ll = "";
				for(int j = 0; j<list.get(i).size(); j++) {
					ll+=list.get(i).get(j) + "  ";
				}
				GWT.log(ll);
				

				
				if(!dictionary.containsKey(blockName)) {
				
					CircuitElm newce = createCe(operation,freeSpace.x,freeSpace.y,freeSpace.x+50,freeSpace.y, 0, inputCount);
					newce.setPoints();
					newce.getConnectionPoints(false);
					elmList.add(newce);
					
					dictionary.put(blockName, newce);
					GWT.log(Boolean.toString(dictionary.containsKey(blockName)));
					
					for(int j = 0; j<list.get(i).size()-2; j++) {
						
						GWT.log("connect " + list.get(i).get(j) + " with "+ blockName);
						GWT.log(Boolean.toString(dictionary.containsKey(list.get(i).get(j))));
						GWT.log(Boolean.toString(dictionary.containsKey(blockName)));
						
						if(dictionary.containsKey(list.get(i).get(j))) {
							ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName));
						}else {
							
							GWT.log("He he he, error somewhere/ line 142");
							GWT.log(dictionary.toString());
							GWT.log(list.get(i-1).get(list.get(i-1).size()-1));
							GWT.log(list.get(i).get(j));
							GWT.log(Boolean.toString(list.get(i-1).get(list.get(i-1).size()-1) == (list.get(i).get(j))));
							ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName));
						}
						
					}
					
					
				}
				
				
		}
		
		CreateCircuitOutput(blockName);
		
		
	}
	
	//Create an input elem + 50l wire
	void CreateInput(String[] varNames) {
		
		//AWL = AdditionalWireLength
		AWL = varNames.length * 50;
		
		 freeSpace = new Point(input_freeSpace.x+AWL+300,15);
		
		//добавить кусок провода длиной в 50
		for(int i = 0; i<varNames.length; i++) {
			
			AWL -= 30*i;
			
			CircuitElm newce = createCe("Linput",input_freeSpace.x+50,input_freeSpace.y,input_freeSpace.x,input_freeSpace.y, 0, 2);
			newce.setPoints();
			newce.getConnectionPoints(false);
			elmList.add(newce);
			
			
			CircuitElm newwire = createCe("Wire",newce.OperativePoints.get(0).x,newce.OperativePoints.get(0).y,newce.OperativePoints.get(0).x + AWL,newce.OperativePoints.get(0).y, 0, 2);
			newwire.setPoints();
			newwire.getConnectionPoints(true);
			elmList.add(newwire);
			
			dictionary.put(varNames[i], newwire);
			input_freeSpace.y += 100;
			
			CircuitElm wire = createCe("Wire",newce.OperativePoints.get(0).x, newce.OperativePoints.get(0).y, newce.OperativePoints.get(0).x, newce.OperativePoints.get(0).y+50, 0, 0);
			wire.setPoints();
			wire.getConnectionPoints(true);
			elmList.add(wire);
			
			CircuitElm inverted = createCe("Invertor",newce.OperativePoints.get(0).x, newce.OperativePoints.get(0).y+50, newce.OperativePoints.get(0).x+100, newce.OperativePoints.get(0).y+50, 0, 0);
			inverted.setPoints();
			inverted.getConnectionPoints(true);
			elmList.add(inverted);
			
			String InverseInput = "~"+varNames[i];
			
			dictionary.put(InverseInput, inverted);
			
		}
		
	}
	
	//Create an invertor for input elem (down wire + 50l invertor)
	void CreateInverse(String input) {
		
		GWT.log("&&&&&&&&& " + input.substring(1));
		CircuitElm origin = dictionary.get(input.substring(1));
		GWT.log("&&&&&&&&&");
		
		CircuitElm wire = createCe("Wire",origin.OperativePoints.get(0).x, origin.OperativePoints.get(0).y, origin.OperativePoints.get(0).x, origin.OperativePoints.get(0).y-10, 0, 0);
		wire.setPoints();
		elmList.add(wire);
		
		CircuitElm inverted = createCe("Invertor",origin.OperativePoints.get(0).x, origin.OperativePoints.get(0).y-10, origin.OperativePoints.get(0).x+50, origin.OperativePoints.get(0).y-10, 0, 0);
		inverted.setPoints();
		elmList.add(inverted);
		
		dictionary.put(input, inverted);
		
	}
	
	//
	void CreateCircuitOutput(String lastBlock) {
		
		
		Point lastBlockOut = dictionary.get(lastBlock).OperativePoints.get(dictionary.get(lastBlock).OperativePoints.size()-1);
		
		CircuitElm newce4 = createCe("Loutput",lastBlockOut.x, lastBlockOut.y, lastBlockOut.x+50, lastBlockOut.y, 0, 2);
		newce4.setPoints();
		newce4.getConnectionPoints(true);
		elmList.add(newce4);
		
		ConnectElements(dictionary.get(lastBlock),newce4);
		
	}
	
	//не учитывает многовходные элементы
  	void ConnectElements(CircuitElm out, CircuitElm in) {
  		
  		//GWT.log("in op points size " + Integer.toString(in.OperativePoints.size()));
  		//GWT.log("out op points size " + Integer.toString(out.OperativePoints.size()));
  		
  		
  		Point prevOutput = out.OperativePoints.get(out.OperativePoints.size()-1);
		Point currentInput = in.OperativePoints.get(0);
		GWT.log(Integer.toString(prevOutput.x));
		GWT.log(Integer.toString(currentInput.x));
		int diap = ((prevOutput.x) + (int) (Math.random() * (currentInput.x-prevOutput.x)));
		GWT.log(Integer.toString(diap));
  		if(prevOutput.y != currentInput.y) {
  			
  			//int xDiff = Math.abs(currentInput.x - prevOutput.x)/2+prevOutput.x;
  			//int yDiff
  		
  			
  			CircuitElm shit1 = createCe("Wire",prevOutput.x, prevOutput.y, diap, prevOutput.y, 0, 0);
  			shit1.setPoints();
  			elmList.add(shit1);
  			
  			
  			CircuitElm shit2 = createCe("Wire",diap, prevOutput.y, diap, currentInput.y, 0, 0);
  			shit2.setPoints();
  			elmList.add(shit2);
  			
  			
  			CircuitElm shit3 = createCe("Wire",diap, currentInput.y, currentInput.x, currentInput.y, 0, 0);
  			shit3.setPoints();
  			elmList.add(shit3);
  			
  			
  			
  			
  			
  			/*	
  			CircuitElm shit1 = createCe("Wire",prevOutput.x, prevOutput.y, xDiff, prevOutput.y, 0, 0);
  			shit1.setPoints();
  			elmList.add(shit1);
  			
  			
  			CircuitElm shit2 = createCe("Wire",xDiff, prevOutput.y, xDiff, currentInput.y, 0, 0);
  			shit2.setPoints();
  			elmList.add(shit2);
  			
  			
  			CircuitElm shit3 = createCe("Wire",xDiff, currentInput.y, currentInput.x, currentInput.y, 0, 0);
  			shit3.setPoints();
  			elmList.add(shit3);
  			*/
  			
  			if(in.OperativePoints.size()>2) {
  				
  				in.OperativePoints.remove(0);
  				//GWT.log("op points size" + Integer.toString(in.OperativePoints.size()) + " line 223");
  				GWT.log("Confirmed");
  			}

  		}
  		else {
  		
	  		CircuitElm newce99 = createCe("Wire",prevOutput.x, prevOutput.y, currentInput.x, currentInput.y, 0, 0);
			newce99.setPoints();
			elmList.add(newce99);
			
			if(in.OperativePoints.size()>1) {
  				in.OperativePoints.remove(0);
  				//GWT.log("op points size" + Integer.toString(in.OperativePoints.size()) + " line 236");
  				GWT.log("Confirmed");
  			}
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
