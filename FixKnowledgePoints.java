import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class FixKnowledgePoints {
    public static void main(String[] args) {
        try {
            // 加载JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 建立数据库连接
            String url = "jdbc:mysql://localhost:3306/tg_undergraduate_major_management?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false";
            String username = "root";
            String password = "060813";
            Connection conn = DriverManager.getConnection(url, username, password);
            
            // 查询所有章节
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
            
            // 查询知识点数据，查看当前状态
            System.out.println("\n修复前的知识点数据（前10条）：");
            rs = stmt.executeQuery("SELECT point_id, point_name, point_level, chapter_id, parent_id FROM knowledge_point LIMIT 10");
            System.out.println("point_id\tpoint_name\tlevel\tchapter_id\tparent_id");
            while (rs.next()) {
                int pointId = rs.getInt("point_id");
                String pointName = rs.getString("point_name");
                int level = rs.getInt("point_level");
                int chapterId = rs.getInt("chapter_id");
                Integer parentId = rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null;
                System.out.println(pointId + "\t\t" + pointName + "\t\t" + level + "\t\t" + chapterId + "\t\t" + parentId);
            }
            
            // 修复知识点数据
            // 1. 为所有知识点分配章节（假设章节ID为1）
            System.out.println("\n开始修复知识点数据...");
            
            // 首先获取可用的章节ID
            rs = stmt.executeQuery("SELECT chapter_id FROM chapter LIMIT 1");
            int chapterId = 1; // 默认章节ID
            if (rs.next()) {
                chapterId = rs.getInt("chapter_id");
            }
            System.out.println("使用章节ID: " + chapterId);
            
            // 2. 更新所有知识点的章节ID
            String updateChapterSql = "UPDATE knowledge_point SET chapter_id = ?";
            PreparedStatement updateChapterStmt = conn.prepareStatement(updateChapterSql);
            updateChapterStmt.setInt(1, chapterId);
            int chapterUpdateCount = updateChapterStmt.executeUpdate();
            System.out.println("更新了 " + chapterUpdateCount + " 个知识点的章节ID");
            
            // 3. 清空一级知识点的父知识点
            String updateParentSql = "UPDATE knowledge_point SET parent_id = NULL WHERE point_level = 1";
            PreparedStatement updateParentStmt = conn.prepareStatement(updateParentSql);
            int parentUpdateCount = updateParentStmt.executeUpdate();
            System.out.println("清空了 " + parentUpdateCount + " 个一级知识点的父知识点");
            
            // 查询修复后的知识点数据
            System.out.println("\n修复后的知识点数据（前10条）：");
            rs = stmt.executeQuery("SELECT point_id, point_name, point_level, chapter_id, parent_id FROM knowledge_point LIMIT 10");
            System.out.println("point_id\tpoint_name\tlevel\tchapter_id\tparent_id");
            while (rs.next()) {
                int pointId = rs.getInt("point_id");
                String pointName = rs.getString("point_name");
                int level = rs.getInt("point_level");
                int updatedChapterId = rs.getInt("chapter_id");
                Integer updatedParentId = rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null;
                System.out.println(pointId + "\t\t" + pointName + "\t\t" + level + "\t\t" + updatedChapterId + "\t\t" + updatedParentId);
            }
            
            // 关闭资源
            rs.close();
            stmt.close();
            updateChapterStmt.close();
            updateParentStmt.close();
            conn.close();
            
            System.out.println("\n知识点数据修复完成！");
            System.out.println("1. 所有知识点已分配到章节ID: " + chapterId);
            System.out.println("2. 所有一级知识点的父知识点已清空");
            System.out.println("3. 现在添加知识点功能应该能正常使用了");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}