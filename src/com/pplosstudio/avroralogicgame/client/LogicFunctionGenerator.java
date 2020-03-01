package com.pplosstudio.avroralogicgame.client;

import com.google.gwt.core.client.GWT;

public class LogicFunctionGenerator {
	
    private String[][] VectorFunctions;
    private int[]  VectorFunction;
    private String[] VarNames;
    private int seed = 1;
	
	LogicFunctionGenerator(int varCount, int funcCount){
		GenerateVectorFunction( varCount, funcCount);
	}
	

	private void GenerateVectorFunction(int varCount, int funcCount) {
  		
  		int totalVarCount = (int) Math.pow(2,varCount);
  		GWT.log(Integer.toString(totalVarCount));
  		
  		VectorFunctions = new String[varCount][totalVarCount];
  		VectorFunction = new int[totalVarCount];
  		VarNames = new String[varCount];
  				
  		for(int i = 0; i < funcCount; i++) 
  		{
  			String varName = "x"+i;
  			VarNames[i] = varName;
  			
  			for(int j = 0; j < totalVarCount; j++) 
  			{
  				VectorFunctions[i][j] = Integer.toString(randomBit(seed));
  				GWT.log(VectorFunctions[i][j]);
  			}
  			
  		}
  		
  	}
	
    public static int randomBit(long seed){
    	return (int) (Math.random() * ++seed);
    }

}
