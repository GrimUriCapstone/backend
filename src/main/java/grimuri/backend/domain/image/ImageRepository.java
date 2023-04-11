package grimuri.backend.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByDiaryId(Long diaryId);

    List<Image> findByDiary_User_Username(Long userSeq);
}
