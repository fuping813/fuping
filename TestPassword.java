import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("Encoded password: " + encodedPassword);
        
        // 数据库中的密码
        String dbPassword = "$2a$10$N.zmdr9k7uOCQb35c5hcCeywF7lWv4Xgs5lBl5Cz8Q6v4aGtXn1zu";
        System.out.println("Password match: " + passwordEncoder.matches(rawPassword, dbPassword));
    }
}