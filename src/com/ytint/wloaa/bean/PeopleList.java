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
public class PeopleList extends Entity {
	private List<People> info = new ArrayList<People>();

	public static PeopleList parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		PeopleList list = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(                  
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		list = mapper.readValue(strResult, PeopleList.class);
		return list;
	}

	public List<People> getInfo() {
		return info;
	}

	public void setInfo(List<People> info) {
		this.info = info;
	}

}
