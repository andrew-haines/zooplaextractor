package com.propertyforcast.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private final static Logger LOG = LoggerFactory.getLogger(RestZooplaDao.class);

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
	
	public PagenatedIterable<Listing> getListings(String postcode, double radius, PaginationDetails pagenationDetails) throws IOException, ZooplaDaoException{
		
		LOG.debug("calling zoopla for postcode: "+postcode+" radius: "+radius+" pageDetails: "+pagenationDetails);
		
		String apiRequestUrl = ZOOPLA_API_GET_PROPERTY_LISTINGS_BY_POSTCODE+postcode+apiKeyParam+"&include_sold=1&include_rented=1&page_number="+pagenationDetails.getPageNum()+"&page_size="+pagenationDetails.getPageSize()+"&radius="+radius;
		
		HttpGet getter = new HttpGet(apiRequestUrl);
		
		LOG.debug("Executing zoopla api call: "+apiRequestUrl);
		
		try(CloseableHttpResponse response = client.execute(getter)){
		
			if (response.getStatusLine().getStatusCode() >= 300){
				
				if (response.getStatusLine().getStatusCode() == 403){
					throw new ForbiddenZooplaDaoException("Zoopla api is forbidden.");
				}
				throw new ZooplaDaoException("Unable to fetch listings from zoopla: "+response.getStatusLine().getReasonPhrase());
			}
			
			LOG.debug("Recieved response");
			
			JsonElement root = parser.parse(new InputStreamReader(response.getEntity().getContent()));
			
			int resultSetSize = root.getAsJsonObject().get("result_count").getAsInt();
			
			Iterable<Listing> listings = getListings(root.getAsJsonObject().get("listing").getAsJsonArray(), postcode);
			
			return new PagenatedIterable<Listing>(listings, resultSetSize, pagenationDetails);
		}
	}

	private Iterable<Listing> getListings(JsonArray listings, String searchPostcode) {
		
		Collection<Listing> listingCollection = new ArrayList<Listing>(listings.size());
		
		for (JsonElement listing: listings){
			listingCollection.add(getListing(listing.getAsJsonObject(), searchPostcode));
		}
		
		return listingCollection;
	}
	
	private String getStringIfSet(JsonObject element, String tagName){
		
		String value = null;
		if (element.has(tagName) && !element.get(tagName).isJsonNull() && element.get(tagName).getAsString() != null){
			value = element.get(tagName).getAsString();
		}
		return value;
	}

	private Listing getListing(JsonObject listing, String searchPostcode) {
		
		Property property = getProperty(listing);
		Agent agent = getAgent(listing);
		
		int id = listing.get("listing_id").getAsInt();
		String countryStr = getStringIfSet(listing, "country");
		Country country = null;
		if (countryStr != null){
			country = Country.valueOf(countryStr.toUpperCase());
		}
		String county = getStringIfSet(listing, "county");
		String description = getStringIfSet(listing, "description");
		String shortDescription = getStringIfSet(listing, "short_description");
		String detailsUrl = getStringIfSet(listing, "details_url");
		String firstDateStr = getStringIfSet(listing, "first_published_date");
		String lastDateStr = getStringIfSet(listing, "last_published_date");
		
		try{
			Date firstPublishedDate = Util.DATE_FORMAT_INSTANCE.parse(firstDateStr);
			Date lastPublishedDate = Util.DATE_FORMAT_INSTANCE.parse(lastDateStr);
		
			String imageCaption = getStringIfSet(listing, "image_caption");
			String imageUrl = getStringIfSet(listing, "image_url");
		
			Status status = Status.forString(getStringIfSet(listing, "status"));
		
			LatLng latLong = null;
			
			if (!listing.get("latitude").isJsonNull() && !listing.get("longitude").isJsonNull()){
				latLong = new LatLng(listing.get("latitude").getAsDouble(), listing.get("longitude").getAsDouble());
			}
		
			double price = listing.get("price").getAsDouble();
			
			String priceModifierStr = getStringIfSet(listing, "price_modifier");
			
			PriceModifier modifier = null;
			if (priceModifierStr != null){
				modifier = PriceModifier.forString(priceModifierStr);
			}
		
			String thumbnailUrl = getStringIfSet(listing, "thumbnail_url");
			
			Iterable<PriceChange> priceChanges = Collections.emptyList();
			if (listing.has("price_change")){
				priceChanges = getPriceChanges(listing.get("price_change").getAsJsonArray());
			}
		
			return new Listing(id, agent, country, county, description, shortDescription, detailsUrl, firstPublishedDate, lastPublishedDate, imageCaption, imageUrl, latLong, status, property, price, modifier, thumbnailUrl, priceChanges, clock.getCurrentTime(), searchPostcode);
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
		
		String address = getStringIfSet(listing, "agent_address");
		String logo = getStringIfSet(listing, "agent_logo");
		String name = getStringIfSet(listing, "agent_name");
		String phone = getStringIfSet(listing, "agent_phone");
		
		return new Agent(address, logo, name, phone);
	}

	private Property getProperty(JsonObject listing) {
		
		int numBathrooms = listing.get("num_bathrooms").getAsInt();
		int numBedrooms = listing.get("num_bedrooms").getAsInt();
		int numFloors = listing.get("num_floors").getAsInt();
		int numReceptions = listing.get("num_recepts").getAsInt();
		String streetName = getStringIfSet(listing, "street_name");
		String outcode = listing.get("outcode").getAsString();
		String postTown = getStringIfSet(listing, "post_town");
		PropertyType type = null;
		if (listing.get("property_type") != null && !listing.get("property_type").getAsString().equals("")){
			type = PropertyType.forString(listing.get("property_type").getAsString());
		}
		String displayableAddress = getStringIfSet(listing, "displayable_address");
		boolean newHome = false;
		
		if (listing.has("new_home")){
			newHome = listing.get("new_home").getAsBoolean();
		}
		
		return new Property(numBathrooms, numBedrooms, numFloors, numReceptions, streetName, outcode, postTown, type, displayableAddress, newHome);
		
	}
}
