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
 Сначала надо высчитать эти точки - массив getOperativePoint() а любом объекте класса circuitElm
 Последняя точка в этом массиве - выход элемента, всё остальное входы.
 В случае провода - 0 индекс - точка начала провода (левый конец, если провод идёт слева на право), соответственно индекс 1 - точка окончания
 */

/*
 * drawPost отвечает за отрисовку точек
 */

/*	  	   _____________
 * 		0--|комб.логика|--0
 * 			-----------
 * входная переменная     выходная переменная
 */		 

public class CircuitSynthesizer {
	
	//Debug log
	private boolean debug = false;
	private boolean ShuntingYardDebug = false;
	private boolean LogicVectorGeneratorDebug = false;
	private boolean BasisConverterDebug = false;
	public  String dump = ""; 
	
	private boolean MDNF, factorize;
	private String basis = "";
	private int funcCount = 0;
	private int varCount = 0;
	private ArrayList<String> sharedVars = new ArrayList();

	private Point input_freeSpace = new Point(50,80); //Точка начала отрисовки входов схемы
	private Point StartPoint = new Point(1,1); // фиксация начальной точки текущей функции
	private Point NextStartPoint = new Point(2,2); //фиксация начальной точки следующей функции
	private Point freeSpace = new Point(3,3); //текущая свободнеая точка
	private int lastElemPos = 0;
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	Map<String,CircuitElm> dictionary = new HashMap<String,CircuitElm>();
	
	public Vector<CircuitElm> elmList = new Vector<CircuitElm>();//все элементы схемы. Нужны для отрисовки
	
	ArrayList<String> splittedWires = new ArrayList<String>();//см логику соединения
	
	//Для удаления неиспользуемых входных переменных
	ArrayList<ArrayList<CircuitElm>> UnusedInputs = new ArrayList<>();
	ArrayList<String> UnusedVarNames = new ArrayList<>();
	ArrayList<ArrayList<String>> allInputs = new ArrayList(); 
	
	//Для игровой логики
	ArrayList<SwitchElm> inElems = new ArrayList(); //список входных элементов
	ArrayList<CircuitElm> outElems = new ArrayList(); //список выходных элементов

	//AWL = Additional Wire Length
	private int AWL;
	
	int width, height;

	private float minTrue = 0.5f;//Устанавливает максимальное и минимальное количество единиц в вектор функции
	private float maxTrue = 0.6f;
	int dashCirc = 0; //Индекс текущей схемы для смещения
	
	int wireSplitPoint = 0; //x координата перелома провода
	
	BasisConverter converter = new BasisConverter(BasisConverterDebug);
    Factorisator_V_2 factorizator = new Factorisator_V_2();
    ShuntingYard shuntingYard = new ShuntingYard(ShuntingYardDebug);
	LogicFunctionGenerator generator = new LogicFunctionGenerator(LogicVectorGeneratorDebug);  
	
