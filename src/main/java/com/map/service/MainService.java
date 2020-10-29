package com.map.service;

import java.util.List;
import java.util.Map;

public interface MainService {
	List<String> openDataList() throws Exception;
	List<Object> showData(String Listname) throws Exception;
	List<Map<String, Object>> searchData(String search_keyword) throws Exception;
}
