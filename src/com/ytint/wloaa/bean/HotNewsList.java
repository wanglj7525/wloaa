//
//package com.ytint.wloaa.bean;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.codehaus.jackson.JsonParseException;
//import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.map.ObjectMapper;
//
///**
// * @author zhangyg
// */
//public class HotNewsList extends Entity {
//	private List<HotNews> info = new ArrayList<HotNews>();
//
//	public static HotNewsList parseJson(String strResult)
//			throws JsonParseException, JsonMappingException, IOException {
//		HotNewsList list = null;
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.getDeserializationConfig().set(                  
//				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		list = mapper.readValue(strResult, HotNewsList.class);
//		return list;
//	}
//
//	public List<HotNews> getInfo() {
//		return info;
//	}
//
//	public void setInfo(List<HotNews> info) {
//		this.info = info;
//	}
//	
//
//}