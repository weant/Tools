/**
 * FileName: Task
 * Author:   Administrator
 * Date:     2018/11/19 23:04
 * Description: Task
 */
package com.otn.tool.common.beans;

public class Task {
    long id;
    String name;
    long groupId;
    String params;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