	public void Synthesis(int w, int h) {
		
		sharedVars = new ArrayList();	
		
		width = w;
		height = h;
			
		int circCount = random(1,6);
		
		if(debug) {
			dump += "Circ count "+ Integer.toString(circCount) + "\n";
			GWT.log("Circ count "+ Integer.toString(circCount));
			GWT.log("");
		}
		
		//minTrue = random(2,6)/10;
		//maxTrue = minTrue+0.2f;
		
		if(debug) {
			GWT.log("Circ count "+ Integer.toString(circCount));
			GWT.log("");
			GWT.log("maxT "+ maxTrue);
		}dump += "Circ count "+ Integer.toString(circCount) + "\n"+"\n" + "maxT" + maxTrue+ "\n";
		
		for(int i = 0; i < circCount; i++) {
			
			int Basis =  random(0,2);
			
			int mdnf =  random(0,1);
			if(mdnf == 0) {
				MDNF = false;
			}else {MDNF = true;}
			
			if(Basis == 0) {
				basis = "Default";
				if(MDNF == true) {
					int factr =  random(0,1);
					if(factr == 1) {
						factorize = true;
						varCount =  random(2,4);
					}else {
						factorize = false;
						varCount =  random(2,4);
					}
				}
				else {varCount =  random(2,4);}
			}else if(Basis == 1) {
				basis = "Nor";
				varCount =  random(2,3);
			}else if(Basis == 2) {
				basis = "Nand";
				varCount =  random(2,3);
			}else if(Basis == 3) {
				basis = "Zhegalkin";
				varCount =  random(2,3);
			}
			
			funcCount =  random(1,4);
			//funcCount = random(1,(int)Math.floor(0.5f*Math.log(circDifficult-0.5f)+2));

			
			 if(debug) {
				 GWT.log("MDNF " + MDNF);
				 GWT.log("Factr "+factorize);
				 GWT.log("Basis " + basis);
				 GWT.log("Function count " + funcCount);
				 GWT.log("Var count " + varCount);
			 }
			 dump += "MDNF " + MDNF + "\n" + "Factr "+factorize + "\n" + "Basis " + basis + "\n" + 
					 "Function count " + funcCount + "\n" + "Var count " + varCount + "\n";
			 
					//Add shared input
					if(i>0 && i < circCount - 1 && circCount>1) {
						GWT.log("Shared Vars++");
						dump+= "Include Shared Vars"+"\n";
						int n = random(0,1);
						
						if(n==1) {
							
							int sharedVarsCount = random(1,varCount-1);
							
							for(int j = 0; j<sharedVarsCount; j++) {
								add();
							}
						
						}
						
					} 
			
			dashCirc++;		
			InitializeParametrs();
			input_freeSpace.y += 100;
			freeSpace.y = input_freeSpace.y;
			
			
		}
		DeleteUnusedInputs();
		//GWT.log("Demp"+dump);	
}
	
	public void Synthesis(int w, int h, String url) {
		
		 
		width = w;
		height = h;
	
		//GetConfigurationFile();
		sharedVars = new ArrayList();	
	
		 MDNF = true;
		 factorize = true;
		 basis = "Default";
		// basis = "Nor";
		// basis = "Nand";
		// basis = "Zhegalkin";
		 funcCount = 4;
		 varCount = 4;
		 
		 if(debug) {
			 GWT.log("MDNF " + MDNF);
			 GWT.log("Basis " + basis);
			 GWT.log("Function count " + funcCount);
			 GWT.log("Var count " + varCount);
		 }
		 
		
		InitializeParametrs();
	
}
	
	public void Synthesis(int w, int h, int circDifficult) {
		
		generator.callOnce = true;
		
		sharedVars = new ArrayList();	
		
		width = w;
		height = h;
			
		int circCount = (int)Math.floor(((0.35f*circDifficult-1.8f)*(0.35f*circDifficult-1.8f)+3));
		
		if(circDifficult <6) {
			minTrue = 0.5f;
			maxTrue = 0.6f;
		}else {
			minTrue = 0.3f;
			maxTrue = 0.4f;
		}
		
		if(debug) {
			GWT.log("Circ count "+ Integer.toString(circCount));
			GWT.log("");
			GWT.log("maxT "+ maxTrue);
		}dump += "Circ count "+ Integer.toString(circCount) + "\n"+"\n" + "maxT" + maxTrue+ "\n";
		
		for(int i = 0; i < circCount; i++) {
			//dashCirc = 0;
			
			int Basis = 0;
			if(circDifficult < 5) {
				int maxDif = (int)Math.floor(circDifficult*0.6f);
				if(maxDif>2)maxDif=2;
				Basis =  random(1,maxDif);
			}else {
				Basis =  random(1,3);
				
			}
			int mdnf =  random(0,1);
			if(mdnf == 0) {
				MDNF = false;
			}else {MDNF = true;}
			
			if(Basis == 1) {
				basis = "Default";
				if(MDNF == true) {
					int factr =  random(0,1);
					if(factr == 1) {
						factorize = true;
					}else {
						factorize = false;
					}
				}
			}else if(Basis == 2) {
				basis = "Nor";
				varCount =  random(2,3);
			}else if(Basis == 3) {
				basis = "Nand";
				varCount =  random(2,3);
			}else if(Basis == 4) {
				basis = "Zhegalkin";
				varCount =  random(2,3);
			}
			
			varCount = (int)(Math.floor(0.0025f*Math.pow((circDifficult-2), 3)+2.9f));
			funcCount =  (int)Math.floor(0.5f*Math.log(circDifficult-0.5f)+2);
			
			 if(debug) {
				 GWT.log("MDNF " + MDNF);
				 GWT.log("Factr "+factorize);
				 GWT.log("Basis " + basis);
				 GWT.log("Function count " + funcCount);
				 GWT.log("Var count " + varCount);
			 }
			 dump += "MDNF " + MDNF + "\n" + "Factr "+factorize + "\n" + "Basis " + basis + "\n" + 
					 "Function count " + funcCount + "\n" + "Var count " + varCount + "\n";
			 
					//Add shared input
					if(i>0 && i < circCount - 1 && circCount>1 && circDifficult > 4) {
						sharedVars.clear();
						if(debug)GWT.log("Shared Vars++");dump+= "Include Shared Vars"+"\n";

							int sharedVarsCount = random(1,varCount-2);
							
							for(int j = 0; j<sharedVarsCount; j++) {
								add();
							}
						
					} 
			try{
				if(!sharedVars.isEmpty())dashCirc++;
				
				InitializeParametrs();
				
				input_freeSpace.y += 100;
				freeSpace.y = input_freeSpace.y;
				if(sharedVars.isEmpty())lastElemPos = 0;
				
			}catch(Exception e){	
				dump  = e.toString() +"\n\n"+ dump;	
			}
			
			if(debug)GWT.log("End of Circuit");
			dump+="End of Circuit"+"\n\n";
		}
		
		DeleteUnusedInputs();
		
		GWT.log(dump);
	}
	
