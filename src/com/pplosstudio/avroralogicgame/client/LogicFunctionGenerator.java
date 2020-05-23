package com.pplosstudio.avroralogicgame.client;

import java.util.ArrayList;

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
    private float maxTruePercent = 0.5f;
    private float minTruePercent = 0.4f;
    private int lastVarIndex = 1;
    char[][] buffVector;
    private boolean debug = false;

    public LogicFunctionGenerator() {}
    
    
    public LogicFunctionGenerator(boolean dbg){
    	debug = dbg;
    }

    //генератор логической функции в виде таблицы истинности
    public void GenerateVectorFunction(int varCount, int funcCount, ArrayList<String> sharedVars) {
    	

        int totalVarCount = (int) Math.pow(2,varCount);

        //VectorFunctions = new char[totalVarCount][varCount];
        VectorFunctions = new char[totalVarCount][funcCount];
        //массивы имён входных переменных и выходных функций. нужны для Квайн-Мак-Класки
        VarNames = new String[varCount];
        OutNames = new String[funcCount];
        
        if(debug) {GWT.log("Shared var length "+Integer.toString(sharedVars.size()));
        GWT.log(sharedVars.get(0));
        GWT.log(sharedVars.get(1));
        }
    	if(sharedVars.size()>0) {
    		for(int i = 0; i < sharedVars.size(); i++) {
    			VarNames[i] = sharedVars.get(i);
    			 if(debug)GWT.log(sharedVars.get(i));
    		}
    	}

        for(int i = 0; i < varCount; i++){
        	
        	if(i>sharedVars.size()-1 || sharedVars.size()==0) {
	            String varName = "x"+(lastVarIndex);
	            VarNames[i] = varName;
	            lastVarIndex++;
        	}
            if(debug)GWT.log(VarNames[i]);
        }

        for(int i = 0; i < funcCount; i++)
        {
            String outName = "z"+i;
            OutNames[i] = outName;
            
            //на нулевой комбинации значение функции всегда 0, иначе тренировка теряет смысол
            VectorFunctions[0][i] = '0';
            Generator(totalVarCount);

            if(debug) {
	            GWT.log("Function №"+Integer.toString(i));
	            GWT.log("0");
            }
            
            //заполняем выходной масссив
            for(int j = 1; j < totalVarCount; j++)
            {
                VectorFunctions[j][i] = buffVector[j][0];
                if(debug)GWT.log(Character.toString(VectorFunctions[j][i]));
            }
            if(debug)GWT.log("end of function");
        }
        

    }

    //Реализация Генератора
    public void Generator (int totalVarCount){

        buffVector = new char[totalVarCount][1];

        int unitCounter = 0;

        for(int j = 1; j < totalVarCount; j++)
        {
           // int random = randomBit(Random.nextInt());
            int random = randomBit(seed);
            if(random == 1){unitCounter++;}
            buffVector[j][0] = Integer.toString(random).charAt(0);
        }

        //Если функция тривиальна (1 или 0 на всех значениях) или слишком мало комбинация на которых функция принимает значение истина, то генерируем заново
        if(     unitCounter == 0 || unitCounter == totalVarCount
                || unitCounter > Math.floor(maxTruePercent*totalVarCount)
                || unitCounter < Math.floor(minTruePercent*totalVarCount)
        ){
            Generator(totalVarCount);
        }

    }

    public static int randomBit(long seed){
        return (int) (Math.random() * ++seed);
    }

}
