package com.pplosstudio.avroralogicgame.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

//x0, x1, ~x0 Рё С‚Рґ - Р’С…РѕРґРЅС‹Рµ СЌР»РµРјРµРЅС‚С‹ СЃС…РµРјС‹

//РћРїРёСЃР°РЅРёРµ СЃС…РµРјС‹ РІС‹РіР»СЏРґРёС‚ СЃР»РµРґСѓСЋС‰РёРј РѕР±СЂР°Р·РѕРј
// [ [,РІС…РѕРґРЅРѕР№ СЌР»РµРјРµРЅС‚ 1 РїСЂРёРј. x0,РІС…Р»РґРЅРѕР№ СЌР»РµРјРµРЅС‚ 2,...,СЃРёРјРІРѕР» РѕРїРµСЂР°С†РёРё, РёРјСЏ РЅРѕРІРѕРіРѕ СЌР»РµРјРµРЅС‚Р°], [Р°РЅР°Р»РѕРіРёС‡РЅРѕ] ,...]

//РџСЂРѕ СЃРёСЃС‚РµРјСѓ РёРЅРґРµРєСЃРѕРІ РІС…РѕРґРѕРІ, РІС‹С…РѕРґРѕРІ СЌР»РµРјРµРЅС‚Р°. 
/*
 РЎРЅР°С‡Р°Р»Р° РЅР°РґРѕ РІС‹СЃС‡РёС‚Р°С‚СЊ СЌС‚Рё С‚РѕС‡РєРё - РјР°СЃСЃРёРІ getOperativePoint Р° Р»СЋР±РѕРј РѕР±СЉРµРєС‚Рµ РєР»Р°СЃСЃР° circuitElm
 РџРѕСЃР»РµРґРЅСЏСЏ С‚РѕС‡РєР° РІ СЌС‚РѕРј РјР°СЃСЃРёРІРµ - РІС‹С…РѕРґ СЌР»РµРјРµРЅС‚Р°, РІСЃС‘ РѕСЃС‚Р°Р»СЊРЅРѕРµ РІС…РѕРґС‹.
 Р’ СЃР»СѓС‡Р°Рµ РїСЂРѕРІРѕРґР° - 0 РёРЅРґРµРєСЃ - С‚РѕС‡РєР° РЅР°С‡Р°Р»Р° РїСЂРѕРІРѕРґР° (Р»РµРІС‹Р№ РєРѕРЅРµС†, РµСЃР»Рё РїСЂРѕРІРѕРґ РёРґС‘С‚ СЃР»РµРІР° РЅР° РїСЂР°РІРѕ), СЃРѕРѕС‚РІРµС‚СЃС‚РІРµРЅРЅРѕ РёРЅРґРµРєСЃ 1 - С‚РѕС‡РєР° РѕРєРѕРЅС‡Р°РЅРёСЏ
 */

/*
 * drawPost РѕС‚РІРµС‡Р°РµС‚ Р·Р° РѕС‚СЂРёСЃРѕРІРєСѓ С‚РѕС‡РµРє
 */


/*
 * To Do
 * СЂР°Р·Р±РёРµРЅРёРµ Р»РёРЅРёРё СЃРѕРµРґРёРЅРµРЅРёСЏ РЅР° СЃРµРіРјРµРЅС‚С‹, РµСЃР»Рё out.y = in.y
 * СЂР°СЃРїРѕР»РѕР¶РµРЅРёРµ РґР»СЏ Р¶РµРіР°Р»РєРёРЅР° Рё С„Р°РєС‚РѕСЂРёР·Р°С†РёРё
 * 
 */

public class CircuitSynthesizer {
	
	private boolean debug = true;
	private boolean ShuntingYardDebug = true;
	private boolean LogicVectorGenerator = false;
	private boolean BasisConverterDebug = false;
	private boolean dump = true;
	public static String dmp = ""; 
	
	private boolean MDNF, factorize;
	private String basis = "";
	private int funcCount = 0;
	private int varCount = 0;
	private ArrayList<String> sharedVars = new ArrayList();

	private Point input_freeSpace = new Point(50,80); //РўРѕС‡РєР° РЅР°С‡Р°Р»Р° РѕС‚СЂРёСЃРѕРІРєРё РІС…РѕРґРѕРІ СЃС…РµРјС‹
	
