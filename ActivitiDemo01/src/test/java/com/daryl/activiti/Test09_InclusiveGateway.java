package com.daryl.activiti;

import com.daryl.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * author：Daryl
 * date: 2022/11/20
 */
public class Test09_InclusiveGateway {
    /**
     * 部署流程
     */
    @Test
    public void test01() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/gateway/evection_inclusive.bpmn20.xml")
                .addClasspathResource("bpmn/gateway/evection_inclusive.png")
                .name("inclusiveGateway")
                .deploy();
        System.out.println(deployment.getName());
        System.out.println(deployment.getId());
    }

    /**
     * 创建流程实例
     */
    @Test
    public void test02() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = engine.getRuntimeService();
        Evection evection = new Evection();
        evection.setNum(8d);
        Map<String, Object> variables = new HashMap<>();
        variables.put("evection", evection);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection_inclusive", variables);
        System.out.println(processInstance.getProcessDefinitionKey());
        System.out.println(processInstance.getProcessInstanceId());
    }
    /**
     * 完成任务
     */
    @Test
    public void test03(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("evection_inclusive")
                .taskAssignee("chenba").singleResult();
        if (task!=null){
            taskService.complete(task.getId());
            System.out.println("执行任务: "+ task.getId());
        }
    }
}

