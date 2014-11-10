package com.propertyforcast.model;

import java.util.Date;

import com.javadocmd.simplelatlng.LatLng;

public class Listing {

	private final int id;
	private final Agent agent;
	private final Country country;
	private final String county;
	private final String description;
	private final String shortDescription;
	private final String detailsUrl;
	private final Date firstPublishedDate;
	private final Date lastPublishedDate;
	private final long extractionTime;
	private final String imageCaption;
	private final String imageUrl;
	private final LatLng latLong;
	private final Status status;
	private final Property propertyDetails;
	private final double price;
	private final PriceModifier modifier;
	private final String thumbnailUrl;
	private final Iterable<PriceChange> priceChanges;
	private final String searchPostcode;
	
	public Listing(int id, Agent agent, Country country, String county,
			String description, String shortDescription, String detailsUrl,
			Date firstPublishedDate,
			Date lastPublishedDate, String imageCaption, String imageUrl,
			LatLng latLong, Status status, Property propertyDetails,
			double price, PriceModifier modifier, String thumbnailUrl,
			Iterable<PriceChange> priceChanges,
			long extractionTime, String searchPostcode) {
		this.id = id;
		this.agent = agent;
		this.country = country;
		this.county = county;
		this.description = description;
		this.shortDescription = shortDescription;
		this.detailsUrl = detailsUrl;
		this.firstPublishedDate = firstPublishedDate;
		this.lastPublishedDate = lastPublishedDate;
		this.imageCaption = imageCaption;
		this.imageUrl = imageUrl;
		this.latLong = latLong;
		this.status = status;
		this.propertyDetails = propertyDetails;
		this.price = price;
		this.modifier = modifier;
		this.thumbnailUrl = thumbnailUrl;
		this.priceChanges = priceChanges;
		this.extractionTime = extractionTime;
		this.searchPostcode = searchPostcode;
	}
	
	public int getId() {
		return id;
	}
	public Agent getAgent() {
		return agent;
	}
	public Country getCountry() {
		return country;
	}
	public String getCounty() {
		return county;
	}
	public String getDescription() {
		return description;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public String getDetailsUrl() {
		return detailsUrl;
	}
	public Date getFirstPublishedDate() {
		return firstPublishedDate;
	}
	public Date getLastPublishedDate() {
		return lastPublishedDate;
	}
	public String getImageCaption() {
		return imageCaption;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public LatLng getLatLong() {
		return latLong;
	}
	public Status getStatus() {
		return status;
	}
	public Property getPropertyDetails() {
		return propertyDetails;
	}
	public double getPrice() {
		return price;
	}
	public PriceModifier getModifier() {
		return modifier;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public Iterable<PriceChange> getPriceChanges() {
		return priceChanges;
	}

	public long getExtractionTime() {
		return extractionTime;
	}

	public String getSearchPostcode() {
		return searchPostcode;
	}
}
