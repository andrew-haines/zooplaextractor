package com.propertyforcast.model;

public class PaginationDetails {

	private final int pageNum;
	private final int pageSize;
	
	public PaginationDetails(int pageNum, int pageSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}
	
	public int getPageNum() {
		return pageNum;
	}
	
	public int getPageSize() {
		return pageSize;
	}
}
