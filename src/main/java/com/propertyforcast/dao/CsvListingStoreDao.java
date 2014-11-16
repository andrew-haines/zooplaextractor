package com.propertyforcast.dao;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Date;

import javax.inject.Provider;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.propertyforcast.model.Listing;
import com.propertyforcast.model.PriceChange;

public class CsvListingStoreDao implements ListingStoreDao{
	
	private static final Logger LOG = LoggerFactory.getLogger(CsvListingStoreDao.class);
	
	private final Path storeLocation;
	private final Provider<Predicate<Listing>> filterProvider;
	
	public CsvListingStoreDao(Path storeLocation, Provider<Predicate<Listing>> filterProvider){
		this.storeLocation = storeLocation;
		this.filterProvider = filterProvider;
	}

	@Override
	public void store(Iterable<Listing> listings) throws IOException {
		StringBuilder builder = new StringBuilder();
		Predicate<Listing> predicate = filterProvider.get();
		
		LOG.debug("Saving to store");
		
		listings = Iterables.filter(listings, predicate);
        try(CSVPrinter printer = new CSVPrinter(builder, CSVFormat.DEFAULT.withSkipHeaderRecord(true))){
            for (Listing listing: listings){
            	LOG.debug("Saving listing: {} to store",listing.getId());
            	Date latestPriceDate = (Iterables.isEmpty(listing.getPriceChanges()))?listing.getLastPublishedDate():listing.getFirstPublishedDate();
            	
            	for (PriceChange price: Iterables.concat(Arrays.asList(new PriceChange(latestPriceDate, listing.getPrice())), listing.getPriceChanges())){
	            	printer.print(listing.getId());
	            	printer.print(listing.getLatLong().getLatitude());
	            	printer.print(listing.getLatLong().getLongitude());
	            	if (listing.getCountry() != null){
	            		printer.print(listing.getCountry().name());
	            	} else{
	            		printer.print(null);
	            	}
	            	printer.print(listing.getCounty());
	            	printer.print(listing.getPropertyDetails().getDisplayableAddress());
	            	printer.print(listing.getPropertyDetails().getStreetName());
	            	printer.print(listing.getPropertyDetails().getPostTown());
	            	printer.print(listing.getPropertyDetails().getOutcode());
	            	if (listing.getPropertyDetails().getType() != null){
	            		printer.print(listing.getPropertyDetails().getType().name());
	            	} else{
	            		printer.print(null);
	            	}
	            	printer.print(listing.getPropertyDetails().getNumBathrooms());
	            	printer.print(listing.getPropertyDetails().getNumBedrooms());
	            	printer.print(listing.getPropertyDetails().getNumFloors());
	            	printer.print(listing.getPropertyDetails().getNumReceptions());
	            	printer.print(listing.getPropertyDetails().isNewBuild());
	            	printer.print(listing.getAgent().getAddress());
	            	printer.print(listing.getAgent().getLogo());
	            	printer.print(listing.getAgent().getName());
	            	printer.print(listing.getAgent().getPhone());
	            	printer.print(listing.getDetailsUrl());
	            	printer.print(listing.getDescription());
	            	printer.print(listing.getImageCaption());
	            	printer.print(listing.getImageUrl());
	            	if (listing.getModifier() != null){
	            		printer.print(listing.getModifier().name());
	            	} else{
	            		printer.print(null);
	            	}
	            	printer.print(listing.getShortDescription());
	            	printer.print(listing.getThumbnailUrl());
	            	printer.print(listing.getStatus());
	            	printer.print(listing.getSearchPostcode());
	            	printer.print(Util.DATE_FORMAT_INSTANCE.format(listing.getFirstPublishedDate()));
	            	printer.print(Util.DATE_FORMAT_INSTANCE.format(listing.getLastPublishedDate()));
	            	printer.print(listing.getExtractionTime());
	            	printer.print(Util.DATE_FORMAT_INSTANCE.format(price.getDate()));
	            	printer.print(price.getPrice());
	            	printer.println();
            	}
            }
            
            printer.flush();
        }
        try(OutputStream outStream = Files.newOutputStream(storeLocation, StandardOpenOption.APPEND, StandardOpenOption.WRITE, StandardOpenOption.CREATE)){

        	IOUtils.write(builder.toString(), outStream);
        
        	outStream.flush();
        	outStream.close();
        }
	}
}
