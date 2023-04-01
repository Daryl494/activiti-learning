package com.daryl.activiti;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

/**
 * author：Daryl
 * date: 2022/11/19
 */
public class Test06_Group {
    /**
     * 部署组任务流程
     */
    @Test
    public void test01() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/evection-group.bpmn20.xml")
                .addClasspathResource("bpmn/evection-group.png")
                .name("请假流程-组任务")
                .deploy();
        System.out.println("部署id:" + deployment.getId());
        System.out.println("部署名称/流程名称" + deployment.getName());
    }

    /**
     * 创建流程实例（开始一个流程）
     */
    @Test
    public void test02() {
        String processDefKey = "evection-group";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = engine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefKey);
        System.out.println("流程实例id:" + processInstance.getProcessInstanceId());
        System.out.println("流程定义key:" + processInstance.getProcessDefinitionKey());
        System.out.println("流程实例名称:" + processInstance.getProcessDefinitionName());
        System.out.println("开启流程的用户:" + processInstance.getStartUserId());
    }

    /**
     * 查询组任务(根据candidate-user)
     */
    @Test
    public void test03() {
        String username = "zhangsan";
        String processDefKey = "evection-group";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(processDefKey)
                .taskCandidateUser(username)
//                .taskCandidateOrAssigned(username)
                .singleResult();
        System.out.println("taskID: " + task.getId());
        System.out.println("流程实例id:" + task.getProcessInstanceId());
    }

    /**
     * 用户拾取组任务
     */
    @Test
    public void test04() {
        String username = "zhangsan";
        String processDefKey = "evection-group";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(processDefKey)
                .taskCandidateUser(username)
                .singleResult();
        // zhangsan 拾取组任务，然后就从candidate成为了assignee，由组任务变为个人任务
        if (task != null) {
            taskService.claim(task.getId(), username);
        }
    }

    /**
     * 拾取过后完成任务
     */
    @Test
    public void test05() {
        String username = "zhangsan";
        String processDefKey = "evection-group";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(processDefKey)
                .taskAssignee(username)
                .singleResult();
        // 拾取后，zhangsan完成任务，随后流转到下一流程
        if (task != null) {
            taskService.complete(task.getId());
        }
    }

    /**
     * 王五为下一流程的候选人，此处省略拾取任务代码，直接成为assignee
     * 王五拾取后不想完成，回退给组任务 / 转交给别人处理
     */
    @Test
    public void test06() {
        String username = "wangwu";
        String processDefKey = "evection-group";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(processDefKey)
                .taskAssignee(username).singleResult();
        if (task!=null){
            String taskId = task.getId();
            System.out.println(taskId);
//            taskService.setAssignee(taskId, null);  // 回退为组任务
            taskService.setAssignee(taskId, "chenliu");   // 转交给别人处理
        }
    }

}
