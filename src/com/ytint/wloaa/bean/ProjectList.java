package com.ytint.wloaa.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author zhangyg
 */
public class ProjectList extends Entity {
	private List<Project> info = new ArrayList<Project>();

	public static ProjectList parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		ProjectList list = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(                  
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		list = mapper.readValue(strResult, ProjectList.class);
		return list;
	}

	public List<Project> getInfo() {
		return info;
	}

	public void setInfo(List<Project> info) {
		this.info = info;
	}

}
