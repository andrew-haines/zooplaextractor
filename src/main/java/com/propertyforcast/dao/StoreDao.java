package com.propertyforcast.dao;

import java.io.IOException;

public interface StoreDao<T> {

	public void store(T entity) throws IOException;
}
