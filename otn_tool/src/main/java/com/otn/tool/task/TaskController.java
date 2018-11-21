package com.otn.tool.task;

import com.otn.tool.common.beans.Task;
import com.otn.tool.common.beans.TaskGroup;
import com.otn.tool.common.db.TaskDao;
import com.otn.tool.common.enums.Period;
import com.otn.tool.common.enums.TaskState;
import com.otn.tool.common.mvc.MyController;
import com.otn.tool.common.utils.I18N;
import com.otn.tool.common.utils.TimeUtilities;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskController extends MyController {
    private TaskModel model = new TaskModel();
    private TaskView view = new TaskView();
    private static final String TIMESTAMP_FORMAT="yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT="yyyy-MM-dd";
    private static final String TIME_FORMAT="HH:mm:ss";

    public TaskController() {
        super();
        getFrame().setJMenuBar(view.getMenuBar());
        this.setUpMVC(model, view);
        initData();
        // TODO: 2018/11/20 将任务添加进任务调度器
    }

    public void initData(){
        List<TaskGroup> groups = TaskDao.getInstance().getAllTaskGroups();
        if(groups != null) {
            model.setTakGroupTable(groups);
            if(!groups.isEmpty()) {
                groups.forEach(group->{
                    group.setProgress("");
                    group.setStartTime(getTimeString(group.getStartTime(), TIMESTAMP_FORMAT));
                    group.setEndTime(getTimeString(group.getEndTime(), DATE_FORMAT));
                    group.setLastExecuteTime(getTimeString(group.getLastExecuteTime(), TIMESTAMP_FORMAT));
                    group.setState(TaskState.fromInt(Integer.valueOf(group.getState())).getValueString());
                    group.setUnit(Period.fromInt(Integer.valueOf(group.getState())).getValueString());
                });
                initTaskForGrop();
            }
        }
    }

    public void initTaskForGrop(){
        List<Task> tasks = TaskDao.getInstance().getAllTasks();
        if(tasks != null) {
            tasks.forEach(task->{
                model.getTakGroupTable().forEach(group->{
                    if(group.getId() == task.getGroupId()) {
                        if(group.getTasks() == null) {
                            group.setTasks(new ArrayList<>());
                        }
                        group.getTasks().add(task);
                    }
                });
            });
        }
    }

    private String getTimeString(String longTime, String format){
        Date time = new Date();
        time.setTime(Long.parseLong(longTime));
        return TimeUtilities.instance().formatDate(time, format);
    }

    public void exitButtonPressed(){
        int choice = JOptionPane.showConfirmDialog(getFrame(), getString("tool.exit.confirm"), getString("tool.message"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(choice == 0) {
            System.exit(0);
        }
    }

    public String getString(String string) {
        return I18N.getInstance().getString(string);
    }

    public static void main(String[] args) {
        TaskController controller = new TaskController();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        controller.showView(JFrame.EXIT_ON_CLOSE);
    }
}
