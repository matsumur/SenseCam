package org.garacter.sensecam;

public class SensorData {
	
	public String time;
	public Tuple3f accelerometer;
	public Tuple3f gyroscope;
	
	public SensorData(String time, Tuple3f accelerometer, Tuple3f gyroscope){
		
		this.time = time;
		this.accelerometer = accelerometer;
		this.gyroscope = gyroscope;
		
	}
}
