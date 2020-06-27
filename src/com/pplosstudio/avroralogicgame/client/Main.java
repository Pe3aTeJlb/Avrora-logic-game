package com.pplosstudio.avroralogicgame.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

public class Main implements EntryPoint {

	public static final String versionString="2.2.9js (isharp)";
	
	// Set to true if the server runs the shortrelay.php file in the same directory as the circuit simulator
	public static final boolean shortRelaySupported = true;

	static CirSim mysim;
	static Menu menu;
	
  public void onModuleLoad() {  
	  loadSimulator();
  }
  
  public void loadSimulator() {
	
	/*  
	  menu = new Menu();
	  menu.init();
	  
	    Window.addResizeHandler(new ResizeHandler() {
	    	 
            public void onResize(ResizeEvent event)
            {               
            	menu.setCanvasSize();
            }
        });
	  */
	 
	  mysim = new CirSim();
	  mysim.init();

	    Window.addResizeHandler(new ResizeHandler() {
	    	 
            public void onResize(ResizeEvent event)
            {               
            	mysim.setCanvasSize();
            }
        });
        
		   
  }

}
