package com.hcop.ptn.alarm.rootcause.beans;

import java.util.HashMap;
import java.util.Map;

public class TpBean {
    private String neId = null;

    private String neName = null;

    private String equipName = null;

    private String tpId = null;

    private String tpName = null;

    private Map<String, Object> extendedFields = new HashMap<>();

    public String getNeId() {
        return neId;
    }

    public void setNeId(String neId) {
        this.neId = neId;
    }

    public String getNeName() {
        return neName;
    }

    public void setNeName(String neName) {
        this.neName = neName;
    }

    public String getEquipName() {
        return equipName;
    }

    public void setEquipName(String equipName) {
        this.equipName = equipName;
    }

    public String getTpId() {
        return tpId;
    }

    public void setTpId(String tpId) {
        this.tpId = tpId;
    }

    public String getTpName() {
        return tpName;
    }

    public void setTpName(String tpName) {
        this.tpName = tpName;
    }

    public Map <String, Object> getExtendedFields() {
        return extendedFields;
    }

    public void setExtendedFields(Map <String, Object> extendedFields) {
        this.extendedFields = extendedFields;
    }
}
