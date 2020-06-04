package com.pplosstudio.avroralogicgame.client;

import java.util.ArrayList;
import java.util.Collections;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;

//import com.google.gwt.core.client.GWT;

/*
Создание столбца для таблицы истинности
в силу того, что Квайн-Мак-Класки был частично взят из интернета, выходной массив должен быть в формате 
			{
				[],
				[],
				[],
				[],
			}
			
			где первый массив, это первая строка таблицы истинности и тд.
			Первая строка нулевая для любой функции дабы избежать досрочного решения уровня
			В данном столбце/массиве должно выдерживаться процентное содержание 1
			Так же здесь формируются названия входных элементов и выходный. Они так же требуются для Квайна
			
*/

public class LogicFunctionGenerator {

    public char[][] VectorFunctions;
    public String[] VarNames,OutNames;
    private long seed = 1;
    private float maxTruePercent;
    private float minTruePercent;
    private int lastVarIndex = 1;
    char[][] buffVector;
    private boolean debug = false;
    public String dmp = "";
	public boolean callOnce = false;
	private ArrayList<String> prevFunc = new ArrayList();
	private ArrayList<String> currFunc= new ArrayList();

    public LogicFunctionGenerator() {}
    
    
    public LogicFunctionGenerator(boolean dbg){
    	maxTruePercent = 0.3f;
    	minTruePercent = 0.2f;
    	debug = dbg;
    }

    //генератор логической функции в виде таблицы истинности
    public void GenerateVectorFunction(int varCount, int funcCount, ArrayList<String> sharedVars, float min, float max) {
    	
    	dmp = "";
    	
    	maxTruePercent = max;
    	minTruePercent = min;

        int totalVarCount = (int) Math.pow(2,varCount);

        //VectorFunctions = new char[totalVarCount][varCount];
        VectorFunctions = new char[totalVarCount][funcCount];
        //массивы имён входных переменных и выходных функций. нужны для Квайн-Мак-Класки
        VarNames = new String[varCount];
        OutNames = new String[funcCount];
        
        
    	if(sharedVars.size()>0) {
            if(debug) {
                GWT.log("Shared var length "+Integer.toString(sharedVars.size()));
                GWT.log(sharedVars.get(0));
                GWT.log(sharedVars.get(1));
                }
            dmp+= "Shared var length "+Integer.toString(sharedVars.size()) + "\n";
    		for(int i = 0; i < sharedVars.size(); i++) {
    			VarNames[i] = sharedVars.get(i);
    			 if(debug)GWT.log(sharedVars.get(i));
    			 dmp+=sharedVars.get(i)+"\n";
    		}
    		dmp+="End of shared vars"+"\n";
    	}

    	//Создаём имена переменных
        for(int i = 0; i < varCount; i++){
        	
        	if(i>sharedVars.size()-1 || sharedVars.size()==0) {
	            String varName = "x"+(lastVarIndex);
	            VarNames[i] = varName;
	            lastVarIndex++;
        	}
            if(debug)GWT.log(VarNames[i]);
            dmp += VarNames[i]+"\n";
        }

        
        for(int i = 0; i < funcCount; i++)
        {
            String outName = "z"+i;
            OutNames[i] = outName;
            
            //на нулевой комбинации значение функции всегда 0, иначе тренировка теряет смысол (кроме первой функции в схеме)
            if(callOnce) {
            	VectorFunctions[0][i] = '1';
            	callOnce = false;
            	 if(debug) {
     	            GWT.log("Function №"+Integer.toString(i));
     	            GWT.log("1");
                 }
            	 dmp += "Function №"+Integer.toString(i) + "\n"+ "1"+"\n";
            	 
            	 for(int k = 0; k< totalVarCount-1; k++) {
            		 prevFunc.add("0");
            	 }
            	 
            }else {
            	VectorFunctions[0][i] = '0';
	            if(debug) {
		            GWT.log("Function №"+Integer.toString(i));
		            GWT.log("0");
	            }
            dmp += "Function №"+Integer.toString(i) + "\n"+ "0"+"\n";
            }
            Generator(totalVarCount);
 
            //заполняем выходной масссив
            for(int j = 1; j < totalVarCount; j++)
            {
                VectorFunctions[j][i] = buffVector[j][0];
                if(debug)GWT.log(Character.toString(VectorFunctions[j][i]));
                dmp+= Character.toString(VectorFunctions[j][i]) + "\n";
            }
            if(debug)GWT.log("end of function");
            dmp += "end of function" + "\n";
        }
        

    }

    //Реализация Генератора
    public void Generator (int totalVarCount){

    	currFunc.clear();
        buffVector = new char[totalVarCount][1];

        int unitCounter = 0;

        for(int j = 1; j < totalVarCount; j++)
        {
           // int random = randomBit(Random.nextInt());
            int random = randomBit(seed);
            if(random == 1){unitCounter++;}
            buffVector[j][0] = Integer.toString(random).charAt(0);
            currFunc.add(Character.toString(buffVector[j][0]));
        }

        //Если функция тривиальна (1 или 0 на всех значениях) или слишком мало комбинаци на которых функция принимает значение истина, то генерируем заново
        if(     unitCounter == 0 || unitCounter == totalVarCount
                || unitCounter > Math.floor(maxTruePercent*totalVarCount)
                || unitCounter < Math.floor(minTruePercent*totalVarCount)
        ){
            Generator(totalVarCount);
            GWT.log("ReGen");
        }
        
        //текущий вектор не должен совпадать с вектором предыдущей функции
        if(currFunc==prevFunc) {
        	GWT.log(currFunc.toString());
        	GWT.log(prevFunc.toString());
        	GWT.log("ahtung");
        	Generator(totalVarCount);
        }else {
        	//Глубокое копирование Текущая функция становится предыдущей
        	prevFunc.clear();
            for(String obj : currFunc)
            {
                String b = obj;
                prevFunc.add(b);
            }
        	
        }
       

    }

    public static int randomBit(long seed){
        return (int) (Math.random() * ++seed);
    }

}
