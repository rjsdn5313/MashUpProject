package com.map.controller;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.map.service.MainService;

@Controller
public class MapController {
    Logger logger = (Logger) LogManager.getLogger(this.getClass());
    
    @Resource(name="MainService")
    private MainService mainService;
    
    @RequestMapping(value="/map/openMap.do")
    public ModelAndView openExampleList() throws Exception {
        ModelAndView mv = new ModelAndView("/openMap");

        return mv;
    }
    
    @RequestMapping(value="/openDataList.do")
    public @ResponseBody List<String> openDataList() throws Exception{
    	return mainService.openDataList();
    }
    
    @RequestMapping(value="/showData.do")
    public @ResponseBody List<Object> showData(HttpServletRequest request) throws Exception{
    	return mainService.showData(request.getParameter("name"));
    }
    
    @RequestMapping(value="/searchData.do")
    public @ResponseBody List<Map<String, Object>> searchData(@RequestParam String search_keyword) throws Exception {
    	return mainService.searchData(search_keyword);
    }
    
}