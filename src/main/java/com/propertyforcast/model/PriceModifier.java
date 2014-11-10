package com.propertyforcast.model;

public enum PriceModifier {

	OFFERS_OVER("offers_over"),
	POA("poa"),
	FIXED_PRICE("fixed_price"),
	FROM("from"),
	OFFERS_IN_REGION_OF("offers_in_region_of"),
	PART_BUY_PART_RENT("part_buy_part_rent"),
	PRICE_ON_REQUEST("price_on_request"),
	SHARED_EQUITY("shared_equity"),
	SHARED_OWNERSHIP("shared_ownership"),
	GUIDE_PRICE("guide_price"),
	SALE_BY_TENDER("sale_by_tender");
	
	private final String modifierId;
	
	private PriceModifier(String modifierId){
		this.modifierId = modifierId;
	}

	public String getModifierId() {
		return modifierId;
	}
	
	public static PriceModifier forString(String modifierId){
		for (PriceModifier modifier: values()){
			if (modifier.getModifierId().equalsIgnoreCase(modifierId)){
				return modifier;
			}
		}
		throw new IllegalArgumentException("Unknown type for id: "+modifierId);
	}
}
