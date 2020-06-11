package com.pplosstudio.avroralogicgame.client;

import java.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.lang.Math;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/*
 Скрипт расчёта схемы, он же UI и управление им.
 Категорически не рекомендуется лезть в расчёт схемы и в классы для отрисовки графики
 */

public class CirSim implements  MouseDownHandler,  MouseUpHandler, MouseMoveHandler, MouseWheelHandler{

	//this
	static CirSim theSim;
	
	public Vector<CircuitElm> elmList;
	
	//Circuit settings fields
	boolean euroGates = false;
    Random random;
	
	private final int REFRESH_RATE = 30;
	
 /////////////////////
//Set UI Fields//
	RootLayoutPanel root;
	
    Canvas cv;
    Context2d cvcontext;
    Canvas backcv;
    Context2d backcontext;
    
	Rectangle circuitArea; 
	
    DockLayoutPanel layoutPanel;
     
    MenuBar mainBar;
    MenuBar extrasBar;
    MenuBar editBar;
    //MenuBar infoBar;
     
    CheckboxMenuItem alternativeColorCheckItem;
    CheckboxMenuItem printableCheckItem;
     
    private final int MENUBARHEIGHT=30;
    public int width,height;
    
    double transform[];
    
 /////////////////////
//Events//
    long zoomTime;
    int dragScreenX, dragScreenY, initDragGridX, initDragGridY;
    boolean dragging;
    private CircuitElm mouseElm=null;
    static final int POSTGRABSQ=25;
    static final int MINPOSTGRABSIZE = 256;
    
    double timeStep = 5e-6;
    
 ////////////////////////
//Circuit procession
    double circuitMatrix[][],circuitRightSide[],
	origRightSide[], origMatrix[][];
    RowInfo circuitRowInfo[];
    int circuitPermute[];
    boolean circuitNonLinear;
    int voltageSourceCount;
    int circuitMatrixSize, circuitMatrixFullSize;
    boolean circuitNeedsMap;   
    boolean dumpMatrix;
    
    Vector<CircuitNode> nodeList;
    Vector<Point> postDrawList = new Vector<Point>();
    Vector<Point> badConnectionList = new Vector<Point>();
    CircuitElm voltageSources[];
     
    HashMap<Point,NodeMapEntry> nodeMap;
    HashMap<Point,Integer> postCountMap;
    Vector<WireInfo> wireInfoList;
    
    int gridSize, gridMask, gridRound;
    
    private ArrayList<String> currOutput; //Хранят текущее состояние выходов функций
    private ArrayList<CircuitElm> FunctionsOutput;//Список выходных и входных элементов функций
    private ArrayList<SwitchElm> FunctionsInput;
    private int currOutputIndex = 0; // текущая позиция кристалла
    private int tickCounter = 0;
    private boolean refreshGameState = true;
    private int level = 1;
 ////////////////////////
//Circuit Construction
    
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
     //infoBar     = new MenuBar(true);
     
     
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
		alternativeColorCheckItem.setState(false);
		
		//initialize wire color
		CircuitElm.setColorScale();
		
		mainBar.addItem("Options",extrasBar);
		///
		///
     	//mainBar.addItem("Info", infoBar);
		
     	
		layoutPanel.addNorth(mainBar, MENUBARHEIGHT);
	 
		cv = Canvas.createIfSupported();
			  if (cv==null) {
				  RootPanel.get().add(new Label("Not working. You need a browser that supports the CANVAS element."));
				  return;
			  }
			  
		cvcontext=cv.getContext2d();
		
		backcv = Canvas.createIfSupported();
		backcontext = backcv.getContext2d();
		
		setCanvasSize();	  
		
		layoutPanel.add(cv);
		
		root.add(layoutPanel);
		
		cv.addMouseDownHandler(this);
		cv.addMouseUpHandler(this);
		cv.addMouseWheelHandler(this);
		cv.addMouseMoveHandler(this);	
		
		centreCircuit();
		
		GenerateCircuit();
		
		timer.scheduleRepeating(REFRESH_RATE);
		
  	}

  	//Game Logic
  	//also check update method
  	public void GameOverTrigger() {
  		refreshGameState = true;
  		tickCounter = 0;
  	}
  	
  	public void GenerateCircuit() {
  		
  		CircuitSynthesizer v = new CircuitSynthesizer();
		//v.Synthesis(width, height, level);
		v.Synthesis(width, height);
		//v.Synthesis(width, height,"s");
		elmList = v.elmList;
		FunctionsOutput = v.outElems;
		FunctionsInput = v.inElems;
		currOutput = new ArrayList();
  	}
  	
  	
 // *****************************************************************
