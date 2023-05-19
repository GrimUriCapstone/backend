package grimuri.backend.domain.image;

import grimuri.backend.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    void deleteAllByDiary(Diary diary);

    List<Image> findByDiaryId(Long diaryId);

    List<Image> findByDiary_User_Username(Long userSeq);
}
