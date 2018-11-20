package com.otn.tool.common.db;

import com.otn.tool.common.beans.Task;
import com.otn.tool.common.beans.TaskGroup;
import com.otn.tool.common.db.dbrunner.DbRunner;
import com.otn.tool.common.db.tool.ToolDbMgr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {

    private DbRunner tool = new DbRunner(ToolDbMgr.instance());
    private static TaskDao ourInstance = new TaskDao();

    public static TaskDao getInstance() {
        return ourInstance;
    }

    private TaskDao() {
    }

    /**
     * 获取全部任务组
     */
    public List<TaskGroup> getAllTaskGroups() {
        StringBuilder sql = new StringBuilder();
        sql.append("select id, name, interval, unit, starttime as startTime, " +
                "endtime as endTime, lastexecutetime as lastExecuteTime, state from taskgroup order by name");

        try {
            return new ArrayList<>(tool.queryBeanCollection(sql.toString(), TaskGroup.class,( Object[])null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取全部任务
     */
    public List<Task> getAllTasks() {
        StringBuilder sql = new StringBuilder();
        sql.append("select id, name, groupid as groupId, params from task");

        try {
            return new ArrayList<>(tool.queryBeanCollection(sql.toString(), Task.class,( Object[])null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
