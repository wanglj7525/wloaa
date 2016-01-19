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
public class DepartmentList extends Entity {
	private List<Department> info = new ArrayList<Department>();

	public static DepartmentList parseJson(String strResult)
			throws JsonParseException, JsonMappingException, IOException {
		DepartmentList list = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(                  
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		list = mapper.readValue(strResult, DepartmentList.class);
		return list;
	}

	public List<Department> getInfo() {
		return info;
	}

	public void setInfo(List<Department> info) {
		this.info = info;
	}

}
