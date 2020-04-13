package com.pplosstudio.avroralogicgame.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

//x0, x1, ~x0 и тд - Входные элементы схемы

//Описание схемы выглядит следующим образом
// [ [,входной элемент 1 прим. x0,вхлдной элемент 2,...,символ операции, имя нового элемента], [аналогично] ,...]

//Про систему индексов входов, выходов элемента. 
/*
 Сначала надо высчитать эти точки - массив getOperativePoint а любом объекте класса circuitElm
 Последняя точка в этом массиве - выход элемента, всё остальное входы.
 В случае провода - 0 индекс - точка начала провода (левый конец, если провод идёт слева на право), соответственно индекс 1 - точка окончания
 */

/*
 * drawPost отвечает за отрисовку точек
 */

public class CircuitSynthesizer {
	
	private boolean MDNF, factorize;
	private String basis = "";
	private int funcCount = 0;
	private int varCount = 0;
	
	private Point input_freeSpace = new Point(50,80); //Точка начала отрисовки входов схемы
	private Point StartPoint = new Point(1,1); // фиксация текущей свободной точки
	private Point freeSpace = new Point(10,100); //текущая свободнеая точка
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	Map<String,CircuitElm> dictionary = new HashMap<String,CircuitElm>();
	
	public Vector<CircuitElm> elmList = new Vector<CircuitElm>();
	
	ArrayList<String> splittedWires = new ArrayList<String>(); 
	
	//AWL = Additional Wire Length
	private int AWL;
	
	int width, height;
	
	public void Synthesis(int w, int h) {
		
		width = w;
		height = h;
		
			GetConfigurationFile();
			InitializeParametrs();
			
	}
	
	void GetConfigurationFile() {
		
		 MDNF = true;
		 factorize = true;
		 basis = "Default";
		 //basis = "Nor";
		// basis = "Zhegalkin";
		 funcCount = 1;
		 varCount = 3 ;
		
	}
	
