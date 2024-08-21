package com.drivewise.smarttraffic.fetcher;

public interface IAPIFetcher<T> {
	T fetch();
}