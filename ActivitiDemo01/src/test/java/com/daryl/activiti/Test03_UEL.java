package com.daryl.activiti;

import jdk.internal.org.objectweb.asm.Handle;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * author：Daryl
 * date: 2022/11/15
 */
public class Test03_UEL {
    /**
     * 部署一个assignee是uel表达式的流程
     */
    @Test
    public void test01() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/evection_uel.bpmn20.xml")
                .addClasspathResource("bpmn/evection_uel.png").name("出差申请流程_uel").deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }
    /**
     * 创建一个流程实例
     *  给流程定义中的UEL表达式赋值
     */
    @Test
    public void test02(){
        // 获取流程引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 获取RuntimeService对象
        RuntimeService runtimeService = engine.getRuntimeService();
        // 设置assignee的取值
        Map<String, Object> map=new HashMap<String, Object>(){{
            put("assignee0","张三");
            put("assignee1","李四");
            put("assignee2","王五");
            put("assignee3","赵财务");
        }};
        // 创建流程实例， 在创建时传入assignee的取值
        runtimeService.startProcessInstanceByKey("evection_uel",map);
    }
}
