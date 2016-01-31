package com.ytint.wloaa.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings("serial")
public class TaskInfoList extends Entity {
	private List<Task> info = new ArrayList<Task>();

	public static TaskInfoList parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		TaskInfoList list = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(                  
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		list = mapper.readValue(strResult, TaskInfoList.class);
		return list;
	}

	public List<Task> getInfo() {
		return info;
	}

	public void setInfo(List<Task> info) {
		this.info = info;
	}

}
