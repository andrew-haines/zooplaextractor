package com.propertyforcast.model;

public class Property {

	private final int numBathrooms;
	private final int numBedrooms;
	private final int numFloors;
	private final int numReceptions;
	private final String streetName;
	private final String outcode;
	private final String postTown;
	private final PropertyType type;
	private final String displayableAddress;
	private final boolean isNewBuild;
	
	public Property(int numBathrooms, int numBedrooms, int numFloors,
			int numReceptions, String streetName, String outcode, String postTown,
			PropertyType type, String displayableAddress, boolean isNewBuild) {
		
		this.numBathrooms = numBathrooms;
		this.numBedrooms = numBedrooms;
		this.numFloors = numFloors;
		this.numReceptions = numReceptions;
		this.streetName = streetName;
		this.outcode = outcode;
		this.postTown = postTown;
		this.type = type;
		this.displayableAddress = displayableAddress;
		this.isNewBuild = isNewBuild;
	}
	public int getNumBathrooms() {
		return numBathrooms;
	}
	public int getNumBedrooms() {
		return numBedrooms;
	}
	public int getNumFloors() {
		return numFloors;
	}
	public int getNumReceptions() {
		return numReceptions;
	}
	public String getStreetName() {
		return streetName;
	}
	public String getOutcode() {
		return outcode;
	}
	public String getPostTown() {
		return postTown;
	}
	public PropertyType getType() {
		return type;
	}
	public String getDisplayableAddress() {
		return displayableAddress;
	}
	public boolean isNewBuild() {
		return isNewBuild;
	}
}
