package com.otn.tool.common.mvc;

import java.util.EventListener;

public interface TextChangedListener extends EventListener {
	
	void textChanged(TextChangedEvent event);

}
