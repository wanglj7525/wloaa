package com.ytint.wloaa.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings("serial")
public class TaskInfo extends Entity {
	private Task info = new Task();

	public static TaskInfo parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		TaskInfo list = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(                  
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		list = mapper.readValue(strResult, TaskInfo.class);
		return list;
	}

	public Task getInfo() {
		return info;
	}

	public void setInfo(Task info) {
		this.info = info;
	}

}
