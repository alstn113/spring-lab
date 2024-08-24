package transaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    default Log getLogById(long id) {
        String message = String.format("로그를 찾을 수 없습니다. id=%d", id);

        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException(message));
    }
}
