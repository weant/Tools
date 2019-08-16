package com.hcop.ptn.common.beans;

import java.io.Serializable;

public class InternalNe implements Serializable {
    private String neId;
    private String neName;
    private String neIp;
    private String communicationState;
    private String neType;
    private String neVersion;
    private String supervisionState;
    private int emlId;
    private int groupId;
    private String nprId;

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

    public String getNeIp() {
        return neIp;
    }

    public void setNeIp(String neIp) {
        this.neIp = neIp;
    }

    public String getCommunicationState() {
        return communicationState;
    }

    public void setCommunicationState(String communicationState) {
        this.communicationState = communicationState;
    }

    public String getNeType() {
        return neType;
    }

    public void setNeType(String neType) {
        this.neType = neType;
    }

    public String getNeVersion() {
        return neVersion;
    }

    public void setNeVersion(String neVersion) {
        this.neVersion = neVersion;
    }

    public String getSupervisionState() {
        return supervisionState;
    }

    public void setSupervisionState(String supervisionState) {
        this.supervisionState = supervisionState;
    }

    public int getEmlId() {
        return emlId;
    }

    public void setEmlId(int emlId) {
        this.emlId = emlId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getNprId() {
        return nprId;
    }

    public void setNprId(String nprId) {
        this.nprId = nprId;
    }

    @Override
    public int hashCode() {
        return 31 + ((neId == null) ? 0 : neId.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        InternalNe other = (InternalNe) obj;
        if (neId == null) {
            if (other.neId != null) {
                return false;
            }
        } else if (!neId.equals(other.neId)) {
            return false;
        }
        return true;
    }
}
