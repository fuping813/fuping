import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.base.User;
import com.tg.ums.service.base.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@ComponentScan("com.tg.ums")
public class AddUsers {
    public static void main(String[] args) {
        // 创建Spring上下文
        ApplicationContext context = new AnnotationConfigApplicationContext(AddUsers.class);
        
        // 获取UserService实例
        UserService userService = context.getBean(UserService.class);
        
        // 创建BCrypt密码编码器
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // 创建专业主任用户
        User majorDirector = new User();
        majorDirector.setUsername("major_director");
        majorDirector.setPassword(passwordEncoder.encode("123456"));
        majorDirector.setRole(RoleConstants.MAJOR_DIRECTOR);
        majorDirector.setName("专业主任");
        majorDirector.setEmail("major_director@example.com");
        majorDirector.setPhone("13800138001");
        majorDirector.setStatus("enabled");
        
        // 创建授课教师用户
        User teacher = new User();
        teacher.setUsername("teacher");
        teacher.setPassword(passwordEncoder.encode("123456"));
        teacher.setRole(RoleConstants.TEACHER);
        teacher.setName("授课教师");
        teacher.setEmail("teacher@example.com");
        teacher.setPhone("13800138002");
        teacher.setStatus("enabled");
        
        try {
            // 保存用户
            userService.saveUser(majorDirector);
            System.out.println("专业主任用户创建成功");
            
            userService.saveUser(teacher);
            System.out.println("授课教师用户创建成功");
        } catch (Exception e) {
            System.err.println("创建用户失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭上下文
            ((AnnotationConfigApplicationContext) context).close();
        }
    }
}