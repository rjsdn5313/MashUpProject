package com.map.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.dao.MapDAO;

@Service("MainService")
public class MainServiceImpl implements MainService{
	Logger logger = (Logger) LogManager.getLogger(this.getClass());
	
	@Override
	public List<String> openDataList() throws Exception {
		ClassLoader classloader = this.getClass().getClassLoader(); 
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classloader);
		Resource[] resources = resolver.getResources("classpath*:/dataje/*.json") ;
		
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
	
	//DAO 연결
	@javax.annotation.Resource(name="mapDAO")
	private MapDAO mapDAO;
	
	//DB에 데이터 넣기
	@Override
	public void InDataBase() throws Exception{
		
		ObjectMapper mapper = new ObjectMapper();
		URL resource;
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Scanner sc = new Scanner(System.in);
		
		String[] listname = {"전국무인민원발급정보표준데이터", "전국CCTV표준데이터", "전국어린이보호구역표준데이터", "전국무인교통단속카메라표준데이터"};
		int x;
		do {
			System.out.println("1. 전국무인민원발급정보표준데이터 등등");

			x = sc.nextInt();
			
				resource = new ClassPathResource("/data/" + listname[x] + ".json").getURL();
		
				try {
					switch(x){
						case 0 : // 전국무인민원발급정보표준데이터
							dataList = mapper.readValue(resource, new TypeReference<List<Map<String, Object>>>(){});
						
							// JSON 파일을  dataList에 넣고 DAO로 DB에 넣는다.
							for (Map<String, Object> data : dataList) {
								Map<String, Object> resultMap = new HashMap<String, Object>();
								
								resultMap.put("PLACE", data.get("설치장소"));
								resultMap.put("ADDRESS", data.get("소재지지번주소"));
								resultMap.put("LOCATION", data.get("설치위치"));
								resultMap.put("LATITUDE", data.get("위도"));
								resultMap.put("LONGITUDE", data.get("경도"));
							
								mapDAO.insert_complain(resultMap);
							}
							break;
			
						case 1 : //전국CCTV표준데이터
							dataList = mapper.readValue(resource, new TypeReference<List<Map<String, Object>>>(){});
						
							// JSON 파일을  dataList에 넣고 DAO로 DB에 넣는다.
							for (Map<String, Object> data : dataList) {
								Map<String, Object> resultMap = new HashMap<String, Object>();
								
								resultMap.put("FILMINGINFORMATION", data.get("촬영방면정보"));
								resultMap.put("ADDRESS", data.get("소재지지번주소"));
								resultMap.put("PURPOSE", data.get("설치목적구분"));
								resultMap.put("LATITUDE", data.get("위도"));
								resultMap.put("LONGITUDE", data.get("경도"));
							
								mapDAO.insert_cctv(resultMap);
							}
							break;
							
						case 2 : // 전국어린이보호구역표준데이터
							dataList = mapper.readValue(resource, new TypeReference<List<Map<String, Object>>>(){});
						
							// JSON 파일을  dataList에 넣고 DAO로 DB에 넣는다.
							for (Map<String, Object> data : dataList) {
								Map<String, Object> resultMap = new HashMap<String, Object>();
								
								resultMap.put("OBJNAME", data.get("대상시설명"));
								resultMap.put("ADDRESS", data.get("소재지지번주소"));
								resultMap.put("POLICEAREA", data.get("관할경찰서명"));
								resultMap.put("LATITUDE", data.get("위도"));
								resultMap.put("LONGITUDE", data.get("경도"));
							
								mapDAO.insert_children(resultMap);
							}
							break;
							
						case 3 : // 전국무인교통단속카메라표준데이터
							dataList = mapper.readValue(resource, new TypeReference<List<Map<String, Object>>>(){});
						
							// JSON 파일을 분리해서 resultMap에 넣고 DAO로 DB에 넣는다.
							for (Map<String, Object> data : dataList) {
								Map<String, Object> resultMap = new HashMap<String, Object>();
								
								resultMap.put("PLACE", data.get("설치장소"));
								resultMap.put("ADDRESS", data.get("소재지지번주소"));
								resultMap.put("SPEEDLIMIT", data.get("제한속도"));
								resultMap.put("LATITUDE", data.get("위도"));
								resultMap.put("LONGITUDE", data.get("경도"));
							
								mapDAO.insert_trafficcontrol(resultMap);
							}
							break;
					}
				} catch (Exception e) {
					System.out.println(e + " - " + x);
				}
		} while (x != -1);
			sc.close();
	}
	
	
	//검색영역
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
