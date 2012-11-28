package org.garacter.sensecam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class RecordingOverlay extends View {
	private int width, height;  
	public boolean isRecording=false;
	
	public RecordingOverlay(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setRecording(boolean recording){
		this.isRecording = recording;
		this.invalidate();
	}
	
	@Override
	 protected void onSizeChanged(int width, int height, int width_old, int height_old){
	  // ビューのサイズを取得
	  this.width= width;
	  this.height= height;
	 }
	
	 @Override
	 protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(isRecording){
			canvas.drawColor(Color.TRANSPARENT);	
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setARGB(100, 255, 0, 0); 
			canvas.drawCircle(30, 30, 20, paint);
		}
	}

}
