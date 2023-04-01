package com.daryl.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

/**
 * author：Daryl
 * date: 2022/11/15
 */
public class Test04_Listener {
    /**
     * 部署流程
     */
    @Test
    public void test04(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/evection_listener.bpmn20.xml")
                .addClasspathResource("bpmn/evection_listener.png")
                .name("evection_listener").deploy();
        System.out.println(deployment.getId()+"---"+deployment.getName());
    }

    /**
     * 启动流程，然后到了进入“创建请假单”流程时就会触发监听器
     */
    @Test
    public void test05(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = engine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection_listener");
    }
}