	//Инициализация всех объектов и применение файла конфигурации
	void InitializeParametrs() {
				
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
		
		CreateInputElm(generator.VarNames);
        
		String functions[] = sol.getSolution().split("\n");
		
        for(int i = 0; i<functions.length; i++) {
		
	        if(basis.equals("Default")) {
		        if(MDNF) {
		        	if(factorize) {
			        	factorizator.PrepareData(sol.getSolution());
			        	GWT.log(factorizator.output);
		        		//GWT.log("((~x1)*(x0)*(x2+~x2)+(~x0)*(~x2+x2))+~x0*x1*~x2");
			        	shuntingYard.calculateExpression(factorizator.output);
			        	//shuntingYard.calculateExpression("((~x1)*(x0)*(x2+~x2)+(~x0)*(~x2+x2))+~x0*x1*~x2");
			        	//(~x1)*(x0)*(x2+~x2)+(~x0)*(~x2+x2))+~x0*x1*~x2
		        	}else {
		        		shuntingYard.calculateMDNF(sol.getSolution());
		        	}
		        	list = shuntingYard.list;
		        }
		        else {
		        	shuntingYard.calculateMKNF(sol.getSolution());
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
        	
	}
		
	//Проход по всему списку, составление hashmap с созданными эдементами, установка параметров соединения объектов
	void CreateCircuit(ArrayList<ArrayList<String>> list) {
		
		//GWT.log("list size is " + Integer.toString(list.size()));
		
		String blockName = "";
		
		for(int i = 0; i<list.size(); i++) {
			
				//GWT.log(Integer.toString(list.get(i).size())+ "!!!!!!!");
		
				int inputCount = list.get(i).size()-2;
				String operation= list.get(i).get(list.get(i).size()-2);
				blockName = list.get(i).get(list.get(i).size()-1);
				GWT.log("new Block Name " + blockName);
				
				
				//Сдвигаем 
				if(factorize) {
					
					if(i>=1 && list.get(i).get(list.get(i).size()-1).length()/list.get(i-1).get(list.get(i-1).size()-1).length()>=1) {
						
						freeSpace.x += 150;
						freeSpace.y += 50;
					}
					//else if(i>=1 && list.get(i).get(list.get(i).size()-1).length()/list.get(i-1).get(list.get(i-1).size()-1).length()<1 ) {
					//	freeSpace.x -= 150;
					//	freeSpace.y += 50;
				//	}
					else {freeSpace.y += 150;}
					
					
				}
				else {
					
					if(i<list.size()-1) {
						
						if(i>=1 && list.get(i-1).get(list.get(i-1).size()-2) == operation ) {
							
							freeSpace.y += 150;
							
						}else {
							
							freeSpace.x += 100;
							freeSpace.y = StartPoint.y;
							
						}
						
					}else if (i==list.size()-1) {
						
						freeSpace.x += 250;
						freeSpace.y = (int)((input_freeSpace.y - StartPoint.y)/2);
						
					}
				}
				
				String ll = "";
				for(int j = 0; j<list.get(i).size(); j++) {
					ll+=list.get(i).get(j) + "  ";
				}
				GWT.log(ll);
				
				if(!dictionary.containsKey(blockName)) {
				
					CircuitElm newce = createCe(operation,freeSpace.x,freeSpace.y,freeSpace.x+60,freeSpace.y, 0, inputCount);
					newce.setPoints();
					newce.getConnectionPoints(false);
					elmList.add(newce);
					
					dictionary.put(blockName, newce);
					//GWT.log(Boolean.toString(dictionary.containsKey(blockName)));
					
					for(int j = 0; j<list.get(i).size()-2; j++) {
						
						//GWT.log("connect " + list.get(i).get(j) + " with "+ blockName);
						//GWT.log(Boolean.toString(dictionary.containsKey(list.get(i).get(j))));
						//GWT.log(Boolean.toString(dictionary.containsKey(blockName)));
						
						//Выбор параметров соежинения: Перелом, ВходнойЭлементСхемы?
						
						if(dictionary.containsKey(list.get(i).get(j))) {
							
							if(splittedWires.contains(list.get(i).get(j))) {
								
								if(list.get(i).get(j).length()<=3) {
									ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),true,true);
								}else {
									ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),true,false);
									}
							
							}else {
								
								splittedWires.add(list.get(i).get(j));
								if(list.get(i).get(j).length()<=3) {
									ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),false,true);
								}else {
									ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),false,false);
									}
							
							}
							
						}else {
							GWT.log("He he he, error somewhere/ line 142 " + list.get(i).get(j));
							
							ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),false,false);
							//break;
						}
						
						
					}
					
					
				}
				
				GWT.log("END OF BLOCJ !!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		
		CreateCircuitOutput(blockName);
		
		
	}
	
	/*
	 Создаёт входные элементы схемы, их инвертированный вариант и записывает в hashMap
	 
	 Первый вход схемы не доходит 40 единиц до ряда элементов, каждый последующий на 40 больше, (сюда включаются и инвертированные )
	 */
	void CreateInputElm(String[] varNames) {
		
		
		AWL = 100;
		int minus = 0;
		//AWL = 2*varNames.length * 50;
		
		freeSpace = new Point(input_freeSpace.x+AWL+varNames.length*100,input_freeSpace.y);
		StartPoint = new Point(input_freeSpace.x+AWL+varNames.length*100,input_freeSpace.y);
		
		//добавить кусок провода длиной в 50
		for(int i = 0; i<varNames.length; i++) {
			
			minus += 40;
			
			CircuitElm newce = createCe("Linput",input_freeSpace.x+50,input_freeSpace.y,input_freeSpace.x,input_freeSpace.y, 0, 2);
			newce.setPoints();
			newce.getConnectionPoints(false);
			elmList.add(newce);
			
			
			CircuitElm newwire = createCe("Wire",newce.OperativePoints.get(0).x,newce.OperativePoints.get(0).y,freeSpace.x+100-minus,newce.OperativePoints.get(0).y, 0, 2);
			newwire.setPoints();
			newwire.getConnectionPoints(true);
			elmList.add(newwire);
			
			dictionary.put(varNames[i], newwire);
			input_freeSpace.y += 100;
			
			minus += 40;
			
			CircuitElm wire = createCe("Wire",newce.OperativePoints.get(0).x, newce.OperativePoints.get(0).y, newce.OperativePoints.get(0).x, newce.OperativePoints.get(0).y+50, 0, 0);
			wire.setPoints();
			wire.getConnectionPoints(true);
			elmList.add(wire);
			
			CircuitElm inverted = createCe("Invertor",newce.OperativePoints.get(0).x, newce.OperativePoints.get(0).y+50, newce.OperativePoints.get(0).x+100, newce.OperativePoints.get(0).y+50, 0, 0);
			inverted.setPoints();
			inverted.getConnectionPoints(true);
			elmList.add(inverted);
			
			CircuitElm wire2 = createCe("Wire",inverted.OperativePoints.get(1).x,inverted.OperativePoints.get(1).y,freeSpace.x+100-minus,inverted.OperativePoints.get(1).y, 0, 2);
			wire2.setPoints();
			wire2.getConnectionPoints(true);
			elmList.add(wire2);
			
			String InverseInput = "~"+varNames[i];
			
			dictionary.put(InverseInput, wire2);
			
		}
		
	}
	
	/*
	//Create an invertor for input elem (down wire + 50l invertor)
	void CreateInverse(String input) {
		
		//GWT.log("&&&&&&&&& " + input.substring(1));
		CircuitElm origin = dictionary.get(input.substring(1));
		//GWT.log("&&&&&&&&&");
		
		CircuitElm wire = createCe("Wire",origin.OperativePoints.get(0).x, origin.OperativePoints.get(0).y, origin.OperativePoints.get(0).x, origin.OperativePoints.get(0).y-10, 0, 0);
		wire.setPoints();
		elmList.add(wire);
		
		CircuitElm inverted = createCe("Invertor",origin.OperativePoints.get(0).x, origin.OperativePoints.get(0).y-10, origin.OperativePoints.get(0).x+50, origin.OperativePoints.get(0).y-10, 0, 0);
		inverted.setPoints();
		elmList.add(inverted);
		
		dictionary.put(input, inverted);
		
	}
	*/
	
	//Создаёт выходной элемент схемы на последнем лог. элементе
	void CreateCircuitOutput(String lastBlock) {
		
		Point lastBlockOut = dictionary.get(lastBlock).OperativePoints.get(dictionary.get(lastBlock).OperativePoints.size()-1);
		
		CircuitElm newce4 = createCe("Loutput",lastBlockOut.x, lastBlockOut.y, lastBlockOut.x+50, lastBlockOut.y, 0, 2);
		newce4.setPoints();
		newce4.getConnectionPoints(true);
		elmList.add(newce4);
		
		ConnectElements(dictionary.get(lastBlock),newce4,lastBlock,false,false);
		
	}
	
	//Соединение двух элементов
  	void ConnectElements(CircuitElm out, CircuitElm in, String outName, boolean alreadySplitted, boolean isInputElm) {
  		
  		//GWT.log("in op points size " + Integer.toString(in.OperativePoints.size()));
  		//GWT.log("out op points size " + Integer.toString(out.OperativePoints.size()));
  		
  		
  		Point prevOutput = out.OperativePoints.get(out.OperativePoints.size()-1); //выход предыдущего элемента
  		int closestInputIndex = GetClosestInput(prevOutput, in); //поиск индекса ближайшего входа к выходу
  		Point currentInput = in.OperativePoints.get(closestInputIndex);
  		
  		
		//GWT.log(Integer.toString(prevOutput.y));
		//GWT.log(Integer.toString(currentInput.y));

  		if(prevOutput.y != currentInput.y) {
  		
  			//Если в соединении есть узел
  			if(alreadySplitted) {
  				
  				//GWT.log("###########");
  				int diff1 = Math.abs(out.OperativePoints.get(out.OperativePoints.size()-1).y-currentInput.y);
  				int diff2 =		Math.abs(out.OperativePoints.get(0).y-currentInput.y);
  				
  				//Находим разность в высоте между входами элемента и позицией узла
  				//В зависимости от этого выбираем конец провода, от которого идём
  				//в случае их равенства предпочтение отдаётся точке начала провода (Индекс 0)
  				if(diff1<diff2) {
  					prevOutput = out.OperativePoints.get(out.OperativePoints.size()-1);
  				}else if(diff1==diff2){
  					prevOutput =   out.OperativePoints.get(0);	
  				}
  				else {
  					prevOutput =   out.OperativePoints.get(0);					
  				}
  				
  				CircuitElm shit2 = createCe("Wire",prevOutput.x, prevOutput.y, prevOutput.x, currentInput.y, 0, 0);
	  			shit2.setPoints();
	  			shit2.getConnectionPoints(true);
	  			elmList.add(shit2);
	  			
	  			CircuitElm shit3 = createCe("Wire",prevOutput.x, currentInput.y, currentInput.x, currentInput.y, 0, 0);
	  			shit3.setPoints();
	  			elmList.add(shit3);
  				
	  			dictionary.replace(outName, shit2);
  				
  				
  			}else {
  				
  				//Если нет узла
  				
  				int diap = 0;
  				
  				//Если это входной элемент схемы
  				if(isInputElm) { 
  					  					
  					CircuitElm shit2 = createCe("Wire",prevOutput.x, prevOutput.y, prevOutput.x, currentInput.y, 0, 0);
  		  			shit2.setPoints();
  		  			shit2.getConnectionPoints(true);
  		  			elmList.add(shit2);
  		  			
  		  			
  		  			CircuitElm shit3 = createCe("Wire",prevOutput.x, currentInput.y, currentInput.x, currentInput.y, 0, 0);
  		  			shit3.setPoints();
  		  			elmList.add(shit3);	
  		  			
  		  			dictionary.replace(outName, shit2);
  					
  				}else { 
  					
  					diap = ((prevOutput.x+20) + (int) (Math.random() * (currentInput.x-prevOutput.x-40)));
	  		  		int temp = diap%10;
	  		  		
	  		  		if(temp!=0) {
	  		  			diap = diap + (10-diap%10);
	  		  		}
	  		  		
	  		  	//	GWT.log("DIAP "+ Integer.toString(diap));
		  			CircuitElm shit1 = createCe("Wire",prevOutput.x, prevOutput.y, diap, prevOutput.y, 0, 0);
		  			shit1.setPoints();
		  			elmList.add(shit1);
		  			
		  			
		  			CircuitElm shit2 = createCe("Wire",diap, prevOutput.y, diap, currentInput.y, 0, 0);
		  			shit2.setPoints();
		  			shit2.getConnectionPoints(true);
		  			elmList.add(shit2);
		  			
		  			
		  			CircuitElm shit3 = createCe("Wire",diap, currentInput.y, currentInput.x, currentInput.y, 0, 0);
		  			shit3.setPoints();
		  			elmList.add(shit3);
	  		  		
		  			dictionary.replace(outName, shit2);
		  			
  				}
  				

  			
  			}
  			

  			
  			if(in.OperativePoints.size()>2) {
  				
  				in.OperativePoints.remove(closestInputIndex);
  				//GWT.log("op points size" + Integer.toString(in.OperativePoints.size()) + " line 223");
  				//GWT.log("Confirmed");
  			}

  		   
  			
  		}
  		else { //Если выход и вход на одной линии
  			
	  		CircuitElm newce99 = createCe("Wire",prevOutput.x, prevOutput.y, currentInput.x, currentInput.y, 0, 0);
			newce99.setPoints();
			newce99.getConnectionPoints(true);
			elmList.add(newce99);
			
			dictionary.replace(outName, newce99);
			
			if(in.OperativePoints.size()>1) {
  				in.OperativePoints.remove(closestInputIndex);
  				//GWT.log("op points size" + Integer.toString(in.OperativePoints.size()) + " line 236");
  				//GWT.log("Confirmed");
  			}
  		}
		
  		
  	}
	
  	//Индекс входа ближайший к выходу
  	Integer GetClosestInput(Point out, CircuitElm in) {
  		
  		int index = 0;
  		Point point = in.OperativePoints.get(0);
  		
  		int delta = Math.abs(out.y-point.y);
  		
  		for(int i = 1; i<in.OperativePoints.size()-1; i++) {
  			
  			int j = i;
  			int currDelta = Math.abs(out.y-in.OperativePoints.get(i).y);
  					
  			if(currDelta < delta) {
  				index = j;
  				point = in.OperativePoints.get(i);
  				delta = currDelta;
  			}else {}
  			
  		}
  		
  		return index;
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
