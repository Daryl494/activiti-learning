package com.daryl.activiti;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricDetailQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * author：Daryl
 * date: 2022/11/10
 */
public class Test01 {
    /**
     * 生成Activiti的相关表结构(创建processEngine对象时自动生成表)
     */
    @Test
    public void test01() {
        // 使用classpath下的activiti.cfg.xml中的配置来创建ProcessEngine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(engine);
    }

    /**
     * 自定义的方式来加载配置文件
     */
    @Test
    public void test02() {
        // 首先创建ProcessEngineConfiguration对象
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        // 通过ProcessEngineConfiguration对象来创建ProcessEngine
        ProcessEngine processEngine = configuration.buildProcessEngine();
    }

    /**
     * 实现单个文件的部署
     */
    @Test
    public void test03() {
        // 1. 获取ProcessEngine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取RepositoryService进行部署操作
        RepositoryService service = engine.getRepositoryService();
        // 3. 使用RepositoryService进行部署操作
        Deployment deploy = service.createDeployment()
                .addClasspathResource("bpmn/evection.bpmn20.xml")   // 添加bpmn资源
                .addClasspathResource("bpmn/evection.png")  // 添加png资源
                .name("出差申请流程-2222")
                .deploy();
        System.out.println("流程部署的id: " + deploy.getId());
        System.out.println("流程部署的名称: " + deploy.getName());
    }

    /**
     * 将文件打包成zip后部署
     */
    @Test
    public void test04() {
        // 定义zip文件的输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("bpmn/evection.zip");
        // 对inputStream进行装饰
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService service = engine.getRepositoryService();
        Deployment deploy = service.createDeployment().addZipInputStream(zipInputStream).name("出差申请流程").deploy();

        System.out.println("流程部署的id: " + deploy.getId());
        System.out.println("流程部署的名称: " + deploy.getName());
    }

    /**
     * 创建一个流程实例
     */
    @Test
    public void test05() {
        // 1. 创建ProcessEngine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取RuntimeService对象
        RuntimeService runtimeService = engine.getRuntimeService();
        // 3. 根据流程定义id启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection");
        // 4. 输出相关的流程实例信息
        System.out.println("流程定义id: " + processInstance.getProcessDefinitionId());
        System.out.println("流程定义key: " + processInstance.getProcessDefinitionKey());
        System.out.println("流程实例的id: " + processInstance.getProcessInstanceId());
    }

    /**
     * 任务查询(查询某个用户下的所有任务，流程定义key+用户)
     */
    @Test
    public void test06() {
        String assignee = "lisi";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 任务查询, 需要获取一个TaskService对象
        TaskService taskService = engine.getTaskService();
        // 根据流程定义key和任务负责人 查询任务
        List<Task> taskList = taskService.createTaskQuery().processDefinitionKey("evection").taskAssignee(assignee).list();
        System.out.println(taskList.size());
        // 输出当前用户的所有任务
        taskList.forEach(task -> {
            System.out.println("流程实例id: " + task.getProcessInstanceId());
            System.out.println("任务id: " + task.getId());
            System.out.println("任务负责人: " + task.getAssignee());
            System.out.println("任务名称: " + task.getName());
            System.out.println("---------------------");
        });
    }

    /**
     * 流程任务的处理
     */
    @Test
    public void test07() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Task task = taskService.createTaskQuery().processDefinitionKey("evection").taskAssignee("lisi").singleResult();
        // 根据id完成任务， 随后该流程便会流转到下一负责人
        taskService.complete(task.getId());
    }

    /**
     * 流程定义的查询
     */
    @Test
    public void test08() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery
                .processDefinitionKey("evection")   // 根据流程定义key查询
                .orderByProcessDefinitionVersion()      // 按照版本排序
                .desc()     // 逆序
                .list();
        processDefinitionList.forEach(processDefinition -> {
            System.out.println("流程定义key: " + processDefinition.getKey());
            System.out.println("流程定义id: " + processDefinition.getId());
            System.out.println("流程定义name: " + processDefinition.getName());
            System.out.println("流程定义version: " + processDefinition.getVersion());
            System.out.println("流程部署id: " + processDefinition.getDeploymentId());
        });
    }

    /**
     * 删除流程
     */
    @Test
    public void test09() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        // 删除流程定义，但如果已经有流程实例关联的话，就会报错
        repositoryService.deleteDeployment("10001");
        // 设置为true 表示级联操作，将关联的流程实例一同删除，如果为false则和上面不加第二个参数一样，有实例的情况会报错
//        repositoryService.deleteDeployment("2501", true);
    }

    /**
     * 流程资源下载(读取数据库中部署的流程文件 .bpmn /.png)
     */
    @Test
    public void test10() throws IOException {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        // 根据流程定义key先查出  流程definition, 获取到流程定义的相关信息
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("evection").singleResult();

        // 根据部署id和文件分别获取到对应的流
        String deploymentId = definition.getDeploymentId();
        // 获取png图片
        InputStream pngInput = repositoryService.getResourceAsStream(deploymentId, definition.getDiagramResourceName());
        // 获取bpmn文件
        InputStream bpmnInput = repositoryService.getResourceAsStream(deploymentId, definition.getResourceName());

        File pngFile = new File("e:" + File.separator + "evection.png");
        File bpmnFile = new File("e:" + File.separator + "evection.bpmn20.xml");
        OutputStream pngOut = new FileOutputStream(pngFile);
        OutputStream bpmnOut = new FileOutputStream(bpmnFile);

        IOUtils.copy(pngInput, pngOut);
        IOUtils.copy(bpmnInput, bpmnOut);

        pngInput.close();
        pngOut.close();
        bpmnInput.close();
        bpmnOut.close();
    }

    /**
     * 流程历史信息查看
     */
    @Test
    public void test11() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = engine.getHistoryService();
        HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery();
        List<HistoricActivityInstance> list = query.processDefinitionId("evection:1:2504").orderByHistoricActivityInstanceStartTime().desc().list();
        for (HistoricActivityInstance historyInstance : list) {
            System.out.println(
                    historyInstance.getActivityId() + "---"
                            + historyInstance.getActivityName()
                            + "---" + historyInstance.getActivityType()
                            + "---" + historyInstance.getActivityId()
                            + "---" + historyInstance.getAssignee()
                            + "---" + historyInstance.getProcessDefinitionId()
                            + "---" + historyInstance.getProcessInstanceId()
            );
        }
    }
}