	//Создание списка наследуемых входных переменных
	void add() {
		
		int a = random(0,allInputs.size()-1);
		int b = random(0,allInputs.get(a).size()-1);
		if(!sharedVars.contains(allInputs.get(a).get(b))) {
			sharedVars.add(allInputs.get(a).get(b));
		}else {add();}
		
	}
	
	void GetConfigurationFile() {
		
		 MDNF = true;
		 factorize = false;
		 basis = "Default";
		 //basis = "Nor";
		// basis = "Zhegalkin";
		 funcCount = 1;
		 varCount = 3;
		
	}
	
	//Инициализация всех объектов и применение файла конфигурации
	void InitializeParametrs() {
        
        generator.GenerateVectorFunction(varCount, funcCount, sharedVars, minTrue,maxTrue);
		
        dump += generator.dmp;
        
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
		
        ArrayList<String> lst = new ArrayList();
        for(int i = 0; i < generator.VarNames.length; i++) {
        	lst.add(generator.VarNames[i]);
        }
        //GWT.log(lst.toString());
        allInputs.add(lst);
		CreateInputElm(generator.VarNames);
        
		String functions[] = sol.getSolution().split("\n");
		
	      for(int i = 0; i<functions.length; i++) {
	    	  if(debug)GWT.log(functions[i]+"\n");
	    	  dump+=functions[i]+"\n";
	      }
	      dump+="\n";
		
        for(int i = 0; i<functions.length; i++) {
        	
        	list.clear();
        	
        	StartPoint.y = NextStartPoint.y;
        	StartPoint.x = NextStartPoint.x;
        	
        	freeSpace.y = NextStartPoint.y;
        	freeSpace.x = NextStartPoint.x;
        
	        if(basis.equals("Default")) {
		        if(MDNF) {
		        	if(factorize) {
			        	factorizator.PrepareData(functions[i], varCount);
			        	 if(debug)GWT.log("Factr out"+factorizator.output);
			        	 dump+="Factr out"+factorizator.output+"\n\n";
			        	shuntingYard.calculateExpression(factorizator.output);
		        	}else {
		        		shuntingYard.calculateMDNF(functions[i]);
		        	}
		        	list = shuntingYard.list;
		        }
		        else {
		        	shuntingYard.calculateMKNF(functions[i]);
		        	list = shuntingYard.list;
		        }
	        }
	        else if(basis.equals("Nor")) {
	        	converter.ToNor(functions[i], MDNF);
	        	dump+=converter.dmp+"\n";
	        	list = converter.list;
	        }
	        else if(basis.equals("Nand")) {
	        	converter.ToNand(functions[i], MDNF);
	        	dump+=converter.dmp+"\n";
	        	list = converter.list;
	        }
	        else if(basis.equals("Zhegalkin")) {
	        	converter.ToZhegalkinPolynomial(generator.VectorFunctions,i,generator.VarNames);
	        	dump+=converter.dmp+"\n";
	        	list = converter.list;
	        }
	        
	        CreateCircuit(list);
        }
        
        	//DeleteUnusedInputs();
	}
		
