import com.tg.ums.service.ImportService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

public class ExcelImporter {
    public static void main(String[] args) {
        try {
            // 加载Spring上下文
            org.springframework.context.ApplicationContext context = 
                new org.springframework.context.annotation.AnnotationConfigApplicationContext(
                    "com.tg.ums" 
                );
            
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
            
            fis.close();
            
        } catch (Exception e) {
            System.err.println("导入失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}