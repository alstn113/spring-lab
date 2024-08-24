package transaction.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import transaction.domain.Log;
import transaction.domain.LogRepository;

@Service
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    /**
     * 상위 트랜잭션에서 실패해도 로그 기록은 항상 커밋된다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logWithRequiresNew(String message, boolean fail) {
        Log log = new Log(message);
        logRepository.save(log);

        if (fail) {
            throw new IllegalArgumentException("로그 실패");
        }
    }
}
