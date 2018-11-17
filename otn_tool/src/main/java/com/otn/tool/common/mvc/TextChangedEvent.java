package com.otn.tool.common.mvc;

import java.util.EventObject;

public class TextChangedEvent extends EventObject {

	private String newText;
	public TextChangedEvent(Object source,String newText) {
		super(source);
		// TODO Auto-generated constructor stub
		this.newText = newText;
	}
	
	

	public String getNewText() {
		return newText;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
