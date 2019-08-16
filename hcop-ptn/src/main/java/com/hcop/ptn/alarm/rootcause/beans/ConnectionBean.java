package com.hcop.ptn.alarm.rootcause.beans;

import com.hcop.ptn.restful.model.ConnectionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionBean {
    private String connId = null;
    private ConnectionType connType = null;
    private String connName = null;
    private List<TpBean> tps = new ArrayList<TpBean>();
    private Map<String, Object> extendedFields = new HashMap<>();

    public String getConnId() {
        return connId;
    }

    public void setConnId(String connId) {
        this.connId = connId;
    }

    public ConnectionType getConnType() {
        return connType;
    }

    public void setConnType(ConnectionType connType) {
        this.connType = connType;
    }

    public String getConnName() {
        return connName;
    }

    public void setConnName(String connName) {
        this.connName = connName;
    }

    public List <TpBean> getTps() {
        return tps;
    }

    public void setTps(List <TpBean> tps) {
        this.tps = tps;
    }

    public Map <String, Object> getExtendedFields() {
        return extendedFields;
    }

    public void setExtendedFields(Map <String, Object> extendedFields) {
        this.extendedFields = extendedFields;
    }
}
