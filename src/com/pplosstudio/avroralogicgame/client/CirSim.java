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
	Canvas cv;
	final int FASTTIMER=16;
	static final int MENUBARHEIGHT=30;
    static int VERTICALPANELWIDTH=166; // default
    static final int POSTGRABSQ=25;
    static final int MINPOSTGRABSIZE = 256;
    
     MenuBar mainBar;
     MenuBar extrasBar;
     CheckboxMenuItem alternativeColorCheckItem;
     CheckboxMenuItem printableCheckItem;
     
     MenuItem item;
	
   CirSim() {
	theSim = this;
   }
	
  public void setCanvasSize(){
	  
  	int width, height;
  	width=(int)RootLayoutPanel.get().getOffsetWidth();
  	height=(int)RootLayoutPanel.get().getOffsetHeight();
  	height=height-MENUBARHEIGHT;
  	width=width-VERTICALPANELWIDTH;
  	
		if (cv != null) {
			cv.setWidth(width + "PX");
			cv.setHeight(height + "PX");
			cv.setCoordinateSpaceWidth(width);
			cv.setCoordinateSpaceHeight(height);
		}

  	setCircuitArea();
  }
  
  void setCircuitArea() {
  	int height = cv.getCanvasElement().getHeight();
  	int width = cv.getCanvasElement().getWidth();
		//int h = (int) ((double)height * scopeHeightFraction);
		/*if (h < 128 && winSize.height > 300)
		  h = 128;*/
		//circuitArea = new Rectangle(0, 0, width, height-h);
  }
 
 public void init() {
	 
	 mainBar = new MenuBar(true);
	// box = new CheckBox();
	// mainBar.addItem(item);
	 
	 MenuBar m = new MenuBar(true);
	 
	 
		m.addItem(printableCheckItem = new CheckboxMenuItem("White Background",
				new Command() { public void execute(){
					//int i;
					//for (i=0;i<scopeCount;i++)
						//scopes[i].setRect(scopes[i].rect);
				}}));
		//printableCheckItem.setState(printable);
		m.addItem(alternativeColorCheckItem = new CheckboxMenuItem("Alt Color",
				new Command() { public void execute(){

					//CircuitElm.setColorScale();
				}}));
			//alternativeColorCheckItem.setState(getOptionFromStorage("alternativeColor", false));
			mainBar.addItem("Extras",m);
	 
			RootPanel.get().add(mainBar);
	 
	 Button b = new Button("Jump!", new ClickHandler() {
	      public void onClick(ClickEvent event) {
	        Window.alert("How high?");
	      }
	    });
	 
	 RootPanel.get().add(b);
	 
 }
  
  final Timer timer = new Timer() {
      public void run() {
        updateCircuit();
      }
    };

    
    public void updateCircuit() {
    	
    }
	
    
    static SafeHtml LSHTML(String s) { return SafeHtmlUtils.fromTrustedString(s); }
	
	
	
}
