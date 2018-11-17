package com.otn.tool.common.mvc;

import java.util.Collection;

import org.apache.log4j.Logger;

import fi.mmm.yhteinen.swing.core.component.table.YColumn;
import fi.mmm.yhteinen.swing.core.component.table.YTable;

public class MyTable extends YTable {
	 private static Logger logger = Logger.getLogger(MyTable.class);
	 
	 public MyTable(YColumn[] cols){
		 super(cols);
	 }
	 public void setModelValue(Object obj){
		 if(obj == null){
			 YTable.EMPTY_MODEL.clear();
		 }
		 super.setModelValue(obj);
	 }
	 
	 
	 public void clearAll(){
		 Collection modelValue = (Collection)this.getModelValue();
		 modelValue.clear();
		 setModelValue(null);
	 }
}
