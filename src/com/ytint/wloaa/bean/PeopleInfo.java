package com.ytint.wloaa.bean;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author zhangyg
 */
public class PeopleInfo extends Entity {
	private People info = new People();

	public static PeopleInfo parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		PeopleInfo list = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(                  
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		list = mapper.readValue(strResult, PeopleInfo.class);
		return list;
	}

	public People getInfo() {
		return info;
	}

	public void setInfo(People info) {
		this.info = info;
	}

}
