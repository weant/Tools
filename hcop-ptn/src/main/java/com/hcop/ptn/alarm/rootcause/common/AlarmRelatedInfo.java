/**
 * FileName: AlarmRelatedInfo
 * Author:   Administrator
 * Date:     2019/3/30 15:52
 * Description:
 */
package com.hcop.ptn.alarm.rootcause.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmRelatedInfo {
    private String neName;
    private AlarmCategoryEnum categoryEnum;//告警本身的类型
    private AlarmCategoryEnum serviceEnum;//告警业务的类型
    private List<String> relatedInfo = new ArrayList <>();
    private Set<String> rcPcIds = new HashSet<>();

    public String getNeName() {
        return neName;
    }

    public void setNeName(String neName) {
        this.neName = neName;
    }

    public AlarmCategoryEnum getCategoryEnum() {
        return categoryEnum;
    }

    public void setCategoryEnum(AlarmCategoryEnum categoryEnum) {
        this.categoryEnum = categoryEnum;
    }

    public AlarmCategoryEnum getServiceEnum() {
        return serviceEnum;
    }

    public void setServiceEnum(AlarmCategoryEnum serviceEnum) {
        this.serviceEnum = serviceEnum;
    }

    public List<String> getRelatedInfo() {
        return relatedInfo;
    }

    public void setRelatedInfo(List<String> relatedInfo) {
        this.relatedInfo = relatedInfo;
    }

    public Set<String> getRcPcIds() {
        return rcPcIds;
    }

    public void setRcPcIds(Set<String> rcPcIds) {
        this.rcPcIds = rcPcIds;
    }
}
