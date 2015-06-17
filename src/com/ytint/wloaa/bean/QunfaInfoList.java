package com.ytint.wloaa.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings("serial")
public class QunfaInfoList extends Entity {
	private List<Qunfa> info = new ArrayList<Qunfa>();

	public static QunfaInfoList parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		QunfaInfoList list = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(                  
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		list = mapper.readValue(strResult, QunfaInfoList.class);
		return list;
	}

	public List<Qunfa> getInfo() {
		return info;
	}

	public void setInfo(List<Qunfa> info) {
		this.info = info;
	}

}
