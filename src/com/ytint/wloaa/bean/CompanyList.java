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
public class CompanyList extends Entity {
	private List<Company> info = new ArrayList<Company>();

	public static CompanyList parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		CompanyList list = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(                  
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		list = mapper.readValue(strResult, CompanyList.class);
		return list;
	}

	public List<Company> getInfo() {
		return info;
	}

	public void setInfo(List<Company> info) {
		this.info = info;
	}

}
