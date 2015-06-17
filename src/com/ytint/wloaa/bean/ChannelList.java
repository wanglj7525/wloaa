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
public class ChannelList extends Entity {
	private List<People> info = new ArrayList<People>();

	public static ChannelList parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		ChannelList list = null;
		ObjectMapper mapper = new ObjectMapper();
		list = mapper.readValue(strResult, ChannelList.class);
		return list;
	}

	public List<People> getInfo() {
		return info;
	}

	public void setInfo(List<People> info) {
		this.info = info;
	}

}