	private Point StartPoint = new Point(1,1); // С„РёРєСЃР°С†РёСЏ РЅР°С‡Р°Р»СЊРЅРѕР№ С‚РѕС‡РєРё С‚РµРєСѓС‰РµР№ С„СѓРЅРєС†РёРё
	private Point NextStartPoint = new Point(2,2); //С„РёРєСЃР°С†РёСЏ РЅР°С‡Р°Р»СЊРЅРѕР№ С‚РѕС‡РєРё СЃР»РµРґСѓСЋС‰РµР№ С„СѓРЅРєС†РёРё
	private Point freeSpace = new Point(3,3); //С‚РµРєСѓС‰Р°СЏ СЃРІРѕР±РѕРґРЅРµР°СЏ С‚РѕС‡РєР°
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	Map<String,CircuitElm> dictionary = new HashMap<String,CircuitElm>();
	
	public Vector<CircuitElm> elmList = new Vector<CircuitElm>();
	
	ArrayList<String> splittedWires = new ArrayList<String>(); 
	
	ArrayList<ArrayList<CircuitElm>> UnusedInputs = new ArrayList<>();
	ArrayList<String> UnusedVarNames = new ArrayList<>();
	ArrayList<ArrayList<String>> allInputs = new ArrayList(); 

	//AWL = Additional Wire Length
	private int AWL;
	
	int width, height;
	
	BasisConverter converter = new BasisConverter(BasisConverterDebug);
    Factorisator_V_2 factorizator = new Factorisator_V_2();
    ShuntingYard shuntingYard = new ShuntingYard(ShuntingYardDebug);
	LogicFunctionGenerator generator = new LogicFunctionGenerator(LogicVectorGenerator);  
	
	public void Synthesis(int w, int h) {
		
		
		width = w;
		height = h;
	
		//GetConfigurationFile();
		sharedVars = new ArrayList();	
	
		 MDNF = true;
		 factorize = true;
		// basis = "Default";
		// basis = "Nor";
		 //basis = "Nand";
		 basis = "Zhegalkin";
		 funcCount = 2;
		 varCount = 3;
		 
		 if(debug) {
			 GWT.log("MDNF " + MDNF);
			 GWT.log("Basis " + basis);
			 GWT.log("Function count " + funcCount);
			 GWT.log("Var count " + varCount);
		 }
		 
		
		InitializeParametrs();
				
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
		 //basis = "Nand";
		// basis = "Zhegalkin";
		 funcCount = 1;
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
		
		sharedVars = new ArrayList();	
		
		int circCount = (int)Math.floor(((0.4f*circDifficult)*(0.4f*circDifficult)+2.5f));
		
		width = w;
		height = h;
			
		//int circCount = random(1,circDifficult);
		//int circCount = random(1,maxCircuits);
		GWT.log("Circ count "+ Integer.toString(circCount));
		
		for(int i = 0; i < circCount; i++) {
			
			int Basis =  random(0,3);
			
			int mdnf =  (int) (Math.random() * 1);
			if(mdnf == 0) {
				MDNF = false;
			}else {MDNF = true;}
			
			if(Basis == 0) {
				basis = "Default";
				if(MDNF == true) {
					int factr =  random(0,1);
					if(factr == 1) {
						factorize = true;
						varCount =  random(2,5);
						
					}else {
						factorize = false;
						varCount =  random(2,5);
					}
				}
				else {varCount =  random(2,5);}
			}else if(Basis == 1) {
				basis = "Nor";
				varCount =  random(2,4);
			}else if(Basis == 2) {
				basis = "Nand";
				varCount =  random(2,4);
			}else if(Basis == 3) {
				basis = "Zhegalkin";
				varCount =  random(2,3);
			}
			
			
			//funcCount =  random((int)circDifficult/2,circDifficult+1);
			funcCount = (int)(0.5f*Math.log(circDifficult-0.5f)+2);
			
			 if(debug) {
				 GWT.log("MDNF " + MDNF);
				 GWT.log("Basis " + basis);
				 GWT.log("Function count " + funcCount);
				 GWT.log("Var count " + varCount);
			 }
			 
					//Add shared input
					if(i>0 && i < circCount - 1 && circCount>1) {
						GWT.log("Shared Vars++");
						int n = random(0,1);
						
						if(n==1) {
							
							int sharedVarsCount = random(1,varCount-1);
							
							for(int j = 0; j<sharedVarsCount; j++) {
								add();
							}
						
						}
						
					}
			 
			
			InitializeParametrs();
			
		}
		DeleteUnusedInputs();
			 
	}
	
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
	
