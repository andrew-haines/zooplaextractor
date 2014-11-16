package com.propertyforcast.extractor.zoopla;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Provider;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.propertyforcast.model.Listing;

public class ExistingListingPredicateProvider implements Provider<Predicate<Listing>>{

	private final static Logger LOG = LoggerFactory.getLogger(ExistingListingPredicateProvider.class);
	private final Path storeLocation;
	
	public ExistingListingPredicateProvider(Path storeLocation){
		this.storeLocation = storeLocation;
	}
	
	@Override
	public Predicate<Listing> get() {
		try (InputStream stream = Files.newInputStream(storeLocation); 
				
				CSVParser parser = new CSVParser(new InputStreamReader(stream), CSVFormat.DEFAULT.withSkipHeaderRecord(true));
			){
			
			final Set<String> listingIds = new HashSet<String>();
			for (CSVRecord record: parser){
				listingIds.add(Integer.parseInt(record.get(0)) +"#"+record.get(32));
			}
			
			LOG.debug("Filtering: {} listings", listingIds.size());
			
			return  new Predicate<Listing>(){

				@Override
				public boolean apply(Listing input) {
					return !listingIds.contains(input.getId()+"#"+input.getPrice());
				}
				
			};
		} catch (IOException e) {
			throw new RuntimeException("Unable to load: "+storeLocation.getFileName(), e);
		}
	}

}
