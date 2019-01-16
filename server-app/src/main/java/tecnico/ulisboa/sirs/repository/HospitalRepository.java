package tecnico.ulisboa.sirs.repository;

import tecnico.ulisboa.sirs.model.Hospital;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("hospitalRepository")
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {
    List<Hospital> findAll();

    Hospital findByNameAndAndCityAndCountry(String name, String city, String country);

    Hospital findByName(String name);
}