	//Проход по всему списку, составление hashmap с созданными эдементами, установка параметров соединения объектов
	void CreateCircuit(ArrayList<ArrayList<String>> list) {
		
		//GWT.log("list size is " + Integer.toString(list.size()));
		
		String blockName = "";
		
		String ll2 = "";
		for(int i = 0; i<list.size(); i++) {
			for(int j = 0; j<list.get(i).size(); j++) {
				ll2+=list.get(i).get(j) + "  ";
			}
			ll2 += " endl "+"\n";
		}
		if(debug)GWT.log(ll2);
		dump+=ll2;
		
		int Yaccum = 1;
		int MaxFreeSpaceY = 0;
		
		for(int i = 0; i<list.size(); i++) {
		
				int inputCount = list.get(i).size()-2;
				String operation= list.get(i).get(list.get(i).size()-2);
				blockName = list.get(i).get(list.get(i).size()-1);
				//GWT.log("new Block Name " + blockName);
				
	
				/*
				 * Общий смысл алгоритма расположения
				 * для общих случаев Nor Nand Def элементы располагаеются в ряды по операциям.
				 * В Nor Nand есть нюанс с расположением выходного элемента в случае инвертирования ответа через элемент базиса см далее
				 */
				
				//Правила расположения элементов для общего базиса и Жегалкина
				if(basis.equals("Default") || basis.equals("Zhegalkin")) 
				{
					
					/*
					 * При факторизации выражения:
					 * Если есть два блока вида [x1 x2 * x1*x2] и [x3 x1*x2 +* x3+*x1*x2] торой блок будет сдвинут вправо-вниз
					 */
					
					if(factorize) 
					{
						
						if(i<list.size()-1) 
						{
						
							if(i==0)
							{		
								freeSpace.x += 75;
								freeSpace.y = StartPoint.y;
							}
							else if(i>=1 && list.get(i).get(0).length()<=3) 
							{
								
								if(!dictionary.containsKey(blockName)) {
									
									freeSpace.x += 125;
									freeSpace.y = MaxFreeSpaceY+100;
								}
								
							}
							else if(i>=1 && list.get(i-1).get(list.get(i-1).size()-2) == operation ) 
							{
								
								freeSpace.y += 150;
								//freeSpace.x += 25;
						
							}
							else
							{
								freeSpace.x += 150;
								freeSpace.y = StartPoint.y+100*Yaccum;
								Yaccum += 1;
							}
							
						}
						else if(i==list.size()-1)
						{
							if(MaxFreeSpaceY==0) {
								MaxFreeSpaceY = StartPoint.y;
							}
							
							if(i>=1 && list.get(i).get(0).length()<=3) {
								
								if(!dictionary.containsKey(blockName)) {
									freeSpace.x += 125;
									//freeSpace.y += dictionary.get(list.get(i).get(0)).OperativePoints.get(0).y;
									freeSpace.y = (int)(( MaxFreeSpaceY - StartPoint.y)/2)+StartPoint.y+50;
									NextStartPoint.y = freeSpace.y;
								}
							}	
							else {
							
								NextStartPoint.y = freeSpace.y;
								freeSpace.x += 250;
								freeSpace.y = (int)(( MaxFreeSpaceY - StartPoint.y)/2)+StartPoint.y+50;
							}
							
							if(freeSpace.x < lastElemPos){
								GWT.log("ASDASD "+lastElemPos);
								freeSpace.x = lastElemPos;
							}else {lastElemPos = freeSpace.x;}

						}
						
						if(freeSpace.y > MaxFreeSpaceY) {
							MaxFreeSpaceY = freeSpace.y;
						}
						//GWT.log(Integer.toString(MaxFreeSpaceY));
					}
					
					if(!factorize) 
					{
						
						if(i<list.size()-1) 
						{
							
							if(i>=1 && list.get(i-1).get(list.get(i-1).size()-2) == operation ) {
								
								if(!dictionary.containsKey(blockName))
									freeSpace.y += 150;
								
							}else {
								
								freeSpace.x += 100;
								freeSpace.y = StartPoint.y;
							}
							
						}else if (i==list.size()-1) {
							
							NextStartPoint.y = freeSpace.y;
							freeSpace.x += 250;
							freeSpace.y = (int)(( freeSpace.y - StartPoint.y)/2)+StartPoint.y;
							
							if(freeSpace.x < lastElemPos){
								GWT.log("ASDASD "+lastElemPos);
								freeSpace.x = lastElemPos;
							}else {lastElemPos = freeSpace.x;}

						}
					}
				}
				
				
				//Правила расположения элементов для Nor Nand
				if(basis.equals("Nor") || basis.equals("Nand")) {
					
					if(i<list.size()-1) {
						
						if(i>=1 && list.get(i-1).get(list.get(i-1).size()-2) == operation &&
								
								(float)list.get(i-1).get(list.get(i-1).size()-3).length()/list.get(i).get(list.get(i).size()-3).length() > 0.5f
						) {
							
						if(!dictionary.containsKey(blockName))
							freeSpace.y += 150;
							NextStartPoint.y = freeSpace.y;
						}else {
							freeSpace.x += 250;
							freeSpace.y = (int)(( freeSpace.y - StartPoint.y)/2)+StartPoint.y;
						}
						
					}
					else if (i==list.size()-1) {
						
						//Если идёт инвертирование через и-не, или-не, то этот элемент лежит на одной линии с предыдущей
						if( list.get(i).get(list.get(i).size()-4).equals(list.get(i).get(list.get(i).size()-3)) ) {
							freeSpace.x += 100;
						}
						else {
							freeSpace.y = (int)(( freeSpace.y - StartPoint.y)/2)+StartPoint.y;
							freeSpace.x += 250;
						}
						
						if(freeSpace.x < lastElemPos){
							GWT.log("ASDASD "+lastElemPos);
							freeSpace.x = lastElemPos;
						}else {lastElemPos = freeSpace.x;}

					}
						
				}
				
				
					String ll = "";
					for(int j = 0; j<list.get(i).size(); j++) {
						ll+=list.get(i).get(j) + "  ";
					}
					if(debug)GWT.log(ll);
					dump+=ll+"\n";
				
				
				if(!dictionary.containsKey(blockName)) {
				
					CircuitElm newce = createCe(operation,freeSpace.x,freeSpace.y,freeSpace.x+60,freeSpace.y, 0, inputCount);
					
					newce.setPoints();
					newce.getConnectionPoints(false);
					elmList.add(newce);
					
					dictionary.put(blockName, newce);
					
					//сортируем элементы по y
			        for (int m = list.get(i).size() - 3; m >= 1; m--){  
			            for (int n = 0; n < m; n++){       
			                if(dictionary.get(list.get(i).get(n)).y > dictionary.get(list.get(i).get(n+1)).y) {               
			                	String buff =  list.get(i).get(n);     
			                	list.get(i).set(n, list.get(i).get(n+1));  
			                	list.get(i).set(n+1, buff);   
			                }
			            }
			        }
					
					wireSplitPoint = newce.OperativePoints.get(0).x;
					
					for(int j = 0; j<list.get(i).size()-2; j++) {
						
						if(UnusedVarNames.contains(list.get(i).get(j))) {
							UnusedVarNames.remove(list.get(i).get(j));
						}
						
						//GWT.log("connect " + list.get(i).get(j) + " with "+ blockName);
						//GWT.log(Boolean.toString(dictionary.containsKey(list.get(i).get(j))));
						//GWT.log(Boolean.toString(dictionary.containsKey(blockName)));
						
						//Выбор параметров соежинения: Перелом, ВходнойЭлементСхемы?
						
						if(dictionary.containsKey(list.get(i).get(j))) 
						{
							
							if(splittedWires.contains(list.get(i).get(j))) 
							{
								
								
								if(list.get(i).get(j).length()<=3) {
									ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),true,true);
								}else{
									wireSplitPoint = dictionary.get(list.get(i).get(j)).x;
									wireSplitPoint -= 20;
									ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),true,false);
									}
								
							
							}else 
							{
								
								splittedWires.add(list.get(i).get(j));
								if(list.get(i).get(j).length()<=3) {
									ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),false,true);
								}else{
									wireSplitPoint -= 20;
									ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),false,false);
									}
							
							}
							
						}
						else 
						{
							GWT.log("He he he, error somewhere/ line 142 " + list.get(i).get(j));
							dump+="He he he, error somewhere/ line 142 " + list.get(i).get(j)+"\n";
							ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),false,false);
						}	
					}
					if(debug)GWT.log("End of block");
					dump+="End of block"+"\n";
				}
				//wireSplitPoint -= 40;
				
		}
		
		NextStartPoint.y += 100;
		
		/*Проверка для случая, когда схема состоит из 1 лог элемента 
		*(тогда все манипуляции с NextStartPoint были проведены зря, т.к. выссоты по y не хватит и схемы будут накладываться)
		*Нижнюю точку текущей схемы сравниваем с последним созданным входным элементом схемы из списка inElms
		*т.к. туда входят только неинвертированные то накидываем 50 для соответствия инвертированному и 75 сверху
		*/
		if(NextStartPoint.y < inElems.get(inElems.size()-1).y+50) {
			
			NextStartPoint.y = inElems.get(inElems.size()-1).y+50+75;
		}
		
		input_freeSpace.y = NextStartPoint.y;
		
		CreateCircuitOutput(blockName);
		
		if(debug)GWT.log("End of subCirc");
		if(debug)GWT.log("");
		dump+="End of subCirc"+"\n\n";
	}
	
	/*
	 Создаёт входные элементы схемы, их инвертированный вариант и записывает в hashMap
	 
	 Первый вход схемы не доходит 40 единиц до ряда элементов, каждый последующий на 40 больше, (сюда включаются и инвертированные )
	 */
	void CreateInputElm(String[] varNames) {
		
		int minus = 0;
		AWL = 100;
		int x = input_freeSpace.x;
		int y = input_freeSpace.y;
		//AWL = 2*varNames.length * 50;
		
		freeSpace = new Point(x+AWL+varNames.length*100, y);
		StartPoint = new Point(x+AWL+varNames.length*100, y);
		NextStartPoint = new Point(x+AWL+varNames.length*100, y-45);
		
		//добавить кусок провода длиной в 50
		for(int i = 0; i<varNames.length; i++) {
					
			if(!dictionary.containsKey(varNames[i])) {
				
			ArrayList<CircuitElm> UnusedVar = new ArrayList<CircuitElm>(); //для удаления неиспользуемых входов схемы.
			
			minus += 40;
			
			CircuitElm newce = createCe("Linput",input_freeSpace.x+50,input_freeSpace.y,input_freeSpace.x,input_freeSpace.y, 0, 2);
			newce.setPoints();
			newce.getConnectionPoints(false);
			elmList.add(newce);
			
			CircuitElm newwire = createCe("Wire",newce.OperativePoints.get(0).x,newce.OperativePoints.get(0).y,freeSpace.x+100-minus-(53*dashCirc),newce.OperativePoints.get(0).y, 0, 2);
			newwire.setPoints();
			newwire.getConnectionPoints(true);
			elmList.add(newwire);
			
			dictionary.put(varNames[i], newwire);
			input_freeSpace.y += 100;
			
			UnusedVar.add(newwire);
			UnusedInputs.add(UnusedVar);
			inElems.add((SwitchElm)newce);
			
			minus += 40;
			
			CircuitElm wire = createCe("Wire",newce.OperativePoints.get(0).x, newce.OperativePoints.get(0).y, newce.OperativePoints.get(0).x, newce.OperativePoints.get(0).y+50, 0, 0);
			wire.setPoints();
			wire.getConnectionPoints(true);
			elmList.add(wire);
			
			CircuitElm inverted = createCe("Invertor",newce.OperativePoints.get(0).x, newce.OperativePoints.get(0).y+50, newce.OperativePoints.get(0).x+100, newce.OperativePoints.get(0).y+50, 0, 0);
			inverted.setPoints();
			inverted.getConnectionPoints(true);
			elmList.add(inverted);
			
			CircuitElm wire2 = createCe("Wire",inverted.OperativePoints.get(1).x,inverted.OperativePoints.get(1).y,freeSpace.x+100-minus-(53*dashCirc),inverted.OperativePoints.get(1).y, 0, 2);
			wire2.setPoints();
			wire2.getConnectionPoints(true);
			elmList.add(wire2);
			
			String InverseInput = "~"+varNames[i];
			
			dictionary.put(InverseInput, wire2);
			
			UnusedVar = new ArrayList<CircuitElm>();
			
			UnusedVar.add(wire);
			UnusedVar.add(inverted);
			UnusedVar.add(wire2);
			UnusedInputs.add(UnusedVar);
			
			UnusedVarNames.add(varNames[i]);
			UnusedVarNames.add(InverseInput);

		  }
			
		}
		
	}
		
	//Создаёт выходной элемент схемы на последнем лог. элементе
	void CreateCircuitOutput(String lastBlock) {
		
		Point lastBlockOut = dictionary.get(lastBlock).OperativePoints.get(dictionary.get(lastBlock).OperativePoints.size()-1);
		
		//CircuitElm newce4 = createCe("Loutput",lastBlockOut.x+50, lastBlockOut.y, lastBlockOut.x+100, lastBlockOut.y, 0, 2);
		//newce4.setPoints();
		//newce4.getConnectionPoints(true);
		//elmList.add(newce4);
		
		CircuitElm newce4 = createCe("Platform",lastBlockOut.x+50, lastBlockOut.y, lastBlockOut.x+50, lastBlockOut.y, 0, 2);
		newce4.setPoints();
		newce4.getConnectionPoints(true);
		elmList.add(newce4);
		
		outElems.add(newce4);
		wireSplitPoint = newce4.OperativePoints.get(0).x-20;
		ConnectElements(dictionary.get(lastBlock),newce4,lastBlock,false,false);
		
	}
	
	//Соединение двух элементов
  	void ConnectElements(CircuitElm out, CircuitElm in, String outName, boolean alreadySplitted, boolean isInputElm) {

  		Point prevOutput = out.OperativePoints.get(out.OperativePoints.size()-1); //выход предыдущего элемента
  		int closestInputIndex = GetClosestInput(prevOutput, in); //поиск индекса ближайшего входа к выходу
  		Point currentInput = in.OperativePoints.get(closestInputIndex);

  		if(prevOutput.y != currentInput.y) {
  		
  			//Если в соединении есть узел
  			if(alreadySplitted) {
  				
  				//GWT.log("###########");
  				int diff1 = Math.abs(out.OperativePoints.get(out.OperativePoints.size()-1).y-currentInput.y);
  				int diff2 =		Math.abs(out.OperativePoints.get(0).y-currentInput.y);
  				
  				//Находим разность в высоте между входами элемента и позицией узла
  				//В зависимости от этого выбираем конец провода, от которого идём
  				//в случае их равенства предпочтение отдаётся точке начала провода (Индекс 0) это для else
  				
  			//В первом блоке обыгрывается ситуация, когда мы уже подключили вход к самому верхнему лог элементу первого каскада схемы, т.е. уже есть провод к минимальной точке по y схемы. 
  			// Раньше происходило наложение двух проводов друг на дуга в связи с тем, что не реализовано соединение простым контактом проводов (токо по прямым координатам)
  			// Теперь, текущий вертикальный провод мы дробим на 2, актуальный для дальнейшего соединения становится нижний кусок	
  				if(currentInput.y < out.OperativePoints.get(0).y && currentInput.y > out.OperativePoints.get(1).y) {
  					
  					
  					//GWT.log(outName);
  					
  					CircuitElm shit1 = createCe("Wire",prevOutput.x, out.OperativePoints.get(0).y, prevOutput.x, currentInput.y, 0, 0);
  		  			shit1.setPoints();
		  			shit1.getConnectionPoints(true);
  		  			elmList.add(shit1);
  					
  					CircuitElm shit2 = createCe("Wire",prevOutput.x, currentInput.y, currentInput.x, currentInput.y, 0, 0);
  		  			shit2.setPoints();
  		  			elmList.add(shit2);
  					
  					CircuitElm shit3 = createCe("Wire",prevOutput.x, currentInput.y, prevOutput.x, out.OperativePoints.get(1).y, 0, 0);
  		  			shit3.setPoints();
  		  			elmList.add(shit3);
  		  			
  		  			elmList.remove(out);
  		  			dictionary.replace(outName, shit1);
  					
  				}else {
  					
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
	  			
  				}
  				
  				
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
  					
  				}
  				else { 
  				
  					//diap = ((prevOutput.x+20) + (int) (Math.random() * (currentInput.x-prevOutput.x-40)));
	  		  		//int temp = diap%10;
	  		  		
	  		  		//if(temp!=0) {
	  		  		//	diap = diap + (10-diap%10);
	  		  		//}
  					
  					//if(!sharedVars.isEmpty())
  					//wireSplitPoint -= 20;
  					  					
  					diap = wireSplitPoint;
	  		  		
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
  			}
  			
  		}
  		else { 
  			//Если выход и вход на одной линии
  			//Разделим данное соединение на 2, для случая, когда выходной элемент на одной линии с входным + он же коннектится с третьим
  			//обавить рандомную x координату для сегментации
  			
			if(wireSplitPoint == currentInput.x) {
				wireSplitPoint -= 20;		
			}
  			
		  		CircuitElm newce1 = createCe("Wire",prevOutput.x, prevOutput.y, wireSplitPoint, prevOutput.y, 0, 0);
				newce1.setPoints();
				newce1.getConnectionPoints(true);
				elmList.add(newce1);
				
				
				CircuitElm newce2 = createCe("Wire",wireSplitPoint, prevOutput.y, currentInput.x, currentInput.y, 0, 0);
				newce2.setPoints();
				newce2.getConnectionPoints(true);
				elmList.add(newce2);
			

				dictionary.replace(outName, newce2);
				
				if(in.OperativePoints.size()>1) {
	  				in.OperativePoints.remove(closestInputIndex);
	  			}
				
				//wireSplitPoint = 0;
  			
  		}
		
  		
  	}
	
  	void DeleteUnusedInputs() {
  		
  		if(debug)GWT.log("unused vars "+UnusedVarNames.toString());
  		dump += "unused vars "+UnusedVarNames.toString() + "\n";
  		
  		if(UnusedVarNames.size()>0) {
  		
	  		for(int i = 0; i<UnusedVarNames.size(); i++) {
	  		
	  			String p = UnusedVarNames.get(i);
	  			if(UnusedVarNames.get(i).charAt(0) =='~') {
	  				p = p.substring(2,p.length());
	  			}else {
	  				p = p.substring(1,p.length());
	  			}
	  			
	  			if(debug)GWT.log(p);
	  			dump+=p+"\n";
	  			
		  		int IndexToDelete = ((Integer.parseInt(p))-1)*2;
		  		
		  		if(UnusedVarNames.get(i).charAt(0) =='~') {
		  			IndexToDelete +=1;
		  		}  		
		  		
	  			for(int j = 0; j<UnusedInputs.get(IndexToDelete).size(); j++) {
	  				
	  				elmList.remove(UnusedInputs.get(IndexToDelete).get(j));
	  				
	  			}
		  		
	  		}
  		
  		}
  		
  	}
  	
  	//Индекс входа ближайший к выходу
  	Integer GetClosestInput(Point out, CircuitElm in) {
  		int index = 0;
  		/*
  		
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
  		*/
  		index = (in.OperativePoints.size()-2);
  		if(index<0) {index=0;}
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
    	if(marker.equals("Platform")) {
    		return (CircuitElm) new Platform(x1, y1, x2, y2, f);
    	}
    	else {return null;}
    	
    }
	
  	private int random(int min, int max)
  	{
  		max -= min;
  		return (int) (Math.random() * ++max) + min;
  	}
  
  	private boolean RandomOf100(int percent) {
  		
  		int max = 100;
  		
  		int p = (int) (Math.random() * ++max) + 0;
  		
  		if(p<=percent) {
  			return true;
  		}else {return false;}
  		
  	}
}
