package pl.dernovyi.workingermanyback.repositoty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dernovyi.workingermanyback.model.CV;

@Repository
public interface CvRepository extends JpaRepository<CV, Long> {
    CV findByName(String name);
}
