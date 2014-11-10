package com.propertyforcast.time;

public interface Clock {
	
	public final static Clock SYSTEM_CLOCK = new Clock(){

		@Override
		public long getCurrentTime() {
			return System.currentTimeMillis();
		}
		
	};

	long getCurrentTime();
}
