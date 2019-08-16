package com.hcop.ptn.common.db.dao;

import com.hcop.ptn.common.db.dbrunner.DbRunner;
import com.hcop.ptn.common.db.oms.PktRunner;
import com.hcop.ptn.common.db.oms.SdhRunner;
import com.hcop.ptn.common.enums.DummyNeType_PKT;
import com.hcop.ptn.restful.model.NE;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class NeDao {
    private static Logger logger = Logger.getLogger(NeDao.class);
    private static NeDao ourInstance = new NeDao();

    public static NeDao getInstance() {
        return ourInstance;
    }

    private NeDao() {

    }

    public Collection<NE> getNeWith(String neName, String neId, DummyNeType_PKT neType) throws SQLException{

        DynaBean bean = createSqlAndParam(neName, neId, neType);
        logger.info("created sql&param="+bean);
        DbRunner runner = new PktRunner().getRunner();
        logger.info("getRunner runner="+runner);
        String sql = (String)bean.get("sql");
        logger.info("query sql="+sql);
        return runner.queryBeanCollection(sql, NE.class, (Object[])bean.get("params"));
    }

    private DynaBean createSqlAndParam(String name, String id, DummyNeType_PKT type){
        StringBuilder sql= new StringBuilder("SELECT NEID, USERLABEL AS NENAME, NETYPE, IPADDRESS AS NEIP, RELEASE AS NEVERSION ");
        sql.append(" FROM C_NE ");
        StringBuilder conditions = new StringBuilder();
        Collection<Object> params = new ArrayList<>();
        if(name != null && !name.isEmpty()){
            conditions.append(" LOWER(userlabel) like ? ");
            params.add("%"+name.toLowerCase()+"%");
        }

        if(id != null && !id.isEmpty()){
            if(conditions.length() != 0){
                conditions.append(" and ");
            }
            conditions.append(" NEID = ? ");
            params.add(id);
        }

        if(type != null && type != DummyNeType_PKT.DUMMY_NE_TYPE_NONE){
            if(conditions.length() != 0){
                conditions.append(" and ");
            }
            conditions.append(" netype = ? ");
            params.add(type.toValue());
        }else{
            //type is all
            if(conditions.length() != 0){
                conditions.append(" and ");
            }
            conditions.append(" netype in ");
            List<DummyNeType_PKT> allTypes = DummyNeType_PKT.getDummyNeType_PKT_List();

            @SuppressWarnings("unchecked")
            Collection<Integer> typeValues = allTypes.stream().map(DummyNeType_PKT::toValue).collect(Collectors.toList());

            String typeSql = createConditionSqlWithMultipleParam(",","?",typeValues.size());
            conditions.append(typeSql);
            params.addAll(typeValues);
        }

        if(conditions.length() > 0){
            sql.append(" where ");
            sql.append(conditions);
        }
        DynaBean bean = new LazyDynaBean();
        bean.set("sql", sql.toString());
        bean.set("params", params.toArray(new Object[0]));
        return bean;
    }

    private String createConditionSqlWithMultipleParam(String splitter, String sqlInEveryLoop, int numberOfConditions){
        StringBuilder sql = new StringBuilder();
        sql.append(" ( ");
        for(int i = 0; i < numberOfConditions; i++){
            sql.append(sqlInEveryLoop);
            if( i < numberOfConditions - 1){
                sql.append(splitter);
            }
        }
        sql.append(" ) ");
        return sql.toString();
    }

    public Map<String, String> queryNeCommunicationSate(){
        String sql = "select userlabel, CASE WHEN COMMUNICATIONSTATE = 0 THEN 'REACHABLE' ELSE 'UNREACHABLE' END AS COMMUNICATIONSTATE from NE ";

        DbRunner runner = new SdhRunner().getRunner();

        Map<String,String> record = new HashMap <>();
        try {
            List<Map<String,Object>> data = runner.queryMapList(sql);
            data.forEach(map-> record.put(map.get("USERLABEL").toString(), map.get("COMMUNICATIONSTATE").toString()));
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return record;
    }
}
