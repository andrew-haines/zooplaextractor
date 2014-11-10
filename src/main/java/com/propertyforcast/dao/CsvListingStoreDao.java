package com.propertyforcast.dao;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;

import com.google.common.collect.Iterables;
import com.propertyforcast.model.Listing;
import com.propertyforcast.model.PriceChange;

public class CsvListingStoreDao implements ListingStoreDao{
	
	private final OutputStream outStream;
	
	public CsvListingStoreDao(OutputStream outputStream){
		this.outStream = outputStream;
	}

	@Override
	public void store(Iterable<Listing> listings) throws IOException {
		StringBuilder builder = new StringBuilder();
        try(CSVPrinter printer = new CSVPrinter(builder, CSVFormat.DEFAULT.withSkipHeaderRecord(true))){
            for (Listing listing: listings){
            	Date latestPriceDate = (Iterables.isEmpty(listing.getPriceChanges()))?listing.getLastPublishedDate():listing.getFirstPublishedDate();
            	
            	for (PriceChange price: Iterables.concat(Arrays.asList(new PriceChange(latestPriceDate, listing.getPrice())), listing.getPriceChanges())){
	            	printer.print(listing.getId());
	            	printer.print(listing.getLatLong().getLatitude());
	            	printer.print(listing.getLatLong().getLongitude());
	            	printer.print(listing.getCountry().name());
	            	printer.print(listing.getCounty());
	            	printer.print(listing.getPropertyDetails().getDisplayableAddress());
	            	printer.print(listing.getPropertyDetails().getStreetName());
	            	printer.print(listing.getPropertyDetails().getPostTown());
	            	printer.print(listing.getPropertyDetails().getOutcode());
	            	printer.print(listing.getPropertyDetails().getType().name());
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
	            	printer.print(listing.getModifier());
	            	printer.print(listing.getShortDescription());
	            	printer.print(listing.getThumbnailUrl());
	            	printer.print(listing.getStatus());
	            	printer.print(Util.DATE_FORMAT_INSTANCE.format(listing.getFirstPublishedDate()));
	            	printer.print(Util.DATE_FORMAT_INSTANCE.format(listing.getLastPublishedDate()));
	            	printer.print(listing.getExtractionTime());
	            	printer.print(Util.DATE_FORMAT_INSTANCE.format(price.getDate()));
	            	printer.print(price.getPrice());
            	}
            }

            printer.println();

            IOUtils.write(builder.toString(), outStream);
        }
	}
}
