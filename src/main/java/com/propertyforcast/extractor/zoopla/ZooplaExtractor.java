package com.propertyforcast.extractor.zoopla;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyforcast.dao.ForbiddenZooplaDaoException;
import com.propertyforcast.dao.ListingStoreDao;
import com.propertyforcast.dao.StoreDao;
import com.propertyforcast.dao.ZooplaDao;
import com.propertyforcast.model.Listing;
import com.propertyforcast.model.PagenatedIterable;
import com.propertyforcast.model.PaginationDetails;
import com.propertyforcast.time.Clock;

public class ZooplaExtractor {

	private final static Logger LOG = LoggerFactory.getLogger(ZooplaExtractor.class);
	
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
		
		RateLimiter limiter = new RateLimiter(100, clock);
		for (final String postcode: postcodes){
			
			LOG.debug("extracting: {}", postcode);
			
			boolean finishedPostcodeExtraction = false;
			int pageNum = 1;
			int pageSize = 100;
			int totalEntriesExtracted = 0;
			
			while(!finishedPostcodeExtraction){
				
				final int pageNumToUse = pageNum;
				final int pageSizeToUse = pageSize;
				try{
					PagenatedIterable<Listing> listings = limiter.limit(new Callable<PagenatedIterable<Listing>>(){
	
						@Override
						public PagenatedIterable<Listing> call() throws Exception {
							return dao.getListings(postcode, radius, new PaginationDetails(pageNumToUse, pageSizeToUse));
						}
					}, store);
					
					totalEntriesExtracted += pageSize;
					
					finishedPostcodeExtraction = totalEntriesExtracted >= listings.getTotalSize();
					
					pageNum++;
					
					pageSize = Math.min(listings.getTotalSize() - totalEntriesExtracted, pageSize);
					
				} catch (Exception e){
					throw new RuntimeException("Error running extractor", e);
				}
			}
		}
	}
	
	private static class RateLimiter{
		
		private final static Logger LOG = LoggerFactory.getLogger(RateLimiter.class);
		
		private final long MILLISECONDS_IN_HOUR = 3600000;
		
		private final int maxRequestsInHour;
		private final Clock clock;
		private long lastHour;
		private int requestsInHour = 0;
		
		private RateLimiter(int rate, Clock clock){
			this.maxRequestsInHour = rate;
			this.clock = clock;
			this.lastHour = clock.getCurrentTime();
		}
		
		private <T> T limit(Callable<T> callable, StoreDao<? super T> store) throws Exception{
			
			LOG.debug("Executing call");
			try{
				T value = callable.call();
				
				store.store(value);
				
				requestsInHour++;
				do{
					long waitTime = getRandomWaitTime();
					LOG.debug("Waiting {} ms", waitTime);
					Thread.sleep(waitTime);
					
					LOG.debug("Waking up");
					
					long currentTime = clock.getCurrentTime();
					
					if (currentTime - lastHour > MILLISECONDS_IN_HOUR){
						LOG.debug("Resetting rate for the hour.");
						resetHour(currentTime);
					}
				} while(requestsInHour > maxRequestsInHour);
				
				return value;
			} catch (ForbiddenZooplaDaoException e){
				LOG.info("Zoopla is restricting their api. Holding back abit...");
				Thread.sleep(600000);
				
				return limit(callable, store);
			}
		}

		private void resetHour(long currentTime) {
			this.lastHour = currentTime;
			requestsInHour = 0;
		}

		private long getRandomWaitTime() {
			return (long)(Math.random() * (MILLISECONDS_IN_HOUR / maxRequestsInHour)) * 2;
		}
	}
}
