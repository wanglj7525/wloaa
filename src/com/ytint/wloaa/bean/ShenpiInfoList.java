package com.ytint.wloaa.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings("serial")
public class ShenpiInfoList extends Entity {
	private List<Shenpi> info = new ArrayList<Shenpi>();

	public static ShenpiInfoList parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		ShenpiInfoList list = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(                  
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		list = mapper.readValue(strResult, ShenpiInfoList.class);
		return list;
	}

	public List<Shenpi> getInfo() {
		return info;
	}

	public void setInfo(List<Shenpi> info) {
		this.info = info;
	}

}
