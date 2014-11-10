package com.propertyforcast.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.javadocmd.simplelatlng.LatLng;
import com.propertyforcast.model.Agent;
import com.propertyforcast.model.Country;
import com.propertyforcast.model.Listing;
import com.propertyforcast.model.PagenatedIterable;
import com.propertyforcast.model.PaginationDetails;
import com.propertyforcast.model.PriceChange;
import com.propertyforcast.model.PriceModifier;
import com.propertyforcast.model.Property;
import com.propertyforcast.model.PropertyType;
import com.propertyforcast.model.Status;
import com.propertyforcast.time.Clock;

public class RestZooplaDao implements ZooplaDao {

	private static final String ZOOPLA_API_GET_PROPERTY_LISTINGS_BY_POSTCODE = "http://api.zoopla.co.uk/api/v1/property_listings.js?postcode=";
	private static final String API_KEY_PARAM = "&api_key=";
	private final CloseableHttpClient client;
	private final String apiKeyParam;
	private final JsonParser parser;
	private final Clock clock;
	
	public RestZooplaDao(CloseableHttpClient client, String apiKey, Clock clock){
		this.client = client;
		this.apiKeyParam = API_KEY_PARAM+apiKey;
		this.parser = new JsonParser();
		this.clock = clock;
	}
	
	public PagenatedIterable<Listing> getListings(String postcode, double radius, PaginationDetails pagenationDetails) throws IOException{
		
		HttpGet getter = new HttpGet(ZOOPLA_API_GET_PROPERTY_LISTINGS_BY_POSTCODE+postcode+apiKeyParam+"&include_sold=1&include_rented=1&page_number="+pagenationDetails.getPageNum()+"&page_size="+pagenationDetails.getPageSize()+"&radius="+radius);
		
		CloseableHttpResponse response = client.execute(getter);
		
		if (response.getStatusLine().getStatusCode() >= 300){
			throw new RuntimeException("Unable to fetch listings from zoopla: "+response.getStatusLine().getReasonPhrase());
		}
		
		JsonElement root = parser.parse(new InputStreamReader(response.getEntity().getContent()));
		
		
		JsonElement res = root.getAsJsonObject().get("response");
		
		int resultSetSize = res.getAsJsonObject().get("result_count").getAsInt();
		
		Iterable<Listing> listings = getListings(res.getAsJsonObject().get("listing").getAsJsonArray());
		
		return new PagenatedIterable<Listing>(listings, resultSetSize, pagenationDetails);
	}

	private Iterable<Listing> getListings(JsonArray listings) {
		
		Collection<Listing> listingCollection = new ArrayList<Listing>(listings.size());
		
		for (JsonElement listing: listings){
			listingCollection.add(getListing(listing.getAsJsonObject()));
		}
		
		return listingCollection;
	}

	private Listing getListing(JsonObject listing) {
		
		Property property = getProperty(listing);
		Agent agent = getAgent(listing);
		
		int id = listing.get("listing_id").getAsInt();
		Country country = Country.valueOf(listing.get("country").getAsString().toUpperCase());
		String county = listing.get("county").getAsString();
		String description = listing.get("description").getAsString();
		String shortDescription = listing.get("short_description").getAsString();
		String detailsUrl = listing.get("details_url").getAsString();
		String firstDateStr = listing.get("first_published_date").getAsString();
		String lastDateStr = listing.get("last_published_date").getAsString();
		
		try{
			Date firstPublishedDate = Util.DATE_FORMAT_INSTANCE.parse(firstDateStr);
			Date lastPublishedDate = Util.DATE_FORMAT_INSTANCE.parse(lastDateStr);
		
			String imageCaption = listing.get("image_caption").getAsString();
			String imageUrl = listing.get("image_url").getAsString();
		
			Status status = Status.forString(listing.get("status").getAsString());
		
			LatLng latLong = new LatLng(listing.get("latitude").getAsDouble(), listing.get("longitude").getAsDouble());
		
			double price = listing.get("price").getAsDouble();
			PriceModifier modifier = PriceModifier.forString(listing.get("price_modifier").getAsString());
		
			String thumbnailUrl = listing.get("thumbnail_url").getAsString();
			
			Iterable<PriceChange> priceChanges = getPriceChanges(listing.get("price_change").getAsJsonArray());
		
			return new Listing(id, agent, country, county, description, shortDescription, detailsUrl, firstPublishedDate, lastPublishedDate, imageCaption, imageUrl, latLong, status, property, price, modifier, thumbnailUrl, priceChanges, clock.getCurrentTime());
		} catch (ParseException e){
			throw new RuntimeException("unable to parse date", e);
		}
	}

	private Iterable<PriceChange> getPriceChanges(JsonArray changes) throws ParseException {
		Collection<PriceChange> priceChanges = new ArrayList<PriceChange>();
		
		for (JsonElement change: changes){
			
			double price = change.getAsJsonObject().get("price").getAsDouble();
			Date date = Util.DATE_FORMAT_INSTANCE.parse(change.getAsJsonObject().get("date").getAsString());
			
			priceChanges.add(new PriceChange(date, price));
		}
		
		return priceChanges;
	}

	private Agent getAgent(JsonObject listing) {
		
		String address = listing.get("agent_address").getAsString();
		String logo = listing.get("agent_logo").getAsString();
		String name = listing.get("agent_name").getAsString();
		String phone = listing.get("agent_phone").getAsString();
		
		return new Agent(address, logo, name, phone);
	}

	private Property getProperty(JsonObject listing) {
		
		int numBathrooms = listing.get("num_bathrooms").getAsInt();
		int numBedrooms = listing.get("num_bedrooms").getAsInt();
		int numFloors = listing.get("num_floors").getAsInt();
		int numReceptions = listing.get("num_recepts").getAsInt();
		String streetName = listing.get("street_name").getAsString();
		String outcode = listing.get("outcode").getAsString();
		String postTown = listing.get("post_town").getAsString();
		PropertyType type = PropertyType.forString(listing.get("property_type").getAsString());
		String displayableAddress = listing.get("displayable_address").getAsString();
		boolean newHome = false;
		
		if (listing.has("new_home")){
			newHome = listing.get("new_home").getAsBoolean();
		}
		
		return new Property(numBathrooms, numBedrooms, numFloors, numReceptions, streetName, outcode, postTown, type, displayableAddress, newHome);
		
	}
}