	//Р�РЅРёС†РёР°Р»РёР·Р°С†РёСЏ РІСЃРµС… РѕР±СЉРµРєС‚РѕРІ Рё РїСЂРёРјРµРЅРµРЅРёРµ С„Р°Р№Р»Р° РєРѕРЅС„РёРіСѓСЂР°С†РёРё
	void InitializeParametrs() {
				  
		//LogicFunctionGenerator generator = new LogicFunctionGenerator(varCount,funcCount,sharedVars);   
        
        generator.GenerateVectorFunction(varCount, funcCount, sharedVars);
		
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
        allInputs.add(lst);
		CreateInputElm(generator.VarNames);
        
		String functions[] = sol.getSolution().split("\n");
		if(debug) {
	      for(int i = 0; i<functions.length; i++) {
	    	  GWT.log(functions[i]+"\n");
	      }
		}
		
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
			        	 if(debug)GWT.log(factorizator.output);
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
	        	list = converter.list;
	        }
	        else if(basis.equals("Nand")) {
	        	converter.ToNand(functions[i], MDNF);
	        	list = converter.list;
	        }
	        else if(basis.equals("Zhegalkin")) {
	        	converter.ToZhegalkinPolynomial(generator.VectorFunctions,0,generator.VarNames);
	        	list = converter.list;
	        }
	        
