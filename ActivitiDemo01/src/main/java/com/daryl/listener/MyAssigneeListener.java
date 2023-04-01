package com.daryl.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * author：Daryl
 * date: 2022/11/15
 */
public class MyAssigneeListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        if ("创建请假单".equals(delegateTask.getName()) && "create".equals(delegateTask.getEventName())) {
            delegateTask.setAssignee("张三-Listener");
        }
    }
}
