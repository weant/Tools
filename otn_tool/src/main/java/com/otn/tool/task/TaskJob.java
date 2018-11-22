/**
 * FileName: TaskJob
 * Author:   Administrator
 * Date:     2018/11/21 22:04
 * Description: TaskJob
 */
package com.otn.tool.task;

import com.otn.tool.common.beans.Task;
import com.otn.tool.common.beans.TaskGroup;
import com.otn.tool.common.utils.TimeUtilities;

import java.util.Calendar;
import java.util.Date;

public class TaskJob {
    private TaskGroup taskGroup;

    public TaskJob(TaskGroup taskGroup){
        this.taskGroup = taskGroup;
    }

    public TaskGroup getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(TaskGroup taskGroup) {
        this.taskGroup = taskGroup;
    }

    public void execute(){
        for(TaskApi task : taskGroup.getTasks()) {
            ThreadPool.instance().execute(task);
        }
    }

    public int getCalendarValue(int value) {
        Calendar staC = Calendar.getInstance();
        staC.setTime(new Date(TimeUtilities.instance().parse("yyyy-MM-dd HH:mm:ss", taskGroup.getStartTime()).getTime()));
        switch(value) {
            case Calendar.YEAR:
                return staC.get(Calendar.YEAR);
            case Calendar.MONTH:
                return staC.get(Calendar.MONTH) + 1;
            case Calendar.DATE:
                return staC.get(Calendar.DATE);
            case Calendar.HOUR:
                return staC.get(Calendar.HOUR_OF_DAY);
            case Calendar.MINUTE:
                return staC.get(Calendar.MINUTE);
            case Calendar.SECOND:
                return staC.get(Calendar.SECOND);
            case Calendar.DAY_OF_WEEK:
                return staC.get(Calendar.DAY_OF_WEEK);
            default:
                return 0;
        }
    }
}
