package com.otn.tool.common.db.dbrunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.dbutils.ResultSetHandler;

public class DynaBeanListHandler implements ResultSetHandler<List<DynaBean>> {
	/**
	 * the dynaClass of beans produced by this handler
	 */
	private DynaClass type;
	 /**
     * The RowProcessor implementation to use when converting rows 
     * into beans.
     */
    private static final DynamicBeanProcessor convert = new DynamicBeanProcessor();
	public DynaBeanListHandler(DynaClass type){
		this.type = type;
	}
	@Override
	public List<DynaBean> handle(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		 return convert.toBeanList(rs, type);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
