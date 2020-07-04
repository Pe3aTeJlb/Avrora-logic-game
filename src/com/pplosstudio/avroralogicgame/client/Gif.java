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
