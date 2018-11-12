package com.otn.tool.common.omsinfo.dynabean;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.reflect.InvocationTargetException;

/**
 * 
 * 以EqualableDynaBeanClass中绑定的keyProps为依据来组织equals方法的逻辑
 * @author dongsy
 *
 */
public final class EqualableDynaBean extends BasicDynaBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public EqualableDynaBean(DynaClass dynaClass) {
		super(dynaClass);
	}
	
	public int hashCode(){
		EqualableDynaBeanClass dynClass = (EqualableDynaBeanClass)this.getDynaClass();
		HashCodeBuilder builder = new HashCodeBuilder();
		for(String propertyName : dynClass.getKeyProps()){
			try {
				Object proVal = PropertyUtils.getProperty(this, propertyName);
				
				builder.append(proVal);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		
		}
		return builder.toHashCode();
	}
	public boolean equals(Object obj){
		if(obj == null)return false;
		if(obj instanceof EqualableDynaBean){
			EqualableDynaBean other = (EqualableDynaBean)obj;
			EqualableDynaBeanClass dynClass = (EqualableDynaBeanClass)other.getDynaClass();
			
			String[] keyProps = dynClass.getKeyProps();
			EqualsBuilder builder = new EqualsBuilder();
			for(String propertyName : keyProps){
				try {
					Object proVal = PropertyUtils.getProperty(this, propertyName);
					Object otherVal = PropertyUtils.getProperty(other, propertyName);
					builder.append(proVal, otherVal);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			
			}
			
			return builder.isEquals();
		}
		
		
		return false;
		
	}

}
