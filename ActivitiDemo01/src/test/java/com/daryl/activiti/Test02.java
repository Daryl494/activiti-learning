package com.daryl.activiti;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import java.util.List;

/**
 * author：Daryl
 * date: 2022/11/13
 */
public class Test02 {
    /**
     * 启动流程实例，添加businessKey来对业务进行关联
     */
    @Test
    public void test01() {
        // 1. 获取ProcessEngine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取RuntimeService对象
        RuntimeService runtimeService = engine.getRuntimeService();
        // 3. 启动流程实例
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("evection", "1001");
        // 4. 输出processInstance相关属性
        System.out.println(instance.getProcessInstanceId() + "---" + instance.getBusinessKey() + "---" + instance.getName() + "---");
        RepositoryService repositoryService = engine.getRepositoryService();
    }

    /**
     * 全部流程挂起与激活
     */
    @Test
    public void test02() {
        //1. 获取Process Engine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        //2. 获取Repository Service对象
        RepositoryService repositoryService = engine.getRepositoryService();
        //3. 查询流程定义的对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("evection").singleResult();
        //4. 获取当前流程的状态
        boolean isSuspended = processDefinition.isSuspended();
        String processDefinitionId = processDefinition.getId();
        //5. 如果是激活就挂起，如果是挂起就激活
        if (isSuspended) {
            // 当前状态为挂起状态，则将其激活，此处通过流程id激活，也可以通过流程定义key进行激活
            repositoryService.activateProcessDefinitionById(processDefinitionId,
                    true, // 是否挂起流程定义下的所有实例
                    null); // 挂起时间，可不传
            System.out.println("流程定义: " + processDefinitionId + "已被激活");
        } else {
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            System.out.println("流程定义: " + processDefinitionId + "已被挂起");
        }
    }

    /**
     * 单个流程实例的挂起与激活
     */
    @Test
    public void test03() {
        // 1. 获取Process Engine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取Runtime Service对象
        RuntimeService runtimeService = engine.getRuntimeService();
        // 3. 获取流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("12501").singleResult();
        // 4. 获取相关的状态操作
        boolean suspended = processInstance.isSuspended();
        String processInstanceId = processInstance.getId();
        // 5. 如果流程实例为激活则将其挂起，如果流程实例为挂起则将其激活
        if (suspended) {
            // 挂起 -> 激活
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println("流程实例: " + processInstanceId + "已被激活");
        } else {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println("流程实例: " + processInstanceId + "已被挂起");
        }
    }
}
