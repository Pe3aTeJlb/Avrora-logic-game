package com.pplosstudio.avroralogicgame.client;

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
    char[][] buffVector;

    public LogicFunctionGenerator(int varCount, int funcCount){
        GenerateVectorFunction( varCount, funcCount);
    }

    //генератор логической функции в виде таблицы истинности
    private void GenerateVectorFunction(int varCount, int funcCount) {

        int totalVarCount = (int) Math.pow(2,varCount);

        //VectorFunctions = new char[totalVarCount][varCount];
        VectorFunctions = new char[totalVarCount][funcCount];
        //массивы имён входных переменных и выходных функций. нужны для Квайн-Мак-Класки
        VarNames = new String[varCount];
        OutNames = new String[funcCount];

        for(int i = 0; i < varCount; i++){
            String varName = "x"+(i);
            VarNames[i] = varName;
           // GWT.log(varName);
        }

        for(int i = 0; i < funcCount; i++)
        {
            String outName = "z"+i;
            OutNames[i] = outName;

            //на нулевой комбинации значение функции всегда 0, иначе тренировка теряет смысол
            VectorFunctions[0][i] = '0';
            Generator(totalVarCount);

            //заполняем выходной масссив
            for(int j = 1; j < totalVarCount; j++)
            {
                VectorFunctions[j][i] = buffVector[j][0];
                //GWT.log("Generator " +Character.toString(VectorFunctions[j][i]));
            }

        }

    }

    //Реализация Генератора
    public void Generator (int totalVarCount){

        buffVector = new char[totalVarCount][1];

        int unitCounter = 0;

        for(int j = 1; j < totalVarCount; j++)
        {
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
