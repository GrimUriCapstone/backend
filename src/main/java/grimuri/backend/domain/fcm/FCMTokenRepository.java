package grimuri.backend.domain.fcm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {

    List<FCMToken> findAllByUser_Email(String email);
}
