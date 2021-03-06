package com.ytint.wloaa.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings("serial")
public class ShenpiInfo extends Entity {
	private Shenpi info = new Shenpi();

	public static ShenpiInfo parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		ShenpiInfo list = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(                  
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		list = mapper.readValue(strResult, ShenpiInfo.class);
		return list;
	}

	public Shenpi getInfo() {
		return info;
	}

	public void setInfo(Shenpi info) {
		this.info = info;
	}

}
