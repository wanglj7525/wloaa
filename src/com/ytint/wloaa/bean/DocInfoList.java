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
//public class DocInfoList extends Entity {
//	private List<DocInfo> info = new ArrayList<DocInfo>();
//
//	public static DocInfoList parseJson(String strResult)
//			throws JsonParseException, JsonMappingException, IOException {
//		DocInfoList list = null;
//		ObjectMapper mapper = new ObjectMapper();
//		list = mapper.readValue(strResult, DocInfoList.class);
//		return list;
//	}
//
//	public List<DocInfo> getInfo() {
//		return info;
//	}
//
//	public void setInfo(List<DocInfo> info) {
//		this.info = info;
//	}
//
//}
