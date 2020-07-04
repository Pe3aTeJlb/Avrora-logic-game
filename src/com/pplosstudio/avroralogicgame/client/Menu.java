package com.pplosstudio.avroralogicgame.client;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
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
    
    private final int REFRESH_RATE = 15;
    
    static CirSim mysim;
    
    int width,height;
    int x, y = 0;
    ArrayList<ArrayList<Point>> lines = new ArrayList<ArrayList<Point>>();
	
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
					layoutPanel.add(b);  
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
	    
	    Point p = new Point(random(50,width/2),random(50,height-50));
	    ArrayList<Point> l = new ArrayList<Point>();
	    l.add(p);
	    l.add(new Point(p.x+random(20,150), p.y));

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
		
		/*
		for(int i = 0; i < lines.size(); i++) {
			
			for(int j = 0; j<lines.get(i).size()-1; j++) {
				g.setColor(Color.green);
				g.setLineWidth(1);
				g.drawLine(lines.get(i).get(j).x, lines.get(i).get(j).y, lines.get(i).get(j+1).x, lines.get(i).get(j+1).y);
				//lines.get(i).add(new Point(lines.get(i).get(lines.get(i).size()-1).x+random(20,100), lines.get(i).get(lines.get(i).size()-1).y));
			}
			
		}
		*/
		x+=3;
		g.setColor(Color.green);
		g.setLineWidth(1);
		g.drawLine(x,50,x+20,50);
		g.drawLine(x,150,x+20,150);
		g.drawLine(x,250,x+20,250);
		
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
	
  	private int random(int min, int max)
  	{
  		max -= min;
  		return (int) (Math.random() * ++max) + min;
  	}
	
}
