import org.activiti.engine.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * author：Daryl
 * date: 2022/11/20
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:activiti-spring.xml"} )
public class ActivitiTest {
    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void test01(){
        // 如果输出成功，且数据库表创建成功，则表示已完成集成
        System.out.println(repositoryService);
    }
}
