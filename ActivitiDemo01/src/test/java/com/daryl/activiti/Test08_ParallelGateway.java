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
public class Test08_ParallelGateway {
    /**
     * 部署流程
     */
    @Test
    public void test01() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/gateway/parallelGateway.bpmn20.xml")
                .addClasspathResource("bpmn/gateway/evection_parallel.png")
                .name("parallelGateway").deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void test02() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = engine.getRuntimeService();
        // 创建时指定天数
        Evection evection = new Evection();
        evection.setNum(5d);
        Map<String, Object> map = new HashMap<>();
        map.put("evection", evection);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection_parallel", map);
        System.out.println(processInstance.getName());
        System.out.println(processInstance.getProcessDefinitionKey());
        System.out.println(processInstance.getProcessInstanceId());
    }
    /**
     * 完成任务
     */
    @Test
    public void test03(){
        // 到了并行网关时会有lisi和wangwu两个人的任务，只有当两个人都各自完成自己的任务后才会到下一步
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("evection_parallel")
                .taskAssignee("wangwu").singleResult();
        if (task!=null){
            System.out.println("完成任务: "+ task.getId());
            taskService.complete(task.getId());
        }
    }
}
