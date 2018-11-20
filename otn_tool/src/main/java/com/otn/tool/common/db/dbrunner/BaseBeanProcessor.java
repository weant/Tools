package com.otn.tool.common.db.dbrunner;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BeanProcessor;

public class BaseBeanProcessor extends BeanProcessor {
	 private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>();

	    static {
	        primitiveDefaults.put(Integer.TYPE, 0);
	        primitiveDefaults.put(Short.TYPE, (Short)((short) 0));
	        primitiveDefaults.put(Byte.TYPE, (Byte)((byte) 0));
	        primitiveDefaults.put(Float.TYPE, (Float)(float)(0));
	        primitiveDefaults.put(Double.TYPE, (Double)(double)(0));
	        primitiveDefaults.put(Long.TYPE, (Long)(0L));
	        primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
	        primitiveDefaults.put(Character.TYPE, '\u0000');
	    }
	/**
	 * <p>
	 * <code>BaseBeanProcessor</code> matches column names to bean property names
	 * and converts <code>ResultSet</code> columns into objects for those bean
	 * properties. Subclasses should override the methods in the processing
	 * chain to customize behavior.
	 * </p>
	 * 
	 * <p>
	 * This class is thread-safe.
	 * </p>
	 * 
	 * @see BasicRowProcessor
	 * 
	 * @since DbUtils 1.1
	 */

	/**
	 * Constructor for BaseBeanProcessor.
	 */
	public BaseBeanProcessor() {
		super();
	}

	/**
	 * Convert a <code>ResultSet</code> column into an object. Simple
	 * implementations could just call <code>rs.getObject(index)</code> while
	 * more complex implementations could perform type manipulation to match the
	 * column's type to the bean property type.
	 * 
	 * <p>
	 * This implementation calls the appropriate <code>ResultSet</code> getter
	 * method for the given property type to perform the type conversion. If the
	 * property type doesn't match one of the supported <code>ResultSet</code>
	 * types, <code>getObject</code> is called.
	 * </p>
	 * 
	 * @param rs
	 *            The <code>ResultSet</code> currently being processed. It is
	 *            positioned on a valid row before being passed into this
	 *            method.
	 * 
	 * @param index
	 *            The current column index being processed.
	 * 
	 * @param propType
	 *            The bean property type that this column needs to be converted
	 *            into.
	 * 
	 * @throws SQLException
	 *             if a database access error occurs
	 * 
	 * @return The object from the <code>ResultSet</code> at the given column
	 *         index after optional type processing or <code>null</code> if the
	 *         column value was SQL NULL.
	 */
	protected Object processColumn(ResultSet rs, int index, Class<?> propType)
			throws SQLException {

		if (!propType.isPrimitive() && rs.getObject(index) == null) {
			return null;
		}

		if (!propType.isPrimitive() && rs.getObject(index) == null) {
			return null;
		}

		if (propType.isEnum()) {// 如果是枚举类型,则数据库中存储的值可能是字符串 可能是int

			Object dbValue = rs.getObject(index);
			return handleEnum(dbValue, propType);
			

		}
		// invoke super
		return super.processColumn(rs, index, propType);
	}

