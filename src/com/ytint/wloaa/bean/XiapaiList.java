package com.ytint.wloaa.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings("serial")
public class XiapaiList extends Entity {
	private List<Xiapai> info = new ArrayList<Xiapai>();

	public static XiapaiList parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		XiapaiList list = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(                  
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		list = mapper.readValue(strResult, XiapaiList.class);
		return list;
	}

	public List<Xiapai> getInfo() {
		return info;
	}

	public void setInfo(List<Xiapai> info) {
		this.info = info;
	}

}
