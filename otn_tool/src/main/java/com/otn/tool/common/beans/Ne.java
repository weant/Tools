package com.otn.tool.common.beans;

import java.io.Serializable;

public class Ne implements Serializable {
    private String id;
    private String name;
    private String ipAddress;
    private String communicationState;
    private String type;
    private String version;
    private String supervisionState;
    private int emlId;
    private int groupId;
    private String nprId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCommunicationState() {
        return communicationState;
    }

    public void setCommunicationState(String communicationState) {
        this.communicationState = communicationState;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSupervisionState() {
        return supervisionState;
    }

    public void setSupervisionState(String supervisionState) {
        this.supervisionState = supervisionState;
    }

    public int getEmlId() { return emlId; }

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
        return 31 + ((id == null) ? 0 : id.hashCode());
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
        Ne other = (Ne) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
