package com.pplosstudio.avroralogicgame.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;


public class Main implements EntryPoint {

	public static final String versionString="2.2.9js (isharp)";
	
	// Set to true if the server runs the shortrelay.php file in the same directory as the circuit simulator
	public static final boolean shortRelaySupported = true;

	static CirSim mysim;
	
  public void onModuleLoad() {  
	  loadSimulator();
  }
  
  public void loadSimulator() {
	  
	  mysim = new CirSim();
	  mysim.init();

	    Window.addResizeHandler(new ResizeHandler() {
	    	 
            public void onResize(ResizeEvent event)
            {               
            	mysim.setCanvasSize();
                //mysim.setiFrameHeight();	
                	
            }
        });
	    
	  mysim.updateCircuit();
	   
  	}

}
