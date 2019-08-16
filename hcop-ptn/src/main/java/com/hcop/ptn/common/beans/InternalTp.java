package com.hcop.ptn.common.beans;

public class InternalTp {
    private InternalNe ne;
    private String tpId;
    private String ptpId;
    private String tpName;
    private String ifIndex;
    private String equipName;
    private String frequency;
    private String effectiveRate;

    public InternalNe getNe() {
        return ne;
    }

    public void setNe(InternalNe ne) {
        this.ne = ne;
    }

    public String getTpId() {
        return tpId;
    }

    public void setTpId(String tpId) {
        this.tpId = tpId;
    }

    public String getPtpId() {
        return ptpId;
    }

    public String getEquipName() { return equipName; }

    public void setEquipName(String equipName) { this.equipName = equipName; }

    public void setPtpId(String ptpId) {
        this.ptpId = ptpId;
    }

    public String getTpName() {
        return tpName;
    }

    public void setTpName(String name) {
        this.tpName = tpName;
    }

    public String getIfIndex() {
        return ifIndex;
    }

    public void setIfIndex(String ifIndex) {
        this.ifIndex = ifIndex;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getEffectiveRate() {
        return effectiveRate;
    }

    public void setEffectiveRate(String effectiveRate) {
        this.effectiveRate = effectiveRate;
    }
}
