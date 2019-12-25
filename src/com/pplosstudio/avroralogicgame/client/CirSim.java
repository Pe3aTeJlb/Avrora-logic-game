package com.pplosstudio.avroralogicgame.client;

import java.util.Vector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import com.google.gwt.core.client.impl.AsyncFragmentLoader.Logger;
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
	private static final int REFRESH_RATE = 16;
	
	RootLayoutPanel root;
	
    Canvas cv;
    Context2d cvcontext;
    Canvas backcv;
    Context2d backcontext;
	
    DockLayoutPanel layoutPanel;
     
    MenuBar mainBar;
    MenuBar extrasBar;
    MenuBar editBar;
    MenuBar infoBar;
     
    CheckboxMenuItem alternativeColorCheckItem;
    CheckboxMenuItem printableCheckItem;
     
    static final int MENUBARHEIGHT=30;
    int width,height;
      
    int gridSize, gridMask, gridRound;
     
    Vector<CircuitElm> elmList;

    double transform[];
    
    Rectangle circuitArea;
    double scopeHeightFraction = 0.2;
    
    boolean euroGates = false;
    
    Random random;
     
   CirSim() {
	theSim = this;
   }
	
  public void setCanvasSize(){
	  
  	width  = (int)RootLayoutPanel.get().getOffsetWidth();
  	height = (int)RootLayoutPanel.get().getOffsetHeight()-MENUBARHEIGHT;
  	
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

		circuitArea = new Rectangle(0, 0, width, height);
		//setCircuitArea();
		
  }
  
  void setCircuitArea() {
	circuitArea = new Rectangle(0, 0, width, height);
  }
  
  	public void init() {
	 
	 transform = new double[6];
	 
	 CircuitElm.initClass(this);
	 elmList = new Vector<CircuitElm>();
	 
	 root = RootLayoutPanel.get();
	 
	 layoutPanel = new DockLayoutPanel(Unit.PX);
	 mainBar     = new MenuBar(false);
	 extrasBar   = new MenuBar(true);
	 editBar     = new MenuBar(true);
     infoBar     = new MenuBar(true);
     
     
		editBar.addItem(new MenuItem("Center Circuit", new Command() { public void execute(){
			centreCircuit();
		}}));
		editBar.addItem(new MenuItem("Zoom 100%",  new Command() { public void execute(){
			setCircuitScale(1);
		}}));
		editBar.addItem(new MenuItem("Zoom In",  new Command() { public void execute(){
			zoomCircuit(20);
		}}));
		editBar.addItem(new MenuItem("Zoom Out",  new Command() { public void execute(){
			zoomCircuit(-20);
		}}));
		
		mainBar.addItem("Edit", editBar);
	 
		
	 	extrasBar.addItem(printableCheckItem = new CheckboxMenuItem("White back",
				new Command() { public void execute(){
				}}));
		printableCheckItem.setState(false);
		
		extrasBar.addItem(alternativeColorCheckItem = new CheckboxMenuItem("Alt Color",
				new Command() { public void execute(){
					CircuitElm.setColorScale();
				}}));
			
		mainBar.addItem("Options",extrasBar);
		
		///
		///
     	mainBar.addItem("Info", infoBar);
		
		
		layoutPanel.addNorth(mainBar, MENUBARHEIGHT);
	 
		cv = Canvas.createIfSupported();
			  if (cv==null) {
				  RootPanel.get().add(new Label("Not working. You need a browser that supports the CANVAS element."));
				  return;
			  }
			  
		cvcontext=cv.getContext2d();
		
		backcv = Canvas.createIfSupported();
		backcontext=backcv.getContext2d();
		
		setCanvasSize();	  
		
		layoutPanel.add(cv);
		
		root.add(layoutPanel);
		
		
	try {			
		//CircuitElm newce = createCe("Wire",width/2, height/2, (width/2)+10, (height/2)+50, 0,0);
		//newce.setPoints();
		//elmList.add(newce);
		
		CircuitElm newce2 = createCe("And",width/2, height/2, (width/2)-45, (height/2), 0, 2);
		newce2.setPoints();
		elmList.add(newce2);
		
		//CircuitElm newce3 = createCe("Linput",10, 10, 20, 10, 0, 2);
		//newce2.setPoints();
		//elmList.add(newce3);
		
		//CircuitElm newce4 = createCe("Loutput",10, 20, 20, 20, 0, 2);
		//newce2.setPoints();
		//elmList.add(newce4);
		
		CircuitElm newce5 = createCe("Or",width/2, height/2, (width/2)-45, (height/2), 0, 2);
		newce2.setPoints();
		elmList.add(newce5);
		
		CircuitElm newce6 = createCe("XOR",width/2, height/2, (width/2)-45, (height/2), 0, 2);
		newce2.setPoints();
		elmList.add(newce6);
		
		CircuitElm newce7 = createCe("Nor",width/2, height/2, (width/2)-45, (height/2), 0, 2);
		newce2.setPoints();
		elmList.add(newce7);
		
		CircuitElm newce8 = createCe("Nand",width/2, height/2, (width/2)-45, (height/2), 0, 2);
		newce2.setPoints();
		elmList.add(newce8);
	
	} catch (Exception ee) {
	    ee.printStackTrace();
	    GWT.log("exception while undumping " + ee);
	}
		

		centreCircuit();
		
		timer.scheduleRepeating(REFRESH_RATE);
			
				
 }
  
	final Timer timer = new Timer() {
	      public void run() {
	        updateCircuit();
	      }
	 };

    public void updateCircuit() {
    	
    //	GWT.log("ms");
    	setCanvasSize();
    	
    	Graphics g = new Graphics(backcontext);
    	
    	CircuitElm.selectColor = Color.cyan;
    	
    	if (printableCheckItem.getState()) {
    		CircuitElm.whiteColor = Color.black;
      	    CircuitElm.lightGrayColor = Color.black;
      	    g.setColor(Color.white);
    	} else {
    		CircuitElm.whiteColor = Color.white;
    		CircuitElm.lightGrayColor = Color.lightGray;
    		g.setColor(Color.black);
    	}
    	g.fillRect(0, 0, g.context.getCanvas().getWidth(), g.context.getCanvas().getHeight());
    	//
    	
    	backcontext.setTransform(transform[0], transform[1], transform[2],
				 transform[3], transform[4], transform[5]);
    
    	
    	for (int i = 0; i != elmList.size(); i++) {
    		
    		if(printableCheckItem.getState()){
    			g.setColor(Color.black);	
    		}else {
    			g.setColor(Color.white);
    		}
    		
    	    getElm(i).draw(g);
    	}
    	
    	//backcontext.setTransform(1, 0, 0, 1, 0, 0);
    	
    	cvcontext.drawImage(backcontext.getCanvas(),0.0,0.0);
    	
    }
    
    void setGrid() {
	gridSize = 8;
	gridMask = ~(gridSize-1);
	gridRound = gridSize/2-1;
    }

    void zoomCircuit(int dy) {
    	
    	double newScale;
    	double oldScale = transform[0];
    	double val = dy*.01;
    	newScale = Math.max(oldScale+val, .2);
    	newScale = Math.min(newScale, 2.5);
    	setCircuitScale(newScale);
    	
    }
    
    void setCircuitScale(double newScale) {
    	
		int cx = inverseTransformX(circuitArea.width/2);
		int cy = inverseTransformY(circuitArea.height/2);
		transform[0] = transform[3] = newScale;
	
		// adjust translation to keep center of screen constant
		// inverse transform = (x-t4)/t0
		transform[4] = circuitArea.width /2 - cx*newScale;
		transform[5] = circuitArea.height/2 - cy*newScale;
    }
    
    void centreCircuit() {
    	
	Rectangle bounds = getCircuitBounds();
	
    	double scale = 1;
    	
    	if (bounds != null)
    	    // add some space on edges because bounds calculation is not perfect
    	    scale = Math.min(circuitArea.width /(double)(bounds.width+140),
    	    		circuitArea.height/(double)(bounds.height+100));
    	scale = Math.min(scale, 1.5); // Limit scale so we don't create enormous circuits in big windows

    	// calculate transform so circuit fills most of screen
    	transform[0] = transform[3] = scale;
    	transform[1] = transform[2] = transform[4] = transform[5] = 0;
    	
    	if (bounds != null) {
    	    transform[4] = (circuitArea.width  -  bounds.width *scale)/2 - bounds.x*scale;
    	    transform[5] = (circuitArea.height -  bounds.height*scale)/2 - bounds.y*scale;
    	}
    	
    }
        
    Rectangle getCircuitBounds() {
    	
    	int i;
    	int minx = 1000, maxx = 0, miny = 1000, maxy = 0;
    	
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		// centered text causes problems when trying to center the circuit,
    		// so we special-case it here
    		//if (!ce.isCenteredText()) {
    			//minx = min(ce.x, min(ce.x2, minx));
    			//maxx = max(ce.x, max(ce.x2, maxx));
    		//}
    		miny = min(ce.y, min(ce.y2, miny));
    		maxy = max(ce.y, max(ce.y2, maxy));
    	}
    	
    	if (minx > maxx) {
    	    return null;
    	    }
    	return new Rectangle(minx, miny, maxx-minx, maxy-miny);
    }
        
    int min(int a, int b) { return (a < b) ? a : b; }
    int max(int a, int b) { return (a > b) ? a : b; }
        
    // convert screen coordinates to grid coordinates by inverting circuit transform
    int inverseTransformX(double x) {
    	return (int) ((x-transform[4])/transform[0]);
    }

    int inverseTransformY(double y) {
    	return (int) ((y-transform[5])/transform[3]);
    }
    
    // convert grid coordinates to screen coordinates
    int transformX(double x) {
    	return (int) ((x*transform[0]) + transform[4]);
    }
    
    int transformY(double y) {
    	return (int) ((y*transform[3]) + transform[5]);
    }
    
    public CircuitElm getElm(int n) {
		if (n >= elmList.size())
		    return null;
		return elmList.elementAt(n);
    }
    
    public static CircuitElm createCe(String marker, int x1, int y1, int x2, int y2, int f, int inputcount) {
    	
    	if(marker.equals("Wire")) {
    	return (CircuitElm) new WireElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("And")) {
        	return (CircuitElm) new AndGateElm(x1, y1, x2, y2, f, inputcount);
        }
    	if(marker.equals("Linput")) {
    		return (CircuitElm) new LogicInputElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("Loutput")) {
    		return (CircuitElm) new LogicOutputElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("Or")) {
    		return (CircuitElm) new OrGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	if(marker.equals("XOR")) {
    		return (CircuitElm) new XorGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	if(marker.equals("Nor")) {
    		return (CircuitElm) new NorGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	if(marker.equals("Nand")) {
    		return (CircuitElm) new NandGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	else {return null;}
    	
    }
    
    static SafeHtml LSHTML(String s) { return SafeHtmlUtils.fromTrustedString(s); }
	
    int getrand(int x) {
		int q = random.nextInt();
		if (q < 0)
			q = -q;
		return q % x;
	}
	
}
