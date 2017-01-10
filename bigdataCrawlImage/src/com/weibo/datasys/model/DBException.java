package com.weibo.datasys.model;

public class DBException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DBException(String msg) {
		super(msg);
	}

	public DBException(Throwable t) {
		super(t);
	}

}