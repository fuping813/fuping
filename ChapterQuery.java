import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ChapterQuery {
    public static void main(String[] args) {
        try {
            // 加载JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 建立数据库连接
            String url = "jdbc:mysql://localhost:3306/tg_undergraduate_major_management?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false";
            String username = "root";
            String password = "123456";
            Connection conn = DriverManager.getConnection(url, username, password);
            
            // 查询章节数据
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT chapter_id, chapter_name, course_id FROM chapter");
            
            System.out.println("章节数据：");
            System.out.println("chapter_id\tchapter_name\tcourse_id");
            while (rs.next()) {
                int chapterId = rs.getInt("chapter_id");
                String chapterName = rs.getString("chapter_name");
                int courseId = rs.getInt("course_id");
                System.out.println(chapterId + "\t\t" + chapterName + "\t\t" + courseId);
            }
            
            // 查询知识点数据
            rs = stmt.executeQuery("SELECT point_id, point_name, level, chapter_id, parent_id FROM knowledge_point");
            
            System.out.println("\n知识点数据（前20条）：");
            System.out.println("point_id\tpoint_name\tlevel\tchapter_id\tparent_id");
            int count = 0;
            while (rs.next() && count < 20) {
                int pointId = rs.getInt("point_id");
                String pointName = rs.getString("point_name");
                int level = rs.getInt("level");
                int chapterId = rs.getInt("chapter_id");
                Integer parentId = rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null;
                System.out.println(pointId + "\t\t" + pointName + "\t\t" + level + "\t\t" + chapterId + "\t\t" + parentId);
                count++;
            }
            
            // 关闭资源
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}