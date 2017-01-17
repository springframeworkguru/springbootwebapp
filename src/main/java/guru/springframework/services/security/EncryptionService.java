package guru.springframework.services.security;

/**
 * Created by jt on 12/14/15.
 */
public interface EncryptionService {
    String encryptString(String input);
    boolean checkPassword(String plainPassword, String encryptedPassword);
}
