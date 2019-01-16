package tecnico.ulisboa.sirs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tecnico.ulisboa.sirs.model.Record;
import tecnico.ulisboa.sirs.model.User;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service("kmsManager")
public class KmsManager {

    Logger logger = LoggerFactory.getLogger(KmsManager.class);


    private final String username = "medicalserver";

    private final String password = "server";

    private String kmsServerLocation = "https://localhost:9443";

    private RestTemplate restTemplate = new RestTemplate();

    private String cookie = kmsLogin();


    private final String masterKey64 = "DvAOeNK011Q1k7OChWPrqA==";
    private final SecretKey masterKey = new SecretKeySpec(Base64.getDecoder().decode(masterKey64), "AES");

    private String kmsLogin() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(kmsServerLocation + "/login", HttpMethod.POST, entity, String.class);
        return response.getHeaders().getFirst(HttpHeaders.SET_COOKIE).split(";")[0];
    }

    void addUserKms(String card) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("card", card);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        restTemplate.exchange(kmsServerLocation + "/users/addUser", HttpMethod.POST, entity, Void.class);
    }

    SecretKey getKey(String card) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("card", card);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(kmsServerLocation + "/users/" + card, HttpMethod.GET, entity, String.class);
        byte[] decodedKey = Base64.getDecoder().decode(response.getBody());

        Cipher c;
        try {
            c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, masterKey);
            return new SecretKeySpec(c.doFinal(decodedKey), "AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    Record encryptRecord(Record record, SecretKey key) {
        Record cloneRecord = new Record();
        Cipher c;
        try {
            c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            cloneRecord.setAuthor(Base64.getEncoder().encodeToString(c.doFinal(record.getAuthor().getBytes())));
            cloneRecord.setDescription(Base64.getEncoder().encodeToString(c.doFinal(record.getDescription().getBytes())));
            cloneRecord.setId(record.getId());
            cloneRecord.setVersion(record.getVersion());
            return cloneRecord;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    Record decryptRecord(Record record, SecretKey key) {
        Cipher c;
        Record cloneRecord = new Record();
        try {
            c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);
            cloneRecord.setAuthor(new String(c.doFinal(Base64.getDecoder().decode(record.getAuthor()))));
            cloneRecord.setDescription(new String(c.doFinal(Base64.getDecoder().decode(record.getDescription()))));
            cloneRecord.setVersion(record.getVersion());
            cloneRecord.setId(record.getId());
            return cloneRecord;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    void encryptUser(User user, SecretKey key) {

        Cipher c;
        try {
            c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            user.setEmail(Base64.getEncoder().encodeToString(c.doFinal(user.getEmail().getBytes())));
            user.setName(Base64.getEncoder().encodeToString(c.doFinal(user.getName().getBytes())));
            user.setLastName(Base64.getEncoder().encodeToString(c.doFinal(user.getLastName().getBytes())));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    User decryptUser(User user, SecretKey key) {
        User cloneUser = new User();
        Cipher c;
        try {
            c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);
            cloneUser.setLastName(new String(c.doFinal(Base64.getDecoder().decode(user.getLastName()))));
            cloneUser.setName(new String(c.doFinal(Base64.getDecoder().decode(user.getName()))));
            cloneUser.setEmail(new String(c.doFinal(Base64.getDecoder().decode(user.getEmail()))));
            cloneUser.setActive(user.getActive());
            cloneUser.setRole(user.getRole());
            cloneUser.setHospitals(user.getHospitals());
            cloneUser.setRecords(user.getRecords());
            cloneUser.setCard(user.getCard());
            cloneUser.setPassword(user.getPassword());
            return cloneUser;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
