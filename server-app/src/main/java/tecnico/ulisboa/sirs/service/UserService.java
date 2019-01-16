package tecnico.ulisboa.sirs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tecnico.ulisboa.sirs.model.Hospital;
import tecnico.ulisboa.sirs.model.Record;
import tecnico.ulisboa.sirs.model.Role;
import tecnico.ulisboa.sirs.model.User;
import tecnico.ulisboa.sirs.repository.HospitalRepository;
import tecnico.ulisboa.sirs.repository.RecordRepository;
import tecnico.ulisboa.sirs.repository.RoleRepository;
import tecnico.ulisboa.sirs.repository.UserRepository;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("userService")
public class UserService {


    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private RecordRepository recordRepository;
    private HospitalRepository hospitalRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private KmsManager kmsManager;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       RecordRepository recordRepository,
                       HospitalRepository hospitalRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       KmsManager kmsManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.hospitalRepository = hospitalRepository;
        this.recordRepository = recordRepository;
        this.kmsManager = kmsManager;
    }

    public User findUserByCardIdAndDecrypt(String id) {
        User user = userRepository.findByCard(id);
        if (user != null) {
            return kmsManager.decryptUser(user, kmsManager.getKey(id));
        }
        return null;
    }

    private User findUserByCardId(String id) {
        return userRepository.findByCard(id);
    }

    public Hospital findHospitalByNameCityCountry(String name, String city, String country) {
        return hospitalRepository.findByNameAndAndCityAndCountry(name, city, country);
    }

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public String saveHospital(Hospital hospital) {
        hospitalRepository.save(hospital);
        return "Hospital Saved Successfully";
    }

    public String saveUser(User user) {
        Role userRole = roleRepository.findByRole(user.getRole().getRole());
        Hospital hospital = hospitalRepository.findByName(user.getHospitals().get(0).getName());
        if (hospital == null) {
            return "Error with hospital";
        }
        if (userRole == null) {
            return "Invalid Role";
        } else {
            kmsManager.addUserKms(user.getCard());
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setActive(1);
            user.setRole(userRole);
            user.getHospitals().clear();
            user.getHospitals().add(hospital);
            Record record = new Record();
            record.setAuthor("Default");
            record.setVersion(0);
            record.setDescription("Default Empty Description to a default Record");

            SecretKey userKey = kmsManager.getKey(user.getCard());
            Record encryptedRecord = kmsManager.encryptRecord(record, userKey);
            kmsManager.encryptUser(user, userKey);
            user.getRecords().add(encryptedRecord);
            userRepository.save(user);
            recordRepository.save(encryptedRecord);

        }
        return "User registered successfully";
    }

    public List<Record> getLast5Records(User user) {
        List<Record> records;
        List<Record> cloneRecords = new ArrayList<>();
        SecretKey key = kmsManager.getKey(user.getCard());
        if (user.getRecords().size() > 5) {
            records = user.getRecords().subList(user.getRecords().size() - 5, user.getRecords().size());
            for (Record record : records) {
                cloneRecords.add(kmsManager.decryptRecord(record, key));
            }
            Collections.reverse(cloneRecords);
            return cloneRecords;
        } else {
            records = user.getRecords();
            for (Record record : records) {
                cloneRecords.add(kmsManager.decryptRecord(record, key));
            }
            Collections.reverse(cloneRecords);
            return cloneRecords;

        }
    }

    public String saveRecord(Record record, User user, User author) {
        Record lastRecord = user.getRecords().get(user.getRecords().size() - 1);
        record.setVersion(lastRecord.getVersion() + 1);
        record.setAuthor(author.getName() + " " + author.getLastName() + " ID: " + author.getCard());
        Record cloneRecord = kmsManager.encryptRecord(record, kmsManager.getKey(user.getCard()));
        user.getRecords().add(cloneRecord);
        recordRepository.save(cloneRecord);
        return "Record Saved";
    }

    public String changePassword(User user, PasswordHolder passwordHolder) {
        User actualUser = findUserByCardId(user.getCard());
        if (!bCryptPasswordEncoder.matches(passwordHolder.getOldPassword(), actualUser.getPassword())) {
            return "Old password incorrect";
        } else if (!passwordHolder.getNewPassword().equals(passwordHolder.getNewPasswordRepeat())) {
            return "New Passwords don't match";
        } else {
            actualUser.setPassword(bCryptPasswordEncoder.encode(passwordHolder.getNewPassword()));
            userRepository.save(actualUser);
            return "Password Changed";
        }
    }

}