package com.hcop.otn.common.beans;

public class InternalTp {
    private String neId;
    private String neName;
    private String tpId;
    private String ptpId;
    private String tpName;
    private String ifIndex;
    private String equipName;
    private String frequency;
    private String effectiveRate;

    public String getNeId() {
        return neId;
    }

    public void setNeId(String neId) { this.neId = neId; }

    public String getNeName() { return neName; }

    public void setNeName(String neName) { this.neName = neName; }

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

    public void setTpName(String tpName) {
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
