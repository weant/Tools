package com.hcop.otn.common.db.dbrunner;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class DynamicBeanProcessor {
	 private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>();
	 protected static final int PROPERTY_NOT_FOUND = -1;
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

	
	
	  public DynaBean toBean(ResultSet rs, DynaClass type) throws SQLException {
		  	
		 
				DynaProperty[] properties = type.getDynaProperties();

		        ResultSetMetaData rsmd = rs.getMetaData();
		        int[] columnToProperty = this.mapColumnsToProperties(rsmd, properties);

		        return this.createBean(rs, type, properties, columnToProperty);
			
			
	    }
	  
	  
	  
	    protected int[] mapColumnsToProperties(ResultSetMetaData rsmd,
	    		DynaProperty[] props) throws SQLException {

	        int cols = rsmd.getColumnCount();
	        int columnToProperty[] = new int[cols + 1];
	        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

	        for (int col = 1; col <= cols; col++) {
	            String columnName = rsmd.getColumnLabel(col);
	            if (null == columnName || 0 == columnName.length()) {
	              columnName = rsmd.getColumnName(col);
	            }
	            for (int i = 0; i < props.length; i++) {

	                if (columnName.equalsIgnoreCase(props[i].getName())) {
	                    columnToProperty[col] = i;
	                    break;
	                }
	            }
	        }

	        return columnToProperty;
	    }

	    /**
	     * Convert a <code>ResultSet</code> into a <code>List</code> of JavaBeans.  
	     * This implementation uses reflection and <code>BeanInfo</code> classes to 
	     * match column names to bean property names. Properties are matched to 
	     * columns based on several factors:
	     * <br/>
	     * <ol>
	     *     <li>
	     *     The class has a writable property with the same name as a column.
	     *     The name comparison is case insensitive.
	     *     </li>
	     * 
	     *     <li>
	     *     The column type can be converted to the property's set method 
	     *     parameter type with a ResultSet.get* method.  If the conversion fails
	     *     (ie. the property was an int and the column was a Timestamp) an
	     *     SQLException is thrown.
	     *     </li>
	     * </ol>
	     * 
	     * <p>
	     * Primitive bean properties are set to their defaults when SQL NULL is
	     * returned from the <code>ResultSet</code>.  Numeric fields are set to 0
	     * and booleans are set to false.  Object bean properties are set to 
	     * <code>null</code> when SQL NULL is returned.  This is the same behavior
	     * as the <code>ResultSet</code> get* methods.
	     * </p>
	     * @param <T> The type of bean to create
	     * @param rs ResultSet that supplies the bean data
	     * @param type Class from which to create the bean instance
	     * @throws SQLException if a database access error occurs
	     * @return the newly created List of beans
	     */
	    public  List<DynaBean> toBeanList(ResultSet rs, DynaClass type) throws SQLException {
	        List<DynaBean> results = new LinkedList<DynaBean>();

	        if (!rs.next()) {
	            return results;
	        }

	        DynaProperty[] props = type.getDynaProperties();
	        ResultSetMetaData rsmd = rs.getMetaData();
	        int[] columnToProperty = this.mapColumnsToProperties(rsmd, props);

	        do {
	            results.add(this.createBean(rs, type, props, columnToProperty));
	        } while (rs.next());

	        return results;
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
	    private DynaBean createBean(ResultSet rs, DynaClass type,
	    		DynaProperty[] props, int[] columnToProperty)
	            throws SQLException {

	       DynaBean bean = null;
		try {
			bean = type.newInstance();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new SQLException("can't access the instantialize method of  the DynaClass '"+type.getName()+"'",e);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			throw new SQLException("can't instantialize the DynaClass '"+type.getName()+"'",e);
		}

	        for (int i = 1; i < columnToProperty.length; i++) {

	            if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
	                continue;
	            }

	            DynaProperty prop = props[columnToProperty[i]];
	            Class<?> propType = prop.getType();

	            Object value = this.processColumn(rs, i, propType);

	            if (propType != null && value == null && propType.isPrimitive()) {
	                value = primitiveDefaults.get(propType);
	            }

	            this.callSetter(bean, prop, value);
	        }

	        return bean;
	    }
	    
	  

	    /**
	     * Convert a <code>ResultSet</code> column into an object.  Simple 
	     * implementations could just call <code>rs.getObject(index)</code> while
	     * more complex implementations could perform type manipulation to match 
	     * the column's type to the bean property type.
	     * 
	     * <p>
	     * This implementation calls the appropriate <code>ResultSet</code> getter 
	     * method for the given property type to perform the type conversion.  If 
	     * the property type doesn't match one of the supported 
	     * <code>ResultSet</code> types, <code>getObject</code> is called.
	     * </p>
	     * 
	     * @param rs The <code>ResultSet</code> currently being processed.  It is
	     * positioned on a valid row before being passed into this method.
	     * 
	     * @param index The current column index being processed.
	     * 
	     * @param propType The bean property type that this column needs to be
	     * converted into.
	     * 
	     * @throws SQLException if a database access error occurs
	     * 
	     * @return The object from the <code>ResultSet</code> at the given column
	     * index after optional type processing or <code>null</code> if the column
	     * value was SQL NULL.
	     */
	    protected Object processColumn(ResultSet rs, int index, Class<?> propType)
	        throws SQLException {
	        
	        if ( !propType.isPrimitive() && rs.getObject(index) == null ) {
	            return null;
	        }
	        
	        if ( !propType.isPrimitive() && rs.getObject(index) == null ) {
	            return null;
	        }
	        
	   

			if (propType.isEnum()) {// 如果是枚举类型,则数据库中存储的值可能是字符串 可能是int

				Object dbValue = rs.getObject(index);
				return handleEnum(dbValue, propType);

			}

	        if (propType.equals(String.class)) {
	            return rs.getString(index);

	        } else if (
	            propType.equals(Integer.TYPE) || propType.equals(Integer.class)) {
	            return (rs.getInt(index));

	        } else if (
	            propType.equals(Boolean.TYPE) || propType.equals(Boolean.class)) {
	            return (rs.getBoolean(index));

	        } else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) {
	            return (rs.getLong(index));

	        } else if (
	            propType.equals(Double.TYPE) || propType.equals(Double.class)) {
	            return (rs.getDouble(index));

	        } else if (
	            propType.equals(Float.TYPE) || propType.equals(Float.class)) {
	            return (rs.getFloat(index));

	        } else if (
	            propType.equals(Short.TYPE) || propType.equals(Short.class)) {
	            return (rs.getShort(index));

	        } else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) {
	            return (rs.getByte(index));

	        } else if (propType.equals(Timestamp.class)) {
	            return rs.getTimestamp(index);

	        } else {
	            return rs.getObject(index);
	        }

	    }
	    /**
	     * Calls the setter method on the target object for the given property.
	     * If no setter method exists for the property, this method does nothing.
	     * @param target The object to set the property on.
	     * @param prop The property to set.
	     * @param value The value to pass into the setter.
	     * @throws SQLException if an error occurs setting the property.
	     */
	    private void callSetter(Object target, DynaProperty prop, Object value)
	            throws SQLException {

	       
	       
	        Class clazz = prop.getType();
	        
	        try {
	            // convert types for some popular ones
	            if (value != null) {
	                if (value instanceof Date) {
	                    if (clazz == Date.class) {
	                        value = new java.sql.Date(((Date) value).getTime());
	                    } else
	                    if (clazz == java.sql.Time.class) {
	                        value = new java.sql.Time(((Date) value).getTime());
	                    } else
	                    if (clazz == Timestamp.class) {
	                        value = new Timestamp(((Date) value).getTime());
	                    }
	                }
	            }

	            // Don't call setter if the value object isn't the right type 
	            if (this.isCompatibleType(value, clazz)) {
	            	 PropertyUtils.setProperty(target, prop.getName(), value);
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
	        } catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
	        	 throw new SQLException(
	 	                "Cannot set " + prop.getName() + " cause of no Setter for this property: " + e.getMessage());
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
	    
	    
	    
	    
	    private Object handleEnum(Object dbValue, Class<?> propType) {
			Method method = null;
			Class vClazz = dbValue.getClass();
			try {
				if (vClazz == String.class) {//如果是字符串类型的枚举值
					method = tryToFindMethod(propType, "valueOf", vClazz);

				} else {//否则是数值型的枚举值,则可以是Float 和 Integer
					if(vClazz == Integer.class){
						String[] methodNames = 	new String[]{"valueOf","from","fromInt"};
						method = tryToFindMethod(propType,methodNames,vClazz);
						if(method == null){//如果还没找到,尝试将dbValue强转为基本类型
//							int priVal = ((Integer)dbValue).intValue();
							method = tryToFindMethod(propType,methodNames,int.class);
						}
						
					}else if(vClazz == Float.class){
						String[] methodNames = 	new String[]{"valueOf","from","fromFloat"};
						method = tryToFindMethod(propType,methodNames,vClazz);
						if(method == null){
							method = tryToFindMethod(propType,methodNames,float.class);
						}
						
					}else if(vClazz == BigDecimal.class){
						String[] methodNames = 	new String[]{"valueOf","from","fromInt"};
						method = tryToFindMethod(propType,methodNames,Integer.class);
						if(method == null){//如果还没找到,尝试将dbValue强转为基本类型
//							int priVal = ((Integer)dbValue).intValue();
							method = tryToFindMethod(propType,methodNames,int.class);
						}
						dbValue = ((BigDecimal)dbValue).intValue();
					}
				}
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

			if (method != null) {
				try {
					return method.invoke(null, dbValue);
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

		/**
		 * @param propType
		 * @param methodName
		 * @param realParamClass
		 * @return
		 */
		private Method tryToFindMethod(Class<?> propType, String methodName,
				Class<?> realParamClass) {

			Method method = null;
			try {
				method = propType.getMethod(methodName, realParamClass);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			return method;

		}
		
		
		
		/**
		 * @param propType
		 * @param methodNames
		 * @param realParamClass
		 * @return
		 */
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
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
