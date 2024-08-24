package transaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    default Account getAccountById(long id) {
        String message = String.format("계좌를 찾을 수 없습니다. id=%d", id);

        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException(message));
    }
}
