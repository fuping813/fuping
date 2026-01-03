import com.tg.ums.service.ImportService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

@Configuration
@ComponentScan("com.tg.ums")
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class DirectExcelImporter {
    public static void main(String[] args) {
        // 创建Spring上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DirectExcelImporter.class);
        
        try {
            // 获取ImportService实例
            ImportService importService = context.getBean(ImportService.class);
            
            // 读取Excel文件
            File file = new File("数据库课程--知识点.xlsx");
            FileInputStream fis = new FileInputStream(file);
            
            // 创建MockMultipartFile对象
            MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "数据库课程--知识点.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                fis
            );
            
            // 调用导入方法
            System.out.println("开始导入数据库课程和知识点...");
            Map<String, Object> result = importService.importDatabaseCourseAndKnowledgePoints(mockMultipartFile);
            
            // 输出导入结果
            System.out.println("导入完成!");
            System.out.println("课程数量: " + ((java.util.List<?>) result.get("courses")).size());
            System.out.println("章节数量: " + ((java.util.List<?>) result.get("chapters")).size());
            System.out.println("知识点数量: " + ((java.util.List<?>) result.get("knowledgePoints")).size());
            
        } catch (Exception e) {
            System.err.println("导入失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭Spring上下文
            context.close();
        }
    }
}