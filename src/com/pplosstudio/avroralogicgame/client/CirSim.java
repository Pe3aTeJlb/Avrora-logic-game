package com.pplosstudio.avroralogicgame.client;

import java.util.Vector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.lang.Math;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.PopupPanel;
import static com.google.gwt.event.dom.client.KeyCodes.*;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window.Navigator;

public class CirSim {

	static CirSim theSim;
	
	final int FASTTIMER=16;
	

    //static final int POSTGRABSQ=25;
    //static final int MINPOSTGRABSIZE = 256;
	
    Canvas cv;
    Context2d cvcontext;
    Canvas backcv;
    Context2d backcontext;
    
	Rectangle circuitArea;
	
	double scopeHeightFraction = 0.2;
	static final int MENUBARHEIGHT=30;
    static int VERTICALPANELWIDTH=166; // default
    
     DockLayoutPanel layoutPanel;
     MenuBar mainBar;
     MenuBar extrasBar;
     CheckboxMenuItem alternativeColorCheckItem;
     CheckboxMenuItem printableCheckItem;

     
     int gridSize, gridMask, gridRound;
     
     double transform[];
     
	
   CirSim() {
	theSim = this;
   }
	
  public void setCanvasSize(){
	  
  	int width, height;
  	width=(int)RootLayoutPanel.get().getOffsetWidth();
  	height=(int)RootLayoutPanel.get().getOffsetHeight();
  	height=height-MENUBARHEIGHT;
  	
		if (cv != null) {
			cv.setWidth(width + "PX");
			cv.setHeight(height + "PX");
			cv.setCoordinateSpaceWidth(width);
			cv.setCoordinateSpaceHeight(height);
		}
		if (backcv != null) {
			backcv.setWidth(width + "PX");
			backcv.setHeight(height + "PX");
			backcv.setCoordinateSpaceWidth(width);
			backcv.setCoordinateSpaceHeight(height);
		}

  	//setCircuitArea();
  }
  
  void setCircuitArea() {
  	int height = cv.getCanvasElement().getHeight();
  	int width = cv.getCanvasElement().getWidth();
	int h = (int) ((double)height * scopeHeightFraction);
	circuitArea = new Rectangle(0, 0, width, height-h);
  }
 
 public void init() {
	 
	 boolean printable = false;
	 transform = new double[6];
	 
	 CircuitElm.initClass(this);
	 
	 layoutPanel = new DockLayoutPanel(Unit.PX);
	 mainBar = new MenuBar(true);
	 extrasBar = new MenuBar(true);
	 
	 
	 extrasBar.addItem(printableCheckItem = new CheckboxMenuItem("White Background",
				new Command() { public void execute(){
					//int i;
					//for (i=0;i<scopeCount;i++)
						//scopes[i].setRect(scopes[i].rect);
				}}));
		printableCheckItem.setState(printable);
		
		extrasBar.addItem(alternativeColorCheckItem = new CheckboxMenuItem("Alt Color",
				new Command() { public void execute(){

					//CircuitElm.setColorScale();
				}}));
			//alternativeColorCheckItem.setState(getOptionFromStorage("alternativeColor", false));
		
			mainBar.addItem("Extras",extrasBar);
			layoutPanel.addNorth(mainBar, MENUBARHEIGHT);
	 
			
	  
			cv = Canvas.createIfSupported();
			  if (cv==null) {
				  RootPanel.get().add(new Label("Not working. You need a browser that supports the CANVAS element."));
				  return;
			  }
			  //cvcontext=cv.getContext2d();
			  //backcv = Canvas.createIfSupported();
			  //backcontext=backcv.getContext2d();
	  
			  
			  
			 layoutPanel.add(cv);
			 setCanvasSize();
			 RootPanel.get().add(layoutPanel);
			 
			 
 }
  
  final Timer timer = new Timer() {
      public void run() {
        updateCircuit();
      }
    };

    
    public void updateCircuit() {
    	
    	
    	Graphics g=new Graphics(cvcontext);
    	
    	if (printableCheckItem.getState()) {
      	   // CircuitElm.whiteColor = Color.black;
      	  //  CircuitElm.lightGrayColor = Color.black;
      	    g.setColor(Color.black);
    	} else {
    	   // CircuitElm.whiteColor = Color.white;
    	   // CircuitElm.lightGrayColor = Color.lightGray;
    	    g.setColor(Color.black);
    	}
    	g.fillRect(0, 0, g.context.getCanvas().getWidth(), g.context.getCanvas().getHeight());
    	
    	backcontext.setTransform(transform[0], transform[1], transform[2],
				 transform[3], transform[4], transform[5]);
    	
    	backcontext.setTransform(1, 0, 0, 1, 0, 0);
    	g.fillRect(0, circuitArea.height, circuitArea.width, cv.getCoordinateSpaceHeight()-circuitArea.height);


    }
	
    void setGrid() {
	gridSize = 8;
	gridMask = ~(gridSize-1);
	gridRound = gridSize/2-1;
    }
    
    
    static SafeHtml LSHTML(String s) { return SafeHtmlUtils.fromTrustedString(s); }
	
	
	
}
