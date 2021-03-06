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

import java.util.ArrayList;
import com.google.gwt.core.client.GWT;

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
	private ArrayList<String> prevFunc = new ArrayList<String>();
	private ArrayList<String> currFunc= new ArrayList<String>();
    
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
    private void Generator (int totalVarCount){

    	currFunc.clear();
        buffVector = new char[totalVarCount][1];

        int unitCounter = 0;

        for(int j = 1; j < totalVarCount; j++)
        {
            int random = randomBit(seed);
            if(random == 1){unitCounter++;}
            buffVector[j][0] = Integer.toString(random).charAt(0);
            currFunc.add(Character.toString(buffVector[j][0]));
        }

        //Если функция тривиальна (1 или 0 на всех значениях) или слишком мало комбинаци на которых функция принимает значение истина, то генерируем заново
        if(     unitCounter == 0 || unitCounter == totalVarCount
                || unitCounter > Math.floor(maxTruePercent*totalVarCount)
                || unitCounter < Math.floor(minTruePercent*totalVarCount)
                || currFunc.equals(prevFunc)
        ){
            Generator(totalVarCount);
        }else{
        
        	//текущий вектор не должен совпадать с вектором предыдущей функции
       
        	//Глубокое копирование Текущая функция становится предыдущей
        	prevFunc.clear();
            for(String obj : currFunc)
            {
                String b = obj;
                prevFunc.add(b);
            }
        	
        }
       

    }

    private static int randomBit(long seed){
        return (int) (Math.random() * ++seed);
    }

}
