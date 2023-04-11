package grimuri.backend.domain.diary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Boolean existsByIdAndSelectedTrue(Long diaryId);

    List<Diary> findByUser_Username(String username);

    Optional<Diary> findById(Long diaryId);

    Page<Diary> findByUser_Username(String username, Pageable pageable);

}
