package com.drivewise.smarttraffic.api.fetcher;

public interface IAPIFetcher<T> {
	T fetch();
	void save(T dto);
}