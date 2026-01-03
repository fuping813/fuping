import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBCryptHash {
    public static void main(String[] args) {
        String password = "admin123";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        System.out.println("Raw password: " + password);
        System.out.println("Encoded password: " + encodedPassword);
    }
}