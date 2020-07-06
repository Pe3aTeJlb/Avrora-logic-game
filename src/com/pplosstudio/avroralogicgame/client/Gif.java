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

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

public class Gif extends Image {
	
	public String litera;
	public int frameCount;
	public int currFrame;
	public Image img;
	public boolean gifEnded = false;
	public boolean isPlaying = false;
	
	Gif(String lit, int fCount){
		litera = lit;
		frameCount = fCount;
		currFrame = 1;
		String tmp =  "Images/" + litera + currFrame + ".png";
		img = new Image(tmp);
		
	}
	
	final Timer timer = new Timer() {
		
        public void run() {
        	currFrame += 1;
	        if (currFrame <= frameCount) {
	        	String tmp =  "Images/" + litera + currFrame + ".png";
				img.setUrl(tmp);
	        }else {
	        	cancel();
	        	gifEnded = true;
	        }

        }
        
    };

	
	public void Play(int frameDelay) {
		
		if(!isPlaying) {
			isPlaying = true;
			gifEnded = false;
			timer.scheduleRepeating(frameDelay);
		}
		
	}
	
}
