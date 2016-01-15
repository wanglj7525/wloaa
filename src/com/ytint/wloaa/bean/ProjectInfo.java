package com.ytint.wloaa.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings("serial")
public class ProjectInfo extends Entity {
	private Project info = new Project();

	public static ProjectInfo parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		ProjectInfo list = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(                  
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		list = mapper.readValue(strResult, ProjectInfo.class);
		return list;
	}

	public Project getInfo() {
		return info;
	}

	public void setInfo(Project info) {
		this.info = info;
	}

}
