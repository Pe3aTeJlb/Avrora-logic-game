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
	private static final int REFRESH_RATE = 1;
	
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
     
    int scopeCount;
    //Scope scopes[];
    int scopeColCount[];
     
	
   CirSim() {
	theSim = this;
   }
	
  public void setCanvasSize(){
	  
  	width=(int)RootLayoutPanel.get().getOffsetWidth();
  	height=(int)RootLayoutPanel.get().getOffsetHeight()-MENUBARHEIGHT;
  	
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

  }
  
 
 public void init() {
	 
	 boolean printable = false;
	 
	 transform = new double[6];
	 
	 CircuitElm.initClass(this);
	 
	 root = RootLayoutPanel.get();
	 
	 layoutPanel = new DockLayoutPanel(Unit.PX);
	 mainBar     = new MenuBar(false);
	 extrasBar   = new MenuBar(true);
	 editBar     = new MenuBar(true);
     infoBar     = new MenuBar(true);
     
     
		editBar.addItem(new MenuItem("Center Circuit", new Command() { public void execute(){
			//centreCircuit();
		}}));
		editBar.addItem(new MenuItem("Zoom 100%",  new Command() { public void execute(){
			//setCircuitScale(1);
		}}));
		editBar.addItem(new MenuItem("Zoom In",  new Command() { public void execute(){
			//zoomCircuit(20);
		}}));
		editBar.addItem(new MenuItem("Zoom Out",  new Command() { public void execute(){
			//zoomCircuit(-20);
		}}));
		
		mainBar.addItem("Edit", editBar);
	 
		
	 	extrasBar.addItem(printableCheckItem = new CheckboxMenuItem("White back",
				new Command() { public void execute(){
					//printableCheckItem.setState(true);
					//int i;
					//for (i=0;i<scopeCount;i++)
						//scopes[i].setRect(scopes[i].rect);
				}}));
		printableCheckItem.setState(true);
		
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
			 
				//scopes = new Scope[20];
				//scopeColCount = new int[20];
				//scopeCount = 0;
				
		CircuitElm newce = createCe(464, 384, 464, 400, 0);
		newce.setPoints();
		elmList.addElement(newce);
		
		timer.scheduleRepeating(REFRESH_RATE);
			
				
 }
  
  final Timer timer = new Timer() {
      public void run() {
        updateCircuit();
      }
    };

    
    public void updateCircuit() {
    	
    	Graphics g = new Graphics(backcontext);
    	
    	if (printableCheckItem.getState()) {
      	    g.setColor(Color.red);
    	} else {
    		g.setColor(Color.green);
    	}
    	
    	g.fillRect(0, 0, width, height);
    	
    	cvcontext.drawImage(backcontext.getCanvas(),0.0,0.0);
    	
    }
    
	
    void setGrid() {
	gridSize = 8;
	gridMask = ~(gridSize-1);
	gridRound = gridSize/2-1;
    }

    
    public static CircuitElm createCe(int x1, int y1, int x2, int y2, int f) {
    	return (CircuitElm) new WireElm(x1, y1, x2, y2, f);
    }
    
    static SafeHtml LSHTML(String s) { return SafeHtmlUtils.fromTrustedString(s); }
	
	
	
}
