package com.propertyforcast.dao;

import java.io.IOException;

import com.propertyforcast.model.Listing;

public interface ListingStoreDao {

	public void store(Iterable<Listing> listings) throws IOException;
}
