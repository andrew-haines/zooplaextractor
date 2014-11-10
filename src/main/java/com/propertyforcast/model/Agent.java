package com.propertyforcast.model;

public class Agent {

	private final String address;
	private final String logo;
	private final String name;
	private final String phone;
	
	public Agent(String address, String logo, String name, String phone) {
		this.address = address;
		this.logo = logo;
		this.name = name;
		this.phone = phone;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getLogo() {
		return logo;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPhone() {
		return phone;
	}
}
