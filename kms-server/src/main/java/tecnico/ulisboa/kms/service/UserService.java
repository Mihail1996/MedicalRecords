package tecnico.ulisboa.kms.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import tecnico.ulisboa.kms.model.User;
import tecnico.ulisboa.kms.repository.UserRepository;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service("userService")
public class UserService {

    private UserRepository userRepository;

    private Base64.Encoder encoder64 = Base64.getEncoder();
    private Base64.Decoder decoder64 = Base64.getDecoder();

    private final String masterKey64 = "DvAOeNK011Q1k7OChWPrqA==";
    private final String keyEncryptKey = "jbHZdudXJbdh7ksxXlkIWw==";
    private final SecretKey kek = new SecretKeySpec(decoder64.decode(keyEncryptKey), "AES");
    private final SecretKey masterKey = new SecretKeySpec(decoder64.decode(masterKey64), "AES");

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getKeyByCardId(String card) {
        User user = userRepository.findByCard(card);
        Cipher c;
        try {
            c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, kek);
            byte[] dek = c.doFinal(decoder64.decode(user.getSecretKey()));
            c.init(Cipher.ENCRYPT_MODE, masterKey);
            byte[] encryptedDek = c.doFinal(dek);
            return encoder64.encodeToString(encryptedDek);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;

    }


    public User findUserByCard(String card) {
        return userRepository.findByCard(card);
    }


    public void addUser(String card) {
        if (findUserByCard(card) != null) {
            return;
        }
        User newUser = new User();
        newUser.setCard(card);
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, kek);
            newUser.setSecretKey(encoder64.encodeToString(c.doFinal(generateKey())));
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        userRepository.save(newUser);
    }

    private byte[] generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
        SecretKey aesKey = keygenerator.generateKey();
        return aesKey.getEncoded();
    }

}
