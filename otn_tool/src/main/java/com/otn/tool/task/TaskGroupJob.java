/**
 * FileName: TaskGroupJob
 * Author:   Administrator
 * Date:     2018/11/21 21:36
 * Description: TaskGroupJob
 */
package com.otn.tool.task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TaskGroupJob implements Job{

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().getName();
        TaskJob task = TaskGroupManager.getInstance().getJobsMap().get(jobName);
        if(!TaskGroupManager.getInstance().checkTaskValidityNow(task)){
            return;
        }else{
            task.execute();
        }
    }
}
