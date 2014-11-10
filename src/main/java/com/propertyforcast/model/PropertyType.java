package com.propertyforcast.model;

public enum PropertyType {

	TERRACED("Terraced"),
	END_OF_TERRACE("End of terrace"),
	SEMI_DETACHED("Semi-detached"),
	DETACHED("Detached"),
	MEWS_HOUSE("Mews house"),
	FLAT("flat"),
	MAISONETTE("Maisonette"),
	BUNGALOW("Bungalow"),
	TOWN_HOUSE("Town house"),
	COTTAGE("Cottage"),
	FARM_BARN("Farm/Barn"),
	MOBILE_STATIC("Mobile/static"),
	LAND("Land"),
	STUDIO("Studio"),
	BLOCK_OF_FLATS("Block of flats"),
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
