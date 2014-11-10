package com.propertyforcast.model;

import java.util.Iterator;

public class PagenatedIterable<T> implements Iterable<T>{

	private final Iterable<T> underlyingIterable;
	private final int totalSize;
	private final PaginationDetails pagenationDetails;

	public PagenatedIterable(Iterable<T> underlyingIterable, int totalSize,
			PaginationDetails pagenationDetails) {
		this.underlyingIterable = underlyingIterable;
		this.totalSize = totalSize;
		this.pagenationDetails = pagenationDetails;
	}
	
	@Override
	public Iterator<T> iterator() {
		return underlyingIterable.iterator();
	}

	public int getTotalSize() {
		return totalSize;
	}

	public PaginationDetails getPagenationDetails() {
		return pagenationDetails;
	}

}
