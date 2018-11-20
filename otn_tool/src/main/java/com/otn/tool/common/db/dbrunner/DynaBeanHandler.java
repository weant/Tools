package com.otn.tool.common.db.dbrunner;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.dbutils.ResultSetHandler;

public class DynaBeanHandler implements ResultSetHandler<DynaBean> { 

	/**
	 * the dynaClass of beans produced by this handler
	 */
	private DynaClass type;
	
	
	
    /**
     * The RowProcessor implementation to use when converting rows 
     * into beans.
     */
    private static final DynamicBeanProcessor convert = new DynamicBeanProcessor();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public DynaBeanHandler(DynaClass type){
		this.type = type;
	
	}
	
	

	@Override
	public DynaBean handle(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		 return rs.next() ? convert.toBean(rs, this.type) : null;

	}

}
