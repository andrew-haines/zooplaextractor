package com.propertyforcast.extractor.zoopla;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.propertyforcast.dao.ListingStoreDao;
import com.propertyforcast.dao.ZooplaDao;
import com.propertyforcast.model.Listing;
import com.propertyforcast.model.PagenatedIterable;
import com.propertyforcast.model.PaginationDetails;
import com.propertyforcast.time.Clock;

public class ZooplaExtractor {

	private final ZooplaDao dao;
	private final ListingStoreDao store;
	private final Clock clock;
	private final double radius;
	
	public ZooplaExtractor(ZooplaDao dao, ListingStoreDao store, Clock clock, double radius){
		this.dao = dao;
		this.store = store;
		this.clock = clock;
		this.radius = radius;
	}
	
	public void extract(Iterable<String> postcodes) throws IOException{
		
		RateLimiter limiter = new RateLimiter(100.0, clock);
		for (final String postcode: postcodes){
			
			boolean finishedPostcodeExtraction = false;
			int pageNum = 0;
			int pageSize = 100;
			
			while(!finishedPostcodeExtraction){
				
				final int pageNumToUse = pageNum;
				final int pageSizeToUse = pageSize;
				try{
					PagenatedIterable<Listing> listings = limiter.limit(new Callable<PagenatedIterable<Listing>>(){
	
						@Override
						public PagenatedIterable<Listing> call() throws Exception {
							return dao.getListings(postcode, radius, new PaginationDetails(pageNumToUse, pageSizeToUse));
						}
					});
					
					store.store(listings);
					
					int currentFetchedRecords = (pageNum * pageSize);
					
					finishedPostcodeExtraction = currentFetchedRecords > listings.getTotalSize();
					
					pageNum++;
					
					pageSize = Math.min(listings.getTotalSize() - currentFetchedRecords, pageSize);
					
				} catch (Exception e){
					throw new RuntimeException("");
				}
			}
		}
	}
	
	private static class RateLimiter{
		
		private final long MILLISECONDS_IN_HOUR = 3600000;
		
		private final double rate;
		
		private RateLimiter(double rate, Clock clock){
			this.rate = rate;
		}
		
		private <T> T limit(Callable<T> callable) throws Exception{
			
			T value = callable.call();
			
			Thread.sleep((long)(MILLISECONDS_IN_HOUR / rate));
			
			return value;
		}
	}
}
