package com.propertyforcast.model;

public enum Status {

	FOR_SALES("for_sale"),
	SALE_UNDER_OFFER("sale_under_offer"),
	SOLD("sold"),
	TO_RENT("to_rent"),
	RENT_UNDER_OFFER("rent_under_offer"),
	RENTED("rented");
	
	private final String statusId;
	
	private Status(String statusId){
		this.statusId = statusId;
	}

	public String getStatusId() {
		return statusId;
	}
	
	public static Status forString(String statusId){
		for (Status status: values()){
			if (status.getStatusId().equalsIgnoreCase(statusId)){
				return status;
			}
		}
		throw new IllegalArgumentException("Unknown type for id: "+statusId);
	}
}