	private Object handleEnum(Object dbValue, Class<?> propType) {
		Method method = null;
		Class vClazz = dbValue.getClass();
		Object paramValue = dbValue;
		try {
			if (vClazz == String.class) {//如果是字符串类型的枚举值
				method = tryToFindMethod(propType, "valueOf", vClazz);

			} else {//否则是数值型的枚举值,则可以是Float 和 Integer
				if(vClazz == BigDecimal.class){
					method = tryToFindBigDecimalCompatibleMethod(propType,"valueOf");
					paramValue = ((BigDecimal)dbValue).intValue();
				}else if(vClazz == Integer.class){
					String[] methodNames = 	new String[]{"valueOf","from","fromInt"};
					method = tryToFindMethod(propType,methodNames,vClazz);
					if(method == null){//如果还没找到,尝试将dbValue强转为基本类型
//						int priVal = ((Integer)dbValue).intValue();
						method = tryToFindMethod(propType,methodNames,int.class);
					}
					
				}else if(vClazz == Float.class){
					String[] methodNames = 	new String[]{"valueOf","from","fromFloat"};
					method = tryToFindMethod(propType,methodNames,vClazz);
					if(method == null){
						method = tryToFindMethod(propType,methodNames,float.class);
					}
					
				}
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		if (method != null) {
			try {
				return method.invoke(null, paramValue);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	private Method tryToFindBigDecimalCompatibleMethod(Class<?> propType, String methodName){
		String[] methodNames = 	new String[]{"valueOf","from","fromInt"};
		Method method = tryToFindMethod(propType,methodNames,Integer.class);
		if(method == null){//如果还没找到,尝试将dbValue强转为基本类型
//			int priVal = ((Integer)dbValue).intValue();
			method = tryToFindMethod(propType,methodNames,int.class);
		}
		
	/*	 methodNames = 	new String[]{"valueOf","from","fromFloat"};
		method = tryToFindMethod(propType,methodNames,Float.class);
		if(method == null){
			method = tryToFindMethod(propType,methodNames,float.class);
		}*/
		return method;
	}
	private Method tryToFindMethod(Class<?> propType, String methodName,
			Class<?> realParamClass) {

		Method method = null;
		try {
			method = propType.getMethod(methodName, realParamClass);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		return method;

	}
	
	
	
	private Method tryToFindMethod(Class<?> propType,String[] methodNames, Class<?> realParamClass){
		Method method = null;
		for(String mn: methodNames){
			method = tryToFindMethod(propType,mn,realParamClass);
			if(method != null){
				break;
			}
		}
		
		return method;
		
	}

    /**
     * Returns a PropertyDescriptor[] for the given Class.
     *
     * @param c The Class to retrieve PropertyDescriptors for.
     * @return A PropertyDescriptor[] describing the Class.
     * @throws SQLException if introspection failed.
     */
    private PropertyDescriptor[] propertyDescriptors(Class<?> c)
        throws SQLException {
        // Introspector caches BeanInfo classes for better performance
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(c);

        } catch (IntrospectionException e) {
            throw new SQLException(
                "Bean introspection failed: " + e.getMessage());
        }

        return beanInfo.getPropertyDescriptors();
    }
    
    
    
    /**
     * Creates a new object and initializes its fields from the ResultSet.
     * @param <T> The type of bean to create
     * @param rs The result set.
     * @param type The bean type (the return type of the object).
     * @param props The property descriptors.
     * @param columnToProperty The column indices in the result set.
     * @return An initialized object.
     * @throws SQLException if a database error occurs.
     */
    private <T> T createBean(ResultSet rs, Class<T> type,
            PropertyDescriptor[] props, int[] columnToProperty)
            throws SQLException {

        T bean = this.newInstance(type);

        for (int i = 1; i < columnToProperty.length; i++) {

            if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
                continue;
            }

            PropertyDescriptor prop = props[columnToProperty[i]];
            Class<?> propType = prop.getPropertyType();

            Object value = this.processColumn(rs, i, propType);

            if (propType != null && value == null && propType.isPrimitive()) {
                value = primitiveDefaults.get(propType);
            }

            this.callSetter(bean, prop, value);
        }

        return bean;
    }

    
    /**
     * Calls the setter method on the target object for the given property.
     * If no setter method exists for the property, this method does nothing.
     * @param target The object to set the property on.
     * @param prop The property to set.
     * @param value The value to pass into the setter.
     * @throws SQLException if an error occurs setting the property.
     */
    private void callSetter(Object target, PropertyDescriptor prop, Object value)
            throws SQLException {

        Method setter = prop.getWriteMethod();

        if (setter == null) {
            return;
        }

        Class<?>[] params = setter.getParameterTypes();
        try {
            // convert types for some popular ones
            if (value != null) {
                if (value instanceof java.util.Date) {
                    if (params[0].getName().equals("java.sql.Date")) {
                        value = new java.sql.Date(((java.util.Date) value).getTime());
                    } else
                    if (params[0].getName().equals("java.sql.Time")) {
                        value = new java.sql.Time(((java.util.Date) value).getTime());
                    } else
                    if (params[0].getName().equals("java.sql.Timestamp")) {
                        value = new java.sql.Timestamp(((java.util.Date) value).getTime());
                    }
                }
            }

            // Don't call setter if the value object isn't the right type 
            if (this.isCompatibleType(value, params[0])) {
                setter.invoke(target, new Object[] { value });
            } else {
              throw new SQLException(
                  "Cannot set " + prop.getName() + ": incompatible types.");
            }

        } catch (IllegalArgumentException e) {
            throw new SQLException(
                "Cannot set " + prop.getName() + ": " + e.getMessage());

        } catch (IllegalAccessException e) {
            throw new SQLException(
                "Cannot set " + prop.getName() + ": " + e.getMessage());

        } catch (InvocationTargetException e) {
            throw new SQLException(
                "Cannot set " + prop.getName() + ": " + e.getMessage());
        }
    }
    
    
    /**
     * ResultSet.getObject() returns an Integer object for an INT column.  The
     * setter method for the property might take an Integer or a primitive int.
     * This method returns true if the value can be successfully passed into
     * the setter method.  Remember, Method.invoke() handles the unwrapping
     * of Integer into an int.
     * 
     * @param value The value to be passed into the setter method.
     * @param type The setter's parameter type.
     * @return boolean True if the value is compatible.
     */
    private boolean isCompatibleType(Object value, Class<?> type) {
        // Do object check first, then primitives
        if (value == null || type.isInstance(value)) {
            return true;

        } else if (
            type.equals(Integer.TYPE) && Integer.class.isInstance(value)) {
            return true;

        } else if (type.equals(Long.TYPE) && Long.class.isInstance(value)) {
            return true;

        } else if (
            type.equals(Double.TYPE) && Double.class.isInstance(value)) {
            return true;

        } else if (type.equals(Float.TYPE) && Float.class.isInstance(value)) {
            return true;

        } else if (type.equals(Short.TYPE) && Short.class.isInstance(value)) {
            return true;

        } else if (type.equals(Byte.TYPE) && Byte.class.isInstance(value)) {
            return true;

        } else if (
            type.equals(Character.TYPE) && Character.class.isInstance(value)) {
            return true;

        } else if (
            type.equals(Boolean.TYPE) && Boolean.class.isInstance(value)) {
            return true;

        } else {
            return false;
        }

    }

}
