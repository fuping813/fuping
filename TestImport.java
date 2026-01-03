import com.tg.ums.service.ImportService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Configuration
@ComponentScan("com.tg.ums")
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class TestImport {
    public static void main(String[] args) {
        try {
            // 创建Spring上下文
            ApplicationContext context = new AnnotationConfigApplicationContext(TestImport.class);
            
            // 获取ImportService实例
            ImportService importService = context.getBean(ImportService.class);
            
            // 调用导入方法
            System.out.println("开始导入数据库课程和知识点...");
            importService.importDatabaseCourseAndKnowledgePointsFromFile("数据库课程--知识点.xlsx");
            System.out.println("导入完成!");
            
            // 关闭上下文
            context.close();
            
        } catch (Exception e) {
            System.err.println("导入失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}