//  Void Update	
  
	final Timer timer = new Timer() {
	      public void run() {
	        updateCircuit();
	      }
	 };
	
   
	 public void updateCircuit() {
    	
    	setCanvasSize();
    	analyzeCircuit();
    	runCircuit();
   
    	if(refreshGameState)tickCounter++;
    		
    	//
    	if(tickCounter > 6 && refreshGameState && currOutputIndex < FunctionsOutput.size()) {
    		
    		currOutput = new ArrayList();
    		
    		for(int i = 0; i<FunctionsOutput.size(); i++) {
      			String s = FunctionsOutput.get(i).volts[0] < 2.5 ? "0" : "1";
      			currOutput.add(s);
      		}
      		GWT.log("curr out index " + currOutputIndex);
      		
      		if(currOutputIndex < FunctionsOutput.size()) {
      			
          		GWT.log(currOutput.toString());
	      		if(currOutputIndex != FunctionsOutput.size()-1 && currOutput.get(currOutputIndex).equals("0") && currOutput.get(currOutputIndex+1).equals("0")) {
	      			GWT.log("Game Over");		
	      			for(int i = 0; i<FunctionsInput.size(); i++) {
	      				FunctionsInput.get(i).position = 0;
	      			}
      				currOutputIndex = 0;
	      		}
	      		
	      		if(currOutputIndex != FunctionsOutput.size()-1 && currOutput.get(currOutputIndex).equals("0") && currOutput.get(currOutputIndex+1).equals("1")) {
	      			currOutputIndex++;		
	      			GWT.log("new curr out index " + currOutputIndex);
	      		}
	      		
	      		if(currOutputIndex == FunctionsOutput.size()-1 && currOutput.get(currOutputIndex).equals("0")) {
	      			currOutputIndex++;		
	      			GWT.log("new curr out index " + currOutputIndex);
	      		}
	      		
	          	if(currOutputIndex == FunctionsOutput.size()) {
	          		
	          		currOutputIndex = 0;
	          		GWT.log("You Won!");
	          		elmList.clear();
	          		level++;
	          		
	          		GenerateCircuit();
	          	}
	          	
      		}
      			    		
     		refreshGameState = false;
   
    	}
    	
    	Graphics g = new Graphics(backcontext);
    	
    	CircuitElm.selectColor = Color.cyan;
    	
    	if (printableCheckItem.getState()) {
    		CircuitElm.whiteColor = Color.black;
      	    CircuitElm.lightGrayColor = Color.black;
      	    g.setColor(Color.white);
    	} else {
    		CircuitElm.whiteColor = Color.white;
    		CircuitElm.lightGrayColor = Color.white;
    		g.setColor(Color.black);
    	}
    	g.fillRect(0, 0, g.context.getCanvas().getWidth(), g.context.getCanvas().getHeight());
    	
    	
    	backcontext.setTransform(transform[0], transform[1], transform[2],
				 				 transform[3], transform[4], transform[5]
				 				);
        		 
    	for (int i = 0; i != elmList.size(); i++) {
    		
    		if(printableCheckItem.getState()){
    			g.setColor(Color.black);	
    		}else {
    			g.setColor(Color.white);
    		}
    		
    		try {
    			getElm(i).draw(g);
    	    }catch(Exception ee) {
    	    	ee.printStackTrace();
    		    GWT.log("exception while drawing " + ee);
    	    }
    		
    	}
    	
    	for (int i = 0; i != postDrawList.size(); i++)
    				CircuitElm.drawPost(g, postDrawList.get(i));
    	
    	for (int i = 0; i != badConnectionList.size(); i++) {
    	    Point cn = badConnectionList.get(i);
    	    g.setColor(Color.red);
    	    g.fillOval(cn.x-3, cn.y-3, 7, 7);
    	}

    	cvcontext.drawImage(backcontext.getCanvas(),0.0,0.0);
    }
 
       
 // *****************************************************************
