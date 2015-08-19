package com.ytint.wloaa.bean;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author zhangyg
 */
public class VersionInfo extends Entity {
	private Version info = new Version();

	public static VersionInfo parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		VersionInfo vi = null;
		ObjectMapper mapper = new ObjectMapper();
		vi = mapper.readValue(strResult, VersionInfo.class);
		return vi;
	}

	public Version getInfo() {
		return info;
	}

	public void setInfo(Version info) {
		this.info = info;
	}

}
