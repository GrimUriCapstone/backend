package grimuri.backend.domain.diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Boolean existsByIdAndSelectedTrue(Long diaryId);

    List<Diary> findByUser(Long userId);
}
