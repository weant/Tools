package com.otn.tool.common.task;

import com.otn.tool.common.mvc.MyController;

import javax.swing.*;

public class TaskController extends MyController {
    private TaskModel model = new TaskModel();
    private TaskView view = new TaskView();

    public TaskController() {
        super();
        getFrame().setJMenuBar(view.getMenuBar());
        this.setUpMVC(model, view);
    }

    public static void main(String[] args) {
        TaskController controller = new TaskController();
        controller.showView(JFrame.EXIT_ON_CLOSE);
    }
}
