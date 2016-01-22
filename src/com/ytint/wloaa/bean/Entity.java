package com.ytint.wloaa.bean;

import java.io.Serializable;

/**
 * 实体�?
 * 
 * @author zhangyg
 * @version 1.0
 * @created 2012-3-21
 */
public abstract class Entity implements Serializable {
	private static final long serialVersionUID = -8539591698448475035L;
	public int code;
	public String msg;
	public int page;
	public int pageSize;
	public int count;
	public boolean hasNextPage = false;
}
