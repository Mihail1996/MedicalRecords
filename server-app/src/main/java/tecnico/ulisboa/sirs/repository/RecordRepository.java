package tecnico.ulisboa.sirs.repository;

import tecnico.ulisboa.sirs.model.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("recordRepository")
public interface RecordRepository extends JpaRepository<Record, Long> {
    Record findById(int record);
}
