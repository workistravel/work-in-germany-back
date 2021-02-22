package pl.dernovyi.workingermanyback.repositoty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dernovyi.workingermanyback.model.Image;
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByName(String name);
}
