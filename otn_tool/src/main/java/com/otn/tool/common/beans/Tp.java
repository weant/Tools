package com.otn.tool.common.beans;

public class Tp {
    private Ne ne;
    private String id;
    private String ptpId;
    private String name;
    private String type;
    private String ifIndex;
    private String boardName;
    private String boardType;
    private String frequency;
    private String effectiveRate;
    private boolean used;

    public Ne getNe() {
        return ne;
    }

    public void setNe(Ne ne) {
        this.ne = ne;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPtpId() {
        return ptpId;
    }

    public void setPtpId(String ptpId) {
        this.ptpId = ptpId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIfIndex() {
        return ifIndex;
    }

    public void setIfIndex(String ifIndex) {
        this.ifIndex = ifIndex;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBoardType() {
        return boardType;
    }

    public void setBoardType(String boardType) {
        this.boardType = boardType;
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

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
