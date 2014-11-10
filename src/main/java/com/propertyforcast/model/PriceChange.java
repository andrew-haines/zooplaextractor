package com.propertyforcast.model;

import java.util.Date;

public class PriceChange {

	private final Date date;
	private final double price;
	
	public PriceChange(Date date, double price){
		this.date = date;
		this.price = price;
	}

	public Date getDate() {
		return date;
	}

	public double getPrice() {
		return price;
	}
}
