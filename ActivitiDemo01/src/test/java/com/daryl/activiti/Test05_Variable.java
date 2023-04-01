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
 * date: 2022/11/15
 */
public class Test05_Variable {
    /**
     * 部署
     */
    @Test
    public void test01() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/evection_variable.bpmn20.xml")
                .addClasspathResource("bpmn/evection_variable.png")
                .name("出差申请流程-流程变量").deploy();
        System.out.println("流程部署id:" + deployment.getId());
        System.out.println("流程部署的名称:" + deployment.getName());
    }

    /**
     * 1. 启动流程实例时设置流程变量
     */
    @Test
    public void test02() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = engine.getRuntimeService();
        // 创建变量集合
        Map<String, Object> variable = new HashMap<>();
        // 创建出差对象pojo
        Evection evection = new Evection();
        // 设置出差天数
        evection.setNum(2d);
        // 添加流程变量到map中
        variable.put("evection", evection);     // map的key与分支上的key对象
        // 设置assignee的取值
        variable.put("assignee0", "张三");
        variable.put("assignee1", "李四");
        variable.put("assignee2", "王五");
        variable.put("assignee3", "赵财务");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection_variable", variable);

        System.out.println("获取流程实例id: " + processInstance.getProcessInstanceId());
        System.out.println("获取流程定义key: " + processInstance.getProcessDefinitionKey());
    }


    /**
     * 完成任务
     */
    @Test
    public void test03() {
        String processDefinitionKey = "evection_variable";
//        String assignee = "张三";
        String assignee = "李四";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskAssignee(assignee)
                .singleResult();
        Map<String, Object> processVariables = task.getTaskLocalVariables();
        processVariables.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + "---" + entry.getValue());
        });
        if (task != null) {
            taskService.complete(task.getId());
            System.out.println("任务执行完成...");
        }
    }

    /**
     * 2. 完成任务时设置流程变量
     */
    public void test04() {
        String processDefinitionKey = "evection_variable";
        String assignee = "张三";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskAssignee(assignee)
                .singleResult();
        // 设置流程变量并填充
        Map<String, Object> variable = new HashMap<>();
        Evection evection = new Evection();
        evection.setNum(4d);
        variable.put("evection", evection);
        // 完成任务的同时设置流程变量
        if (task != null) {
            taskService.complete(task.getId(), variable);
        }
    }

    /**
     * 3. 通过流程实例id来设置流程变量
     */
    @Test
    public void test05() {
        String executionId = "xxx";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = engine.getRuntimeService();
        // 设置流程变量并填充
        Evection evection = new Evection();
        evection.setNum(3d);
        // 一次设置一个值
//        runtimeService.setVariable(executionId, "evection", evection);

        // 一次设置多个值，用map包裹
        Map<String, Object> variable = new HashMap<String, Object>() {{
            put("evection", evection);
        }};
        runtimeService.setVariables(executionId, variable);
    }

    /**
     * 4. 通过任务ID设置流程变量（全局）
     */
    @Test
    public void test06() {
        String taskId = "xxx";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        // 设置流程变量并填充
        Evection evection = new Evection();
        evection.setNum(10d);
        // 一次设置一个值
//        taskService.setVariable(taskId, "evection", evection);

        // 一次设置多个值，用map包裹
        Map<String, Object> variable = new HashMap<String, Object>() {{
            put("evection", evection);
        }};
        taskService.setVariables(taskId, variable);
    }
}
