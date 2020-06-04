package com.pplosstudio.avroralogicgame.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;

public class BasisConverter {

    private String outFunction = "";
    public ArrayList<ArrayList<String>> list = new ArrayList<>();
    ArrayList<String> operands = new ArrayList<>();
    ArrayList<String> terms = new ArrayList<>();
    private boolean debug = false;
    public String dmp;
    
    public BasisConverter(boolean dbg){debug = dbg;}

    //Приведение к базису и-не
    public void ToNand(String func, boolean sum_of_prod) {
    
    	dmp = "";
    	list.clear();
    	terms.clear();
    	outFunction = "";
    	
        if(sum_of_prod){

            String buffFuncName = "";

            func = func.replace(" ","");
            func = func.replace("(","");
            func = func.replace(")","");
            String[] tmp = func.split("\\+");

            for(int i = 0; i < tmp.length; i++){
                operands = new ArrayList<>();
                String funcName = "";
                String[] t = tmp[i].split("\\*");

                if(tmp[i].length()<=3){
                    funcName = tmp[i];
                }else{
                    funcName = "~("+tmp[i]+")";
                    for(int j = 0; j < t.length; j++){
                        operands.add(t[j]);
                    }
                    operands.add("Nand");
                    operands.add(funcName);
                }
                terms.add(funcName);
                buffFuncName += funcName + "*";
                if(operands.size()!=0)list.add(operands);
            }
            if(tmp.length>1) {
            buffFuncName = removeByIndex(buffFuncName, buffFuncName.length()-1);
            buffFuncName = "~("+buffFuncName+")";
            terms.add("Nand");
            terms.add(buffFuncName);
            list.add(terms);
            }


        }else{

            String buffFuncName = "";

            func = func.replace(" ","");
            func = func.replace("(","");
            func = func.replace(")","");
            String[] tmp = func.split("\\*");

            for(int i = 0; i < tmp.length; i++){

                operands = new ArrayList<>();
                String funcName = "";
                String[] t = tmp[i].split("\\+");

                if(tmp[i].length()<=3){
                    funcName = tmp[i];
                }else{
                    for(int j = 0; j < t.length; j++){
                        if(t[j].charAt(0) == '~'){
                            t[j] = removeByIndex(t[j],0);
                            funcName += t[j];
                            funcName += "*";
                        }else if(t[j].charAt(0) != '~'){
                            t[j] = "~" + t[j];
                            funcName += t[j];
                            funcName += "*";
                        }
                        operands.add(t[j]);
                    }
                    funcName = removeByIndex(funcName, funcName.length()-1);
                    funcName = "~("+funcName+")";
                    operands.add("Nand");
                    operands.add(funcName);
                }

                terms.add(funcName);
                buffFuncName += funcName + "*";
                if(operands.size()!=0)list.add(operands);
            }

            buffFuncName = removeByIndex(buffFuncName, buffFuncName.length()-1);
            buffFuncName = "~("+buffFuncName+")";
            terms.add("Nand");
            terms.add(buffFuncName);
            list.add(terms);

            //инверсия через элемент или-не
            terms = new ArrayList<>();
            terms.add(buffFuncName);
            terms.add(buffFuncName);
            terms.add("Nand");
            terms.add("~("+buffFuncName+")");
            list.add(terms);
        }

    }

    //Приведение к базису Или-не
    public void ToNor(String func, boolean sum_of_prod) {
    	
    	dmp = "";
    	list.clear();
    	terms.clear();
    	outFunction = "";
    	
        if(sum_of_prod){

            String buffFuncName = "";

            func = func.replace(" ","");
            func = func.replace("(","");
            func = func.replace(")","");
            String[] tmp = func.split("\\+");

            for(int i = 0; i < tmp.length; i++){

                operands = new ArrayList<>();
                String funcName = "";
                String[] t = tmp[i].split("\\*");

                if(tmp[i].length()<=3){
                    funcName = tmp[i];
                }else{
                    for(int j = 0; j < t.length; j++){
                        if(t[j].charAt(0) == '~'){
                            t[j] = removeByIndex(t[j],0);
                            funcName += t[j];
                            funcName += "+";
                        }else if(t[j].charAt(0) != '~'){
                            t[j] = "~" + t[j];
                            funcName += t[j];
                            funcName += "+";
                        }
                        operands.add(t[j]);
                    }
                    funcName = removeByIndex(funcName, funcName.length()-1);
                    funcName = "~("+funcName+")";
                    operands.add("Nor");
                    operands.add(funcName);
                }

                terms.add(funcName);
                buffFuncName += funcName + "+";
                if(operands.size()!=0)list.add(operands);
            }

            buffFuncName = removeByIndex(buffFuncName, buffFuncName.length()-1);
            if(tmp.length>1) {
            buffFuncName = "~("+buffFuncName+")";
            terms.add("Nor");
            terms.add(buffFuncName);
            list.add(terms);
            }
            //инверсия через элемент или-не
            terms = new ArrayList<>();
            terms.add(buffFuncName);
            terms.add(buffFuncName);
            terms.add("Nor");
            terms.add("~("+buffFuncName+")");
            list.add(terms);
        }else{

            //[[x0, x1, Nor, ~(x0+x1)], [~x1, ~x2, Nor, ~(~x1+~x2)], [~(x0+x1), ~(~x1+~x2), Nor, ~(~(x0+x1)+~(~x1+~x2))]]
            String buffFuncName = "";

            func = func.replace(" ","");
            func = func.replace("(","");
            func = func.replace(")","");
            String[] tmp = func.split("\\*");

            for(int i = 0; i < tmp.length; i++){
               
                operands = new ArrayList<>();
                String funcName = "";
                String[] t = tmp[i].split("\\+");

                if(tmp[i].length()<=3){

                    funcName = tmp[i];

                }else{

                    funcName = "~("+tmp[i]+")";
                    for(int j = 0; j < t.length; j++){
                            operands.add(t[j]);
                    }
                    operands.add("Nor");
                    operands.add(funcName);
                }
                
                terms.add(funcName);
                buffFuncName += funcName + "+";
                //GWT.log("FuncName " + buffFuncName);
                if(operands.size()!=0)list.add(operands);
            }
            if(tmp.length>1) { 
            buffFuncName = removeByIndex(buffFuncName, buffFuncName.length()-1);
            buffFuncName = "~("+buffFuncName+")";
            terms.add("Nor");
            terms.add(buffFuncName);
            
            list.add(terms);
            }
            
        }

    }

