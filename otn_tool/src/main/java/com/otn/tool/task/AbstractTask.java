/**
 * FileName: AbstractTask
 * Author:   Administrator
 * Date:     2018/11/22 23:49
 * Description: AbstractTask
 */
package com.otn.tool.task;

import com.otn.tool.common.beans.Task;

public abstract class AbstractTask implements TaskApi{
    protected Task task;
    protected String result;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public void run() {
        doJob();
    }
}
