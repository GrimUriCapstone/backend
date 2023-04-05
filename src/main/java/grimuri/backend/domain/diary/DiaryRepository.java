package grimuri.backend.domain.diary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Boolean existsByIdAndSelectedTrue(Long diaryId);

    List<Diary> findByUser_Seq(Long userSeq);

    Page<Diary> findByUser_Seq(Long userSeq, Pageable pageable);
}
