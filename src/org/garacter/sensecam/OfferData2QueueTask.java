package org.garacter.sensecam;

import java.util.TimerTask;

import android.os.Handler;


public class OfferData2QueueTask extends TimerTask {
	private Handler handler;
	private MainActivity parent;
	   
	public OfferData2QueueTask(MainActivity parent) {
		handler = new Handler();
		this.parent = parent;
	}
	
	@Override
	public void run() {
		parent.offer2queue();
	}

}
