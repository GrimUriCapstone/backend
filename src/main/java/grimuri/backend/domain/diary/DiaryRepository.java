package grimuri.backend.domain.diary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Boolean existsByIdAndSelectedTrue(Long diaryId);

    List<Diary> findByUser_Email(String username);

    Optional<Diary> findById(Long diaryId);

    Page<Diary> findByUser_Email(String username, Pageable pageable);

    @EntityGraph(attributePaths = { "user" })
    Page<Diary> findByOpenAndSelected(Boolean open, Boolean selected, Pageable pageable);

    Long countByUser_EmailAndImageCreatedIsTrueAndSelectedIsFalse(String email);
}
