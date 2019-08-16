package com.hcop.otn.common.omsinfo.dynabean;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaProperty;

/**
 * 
 * 扩展BasicDynaClass,以添加客户自定义equals逻辑的功能
 *
 */
public class EqualableDynaBeanClass extends BasicDynaClass{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	 protected Class dynaBeanClass = EqualableDynaBean.class;
	private String[] keyProps;
	public EqualableDynaBeanClass(String name,
            DynaProperty[] properties,String[] keyProps){
		super(name, EqualableDynaBean.class, properties);
		this.keyProps = keyProps;
	}
	
	
	public String[] getKeyProps(){
		return keyProps;
	}
	

}