//  SOLVE CIRCUIT
    
    long lastTime = 0, lastFrameTime, lastIterTime, secTime = 0; 
    int frames = 0;
    int steps = 0;
    int framerate = 0, steprate = 0;
    boolean converged;
    int subIterations;
    
    
    class NodeMapEntry {
    		int node;
    		NodeMapEntry() { node = -1; }
    		NodeMapEntry(int n) { node = n; }
    }
     
    class WireInfo {
    		WireElm wire;
    		Vector<CircuitElm> neighbors;
    		int post;
    		WireInfo(WireElm w) {
    		    wire = w;
    		}
    }
     
    class FindPathInfo {
    	 
    	static final int INDUCT  = 1;
    	static final int VOLTAGE = 2;
    	static final int SHORT   = 3;
    	static final int CAP_V   = 4;
    	boolean used[];
    	int dest;
    	CircuitElm firstElm;
    	int type;
    	FindPathInfo(int t, CircuitElm e, int d) {
    		    dest = d;
    		    type = t;
    		    firstElm = e;
    		    used = new boolean[nodeList.size()];
    		}
    	boolean findPath(int n1) { return findPath(n1, -1); }
    	boolean findPath(int n1, int depth) {
    		    if (n1 == dest)
    			return true;
    		    if (depth-- == 0)
    			return false;
    		    if (used[n1]) {
    			return false;
    		    }
    		    used[n1] = true;
    		    int i;
    		    for (i = 0; i != elmList.size(); i++) {
    			CircuitElm ce = getElm(i);
    			if (ce == firstElm)
    			    continue;
    			if (type == INDUCT) {
    			    // inductors need a path free of current sources
    			    //if (ce instanceof CurrentElm)
    				//continue;
    			}
    			if (type == VOLTAGE) {
    			    // when checking for voltage loops, we only care about voltage sources/wires/ground
    			    if (!(ce.isWire()))
    				continue;
    			}
    			// when checking for shorts, just check wires
    			if (type == SHORT && !ce.isWire())
    			    continue;
    			if (type == CAP_V) {
    			    // checking for capacitor/voltage source loops
    			    if (!(ce.isWire()))
    				continue;
    			}
    			if (n1 == 0) {
    			    // look for posts which have a ground connection;
    			    // our path can go through ground
    			    int j;
    			    for (j = 0; j != ce.getConnectionNodeCount(); j++)
    				if (ce.hasGroundConnection(j) &&
    				    findPath(ce.getConnectionNode(j), depth)) {
    				    used[n1] = false;
    				    return true;
    				}
    			}
    			int j;
    			for (j = 0; j != ce.getConnectionNodeCount(); j++) {
    			    if (ce.getConnectionNode(j) == n1)
    				break;
    			}
    			if (j == ce.getConnectionNodeCount())
    			    continue;
    			if (ce.hasGroundConnection(j) && findPath(0, depth)) {
    			    used[n1] = false;
    			    return true;
    			}
    			int k;
    			for (k = 0; k != ce.getConnectionNodeCount(); k++) {
    			    if (j == k)
    				continue;

    			    if (ce.getConnection(j, k) && findPath(ce.getConnectionNode(k), depth)) {
    				used[n1] = false;
    				return true;
    			    }
    			}
    		    }
    		    used[n1] = false;
    		    //System.out.println(n1 + " failed");
    		    return false;
    		}
     
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    void runCircuit() {
    	
    	if (circuitMatrix == null || elmList.size() == 0) {
    	    circuitMatrix = null;
    	    return;
    	}
    	int iter;
    	//int maxIter = getIterCount();
    	boolean debugprint = dumpMatrix;
    	dumpMatrix = false;
    	long steprate = (long) (100);
    	long tm = System.currentTimeMillis();
    	long lit = lastIterTime;
    	if (lit == 0) {
    	    lastIterTime = tm;
    	    return;
    	}
    	
    	// Check if we don't need to run simulation (for very slow simulation speeds).
    	// If the circuit changed, do at least one iteration to make sure everything is consistent.
    	//if (1000 >= steprate*(tm-lastIterTime) && !didAnalyze)
    	  //  return;
    	
    	//boolean delayWireProcessing = canDelayWireProcessing();
    	
    	for (iter = 1; ; iter++) {
    	    int i, j, k, subiter;
    	    for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		ce.startIteration();
    	    }
    	    steps++;
    	    final int subiterCount = 5000;
    	    for (subiter = 0; subiter != subiterCount; subiter++) {
    		converged = true;
    		subIterations = subiter;
    		for (i = 0; i != circuitMatrixSize; i++)
    		    circuitRightSide[i] = origRightSide[i];
    		if (circuitNonLinear) {
    		    for (i = 0; i != circuitMatrixSize; i++)
    			for (j = 0; j != circuitMatrixSize; j++)
    			    circuitMatrix[i][j] = origMatrix[i][j];
    		}
    		for (i = 0; i != elmList.size(); i++) {
    		    CircuitElm ce = getElm(i);
    		    ce.doStep();
    		}
    		//if (stopMessage != null)
    		   // return;
    		boolean printit = debugprint;
    		debugprint = false;
    		for (j = 0; j != circuitMatrixSize; j++) {
    		    for (i = 0; i != circuitMatrixSize; i++) {
    			double x = circuitMatrix[i][j];
    			if (Double.isNaN(x) || Double.isInfinite(x)) {
    			    //stop("nan/infinite matrix!", null);
    			    return;
    			}
    		    }
    		}
    		if (printit) {
    		    for (j = 0; j != circuitMatrixSize; j++) {
    			//String x = "";
    			//for (i = 0; i != circuitMatrixSize; i++)
    			   // x += circuitMatrix[j][i] + ",";
    			//x += "\n";
    			//console(x);
    		    }
    		    //console("");
    		}
    		if (circuitNonLinear) {
    		    if (converged && subiter > 0)
    			break;
    		    if (!lu_factor(circuitMatrix, circuitMatrixSize,
    				  circuitPermute)) {
    			//stop("Singular matrix!", null);
    			return;
    		    }
    		}
    		lu_solve(circuitMatrix, circuitMatrixSize, circuitPermute,
    			 circuitRightSide);
    		
    		for (j = 0; j != circuitMatrixFullSize; j++) {
    		    RowInfo ri = circuitRowInfo[j];
    		    double res = 0;
    		    if (ri.type == RowInfo.ROW_CONST)
    			res = ri.value;
    		    else
    			res = circuitRightSide[ri.mapCol];
    		    if (Double.isNaN(res)) {
    			converged = false;
    			//debugprint = true;
    			break;
    		    }
    		    if (j < nodeList.size()-1) {
    			CircuitNode cn = getCircuitNode(j+1);
    			for (k = 0; k != cn.links.size(); k++) {
    			    CircuitNodeLink cnl = (CircuitNodeLink)
    				cn.links.elementAt(k);
    			    cnl.elm.setNodeVoltage(cnl.num, res);
    			}
    		    } else {
    			int ji = j-(nodeList.size()-1);
    			//System.out.println("setting vsrc " + ji + " to " + res);
    			voltageSources[ji].setCurrent(ji, res);
    		    }
    		}
    		if (!circuitNonLinear)
    		    break;
    	    }
    	    //if (subiter > 5)
    		//console("converged after " + subiter + " iterations\n");
    	    
    	    if (subiter == subiterCount) {
    		//stop("Convergence failed!", null);
    		break;
    	    }
    	    
    	    //t += timeStep;
    	    
    	    for (i = 0; i != elmList.size(); i++) {
    	    	getElm(i).stepFinished();
    		}
    	    
    	    //if (!delayWireProcessing)
    	//	calcWireCurrents();
    	   
    	    /*
    	    for (i = 0; i != scopeCount; i++)
    	    	scopes[i].timeStep();
    	    for (i=0; i != elmList.size(); i++)
    		if (getElm(i) instanceof ScopeElm )
    		    ((ScopeElm)getElm(i)).stepScope();
    		*/
    	    
    	    tm = System.currentTimeMillis();
    	    lit = tm;
    	    // Check whether enough time has elapsed to perform an *additional* iteration after
    	    // those we have already completed.
    	    if ((iter+1)*1000 >= steprate*(tm-lastIterTime) || (tm-lastFrameTime > 500))
    		break;
    	    //if (!simRunning)
    		//break;
    	} // for (iter = 1; ; iter++)
    	lastIterTime = lit;
    	//if (delayWireProcessing)
    	//calcWireCurrents();
    	
    }
    
    void analyzeCircuit() {
    	 
    	 if (elmList.isEmpty()) {
    		    postDrawList = new Vector<Point>();
    		    badConnectionList = new Vector<Point>();
    		    return;
    		}
    		//stopMessage = null;
    		//stopElm = null;
    		int i, j;
    		int vscount = 0;
    		nodeList = new Vector<CircuitNode>();
    		postCountMap = new HashMap<Point,Integer>();
    		boolean gotGround = false;
    		boolean gotRail = false;
    		CircuitElm volt = null;

    		calculateWireClosure();
    		
    		// look for voltage or ground element
    		/*
    		for (i = 0; i != elmList.size(); i++) {
    		    CircuitElm ce = getElm(i);
    		    if (ce instanceof GroundElm) {
    			gotGround = true;
    			break;
    		    }
    		    if (ce instanceof RailElm)
    		    	gotRail = true;
    		    if (volt == null && ce instanceof VoltageElm)
    		    	volt = ce;
    		}
    		*/

    		// if no ground, and no rails, then the voltage elm's first terminal
    		// is ground
    	
    		if (!gotGround && volt != null && !gotRail) {
    		    CircuitNode cn = new CircuitNode();
    		    Point pt = volt.getPost(0);
    		    nodeList.addElement(cn);

    		    // update node map
    		    NodeMapEntry cln = nodeMap.get(pt);
    		    if (cln != null)
    			cln.node = 0;
    		    else
    			nodeMap.put(pt, new NodeMapEntry(0));
    		} 
    		else {
    		    // otherwise allocate extra node for ground
    		    CircuitNode cn = new CircuitNode();
    		    nodeList.addElement(cn);
    		}

    		// allocate nodes and voltage sources
    		//LabeledNodeElm.resetNodeList();
    		for (i = 0; i != elmList.size(); i++) {
    		    CircuitElm ce = getElm(i);
    		    int inodes = ce.getInternalNodeCount();
    		    int ivs = ce.getVoltageSourceCount();
    		    int posts = ce.getPostCount();
    		    
    		    // allocate a node for each post and match posts to nodes
    		    for (j = 0; j != posts; j++) {
    			Point pt = ce.getPost(j);
    			Integer g = postCountMap.get(pt);
    			postCountMap.put(pt, g == null ? 1 : g+1);
    			NodeMapEntry cln = nodeMap.get(pt);
    			
    			// is this node not in map yet?  or is the node number unallocated?
    			// (we don't allocate nodes before this because changing the allocation order
    			// of nodes changes circuit behavior and breaks backward compatibility;
    			// the code below to connect unconnected nodes may connect a different node to ground) 
    			if (cln == null || cln.node == -1) {
    			    CircuitNode cn = new CircuitNode();
    			    CircuitNodeLink cnl = new CircuitNodeLink();
    			    cnl.num = j;
    			    cnl.elm = ce;
    			    cn.links.addElement(cnl);
    			    ce.setNode(j, nodeList.size());
    			    if (cln != null)
    				cln.node = nodeList.size();
    			    else
    				nodeMap.put(pt, new NodeMapEntry(nodeList.size()));
    			    nodeList.addElement(cn);
    			} else {
    			    int n = cln.node;
    			    CircuitNodeLink cnl = new CircuitNodeLink();
    			    cnl.num = j;
    			    cnl.elm = ce;
    			    getCircuitNode(n).links.addElement(cnl);
    			    ce.setNode(j, n);
    			    // if it's the ground node, make sure the node voltage is 0,
    			    // cause it may not get set later
    			    if (n == 0)
    				ce.setNodeVoltage(j, 0);
    			}
    		    }
    		    for (j = 0; j != inodes; j++) {
    			CircuitNode cn = new CircuitNode();
    			cn.internal = true;
    			CircuitNodeLink cnl = new CircuitNodeLink();
    			cnl.num = j+posts;
    			cnl.elm = ce;
    			cn.links.addElement(cnl);
    			ce.setNode(cnl.num, nodeList.size());
    			nodeList.addElement(cn);
    		    }
    		    vscount += ivs;
    		}
    		
    		makePostDrawList();
    		//if (!calcWireInfo())
    		   // return;
    		nodeMap = null; // done with this
    		
    		voltageSources = new CircuitElm[vscount];
    		vscount = 0;
    		circuitNonLinear = false;

    		// determine if circuit is nonlinear
    		for (i = 0; i != elmList.size(); i++) {
    		    CircuitElm ce = getElm(i);
    		    if (ce.nonLinear())
    		    	circuitNonLinear = true;
    		    
    		    int ivs = ce.getVoltageSourceCount();
    		    
    		    for (j = 0; j != ivs; j++) {
	    			voltageSources[vscount] = ce;
	    			ce.setVoltageSource(j, vscount++);
    		    }
    		    
    		}
    		voltageSourceCount = vscount;

    		int matrixSize = nodeList.size()-1 + vscount;
    		circuitMatrix = new double[matrixSize][matrixSize];
    		circuitRightSide = new double[matrixSize];
    		origMatrix = new double[matrixSize][matrixSize];
    		origRightSide = new double[matrixSize];
    		circuitMatrixSize = circuitMatrixFullSize = matrixSize;
    		circuitRowInfo = new RowInfo[matrixSize];
    		circuitPermute = new int[matrixSize];
    		for (i = 0; i != matrixSize; i++)
    		    circuitRowInfo[i] = new RowInfo();
    		circuitNeedsMap = false;
    		
    		// stamp linear circuit elements
    		for (i = 0; i != elmList.size(); i++) {
    		    CircuitElm ce = getElm(i);
    		    ce.stamp();
    		}

    		// determine nodes that are not connected indirectly to ground
    		boolean closure[] = new boolean[nodeList.size()];
    		boolean changed = true;
    		closure[0] = true;
    		
    		while (changed) {
    		    changed = false;
    		    for (i = 0; i != elmList.size(); i++) {
    			CircuitElm ce = getElm(i);
    			if (ce instanceof WireElm)
    			    continue;
    			// loop through all ce's nodes to see if they are connected
    			// to other nodes not in closure
    			for (j = 0; j < ce.getConnectionNodeCount(); j++) {
    			    if (!closure[ce.getConnectionNode(j)]) {
    				if (ce.hasGroundConnection(j))
    				    closure[ce.getConnectionNode(j)] = changed = true;
    				continue;
    			    }
    			    int k;
    			    for (k = 0; k != ce.getConnectionNodeCount(); k++) {
    				if (j == k)
    				    continue;
    				int kn = ce.getConnectionNode(k);
    				if (ce.getConnection(j, k) && !closure[kn]) {
    				    closure[kn] = true;
    				    changed = true;
    				}
    			    }
    			}
    		    }
    		    if (changed)
    			continue;

    		    // connect one of the unconnected nodes to ground with a big resistor, then try again
    		    for (i = 0; i != nodeList.size(); i++)
    			if (!closure[i] && !getCircuitNode(i).internal) {
    			    //console("node " + i + " unconnected");
    			    stampResistor(0, i, 1e8);
    			    closure[i] = true;
    			    changed = true;
    			    break;
    			}
    		}

    	for (i = 0; i != elmList.size(); i++) {
    		    CircuitElm ce = getElm(i);
    		    
    		    // look for inductors with no current path
    		    
    		    /*
    		    if (ce instanceof InductorElm) {
    			FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
    							    ce.getNode(1));
    			// first try findPath with maximum depth of 5, to avoid slowdowns
    			if (!fpi.findPath(ce.getNode(0), 5) &&
    			    !fpi.findPath(ce.getNode(0))) {
//    			    console(ce + " no path");
    			    ce.reset();
    			}
    		    }
    		    
    		    */
    		    
    		    
    		    // look for current sources with no current path
    		    /*
    		    if (ce instanceof CurrentElm) {
    			CurrentElm cur = (CurrentElm) ce;
    			FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
    							    ce.getNode(1));
    			// first try findPath with maximum depth of 5, to avoid slowdowns
    			if (!fpi.findPath(ce.getNode(0), 5) &&
    			    !fpi.findPath(ce.getNode(0))) {
    			    cur.stampCurrentSource(true);
    			} else
    			    cur.stampCurrentSource(false);
    		    }
    		    */
    		    
    		    
    		  /*  
    		    if (ce instanceof VCCSElm) {
    			VCCSElm cur = (VCCSElm) ce;
    			FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
    							    cur.getOutputNode(0));
    			if (cur.hasCurrentOutput() && !fpi.findPath(cur.getOutputNode(1))) {
    			    cur.broken = true;
    			} else
    			    cur.broken = false;
    		    }
    		    */
    		    
    		    
    		    
    		    // look for voltage source or wire loops.  we do this for voltage sources or wire-like elements (not actual wires
    		    // because those are optimized out, so the findPath won't work)
    		    
    		    if (ce.getPostCount() == 2) {
    			if (ce.isWire() && !(ce instanceof WireElm)) {
    			    FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce,
    							    ce.getNode(1));
    			    if (fpi.findPath(ce.getNode(0))) {
    				//stop("Voltage source/wire loop with no resistance!", ce);
    				return;
    			    }
    			}
    		    }
    		    
    		    
    		    // look for path from rail to ground
    		    /*
    		    if (ce instanceof RailElm) {
    			FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce,
    				    ce.getNode(0));
    			if (fpi.findPath(0)) {
    			    stop("Path to ground with no resistance!", ce);
    			    return;
    			}
    		    }
    		    */
    		    
    		    
    		    // look for shorted caps, or caps w/ voltage but no R
    		    /*
    		    if (ce instanceof CapacitorElm) {
    			FindPathInfo fpi = new FindPathInfo(FindPathInfo.SHORT, ce,
    							    ce.getNode(1));
    			if (fpi.findPath(ce.getNode(0))) {
    			    console(ce + " shorted");
    			    ce.reset();
    			} else {
    			    // a capacitor loop used to cause a matrix error. but we changed the capacitor model
    			    // so it works fine now. The only issue is if a capacitor is added in parallel with
    			    // another capacitor with a nonzero voltage; in that case we will get oscillation unless
    			    // we reset both capacitors to have the same voltage. Rather than check for that, we just
    			    // give an error.
    			    fpi = new FindPathInfo(FindPathInfo.CAP_V, ce, ce.getNode(1));
    			    if (fpi.findPath(ce.getNode(0))) {
    				stop("Capacitor loop with no resistance!", ce);
    				return;
    			    }
    			}
    		    }
    		    */
    		    
    		}
    		
    		if (!simplifyMatrix(matrixSize)) {return;}
    		    
    		

    		// check if we called stop()
    		if (circuitMatrix == null)
    		    return;
    		
    		// if a matrix is linear, we can do the lu_factor here instead of
    		// needing to do it every frame
    		if (!circuitNonLinear) {
    		    if (!lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
    			//stop("Singular matrix!", null);
    			return;
    		    }
    		}
    		
    		// show resistance in voltage sources if there's only one
    		//boolean gotVoltageSource = false;
    		//showResistanceInVoltageSources = true;
    		//for (i = 0; i != elmList.size(); i++) {
    		  //  CircuitElm ce = getElm(i);
    		    //if (ce instanceof VoltageElm) {
    			//if (gotVoltageSource)
    			   // showResistanceInVoltageSources = false;
    		//	else
    			  //  gotVoltageSource = true;
    		   // }
    	//	}

    	 
    	 
    } 
         
    void calculateWireClosure() {
    	
    		int i;
    		nodeMap = new HashMap<Point,NodeMapEntry>();
//    		int mergeCount = 0;
    		wireInfoList = new Vector<WireInfo>();
    		
    		for (i = 0; i != elmList.size(); i++) {
    			
    		    CircuitElm ce = getElm(i);
    		    
    		    if (!(ce instanceof WireElm))
    			continue;
    		    
    		    WireElm we = (WireElm) ce;
    		    we.hasWireInfo = false;
    		    wireInfoList.add(new WireInfo(we));
    		    NodeMapEntry cn  = nodeMap.get(ce.getPost(0));
    		    NodeMapEntry cn2 = nodeMap.get(ce.getPost(1));
    		    
    		    if (cn != null && cn2 != null) {
	    			// merge nodes; go through map and change all keys pointing to cn2 to point to cn
	    			for (Map.Entry<Point, NodeMapEntry> entry : nodeMap.entrySet()) {
	    			    if (entry.getValue() == cn2)
	    				entry.setValue(cn);
	    			}
	    			continue;
    		    }
    		    if (cn != null) {
	    			nodeMap.put(ce.getPost(1), cn);
	    			continue;
    		    }
    		    if (cn2 != null) {
	    			nodeMap.put(ce.getPost(0), cn2);
	    			continue;
    		    }
    		    // new entry
    		    cn = new NodeMapEntry();
    		    nodeMap.put(ce.getPost(0), cn);
    		    nodeMap.put(ce.getPost(1), cn);
    		}
    		
    }
    	
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    static void lu_solve(double a[][], int n, int ipvt[], double b[]) {
    	int i;

    	// find first nonzero b element
    	for (i = 0; i != n; i++) {
    	    int row = ipvt[i];

    	    double swap = b[row];
    	    b[row] = b[i];
    	    b[i] = swap;
    	    if (swap != 0)
    		break;
    	}
    	
    	int bi = i++;
    	
    	for (; i < n; i++) {
    	    int row = ipvt[i];
    	    int j;
    	    double tot = b[row];
    	    
    	    b[row] = b[i];
    	    // forward substitution using the lower triangular matrix
    	    for (j = bi; j < i; j++)
    		tot -= a[i][j]*b[j];
    	    b[i] = tot;
    	}
    	
    	for (i = n-1; i >= 0; i--) {
    	    double tot = b[i];
    	    
    	    // back-substitution using the upper triangular matrix
    	    int j;
    	    for (j = i+1; j != n; j++)
    		tot -= a[i][j]*b[j];
    	    b[i] = tot/a[i][i];
    	}
    	
    }  
     
    static boolean lu_factor(double a[][], int n, int ipvt[]) {
    		int i,j,k;
    		
    		// check for a possible singular matrix by scanning for rows that
    		// are all zeroes
    		for (i = 0; i != n; i++) { 
    		    boolean row_all_zeros = true;
    		    for (j = 0; j != n; j++) {
	    			if (a[i][j] != 0) {
	    			    row_all_zeros = false;
	    			    break;
	    			}
    		    }
    		    // if all zeros, it's a singular matrix
    		    if (row_all_zeros)
    			return false;
    		}
    		
    	        // use Crout's method; loop through the columns
    		for (j = 0; j != n; j++) {
    		    
    		    // calculate upper triangular elements for this column
    		    for (i = 0; i != j; i++) {
	    			double q = a[i][j];
	    			for (k = 0; k != i; k++)
	    			    q -= a[i][k]*a[k][j];
	    			a[i][j] = q;
    		    }

    		    // calculate lower triangular elements for this column
    		    double largest = 0;
    		    int largestRow = -1;
    		    
    		    for (i = j; i != n; i++) {
	    			double q = a[i][j];
	    			for (k = 0; k != j; k++)
	    			    q -= a[i][k]*a[k][j];
	    			a[i][j] = q;
	    			double x = Math.abs(q);
	    			if (x >= largest) {
	    			    largest = x;
	    			    largestRow = i;
	    			}
    		    }
    		    
    		    // pivoting
    		    if (j != largestRow) {
	    			double x;
	    			for (k = 0; k != n; k++) {
	    			    x = a[largestRow][k];
	    			    a[largestRow][k] = a[j][k];
	    			    a[j][k] = x;
	    			}
    		    }

    		    // keep track of row interchanges
    		    ipvt[j] = largestRow;

    		    // avoid zeros
    		    if (a[j][j] == 0.0) {
	    			a[j][j]=1e-18;
    		    }

    		    if (j != n-1) {
	    			double mult = 1.0/a[j][j];
	    			for (i = j+1; i != n; i++)
	    			    a[i][j] *= mult;
    		    }
    		}
    		return true;
    }

    void updateVoltageSource(int n1, int n2, int vs, double v) {
    		int vn = nodeList.size()+vs;
    		stampRightSide(vn, v);
    }

    // simplify the matrix; this speeds things up quite a bit, especially for digital circuits
    boolean simplifyMatrix(int matrixSize) {
	 	
    	int i, j;
	 	for (i = 0; i != matrixSize; i++) {
	 		
	 	    int qp = -1;
	 	    double qv = 0;
	 	    RowInfo re = circuitRowInfo[i];
	// 	    if (qp != -100) continue;   // uncomment to disable matrix simplification
	 	    
	 	    if (re.lsChanges || re.dropRow || re.rsChanges)
	 		continue;
	 	    double rsadd = 0;
	
	 	    // look for rows that can be removed
	 	    for (j = 0; j != matrixSize; j++) {
		 		double q = circuitMatrix[i][j];
		 		if (circuitRowInfo[j].type == RowInfo.ROW_CONST) {
		 		    // keep a running total of const values that have been
		 		    // removed already
		 		    rsadd -= circuitRowInfo[j].value*q;
		 		    continue;
		 		}
		 		// ignore zeroes
		 		if (q == 0)
		 		    continue;
		 		// keep track of first nonzero element that is not ROW_CONST
		 		if (qp == -1) {
		 		    qp = j;
		 		    qv = q;
		 		    continue;
		 		}
		 		// more than one nonzero element?
		 		break;
	 	    }
	 	    
	 	    if (j == matrixSize) {
	 	    	
		 		if (qp == -1) {
		 		    // probably a singular matrix, try disabling matrix simplification above to check this
		 		    //stop("Matrix error", null);
		 		    return false;
		 		}
		 		RowInfo elt = circuitRowInfo[qp];
		 		
		 		// we found a row with only one nonzero nonconst entry; that value
		 		// is a constant
		 		if (elt.type != RowInfo.ROW_NORMAL) {
		 	
		 		    continue;
		 		}
		 		
		 		elt.type = RowInfo.ROW_CONST;
		 		elt.value = (circuitRightSide[i]+rsadd)/qv;
		 		circuitRowInfo[i].dropRow = true;
		 		i = -1; // start over from scratch
	 	    }
	 	}
	
	 	// find size of new matrix
	 	int nn = 0;
	 	for (i = 0; i != matrixSize; i++) {
	 	    RowInfo elt = circuitRowInfo[i];
	 	    if (elt.type == RowInfo.ROW_NORMAL) {
	 		elt.mapCol = nn++;
	 		
	 		continue;
	 	    }
	 	    if (elt.type == RowInfo.ROW_CONST)
	 		elt.mapCol = -1;
	 	}
	
	 	// make the new, simplified matrix
	 	int newsize = nn;
	 	double newmatx[][] = new double[newsize][newsize];
	 	double newrs  []   = new double[newsize];
	 	int ii = 0;
	 	for (i = 0; i != matrixSize; i++) {
	 	    RowInfo rri = circuitRowInfo[i];
	 	    if (rri.dropRow) {
	 		rri.mapRow = -1;
	 		continue;
	 	    }
	 	    newrs[ii] = circuitRightSide[i];
	 	    rri.mapRow = ii;
	 	    
	 	    for (j = 0; j != matrixSize; j++) {
	 		RowInfo ri = circuitRowInfo[j];
	 		if (ri.type == RowInfo.ROW_CONST)
	 		    newrs[ii] -= ri.value*circuitMatrix[i][j];
	 		else
	 		    newmatx[ii][ri.mapCol] += circuitMatrix[i][j];
	 	    }
	 	    ii++;
	 	}
	
	// 	console("old size = " + matrixSize + " new size = " + newsize);
	 	
	 	circuitMatrix = newmatx;
	 	circuitRightSide = newrs;
	 	matrixSize = circuitMatrixSize = newsize;
	 	for (i = 0; i != matrixSize; i++)
	 	    origRightSide[i] = circuitRightSide[i];
	 	for (i = 0; i != matrixSize; i++)
	 	    for (j = 0; j != matrixSize; j++)
	 		origMatrix[i][j] = circuitMatrix[i][j];
	 	circuitNeedsMap = true;
	 	return true;
    }
     
    public CircuitNode getCircuitNode(int n) {
    		if (n >= nodeList.size()) {return null;}
    		return nodeList.elementAt(n);
    }
     
    void makePostDrawList() {
    	
    		postDrawList = new Vector<Point>();
    		badConnectionList = new Vector<Point>();
    		
    		for (Map.Entry<Point, Integer> entry : postCountMap.entrySet()) {
    		    
    			if (entry.getValue() != 2) {
    		    	postDrawList.add(entry.getKey());
    			}
    		    
    		    // look for bad connections, posts not connected to other elements which intersect
    		    // other elements' bounding boxes
    		    if (entry.getValue() == 1) {
	    			int j;
	    			boolean bad = false;
	    			Point cn = entry.getKey();
	    			
	    			for (j = 0; j != elmList.size() && !bad; j++) {
	    			    CircuitElm ce = getElm(j);
	    			    //if ( ce instanceof GraphicElm )
	    				//continue;
	    			    // does this post intersect elm's bounding box?
	    			    if (!ce.boundingBox.contains(cn.x, cn.y))
	    				continue;
	    			    int k;
	    			    // does this post belong to the elm?
	    			    int pc = ce.getPostCount();
	    			    for (k = 0; k != pc; k++)
	    				if (ce.getPost(k).equals(cn))
	    				    break;
	    			    if (k == pc)
	    				bad = true;
	    			}
	    			
	    			if (bad) {
	    			    badConnectionList.add(cn);
	    			}
    			
    		    }
    		}
    		postCountMap = null;
    }
     
    void stampResistor(int n1, int n2, double r) {
    		double r0 = 1/r;
    		if (Double.isNaN(r0) || Double.isInfinite(r0)) {
    		    int a = 0;
    		    a /= a;
    		}
    		stampMatrix(n1, n1, r0);
    		stampMatrix(n2, n2, r0);
    		stampMatrix(n1, n2, -r0);
    		stampMatrix(n2, n1, -r0);
    }
     
    // use this if the amount of voltage is going to be updated in doStep(), by updateVoltageSource()
    void stampVoltageSource(int n1, int n2, int vs) {
	 	int vn = nodeList.size()+vs;
	 	stampMatrix(vn, n1, -1);
	 	stampMatrix(vn, n2, 1);
	 	stampRightSide(vn);
	 	stampMatrix(n1, vn, 1);
	 	stampMatrix(n2, vn, -1);
    }
     
    // stamp independent voltage source #vs, from n1 to n2, amount v
    void stampVoltageSource(int n1, int n2, int vs, double v) {
	 	int vn = nodeList.size()+vs;
	 	stampMatrix(vn, n1, -1);
	 	stampMatrix(vn, n2, 1);
	 	stampRightSide(vn, v);
	 	stampMatrix(n1, vn, 1);
	 	stampMatrix(n2, vn, -1);
    }
     
    void stampMatrix(int i, int j, double x) {
    	if (i > 0 && j > 0) {
    		
    		    if (circuitNeedsMap) {
	    			i = circuitRowInfo[i-1].mapRow;
	    			RowInfo ri = circuitRowInfo[j-1];
	    			
	    			if (ri.type == RowInfo.ROW_CONST) {
		    			    
		    			    circuitRightSide[i] -= x*ri.value;
		    			    return;
	    			}
	    			
	    			j = ri.mapCol;

    		    } 
    		    else {
	    			i--;
	    			j--;
    		    }
    		    circuitMatrix[i][j] += x;
    	}	
    }
     
    // stamp value x on the right side of row i, representing an
    // independent current source flowing into node i
    void stampRightSide(int i, double x) {
	 	if (i > 0) {
	 	    if (circuitNeedsMap) {
	 	    	i = circuitRowInfo[i-1].mapRow;
	 	    } 
	 	    else {
	 	    	i--;
	 	    }
	 	    circuitRightSide[i] += x;
	 	}
    }
     
    // indicate that the value on the right side of row i changes in doStep()
    void stampRightSide(int i) {
	 	if (i > 0) {
	 		circuitRowInfo[i-1].rsChanges = true;
	 	}
    }
     
     
