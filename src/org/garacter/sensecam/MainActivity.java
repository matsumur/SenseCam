package org.garacter.sensecam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;

import org.garacter.sensecam.R;

import android.app.ActionBar;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

public class MainActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener, SensorEventListener {
	
	private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    
    private float[] accelerometerData;
    private float[] gyroscopeData;
    private Queue<SensorData> sensorQueue = new LinkedList<SensorData>();
    
    private Timer timer_queue;
    
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final int SIZE_OF_QUEUE = 10000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		
		setContentView(R.layout.activity_main);
		System.out.println("onCreate.");
		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_section1),
								getString(R.string.title_section2) }), this);
		
		//data
		accelerometerData 	= new float[3];
		gyroscopeData 		= new float[3];
		
		//sensor
    	mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope	   = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_GAME);
        
        timer_queue = new Timer();
        timer_queue.scheduleAtFixedRate(new OfferData2QueueTask(this), 10, 10);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause.");
    }
	
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume.");
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		mSensorManager.unregisterListener(this);
		mSensorManager.unregisterListener(this);
		cancelTimer();
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		/*
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		return true;
		*/
		
		System.out.println(position);
		if(position==1){
			Intent intent = new Intent(MainActivity.this, SubActivity.class);
			startActivity(intent);
			ActionBar actionbar = getActionBar();
			actionbar.setSelectedNavigationItem(0);
			
		}
		
		return true;
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			float axisX = event.values[0];
			float axisY = event.values[1];
			float axisZ = event.values[2];
			
			ProgressBar aX = (ProgressBar) findViewById(R.id.ProgressBarAccX);
			ProgressBar aY = (ProgressBar) findViewById(R.id.ProgressBarAccY);
			ProgressBar aZ = (ProgressBar) findViewById(R.id.ProgressBarAccZ);
			aX.setProgress((int)Math.abs(axisX*100));
			aY.setProgress((int)Math.abs(axisY*100));
			aZ.setProgress((int)Math.abs(axisZ*100));
			
			accelerometerData[0] = axisX;
			accelerometerData[1] = axisY;
			accelerometerData[2] = axisZ;
			
		}else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
			float axisX = event.values[0];
			float axisY = event.values[1];
			float axisZ = event.values[2];
			
			ProgressBar gX = (ProgressBar) findViewById(R.id.ProgressBarGyroX);
			ProgressBar gY = (ProgressBar) findViewById(R.id.ProgressBarGyroY);
			ProgressBar gZ = (ProgressBar) findViewById(R.id.ProgressBarGyroZ);
			
			gX.setProgress((int)Math.abs(axisX*180/Math.PI));
			gY.setProgress((int)Math.abs(axisY*180/Math.PI));
			gZ.setProgress((int)Math.abs(axisZ*180/Math.PI));
			
			gyroscopeData[0] = axisX;
			gyroscopeData[1] = axisY;
			gyroscopeData[2] = axisZ;
			
		}
	}
	
	private void cancelTimer() {
		if (timer_queue != null) 
			timer_queue.cancel();
		
	}
	
	private String makeDateString(){
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        
		return sdf.format(now);
	}
	
	public void offer2queue(){
		
		//append accelerometer and gyroscope data to queues
		if(accelerometerData != null && gyroscopeData != null)
			sensorQueue.offer(new SensorData(
									makeDateString(), 
									new Tuple3f(accelerometerData[0], accelerometerData[1], accelerometerData[2]),
									new Tuple3f(gyroscopeData[0], gyroscopeData[1], gyroscopeData[2]) ) );
		
		// Limit the size of queue 
		while(sensorQueue.size() > SIZE_OF_QUEUE ){
			sensorQueue.poll();
		}
		//System.out.println(sensorQueue.size());
		
	}

}
