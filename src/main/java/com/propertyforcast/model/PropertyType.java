package com.propertyforcast.model;

public enum PropertyType {

	TERRACED("Terraced house"),
	END_OF_TERRACE("End terrace house"),
	SEMI_DETACHED("Semi-detached house"),
	DETACHED("Detached house"),
	MEWS_HOUSE("Mews house"),
	FLAT("flat"),
	MAISONETTE("Maisonette"),
	BUNGALOW("Bungalow"),
	TERRACED_BUNGALOW("Terraced bungalow"),
	TOWN_HOUSE("Town house"),
	COTTAGE("Cottage"),
	FARM_BARN("Farm/Barn"),
	MOBILE_STATIC("Mobile/static"),
	MOBILE_PARK_HOME("Mobile/park home"),
	BARN_FARM_HOUSE("Barn conversion/farmhouse"),
	BARN_CONVERSION("Barn conversion"),
	LINKED_DETACHED_HOUSE("Link-detached house"),
	LAND("Land"),
	STUDIO("Studio"),
	BLOCK_OF_FLATS("Block of flats"),
	PARKING_GARAGE("Parking/garage"),
	HOUSE_BOAT("Houseboat"),
	FARM("Farm"),
	RETAIL_PREMISES("Retail premises"),
	RESTURANT_CAFE("Restaurant/cafe"),
	FARMHOUSE("Farmhouse"),
	DETACHED_BUNGALOW("Detached bungalow"),
	SEMI_DETECHED_BUNGALOW("Semi-detached bungalow"),
	VILLA("Villa"),
	CHALET("Chalet"),
	OFFICE("Office");
	
	private final String typeId;
	
	private PropertyType(String typeId){
		this.typeId = typeId;
	}

	public String getTypeId() {
		return typeId;
	}
	
	public static PropertyType forString(String typeId){
		for (PropertyType type: values()){
			if (type.getTypeId().equalsIgnoreCase(typeId)){
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown type for id: "+typeId);
	}
}
