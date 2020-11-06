package com.map.dao;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("mapDAO")
public class MapDAO {
	Logger logger = (Logger) LogManager.getLogger(this.getClass());
	
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	public Object insert_NoLog(String queryId, Object params) {
		if(logger.isDebugEnabled()) {
			logger.debug("\t QueryId \t: " + queryId);
		}
		return sqlSession.insert(queryId, params);
	}
		
	//무인민원
	public void insert_complain(Map<String, Object> map) throws Exception {
		insert_NoLog("insert_complain", map);
	}
	
	//CCTV
	public void insert_cctv(Map<String, Object> map) throws Exception {
		insert_NoLog("insert_cctv", map);
	}
	
	//어린이보호구역
	public void insert_children(Map<String, Object> map) throws Exception {
		insert_NoLog("insert_children", map);
	}
	
	//교통단속
	public void insert_trafficcontrol(Map<String, Object> map) throws Exception {
		insert_NoLog("insert_trafficcontrol", map);
	}
	
}
