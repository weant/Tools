package com.hcop.ptn.common.db.dbrunner;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.SQLException;

public class RSHandlerCreator {
	
	
	private static RowProcessor normalRowProcessor = new BaseRowProcessor(new BaseBeanProcessor());
	
	 public static enum HandlerType {
		 Map,
		 MapList,
		 DynaClass,
		 StaticClass,
		 DynaClassList,
		 StaticClassList;
		
	}
	
	
	/**
	 * 
	 * 根据指定的处理器类型返回相应的resultSet 处理器。
	 * 当处理器类型为DynaClass 或者 StaticClass时，需要指定需要指定classType参数值。
	 * 
	 * handlerType参数值若为HandlerType.DynaClass或HandlerType.DynaClassList时，
	 * classType参数值应是一个<class>DynaClass</class>或其子类的实例
	 * 
	 * handlerType参数值若为HandlerType.StaticClass或HandlerType.StaticClassList时,
	 *  classType参数值应该是一个<class>Class</class>的实例
	 * 
	 * @param handlerType  处理器类型
	 * @param classType    处理器需要的bean类类型。
	 * @return
	 * @throws SQLException 
	 */
	public static ResultSetHandler<?> createHandlerBy(HandlerType handlerType, Object classType) throws SQLException {
		switch (handlerType) {
			case Map:
				return new MapHandler(normalRowProcessor);
			case MapList:
				return new MapListHandler(normalRowProcessor);
			case DynaClass: {
				try {
					DynaClass type = (DynaClass) classType;
					return new DynaBeanHandler(type);
				} catch (ClassCastException e) {
					throw new SQLException("the classType \"" + classType + "\"is not a instance of DynaClass ", e);
				}

			}


			case DynaClassList: {
				try {
					DynaClass type = (DynaClass) classType;
					return new DynaBeanListHandler(type);
				} catch (ClassCastException e) {
					throw new SQLException("the classType \"" + classType + "\"is not a instance of DynaClass ", e);
				}

			}


			case StaticClassList: {
				try {
					Class<?> type = (Class<?>) classType;
					return new BeanListHandler(type, normalRowProcessor);
				} catch (ClassCastException e) {
					throw new SQLException("the classType \"" + classType + "\"is not a instance of Class ", e);
				}
			}

			case StaticClass: {
				try {
					Class<?> type = (Class<?>) classType;
					return new BeanHandler(type, normalRowProcessor);
				} catch (ClassCastException e) {
					throw new SQLException("the classType \"" + classType + "\"is not a instance of Class ", e);
				}
			}

		}

		throw new SQLException("unsupported handler type.");
	}
}
