package com.pplosstudio.avroralogicgame.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Menu implements  MouseDownHandler,  MouseUpHandler, MouseMoveHandler, MouseWheelHandler{

	static Menu menu;
	RootLayoutPanel root;
    LayoutPanel layoutPanel;
    DockLayoutPanel docklayoutPanel;
    VerticalPanel vp;
    
    Canvas cv;
    Context2d cvcontext;
    Canvas backcv;
    Context2d backcontext;
    double transform[];
    Rectangle circuitArea; 
    
    private final int REFRESH_RATE = 30;
    
    static CirSim mysim;
    
    public int width,height;
	
	Menu(){ menu = this;}
	
	public void setCanvasSize(){
		  
	  	width  = (int)RootLayoutPanel.get().getOffsetWidth();
	  	height = (int)RootLayoutPanel.get().getOffsetHeight();
  	
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
  }
	
	public void init() {
		
		transform = new double[6];
		
		Constants constants = (Constants) GWT.create(Constants.class);
		
		cv = Canvas.createIfSupported();
		  if (cv==null) {
			  RootPanel.get().add(new Label("Not working. You need a browser that supports the CANVAS element."));
			  return;
		  }
		backcv = Canvas.createIfSupported();
		backcontext = backcv.getContext2d();
		
		root = RootLayoutPanel.get();
		layoutPanel = new LayoutPanel();
		vp = new VerticalPanel();
		
		layoutPanel.add(cv);
		
		Button casual = new Button(constants.Casual(), new ClickHandler() {
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
		    
		Button test = new Button(constants.Test(), new ClickHandler() {
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
		 
		Button rules = new Button(constants.Rules(), new ClickHandler() {
		      public void onClick(ClickEvent event) {
					AboutBox b = new AboutBox();
					//layoutPanel.add(b);  
		      }
		    });
		 
		casual.setStyleName("menuButton");
		test.setStyleName("menuButton");
		rules.setStyleName("menuButton");
		
		vp.add(casual);
		vp.add(test);
		vp.add(rules);
	
		vp.setCellHorizontalAlignment(casual, HasHorizontalAlignment.ALIGN_CENTER);
		vp.setCellHorizontalAlignment(test, HasHorizontalAlignment.ALIGN_CENTER);
		vp.setCellHorizontalAlignment(rules, HasHorizontalAlignment.ALIGN_CENTER);
		vp.setStyleName("vpTable");
		
		DecoratorPanel dp = new DecoratorPanel();
		dp.add(vp);
		
		layoutPanel.add(dp);
		layoutPanel.setWidgetLeftRight(dp, 40, Style.Unit.EM, 40, Style.Unit.EM);
		layoutPanel.setWidgetTopBottom(dp, 20, Style.Unit.EM, 20, Style.Unit.EM);
		
		
	    root.add(layoutPanel);

	    timer.scheduleRepeating(REFRESH_RATE);
  
	}

	final Timer timer = new Timer() {
	      public void run() {
	       Update();
	      }
	 };
	
	public void Update() {
		
		setCanvasSize();
	
		Graphics g = new Graphics(backcontext);
		g.setColor(Color.black);
		g.fillRect(0, 0, g.context.getCanvas().getWidth(), g.context.getCanvas().getHeight());
		//g.draw
		cv.getContext2d().drawImage(backcontext.getCanvas(), 0.0, 0.0);
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
