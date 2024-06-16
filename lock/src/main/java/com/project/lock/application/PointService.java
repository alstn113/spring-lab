package com.project.lock.application;

import com.project.lock.domain.Point;
import com.project.lock.domain.PointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointService {

    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Transactional
    public Point createPoint(Long amount) {
        return pointRepository.save(new Point(amount));
    }

    @Transactional(readOnly = true)
    public Long getPoint(Long pointId) {
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new IllegalArgumentException("포인트를 찾을 수 없습니다."));

        return point.getAmount();
    }

    @Transactional
    public Long subtractPoint(Long pointId, Long amount) {
        Point point = pointRepository.findByIdWithLock(pointId);

        return point.subtract(amount);
    }
}
