package com.daryl;

import com.daryl.utils.SecurityUtil;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.model.payloads.StartProcessPayload;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.model.payloads.ClaimTaskPayload;
import org.activiti.api.task.runtime.TaskRuntime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ActivitiDemo02SpringApplicationTests {

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;
    @Autowired
    private SecurityUtil securityUtil;

    @Test
    void contextLoads() {
        System.out.println(taskRuntime);
    }

    /**
     * 查询流程定义的数量的同时自动部署流程(需在配置文件中设置    check-process-definitions: true)
     */
    @Test
    void testAutoDeploy() {
        // 因为配置了springSecurity，因此需要先登录
        securityUtil.logInAs("system");
        // 分页查询流程定义
        Page<ProcessDefinition> processDefinitionPage = processRuntime
                .processDefinitions(Pageable.of(0, 10));
        System.out.println("流程定义数量:" + processDefinitionPage.getTotalItems());
        for (ProcessDefinition processDefinition : processDefinitionPage.getContent()) {
            System.out.println("流程定义: " + processDefinition);
        }
    }

    /**
     * 创建流程实例
     */
    @Test
    void testCreateInstance() {
        securityUtil.logInAs("system");
        // 创建流程实例的方法与先前的不一样
        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder.start()
                .withProcessDefinitionKey("evection")
                .build());
        System.out.println(processInstance.getProcessDefinitionKey());
        System.out.println(processInstance.getId());
    }

    /**
     * 任务查询、拾取和完成
     */
    @Test
    void taskComplete() {
        /*
         会自动关联此处登录的用户进行查询/拾取/完成操作。
         且由于之前设置task的candidateGroup为activitiTeam，
         而system用户不在这里面，所以查询不到相关的任务，也完成不了
         只有通过在activitiTeam里面的用户才能完成任务
         */
        securityUtil.logInAs("jack");
        Page<Task> taskPage = taskRuntime.tasks(Pageable.of(0, 10));
        if (taskPage != null && taskPage.getTotalItems() > 0) {
            for (Task task : taskPage.getContent()) {
                // 拾取任务
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
                System.out.println("拾取任务:" + task.getId());
                // 完成任务
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
                System.out.println("完成任务: " + task.getId());
            }
        }
        Page<Task> taskPage2 = taskRuntime.tasks(Pageable.of(0, 10));
        if (taskPage2 != null && taskPage2.getTotalItems() > 0) {
            System.out.println(taskPage2);
        }
    }
}
