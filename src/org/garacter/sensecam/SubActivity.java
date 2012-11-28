package org.garacter.sensecam;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Size;


public class SubActivity extends Activity 
						 implements SurfaceHolder.Callback, OnClickListener{

	private Camera camera;
	private SurfaceHolder holder;
	private RecordingOverlay overlay;
	private MediaRecorder recorder;
	private boolean isRecording=false;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surface_view);
		
		holder = surfaceView.getHolder();
		holder.addCallback(this);
		//holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		overlay = new RecordingOverlay(this); 
		
		addContentView(
				overlay,
				new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)); 
		
		surfaceView.setClickable(true);
		surfaceView.setOnClickListener(this);
		
		recorder = new MediaRecorder();
	}
	
	private void initRecorder() {
		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        CamcorderProfile camcorderProfile = CamcorderProfile
                .get(CamcorderProfile.QUALITY_HIGH);
        camcorderProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
        recorder.setProfile(camcorderProfile);
        
        File path = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_DOWNLOADS);
        
	    File file = new File(path, makeDateString()+".mp4");
        
	    System.out.println(file.getPath());
	    
        recorder.setOutputFile(file.getPath());
        recorder.setVideoFrameRate(30);
        recorder.setMaxDuration(10000); // 10 seconds
        recorder.setMaxFileSize(50000000); // Approximately 50 megabytes
	}
	
    private void prepareRecorder() {
        try {
        	recorder.setPreviewDisplay(holder.getSurface()); 
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

	@Override
	public void onPause(){
		super.onPause();
		finish();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(camera != null){
			Camera.Parameters params = camera.getParameters();
			List<Size> sizes = params.getSupportedPictureSizes();
			// See which sizes the camera supports and choose one of those
			Size mSize = sizes.get(0);
			params.setPictureSize(mSize.width, mSize.height);
			camera.setParameters(params);
			camera.startPreview();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		cameraOpen();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		camera.stopPreview();
		recorder.release();
		camera.release();
		
	}
	
	private void cameraOpen(){
		camera = Camera.open();
		try{
			camera.setPreviewDisplay(holder);
		} catch(Exception e){
			e.printStackTrace();
			finish();
		}
	}

	@Override
	public void onClick(View arg0) {		
		if(overlay.isRecording){
			recorder.stop();
			overlay.setRecording(false);
			cameraOpen();
			camera.startPreview();
		}else{
        	camera.stopPreview();
        	camera.release();
			initRecorder();
			prepareRecorder();
			recorder.start();
			overlay.setRecording(true);
		}
	}
	
	private String makeDateString(){
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        
		return sdf.format(now);
	}

}