// *****************************************************************
//  BEHAVIOUR
        
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
       
    
// *****************************************************************
//  MOUSE EVENTS
    
	@Override
	public void onMouseWheel(MouseWheelEvent e) {
		e.preventDefault();
		zoomCircuit(e.getDeltaY());
	}

	@Override
	public void onMouseUp(MouseUpEvent e) {
		e.preventDefault();
		if (e.getNativeButton()==NativeEvent.BUTTON_RIGHT) {return;}
		dragging = false;
	}
	
	@Override
	public void onMouseDown(MouseDownEvent e) {
		e.preventDefault();
	
		if (e.getNativeButton() != NativeEvent.BUTTON_LEFT && 
			e.getNativeButton() != NativeEvent.BUTTON_MIDDLE
			)
	    	{return;}
		
		CircuitElm newMouseElm=null;
		int sx = e.getX();
    	int sy = e.getY();
    	int gx = inverseTransformX(sx);
    	int gy = inverseTransformY(sy);
				
		if(e.getNativeButton()==NativeEvent.BUTTON_MIDDLE) {
			dragging = true;
		}
		
		if(e.getNativeButton()==NativeEvent.BUTTON_LEFT) {
			
			if (mouseElm != null && mouseElm.getHandleGrabbedClose(gx, gy, POSTGRABSQ, MINPOSTGRABSIZE)>=0) {
	    		newMouseElm = mouseElm;
	    	} else { 
	    		
	    		int bestDist = 100000000;
	    		int bestArea = 100000000;
	    		for (int i = 0; i != elmList.size(); i++) {
	    			CircuitElm ce = getElm(i);
	    			if (ce.boundingBox.contains(gx, gy)) {
	    				int j;
	    				int area = ce.boundingBox.width * ce.boundingBox.height;
	    				int jn = ce.getPostCount();
	    				if (jn > 2)
	    					jn = 2;
	    				for (j = 0; j != jn; j++) {
	    					Point pt = ce.getPost(j);
	    					int dist = Graphics.distanceSq(gx, gy, pt.x, pt.y);

	    					// if multiple elements have overlapping bounding boxes,
	    					// we prefer selecting elements that have posts close
	    					// to the mouse pointer and that have a small bounding
	    					// box area.
	    					if (dist <= bestDist && area <= bestArea) {
	    						bestDist = dist;
	    						bestArea = area;
	    						newMouseElm = ce;
	    					}
	    				}
	    				// prefer selecting elements that have small bounding box area (for
	    				// elements with no posts)
	    				if (ce.getPostCount() == 0 && area <= bestArea) {
	    				    newMouseElm = ce;
	    				    bestArea = area;
	    				}
	    			}
	    		}	
	    		
	    	}
		
			setMouseElm(newMouseElm);
		}
		
		dragScreenX = e.getX();
	 	dragScreenY = e.getY();
	 	
	}
	
	public void onMouseMove(MouseMoveEvent e) {
		e.preventDefault();
		
		if(dragging == true) {
			dragAll(e.getX(), e.getY());
		}
		
	}
	
	void dragAll (int x, int y) {
		
    	int dx = x-dragScreenX;
    	int dy = y-dragScreenY;
    	if (dx == 0 && dy == 0) {return;}
    	transform[4] += dx;
    	transform[5] += dy;
    	dragScreenX = x;
    	dragScreenY = y;
		
	}
	
    void setMouseElm(CircuitElm ce) {
    	if (ce!=mouseElm) {
    		if (mouseElm!=null)
    			mouseElm.setMouseElm(false);
    		if (ce!=null)
    			ce.setMouseElm(true);
    		mouseElm=ce;
    	}
    	if (mouseElm != null && (mouseElm instanceof SwitchElm)) {
    		SwitchElm se = (SwitchElm) mouseElm;
    		se.toggle();
    	}
    }
	
    
// *****************************************************************
//  TOOLS    
    
    int getrand(int x) {
		int q = random.nextInt();
		if (q < 0)
			q = -q;
		return q % x;
	}
       
    static SafeHtml LSHTML(String s) { return SafeHtmlUtils.fromTrustedString(s); }

    public Rectangle getCircuitBounds() {
    	
    	int i;
    	int minx = 1000, maxx = 0, miny = 1000, maxy = 0;
    	
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		miny = min(ce.y, min(ce.y2, miny));
    		maxy = max(ce.y, max(ce.y2, maxy));
    	}
    	
    	if (minx > maxx) {return null;}
    	
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
		if (n >= elmList.size()){return null;}
		return elmList.elementAt(n);
    }
    
}