    //Приведение к полиному Жегалкина методом треугольника
    public void ToZhegalkinPolynomial(char[][] functionVector, int row, String[] varNames) {
    	
    	dmp = "";
    	list.clear();
    	terms.clear();
    	outFunction = "";
    	
        ArrayList<Integer> vectorFunc = new ArrayList<>();
        ArrayList<Integer> ZhegalikinIndexes = new ArrayList<>();

        for(int i = 0; i<functionVector.length;i++){
            vectorFunc.add(Integer.parseInt(Character.toString(functionVector[i][row])));
        }

        if(debug)GWT.log("Init vf "+ vectorFunc.toString());
        dmp+=vectorFunc.toString()+"\n";

        int m = vectorFunc.size();
        ZhegalikinIndexes.add(vectorFunc.get(0));

        for(int k = 0; k<m-1; k++) {
        	

            for (int i = 1; i < vectorFunc.size(); i++) {

                if (i == 1) {

                    if(vectorFunc.get(0) == 1){
                        operands = new ArrayList<>();
                        String v = Integer.toBinaryString(k);
                        //if(debug)GWT.log("term "+v);
                        //dmp+=v+"\n";
                        String tmp = "";
                        int y = 0;
                        //Если мы берём нулевую комбинацию, то всё разваливается. с другой стороны, в Жегалкине 0 комбинация станет константной единицей в схеме, что не реализовано, а это делать мне лень
                        if(!v.equals("0")) {
	                        for(int l = varNames.length-v.length(); l<varNames.length; l++){
	                            if(v.charAt(y) == '1'){
	                                tmp += varNames[l]+"*";
	                                operands.add(varNames[l]);
	                            }
	                            y++;
	                        }
                        

                            tmp = removeByIndex(tmp,tmp.length()-1);

                            if(operands.size()>1){
                                operands.add("*");
                                operands.add(tmp);
                                terms.add(tmp);
                                list.add(operands);
                            }else{terms.add(operands.get(0));}

                            outFunction += tmp+"@";
                            if(debug)GWT.log(outFunction);
                        }
                    }

                }
                int j = vectorFunc.get(i - 1) ^ vectorFunc.get(i);
                vectorFunc.set(i-1,j);
            }
           
            vectorFunc.remove(vectorFunc.size()-1);
            
            dmp += vectorFunc.toString() + "\n";
            if(debug)GWT.log(vectorFunc.toString());
            ZhegalikinIndexes.add(vectorFunc.get(0));
        }
        
        if(vectorFunc.get(0) == 1){
            operands = new ArrayList<>();
            String v = Integer.toBinaryString(m-1);
            if(debug)GWT.log(v);
            //dmp+=v+"\n";
            String tmp = "";
            int y = 0;
            for(int l = varNames.length-v.length(); l<varNames.length; l++){
                if(v.charAt(y) == '1'){
                    tmp += varNames[l]+"*";
                    operands.add(varNames[l]);
                }
                y++;
            }

                tmp = removeByIndex(tmp,tmp.length()-1);

                if(operands.size()>1){
                    operands.add("*");
                    operands.add(tmp);
                    terms.add(tmp);
                    list.add(operands);
                }else{terms.add(operands.get(0));}

                outFunction += tmp+"@";
                if(debug)GWT.log(outFunction);
        }

        outFunction = removeByIndex(outFunction, outFunction.length()-1);
        terms.add("Xor");
        terms.add(outFunction);
        list.add(terms);
        if(debug)GWT.log(ZhegalikinIndexes.toString());
        if(debug)GWT.log(outFunction);
        if(debug)GWT.log(list.toString());
        dmp+=ZhegalikinIndexes.toString()+"\n"+outFunction+"\n"+list.toString()+"\n";

    }

    private String removeByIndex(String str, int index) {
        return str.substring(0,index)+str.substring(index+1);
    }

    public String getBooleanFunction() {return outFunction; }

}
