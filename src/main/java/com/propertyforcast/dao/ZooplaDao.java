package com.propertyforcast.dao;

import java.io.IOException;

import com.propertyforcast.model.Listing;
import com.propertyforcast.model.PagenatedIterable;
import com.propertyforcast.model.PaginationDetails;

public interface ZooplaDao {

	public PagenatedIterable<Listing> getListings(String postcode, double radius, PaginationDetails pagenationDetails) throws IOException;
}
