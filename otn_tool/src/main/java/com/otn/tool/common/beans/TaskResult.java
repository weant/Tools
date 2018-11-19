/**
 * FileName: TaskResult
 * Author:   Administrator
 * Date:     2018/11/19 23:04
 * Description: TaskResult
 */
package com.otn.tool.common.beans;

public class TaskResult {
    long id;
    long taskId;
    long executeTime;
    long state;
    String data;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
