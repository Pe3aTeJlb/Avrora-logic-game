package com.pplosstudio.avroralogicgame.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class Menu implements  MouseDownHandler,  MouseUpHandler, MouseMoveHandler, MouseWheelHandler{

	static Menu menu;
	RootLayoutPanel root;
    Canvas cv;
    
    DockLayoutPanel layoutPanel;
    
    static CirSim mysim;
    
    public int width,height;
	
	Menu(){ menu = this;}
	
	public void setCanvasSize(){
		  
	  	width  = (int)RootLayoutPanel.get().getOffsetWidth()/2;
	  	height = (int)RootLayoutPanel.get().getOffsetHeight()/2;
  	
		if (cv != null) {
			cv.setWidth(width + "PX");
			cv.setHeight(height + "PX");
			cv.setCoordinateSpaceWidth(width);
			cv.setCoordinateSpaceHeight(height);
		}	
		
  }
	
	
	public void init() {
		
		cv = Canvas.createIfSupported();
		  if (cv==null) {
			  RootPanel.get().add(new Label("Not working. You need a browser that supports the CANVAS element."));
			  return;
		  }
		
		root = RootLayoutPanel.get();
		 
		layoutPanel = new DockLayoutPanel(Unit.PX);
		
		 Button casula = new Button("Casual", new ClickHandler() {
		      public void onClick(ClickEvent event) {
		    	  
		    	  mysim = new CirSim();
		    	  mysim.init();

		    	    Window.addResizeHandler(new ResizeHandler() {
		    	    	 
		                public void onResize(ResizeEvent event)
		                {               
		                	mysim.setCanvasSize();
		                }
		            });
		    	  
		      }
		    });

		  root.add(casula);
		    
		  Button test = new Button("Test", new ClickHandler() {
		      public void onClick(ClickEvent event) {
		    	  
		    	  mysim = new CirSim();
		    	  mysim.init();

		    	    Window.addResizeHandler(new ResizeHandler() {
		    	    	 
		                public void onResize(ResizeEvent event)
		                {               
		                	mysim.setCanvasSize();
		                }
		            });
		    	  
		      }
		    });
		  root.add(test);
		  
		  Button rules = new Button("Test", new ClickHandler() {
		      public void onClick(ClickEvent event) {
		    	  
		    	  mysim = new CirSim();
		    	  mysim.init();

		    	    Window.addResizeHandler(new ResizeHandler() {
		    	    	 
		                public void onResize(ResizeEvent event)
		                {               
		                	mysim.setCanvasSize();
		                }
		            });
		    	  
		      }
		    });
		  root.add(rules);
		  
		   
		  
		
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	
}