	        CreateCircuit(list);
        }
        	//DeleteUnusedInputs();
	}
		
	//РџСЂРѕС…РѕРґ РїРѕ РІСЃРµРјСѓ СЃРїРёСЃРєСѓ, СЃРѕСЃС‚Р°РІР»РµРЅРёРµ hashmap СЃ СЃРѕР·РґР°РЅРЅС‹РјРё СЌРґРµРјРµРЅС‚Р°РјРё, СѓСЃС‚Р°РЅРѕРІРєР° РїР°СЂР°РјРµС‚СЂРѕРІ СЃРѕРµРґРёРЅРµРЅРёСЏ РѕР±СЉРµРєС‚РѕРІ
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
		GWT.log(ll2);
		
		int Yaccum = 1;
		int MaxFreeSpaceY = 0;
		
		for(int i = 0; i<list.size(); i++) {
		
				int inputCount = list.get(i).size()-2;
				String operation= list.get(i).get(list.get(i).size()-2);
				blockName = list.get(i).get(list.get(i).size()-1);
				//GWT.log("new Block Name " + blockName);
				
				
				
				/*
				 * РћР±С‰РёР№ СЃРјС‹СЃР» Р°Р»РіРѕСЂРёС‚РјР° СЂР°СЃРїРѕР»РѕР¶РµРЅРёСЏ
				 * РґР»СЏ РѕР±С‰РёС… СЃР»СѓС‡Р°РµРІ Nor Nand Def СЌР»РµРјРµРЅС‚С‹ СЂР°СЃРїРѕР»Р°РіР°РµСЋС‚СЃСЏ РІ СЂСЏРґС‹ РїРѕ РѕРїРµСЂР°С†РёСЏРј.
				 * Р’ Nor Nand РµСЃС‚СЊ РЅСЋР°РЅСЃ СЃ СЂР°СЃРїРѕР»РѕР¶РµРЅРёРµРј РІС‹С…РѕРґРЅРѕРіРѕ СЌР»РµРјРµРЅС‚Р° РІ СЃР»СѓС‡Р°Рµ РёРЅРІРµСЂС‚РёСЂРѕРІР°РЅРёСЏ РѕС‚РІРµС‚Р° С‡РµСЂРµР· СЌР»РµРјРµРЅС‚ Р±Р°Р·РёСЃР° СЃРј РґР°Р»РµРµ
				 */
				
				//РџСЂР°РІРёР»Р° СЂР°СЃРїРѕР»РѕР¶РµРЅРёСЏ СЌР»РµРјРµРЅС‚РѕРІ РґР»СЏ РѕР±С‰РµРіРѕ Р±Р°Р·РёСЃР° Рё Р–РµРіР°Р»РєРёРЅР°
				if(basis.equals("Default") || basis.equals("Zhegalkin")) 
				{
					
					/*
					 * РџСЂРё С„Р°РєС‚РѕСЂРёР·Р°С†РёРё РІС‹СЂР°Р¶РµРЅРёСЏ:
					 * Р•СЃР»Рё РµСЃС‚СЊ РґРІР° Р±Р»РѕРєР° РІРёРґР° [x1 x2 * x1*x2] Рё [x3 x1*x2 +* x3+*x1*x2] С‚РѕСЂРѕР№ Р±Р»РѕРє Р±СѓРґРµС‚ СЃРґРІРёРЅСѓС‚ РІРїСЂР°РІРѕ-РІРЅРёР·
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
									freeSpace.y = MaxFreeSpaceY+75;
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
							
						}
						
						if(freeSpace.y > MaxFreeSpaceY) {
							MaxFreeSpaceY = freeSpace.y;
						}
						GWT.log(Integer.toString(MaxFreeSpaceY));
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
						}
					}
				}
				
				
				//РџСЂР°РІРёР»Р° СЂР°СЃРїРѕР»РѕР¶РµРЅРёСЏ СЌР»РµРјРµРЅС‚РѕРІ РґР»СЏ Nor Nand
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
						
						//Р•СЃР»Рё РёРґС‘С‚ РёРЅРІРµСЂС‚РёСЂРѕРІР°РЅРёРµ С‡РµСЂРµР· Рё-РЅРµ, РёР»Рё-РЅРµ, С‚Рѕ СЌС‚РѕС‚ СЌР»РµРјРµРЅС‚ Р»РµР¶РёС‚ РЅР° РѕРґРЅРѕР№ Р»РёРЅРёРё СЃ РїСЂРµРґС‹РґСѓС‰РµР№
						if( list.get(i).get(list.get(i).size()-4).equals(list.get(i).get(list.get(i).size()-3)) ) {
							freeSpace.x += 250;
						}
						else {
							freeSpace.y = (int)(( freeSpace.y - StartPoint.y)/2)+StartPoint.y;
							freeSpace.x += 250;
						}

					}
						
				}
				
				if(debug) {
				String ll = "";
					for(int j = 0; j<list.get(i).size(); j++) {
						ll+=list.get(i).get(j) + "  ";
					}
					GWT.log(ll);
				}
				
				if(!dictionary.containsKey(blockName)) {
				
					CircuitElm newce = createCe(operation,freeSpace.x,freeSpace.y,freeSpace.x+60,freeSpace.y, 0, inputCount);
					
					newce.setPoints();
					newce.getConnectionPoints(false);
					elmList.add(newce);
					
					dictionary.put(blockName, newce);
					
					for(int j = 0; j<list.get(i).size()-2; j++) {
						
						if(UnusedVarNames.contains(list.get(i).get(j))) {
							UnusedVarNames.remove(list.get(i).get(j));
						}else {}
						
						//GWT.log("connect " + list.get(i).get(j) + " with "+ blockName);
						//GWT.log(Boolean.toString(dictionary.containsKey(list.get(i).get(j))));
						//GWT.log(Boolean.toString(dictionary.containsKey(blockName)));
						
						//Р’С‹Р±РѕСЂ РїР°СЂР°РјРµС‚СЂРѕРІ СЃРѕРµР¶РёРЅРµРЅРёСЏ: РџРµСЂРµР»РѕРј, Р’С…РѕРґРЅРѕР№Р­Р»РµРјРµРЅС‚РЎС…РµРјС‹?
						
						if(dictionary.containsKey(list.get(i).get(j))) 
						{
							
							if(splittedWires.contains(list.get(i).get(j))) 
							{
								
								if(list.get(i).get(j).length()<=3) {
									ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),true,true);
								}else{
									ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),true,false);
									}
							
							}else 
							{
								
								splittedWires.add(list.get(i).get(j));
								if(list.get(i).get(j).length()<=3) {
									ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),false,true);
								}else{
									ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),false,false);
									}
							
							}
							
						}
						else 
						{
							GWT.log("He he he, error somewhere/ line 142 " + list.get(i).get(j));
							ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),false,false);
						}	
					}
					
					
				}
				
		}
		
		
		NextStartPoint.y += 100;
		input_freeSpace.y = NextStartPoint.y;
		
		CreateCircuitOutput(blockName);
	}
	
	/*
	 РЎРѕР·РґР°С‘С‚ РІС…РѕРґРЅС‹Рµ СЌР»РµРјРµРЅС‚С‹ СЃС…РµРјС‹, РёС… РёРЅРІРµСЂС‚РёСЂРѕРІР°РЅРЅС‹Р№ РІР°СЂРёР°РЅС‚ Рё Р·Р°РїРёСЃС‹РІР°РµС‚ РІ hashMap
	 
	 РџРµСЂРІС‹Р№ РІС…РѕРґ СЃС…РµРјС‹ РЅРµ РґРѕС…РѕРґРёС‚ 40 РµРґРёРЅРёС† РґРѕ СЂСЏРґР° СЌР»РµРјРµРЅС‚РѕРІ, РєР°Р¶РґС‹Р№ РїРѕСЃР»РµРґСѓСЋС‰РёР№ РЅР° 40 Р±РѕР»СЊС€Рµ, (СЃСЋРґР° РІРєР»СЋС‡Р°СЋС‚СЃСЏ Рё РёРЅРІРµСЂС‚РёСЂРѕРІР°РЅРЅС‹Рµ )
	 */
	void CreateInputElm(String[] varNames) {
		
		
		AWL = 100;
		int minus = 0;
		int x = input_freeSpace.x;
		int y = input_freeSpace.y;
		//AWL = 2*varNames.length * 50;
		
		freeSpace = new Point(x+AWL+varNames.length*100, y);
		StartPoint = new Point(x+AWL+varNames.length*100, y);
		NextStartPoint = new Point(x+AWL+varNames.length*100, y);
		
		//РґРѕР±Р°РІРёС‚СЊ РєСѓСЃРѕРє РїСЂРѕРІРѕРґР° РґР»РёРЅРѕР№ РІ 50
		for(int i = 0; i<varNames.length; i++) {
					
			if(!dictionary.containsKey(varNames[i])) {
				
			ArrayList<CircuitElm> UnusedVar = new ArrayList<CircuitElm>(); //РґР»СЏ СѓРґР°Р»РµРЅРёСЏ РЅРµРёСЃРїРѕР»СЊР·СѓРµРјС‹С… РІС…РѕРґРѕРІ СЃС…РµРјС‹.
			
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
			
			UnusedVar.add(newwire);
			UnusedInputs.add(UnusedVar);
			
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
		
	//РЎРѕР·РґР°С‘С‚ РІС‹С…РѕРґРЅРѕР№ СЌР»РµРјРµРЅС‚ СЃС…РµРјС‹ РЅР° РїРѕСЃР»РµРґРЅРµРј Р»РѕРі. СЌР»РµРјРµРЅС‚Рµ
	void CreateCircuitOutput(String lastBlock) {
		
		Point lastBlockOut = dictionary.get(lastBlock).OperativePoints.get(dictionary.get(lastBlock).OperativePoints.size()-1);
		
		CircuitElm newce4 = createCe("Loutput",lastBlockOut.x, lastBlockOut.y, lastBlockOut.x+50, lastBlockOut.y, 0, 2);
		newce4.setPoints();
		newce4.getConnectionPoints(true);
		elmList.add(newce4);
		
		ConnectElements(dictionary.get(lastBlock),newce4,lastBlock,false,false);
		
	}
	
	//РЎРѕРµРґРёРЅРµРЅРёРµ РґРІСѓС… СЌР»РµРјРµРЅС‚РѕРІ
  	void ConnectElements(CircuitElm out, CircuitElm in, String outName, boolean alreadySplitted, boolean isInputElm) {

  		Point prevOutput = out.OperativePoints.get(out.OperativePoints.size()-1); //РІС‹С…РѕРґ РїСЂРµРґС‹РґСѓС‰РµРіРѕ СЌР»РµРјРµРЅС‚Р°
  		int closestInputIndex = GetClosestInput(prevOutput, in); //РїРѕРёСЃРє РёРЅРґРµРєСЃР° Р±Р»РёР¶Р°Р№С€РµРіРѕ РІС…РѕРґР° Рє РІС‹С…РѕРґСѓ
  		Point currentInput = in.OperativePoints.get(closestInputIndex);

  		if(prevOutput.y != currentInput.y) {
  		
  			//Р•СЃР»Рё РІ СЃРѕРµРґРёРЅРµРЅРёРё РµСЃС‚СЊ СѓР·РµР»
  			if(alreadySplitted) {
  				
  				//GWT.log("###########");
  				int diff1 = Math.abs(out.OperativePoints.get(out.OperativePoints.size()-1).y-currentInput.y);
  				int diff2 =		Math.abs(out.OperativePoints.get(0).y-currentInput.y);
  				
  				//РќР°С…РѕРґРёРј СЂР°Р·РЅРѕСЃС‚СЊ РІ РІС‹СЃРѕС‚Рµ РјРµР¶РґСѓ РІС…РѕРґР°РјРё СЌР»РµРјРµРЅС‚Р° Рё РїРѕР·РёС†РёРµР№ СѓР·Р»Р°
  				//Р’ Р·Р°РІРёСЃРёРјРѕСЃС‚Рё РѕС‚ СЌС‚РѕРіРѕ РІС‹Р±РёСЂР°РµРј РєРѕРЅРµС† РїСЂРѕРІРѕРґР°, РѕС‚ РєРѕС‚РѕСЂРѕРіРѕ РёРґС‘Рј
  				//РІ СЃР»СѓС‡Р°Рµ РёС… СЂР°РІРµРЅСЃС‚РІР° РїСЂРµРґРїРѕС‡С‚РµРЅРёРµ РѕС‚РґР°С‘С‚СЃСЏ С‚РѕС‡РєРµ РЅР°С‡Р°Р»Р° РїСЂРѕРІРѕРґР° (Р�РЅРґРµРєСЃ 0)
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
  				
  				//Р•СЃР»Рё РЅРµС‚ СѓР·Р»Р°
  				
  				int diap = 0;
  				
  				//Р•СЃР»Рё СЌС‚Рѕ РІС…РѕРґРЅРѕР№ СЌР»РµРјРµРЅС‚ СЃС…РµРјС‹
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
  		else { //Р•СЃР»Рё РІС‹С…РѕРґ Рё РІС…РѕРґ РЅР° РѕРґРЅРѕР№ Р»РёРЅРёРё
  			
  			//Р Р°Р·РґРµР»РёРј РґР°РЅРЅРѕРµ СЃРѕРµРґРёРЅРµРЅРёРµ РЅР° 2, РґР»СЏ СЃР»СѓС‡Р°СЏ, РєРѕРіРґР° РІС‹С…РѕРґРЅРѕР№ СЌР»РµРјРµРЅС‚ РЅР° РѕРґРЅРѕР№ Р»РёРЅРёРё СЃ РІС…РѕРґРЅС‹Рј + РѕРЅ Р¶Рµ РєРѕРЅРЅРµРєС‚РёС‚СЃСЏ СЃ С‚СЂРµС‚СЊРёРј
  			//РѕР±Р°РІРёС‚СЊ СЂР°РЅРґРѕРјРЅСѓСЋ x РєРѕРѕСЂРґРёРЅР°С‚Сѓ РґР»СЏ СЃРµРіРјРµРЅС‚Р°С†РёРё
  			
	  		CircuitElm newce1 = createCe("Wire",prevOutput.x, prevOutput.y, prevOutput.x+20, prevOutput.y, 0, 0);
			newce1.setPoints();
			newce1.getConnectionPoints(true);
			elmList.add(newce1);
			
			CircuitElm newce2 = createCe("Wire",prevOutput.x+20, prevOutput.y, currentInput.x, currentInput.y, 0, 0);
			newce2.setPoints();
			newce2.getConnectionPoints(true);
			elmList.add(newce2);
			
			dictionary.replace(outName, newce2);
			
			if(in.OperativePoints.size()>1) {
  				in.OperativePoints.remove(closestInputIndex);
  			}
  		}
		
  		
  	}
	
  	void DeleteUnusedInputs() {
  		
  		GWT.log(UnusedVarNames.toString());
  		
  		if(UnusedVarNames.size()>0) {
  		
	  		for(int i = 0; i<UnusedVarNames.size(); i++) {
	  		
	  			GWT.log(Character.toString(UnusedVarNames.get(i).charAt(UnusedVarNames.get(i).length()-1)));
		  		int IndexToDelete = (Integer.parseInt(Character.toString(UnusedVarNames.get(i).charAt(UnusedVarNames.get(i).length()-1)))-1)*2;
		  		
		  		if(UnusedVarNames.get(i).charAt(0) =='~') {
		  			IndexToDelete +=1;
		  		}  		
		  		
	  			for(int j = 0; j<UnusedInputs.get(IndexToDelete).size(); j++) {
	  				
	  				elmList.remove(UnusedInputs.get(IndexToDelete).get(j));
	  				
	  			}
		  		
	  		}
  		
  		}
  		
  	}
  	
  	//Р�РЅРґРµРєСЃ РІС…РѕРґР° Р±Р»РёР¶Р°Р№С€РёР№ Рє РІС‹С…РѕРґСѓ
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
	
  	int random(int min, int max)
  	{
  		max -= min;
  		return (int) (Math.random() * ++max) + min;
  	}
}
