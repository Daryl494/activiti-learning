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
 * date: 2022/11/19
 */
public class Test07_ExclusiveGateWay {
    /**
     * 部署流程
     */
    @Test
    public void test01() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/gateway/evection_exclusive.bpmn20.xml")
                .addClasspathResource("bpmn/gateway/evection_exclusive.png")
                .name("出差申请_exclusiveGateway")
                .deploy();
        System.out.println("部署id:" + deployment.getId());
        System.out.println(deployment.getName());
        System.out.println(deployment.getKey());
    }

    /**
     * 创建流程实例
     */
    @Test
    public void test02() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = engine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection_exclusive");
        System.out.println(processInstance.getProcessInstanceId());
        System.out.println(processInstance.getProcessDefinitionKey());
        System.out.println(processInstance.getName());
    }

    /**
     * 完成任务，流转到网关
     */
    @Test
    public void test03() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        // 添加“申请天数”参数，让网关能够决策
        Evection evection = new Evection();
        evection.setNum(4d);
        Map<String, Object> variables = new HashMap<>();
        variables.put("evection", evection);
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("evection_exclusive")
                .taskAssignee("lisi").singleResult();
        if (task != null) {
            taskService.complete(task.getId(), variables);
        }
    }
}
