/*    
Copyright (C) Pplos Studio
    
    This file is a part of Avrora Logic Game, which based on CircuitJS1
    https://github.com/Pe3aTeJlb/Avrora-logic-game
    
    CircuitJS1 was originally written by Paul Falstad.
	http://www.falstad.com/

	JavaScript conversion by Iain Sharp.
	http://lushprojects.com/
    
    Avrora Logic Game is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 1, 2 of the License, or
    (at your option) any later version.
    Avrora Logic Game is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License 
    along with Avrora Logic Game.  If not, see <http://www.gnu.org/licenses/>.
*/

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
	
	 
	  menu = new Menu();
	  menu.init();
	  
	  Window.addResizeHandler(new ResizeHandler() {
	    	 
		  public void onResize(ResizeEvent event)
          {               
			  menu.setCanvasSize();
          }
      });
	  
	 /*
	  mysim = new CirSim();
	  mysim.init();

	    Window.addResizeHandler(new ResizeHandler() {
	    	 
            public void onResize(ResizeEvent event)
            {               
            	mysim.setCanvasSize();
            }
        });
        */
		   
  }

}
