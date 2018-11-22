/**
 * FileName: TaskGroup
 * Author:   Administrator
 * Date:     2018/11/19 22:56
 * Description: TaskGroup
 */
package com.otn.tool.common.beans;

import com.otn.tool.task.TaskApi;

import java.util.List;

public class TaskGroup {
    long id;
    String name;
    int interval;
    String unit;
    String state;
    String startTime;
    String endTime;
    String lastExecuteTime;
    String progress;
    List<TaskApi> tasks;

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

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLastExecuteTime() {
        return lastExecuteTime;
    }

    public void setLastExecuteTime(String lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public List<TaskApi> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskApi> tasks) {
        this.tasks = tasks;
    }
}
