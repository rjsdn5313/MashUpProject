package com.map.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("MainService")
public class MainServiceImpl implements MainService{
	Logger logger = (Logger) LogManager.getLogger(this.getClass());
	

	@Override
	public List<String> openDataList() throws Exception {
		ClassLoader classloader = this.getClass().getClassLoader(); 
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classloader);
		Resource[] resources = resolver.getResources("classpath*:/data/*.json") ;
		
		List<String> resultList = new ArrayList<String>();
		for (Resource resource: resources){
		    resultList.add(resource.getFilename().replace(".json", ""));
		}
		return resultList;
	}
	
	@Override
	public List<Object> showData(String Listname) throws Exception{
    	URL resource = new ClassPathResource("/data/" + Listname + ".json").getURL();
    	ObjectMapper mapper = new ObjectMapper();
    	List<Object> oList = mapper.readValue(resource, new TypeReference<List<Object>>(){});
    	
    	return oList;
	}
	
	@Override
	public List<Map<String, Object>> searchData(String search_keyword) throws Exception{
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		List<String> ListName = openDataList();
		
		for(String listname : ListName) {
			List<Object> data = showData(listname);
			
			for(Object tmpData : data) {
				String[] StrList = tmpData.toString().split(",");
				
				for(String tmpStr : StrList) {
					if(tmpStr.contains("설치장소") || tmpStr.contains("촬영방면정보") || tmpStr.contains("대상시설명")) {
						if(tmpStr.contains(search_keyword)) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("data", tmpData);
							resultList.add(map);
							continue;
						}
					}
				}
			}
		}
		return resultList;
	}
}
